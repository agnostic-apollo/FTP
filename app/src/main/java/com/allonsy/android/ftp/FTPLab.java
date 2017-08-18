package com.allonsy.android.ftp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.allonsy.android.ftp.database.FTPDbSchema.ConnectionTable;
import com.allonsy.android.ftp.database.FTPDbSchema.SourceTable;

import com.allonsy.android.ftp.database.FTPBaseHelper;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static android.content.ContentValues.TAG;


public class FTPLab {

    private static FTPLab sFTPLab;
    private static Context mContext;
    private SQLiteDatabase mDatabase;

    public static FTPLab get(Context context) {
        if (sFTPLab == null) {
            sFTPLab = new FTPLab(context);
        }
        return sFTPLab;
    }
    private FTPLab(Context context)
    {
        mContext = context.getApplicationContext();
        mDatabase = new FTPBaseHelper(mContext)
                .getWritableDatabase();
    }

    public void addFTP(FTP f, String password)
    {
        ContentValues connectionValues = getConnectionContentValues(f);
        List<ContentValues> sourceValues = getSourceContentValues(f);

        mDatabase.insert(ConnectionTable.NAME, null, connectionValues);
        for(int i=0; i!=sourceValues.size();i++)
             mDatabase.insert(SourceTable.NAME, null, sourceValues.get(i));
        storeServerPassword(f, password);

    }


    public void updateFTP(FTP ftp, String password) {
        String uuidString = ftp.getId().toString();

        //update connection values
        ContentValues values = getConnectionContentValues(ftp);
        mDatabase.update(ConnectionTable.NAME, values,
                ConnectionTable.Cols.UUID + " = ?",
                new String[] { uuidString });

        String temp;

        //update sources
        List<String> newSources= new ArrayList<>();
        List<String> databaseSources = getSources(uuidString);
        List<String> ftpSources = ftp.getSources();

        //check for blank and null sources
        for(int i=0; i!=ftpSources.size();i++) {
            temp = ftpSources.get(i);
            if(temp != null && !temp.isEmpty()) {
                newSources.add(temp);
            }

        }

        //check if newSources and databaseSources are different and update
        if(databaseSources != null && (databaseSources.size() == newSources.size())){
            databaseSources.removeAll(newSources);
            if(!databaseSources.isEmpty()) { //both lists not same.
                mDatabase.delete(SourceTable.NAME,
                        SourceTable.Cols.UUID + " = ?",
                        new String[] { uuidString });
                ftp.setSources(newSources);
                List<ContentValues> sourceValues = getSourceContentValues(ftp);
                for(int i=0; i!=sourceValues.size();i++)
                    mDatabase.insert(SourceTable.NAME, null, sourceValues.get(i));
            }
        }
        else if (databaseSources != null && (databaseSources.size() != newSources.size())){
                mDatabase.delete(SourceTable.NAME,
                        SourceTable.Cols.UUID + " = ?",
                        new String[] { uuidString });
                ftp.setSources(newSources);
                List<ContentValues> sourceValues = getSourceContentValues(ftp);
                for(int i=0; i!=sourceValues.size();i++)
                    mDatabase.insert(SourceTable.NAME, null, sourceValues.get(i));
        }

        storeServerPassword(ftp, password);

    }

    public void deleteFTP(FTP ftp) {
        String uuidString = ftp.getId().toString();

        //delete ftp
        mDatabase.delete(ConnectionTable.NAME,
                ConnectionTable.Cols.UUID + " = ?",
                new String[] { uuidString });

        mDatabase.delete(SourceTable.NAME,
                SourceTable.Cols.UUID + " = ?",
                new String[] { uuidString });

        deleteServerPassword(ftp);
    }

    public List<FTP> getFTPs()
    {
        List<FTP> ftps = new ArrayList<>();
        FTPCursorWrapper connectionCursor = queryFTPs(ConnectionTable.NAME,null, null);
        try {
            connectionCursor.moveToFirst();
            while (!connectionCursor.isAfterLast()) {

                String uuid = connectionCursor.getFTPConnectionUUID();
                String connectionName = connectionCursor.getFTPConnectionConnectionName();
                String serverIP = connectionCursor.getFTPConnectionConnectionServerIP();
                String serverPort = connectionCursor.getFTPConnectionConnectionServerPort();
                String serverUsername = connectionCursor.getFTPConnectionConnectionServerUsername();
                String destination = connectionCursor.getFTPConnectionConnectionServerDestination();
                List<String> sources = getSources(uuid);

                FTP ftp = new FTP(UUID.fromString(uuid));
                ftp.setConnectionName(connectionName);
                ftp.setServerIP(serverIP);
                ftp.setServerPort(serverPort);
                ftp.setServerUsername(serverUsername);
                ftp.setDestination(destination);
                ftp.setSources(sources);

                ftps.add(ftp);
                connectionCursor.moveToNext();
            }
        } finally {
            connectionCursor.close();
        }
        return ftps;
    }

