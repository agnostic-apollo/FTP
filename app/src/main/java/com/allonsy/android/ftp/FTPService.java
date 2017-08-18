package com.allonsy.android.ftp;


import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

import static android.content.ContentValues.TAG;


public class FTPService extends Service {

    private static final String EXTRA_FTP_ID = "com.allonsy.android.ftp.id";

    private FTP mFTP;
    private String serverIP;
    private String serverPort;
    private String serverUsername;
    private String serverPassword;
    private String destination;
    private List<String> sources;
    private static long totalTransferSize;
    private static long currentTransferSize;
    private static long filesTransfreedSize;
    private static int progress;
    private static long currentFileSize;
    private static int notificationId;
    private static long updater;
    private static long counter;
    private static  List<String> localDirList;
    private static  List<CustomFile> localFileList;
    private boolean success=true;
    private Handler handler;

    private static FTPClient ftpClient = new FTPClient();

    private static NotificationManager mNotifyManager;
    private static NotificationCompat.Builder mBuilder;

    private WifiManager.WifiLock wifiLock;
    private PowerManager.WakeLock wakeLock;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        acquireWakelocks();
        UUID ftpId = (UUID) intent.getSerializableExtra(EXTRA_FTP_ID);

        mFTP = FTPLab.get(this).getFTP(ftpId);
        serverPassword = FTPLab.get(this).retrieveServerPassword(mFTP);
        serverIP = mFTP.getServerIP();
        serverPort = mFTP.getServerPort();
        serverUsername = mFTP.getServerUsername();
        destination = mFTP.getDestination();
        sources = mFTP.getSources();
        totalTransferSize=0;
        filesTransfreedSize=0;
        notificationId=0;

