package com.allonsy.android.ftp;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class FTPViewFragment extends Fragment {

    private FTP mFTP;
    private String mPassword;
	private TextView mConnectionName;
	private TextView mServerIpPort;
	private TextView mServerUsername;
	private TextView mDestination;
	private TextView mSources;
    List<String> sources;

    int imageViewWidth=0;
    int imageViewHeight=0;


    private static final String ARG_FTP_ID = "ftp_id";
    private static final String DIALOG_CONTACT_IMAGE = "DialogContactImage";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID ftpId = (UUID) getArguments().getSerializable(ARG_FTP_ID);
        mFTP = FTPLab.get(getActivity()).getFTP(ftpId);
        mPassword  = FTPLab.get(getActivity()).retrieveServerPassword(mFTP);

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ftp_view, container, false);

        mConnectionName = (TextView) v.findViewById(R.id.view_ftp_connection_name);
        mConnectionName.setText("Connection Name : " + mFTP.getConnectionName());
		
		mServerIpPort = (TextView) v.findViewById(R.id.view_ftp_server_ip_port);
        mServerIpPort.setText("Ip/port : " + mFTP.getServerIP() + ":" + mFTP.getServerPort());
		
		mServerUsername = (TextView) v.findViewById(R.id.view_ftp_server_username);
        mServerUsername.setText("Username : " + mFTP.getServerUsername());
		
		mDestination = (TextView) v.findViewById(R.id.view_ftp_server_destination);
        mDestination.setText("Destination : " + mFTP.getDestination());

		
    
        sources = mFTP.getSources();
		String sourcesString="";
        for(int i=0;i!=sources.size();i++)
        {
            sourcesString+=sources.get(i) + "\n";
        }
		mSources = (TextView) v.findViewById(R.id.view_ftp_source_list);
		mSources.setText(sourcesString);
     

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_ftp_view, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_ftp_run:
                if (!isMyServiceRunning(FTPService.class)) {
                    Toast.makeText(getActivity(), "Transfer Started", Toast.LENGTH_SHORT).show();
                    Intent intent1 = FTPService.newIntent(getActivity(), mFTP.getId());
                    getContext().startService(intent1);
                } else
                    Toast.makeText(getActivity(), "An FTP transfer already running", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_item_ftp_edit:
                Intent intent2 = FTPEditActivity.newIntent(getActivity(), mFTP.getId());
                startActivity(intent2);
                getActivity().finish();
                return true;
            case R.id.menu_item_ftp_delete:
                FTPLab.get(getActivity()).deleteFTP(mFTP);
                getActivity().finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
    }

    public static FTPViewFragment newInstance(UUID ftpId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FTP_ID, ftpId);
        FTPViewFragment fragment = new FTPViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