    public String retrieveServerPassword(FTP f) {

        String decryptedPassword="";
        try {
        KeyHelper encryptionManager = new KeyHelper(mContext);
        String encryptedPassword = QueryPreferences.getPassword(mContext, f.getId().toString());
        decryptedPassword = encryptionManager.decrypt(mContext, encryptedPassword);

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (NoSuchProviderException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (InvalidAlgorithmParameterException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (KeyStoreException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (CertificateException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }  catch (NoSuchPaddingException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }  catch (BadPaddingException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (IllegalBlockSizeException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (UnsupportedOperationException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return decryptedPassword;
    }

    public void storeServerPassword(FTP f, String password) {
        try {

            KeyHelper encryptionManager = new KeyHelper(mContext);
            String encryptedPassword = encryptionManager.encrypt(mContext, password);
            QueryPreferences.setPassword(mContext, f.getId().toString(), encryptedPassword);

            } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            } catch (NoSuchProviderException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            } catch (InvalidAlgorithmParameterException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            } catch (KeyStoreException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            } catch (CertificateException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            } catch (IOException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }  catch (NoSuchPaddingException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }  catch (BadPaddingException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            } catch (IllegalBlockSizeException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            } catch (UnsupportedOperationException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
    }
    public void deleteServerPassword(FTP f) {
        QueryPreferences.deletePassword(mContext, f.getId().toString());
    }

    public List<FTP> searchFTPByName(String search)
    {
        List<FTP> ftps = new ArrayList<>();
        FTPCursorWrapper connectionCursor = queryFTPs(ConnectionTable.NAME, ConnectionTable.Cols.CONNECTION_NAME + " LIKE ?",
                new String[] { "%" + search + "%" });
        try {
            connectionCursor.moveToFirst();
            while (!connectionCursor.isAfterLast()) {

                String uuid = connectionCursor.getFTPConnectionUUID();
                String connectionName = connectionCursor.getFTPConnectionConnectionName();
                String serverIP = connectionCursor.getFTPConnectionConnectionServerIP();
                String serverPort = connectionCursor.getFTPConnectionConnectionServerPort();
                String serverUsername = connectionCursor.getFTPConnectionConnectionServerUsername();
                String destination = connectionCursor.getFTPConnectionConnectionServerDestination();
                List<String> sources = getSources(uuid);

                FTP ftp = new FTP(UUID.fromString(uuid));
                ftp.setConnectionName(connectionName);
                ftp.setServerIP(serverIP);
                ftp.setServerPort(serverPort);
                ftp.setServerUsername(serverUsername);
                ftp.setDestination(destination);
                ftp.setSources(sources);

                ftps.add(ftp);
                connectionCursor.moveToNext();
            }
        } finally {
            connectionCursor.close();
        }
        return ftps;
    }

    public List<String> getSources(String id)
    {
        List<String> sources = new ArrayList<>();

        FTPCursorWrapper cursor = queryFTPs(
                SourceTable.NAME,
                SourceTable.Cols.UUID + " = ?",
                new String[] { id }
        );
        try {
            if (cursor.getCount() == 0) {
                return new ArrayList<>();
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                sources.add(cursor.getFTPSourceSource());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return sources;
    }


    public FTP getFTP(UUID id) {

        FTPCursorWrapper connectionCursor = queryFTPs(
                ConnectionTable.NAME,
                ConnectionTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );
        try {
            if (connectionCursor.getCount() == 0) {
                return null;
            }
            connectionCursor.moveToFirst();

            String uuid = connectionCursor.getFTPConnectionUUID();
            String connectionName = connectionCursor.getFTPConnectionConnectionName();
            String serverIP = connectionCursor.getFTPConnectionConnectionServerIP();
            String serverPort = connectionCursor.getFTPConnectionConnectionServerPort();
            String serverUsername = connectionCursor.getFTPConnectionConnectionServerUsername();
            String destination = connectionCursor.getFTPConnectionConnectionServerDestination();
            List<String> sources = getSources(uuid);

            FTP ftp = new FTP(UUID.fromString(uuid));
            ftp.setConnectionName(connectionName);
            ftp.setServerIP(serverIP);
            ftp.setServerPort(serverPort);
            ftp.setServerUsername(serverUsername);
            ftp.setDestination(destination);
            ftp.setSources(sources);

            return ftp;
        } finally {
            connectionCursor.close();
        }
    }


    private static ContentValues getConnectionContentValues(FTP ftp) {
        ContentValues values = new ContentValues();
        values.put(ConnectionTable.Cols.UUID, ftp.getId().toString());
        values.put(ConnectionTable.Cols.CONNECTION_NAME, ftp.getConnectionName());
        values.put(ConnectionTable.Cols.Server_IP, ftp.getServerIP());
        values.put(ConnectionTable.Cols.Server_PORT, ftp.getServerPort());
        values.put(ConnectionTable.Cols.Server_USERNAME, ftp.getServerUsername());
        values.put(ConnectionTable.Cols.Server_DESTINATION, ftp.getDestination());
        return values;
    }

    private static List<ContentValues> getSourceContentValues(FTP ftp) {

        List<ContentValues>  values =  new ArrayList<>();
        List<String> sources = ftp.getSources();

        for(int i=0; i!=sources.size();i++) {
            ContentValues value = new ContentValues();
            value.put(SourceTable.Cols.UUID, ftp.getId().toString());
            value.put(SourceTable.Cols.SOURCE, sources.get(i));
            values.add(value);
        }

        return values;
    }


    private FTPCursorWrapper queryFTPs(String table, String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                table,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new FTPCursorWrapper(cursor);
    }

}