        localDirList = new ArrayList<>();
        localFileList = new ArrayList<>();

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);

        //start FTP Transfer
        startFTP();

        return START_NOT_STICKY;
    }


    public static Intent newIntent(Context packageContext, UUID ftpId) {
        Intent intent = new Intent(packageContext, FTPService.class);
        intent.putExtra(EXTRA_FTP_ID, ftpId);
        return intent;
    }

    private void startFTP()
    {
        mBuilder.setContentTitle("FTP Transfer")
                .setContentText("Transfer in progress")
                .setSmallIcon(R.drawable.ic_launcher);

        new Thread(new Runnable() {
            public void run() {
                    try {
                        handler = new Handler(getMainLooper());
                        ftpClient.connect(serverIP, Integer.valueOf(serverPort));
                        ftpClient.login(serverUsername, serverPassword);
                        ftpClient.enterLocalPassiveMode();
                        ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
                        ftpClient.setCopyStreamListener(createListener());
                        ftpClient.setConnectTimeout(30000);
                        //ftpClient.setBufferSize(1024*1024);
                        //Log.d(TAG,"'" + destination + "'");
                        if(destination!=null && !destination.equals("")) {
                            if (!ftpCreateDirectoryTree(destination)) {
                                stop(false);
                                return;
                            }
                        }
                        else
                            destination = "/";

                        //destination=ftpClient.printWorkingDirectory();
                        //Log.d(TAG, destination);



                        //get list of all directories and files to transfer and calculate their size
                        for (int i = 0; i != sources.size(); i++)
                            walkFilesAndDirectories(sources.get(i));


                        //create all the missing sub directories on server
                        for (int i = 0; i != localDirList.size(); i++) {
                            if(!ftpChangeToDestinationDirectory()) {
                                stop(false);
                                return;
                            }
                            //Log.v(TAG,"creating dir : " + localDirList.get(i));
                            if(!ftpCreateDirectoryTree(localDirList.get(i))){
                                stop(false);
                                return;
                            }
                            //Log.d(TAG, "current dir : " + ftpClient.printWorkingDirectory());
                         }

                        // Displays the progress bar for the first time.
                        mBuilder.setProgress(100, 0, false);
                        mNotifyManager.notify(notificationId, mBuilder.build());
                        Log.v(TAG,String.valueOf(totalTransferSize));
                        String remoteFile;

                        updater =(totalTransferSize/1024)/100;
                        if(updater==0)
                            updater=totalTransferSize/1024;
                        if(updater==0)
                            updater=10;
                        counter=0;
                        //send all files to server
                        for (int i = 0; i != localFileList.size(); i++) {
                            //Log.v(TAG,"file : " + localFileList.get(i).subPath);
                            remoteFile = localFileList.get(i).file.getName();
                            currentFileSize=localFileList.get(i).size;
                            ftpClient.changeWorkingDirectory(destination + "/" + localFileList.get(i).subPath);
                            InputStream inputStream = new FileInputStream(localFileList.get(i).file);

                            if(!ftpClient.storeFile(remoteFile, inputStream))
                            {
                                success=false;
                                break;
                            }
                            inputStream.close();
                            filesTransfreedSize+=currentFileSize;
                        }

                    }


                    catch (IOException ex) {
                        success=false;
                        System.out.println("Error: " + ex.getMessage());
                        showToast(ex.getMessage());
                        ex.printStackTrace();
                    }

                    stop(success);

                }
            }).start();

    }

    private void stop(boolean success)
    {
        try {
            //set notification
            if(mBuilder!=null) {
                if(success) {
                    mBuilder.setContentText("Transfer Complete")
                            .setProgress(0, 0, false);
                    Log.v(TAG,"Transfer Complete");
                    showToast("Transfer Complete");
                }
                else {
                    mBuilder.setContentText("Transfer Failed")
                            .setProgress(0, 0, false);
                    Log.v(TAG,"Transfer Failed");
                    showToast("Transfer Failed");
                }
            }

            if(mNotifyManager!=null)
                 mNotifyManager.notify(notificationId, mBuilder.build());

            if(ftpClient!=null) {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        releaseWakelocks();
        stopSelf();
    }

    private void walkFilesAndDirectories(String source) throws IOException {
        File sourceFile = new File(source);
        File d = new File(source);
        if(d.exists()) {
            if (d.isFile()) {
                String localDirSubPath = source.replace(d.getParent() + "/", "");
                long fileSize = d.length();
                localFileList.add(new CustomFile(d, "", fileSize));   //add file to file list
                totalTransferSize += d.length();    //add file size in total size to transfer
            } else if (d.isDirectory()) {
                TreeSet<File> closed = new TreeSet<File>(new FileComp());
                Deque<File> open = new ArrayDeque<File>();
                open.push(d);
                closed.add(d);
                boolean deepestDir;

                while (!open.isEmpty()) {
                    d = open.pop();
                    deepestDir = true;
                    for (File f : d.listFiles()) {
                        if (f.isDirectory() && !closed.contains(f)) {
                            open.push(f);
                            closed.add(f);
                            deepestDir = false;
                            //add directory to dir list only if not a symbolic link
                        } else if (f.isFile()) {
                            String localDirSubPath = f.getParent().replace(sourceFile.getParent() + "/", "");
                            long fileSize = f.length();
                            localFileList.add(new CustomFile(f, localDirSubPath, fileSize));   //add file to file list
                            currentTransferSize += fileSize;    //add file size in total size to transfer
                        }
                    }
                    if (deepestDir) //if deepest directory with no sub directory
                    {
                        localDirList.add(d.getAbsolutePath().replace(sourceFile.getParent() + "/", ""));
                    }

                }
            }
        }
    }

    public class FileComp implements Comparator<File>{
        @Override
        public int compare(File f1, File f2) {
            return f1.toString().compareTo(f2.toString());
        }
    }

    public class CustomFile {
        public File file;
        public String subPath;
        public long size;

        public CustomFile(File f,String p,long s)
        {
            file=f;
            subPath=p;
            size=s;
        }
    }

    private static CopyStreamListener createListener() {
                 return new CopyStreamListener() {

                     public void bytesTransferred(CopyStreamEvent event) {
                                 bytesTransferred(event.getTotalBytesTransferred(), event.getBytesTransferred(),
                                                 event.getStreamSize());
                             }

                     public void bytesTransferred(long totalBytesTransferred,
                     int bytesTransferred, long streamSize) {
                                currentTransferSize=filesTransfreedSize+totalBytesTransferred;
                                //Log.v(TAG,String.valueOf(counter));
                                if(counter%updater==0) {
                                    progress = (int) (currentTransferSize * 100 / totalTransferSize);
                                    //Log.v(TAG,String.valueOf(totalBytesTransferred))
                                    mBuilder.setProgress(100, progress, false);
                                    mNotifyManager.notify(notificationId, mBuilder.build());
                                }
                                counter++;
                             }
         };
    }
    private boolean ftpChangeToDestinationDirectory() {
        try {
            if (!ftpClient.changeWorkingDirectory(destination)) {
                Log.e(TAG, "Unable to change into destination directory '" + destination + "'.  error='" + ftpClient.getReplyString() + "'");
                showToast("Unable to change into destination directory '" + destination + "'.");
                return false;
            }
        }
       catch (IOException ex) {
                System.out.println("Error: " + ex.getMessage());
                ex.printStackTrace();
                return false;
            }
        return true;
    }

    private boolean ftpCreateDirectoryTree(String dirTree ) {
        boolean dirExists = true;
        try{
            //tokenize the string and attempt to change into each directory level.  If you cannot, then start creating.
            String[] directories = dirTree.split("/");
            for (String dir : directories ) {
                if (!dir.isEmpty() ) {
                    dirExists = ftpClient.changeWorkingDirectory(dir);

                    if (!dirExists) {
                        //Log.v(TAG,"creating dir : " + dir);
                        if (!ftpClient.makeDirectory(dir)) {
                            Log.e(TAG, "Unable to create remote directory '" + dir + "'.  error='" + ftpClient.getReplyString()+"'");
                            showToast("Unable to create remote directory '" + dir + "'.");
                            return false;
                        }
                        if (!ftpClient.changeWorkingDirectory(dir)) {
                            Log.e(TAG, "Unable to change into newly created remote directory '" + dir + "'.  error='" + ftpClient.getReplyString()+"'");
                            showToast("Unable to change into newly created remote directory '" + dir + "'.");
                            return false;
                        }
                    }
                }
            }
        }
       catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
           return false;
        }

        return true;
    }

    private void acquireWakelocks() {
        if(wifiLock==null) {
            WifiManager wMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiLock = wMgr.createWifiLock(WifiManager.WIFI_MODE_FULL, "com.allonsy.android.ftp.wifilock");
        }

        //Logger.logDebug("aquiring wakelock ");
        try {
            if (wifiLock != null && !wifiLock.isHeld())
                wifiLock.acquire();
        } catch (Exception e) {
            //Logger.logError("Error getting wifiLock: " + e.getMessage());
        }

        if(wakeLock==null) {
            PowerManager pMgr = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            wakeLock = pMgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "com.allonsy.android.ftp.wakelock");
        }

        try {
            if (wakeLock != null && !wakeLock.isHeld())
                wakeLock.acquire();
        } catch (Exception e) {
            //Logger.logError("Error getting wifiLock: " + e.getMessage());
        }
    }

    private void releaseWakelocks() {

        try {
            if (wifiLock != null && wifiLock.isHeld()) {
                wifiLock.release();
                wifiLock=null;
            }
        } catch (Exception e) {
            //Logger.logError("Error releasing wifiLock: " + e.getMessage());
        }
        try {
            if (wakeLock != null && wakeLock.isHeld()) {
                wakeLock.release();
                wakeLock=null;
            }
        } catch (Exception e) {
            //Logger.logError("Error releasing wakeLock: " + e.getMessage());
        }
    }


    private void showToast(final String message)
    {
        if(handler!=null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}

