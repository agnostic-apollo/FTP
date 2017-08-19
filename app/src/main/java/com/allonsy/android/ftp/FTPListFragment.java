package com.allonsy.android.ftp;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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

public class FTPListFragment extends Fragment {

    private RecyclerView mFTPRecyclerView;
    private TextView mFTPTextView;
    private FTPAdapter mAdapter;
    private String mQuery;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private static final int ADD_FTP = 0;
	private static final String EXTRA_FTP_ID = "ftpId";
	private static final String EXTRA_RETURN_STATE = "ftpState";
    private MyListener callback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkPermissions();
    }

    @Override
    public void onAttach(Context  context) {
        super.onAttach(context);
        callback= (MyListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ftp_list, container, false);
        mFTPRecyclerView = (RecyclerView) view
                .findViewById(R.id.ftp_recycler_view);
        mFTPRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFTPTextView = (TextView) view
                .findViewById(R.id.empty_view);

        updateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_ftp_list, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mQuery=s;
                updateUI();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery(mQuery, false);
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener()
        {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item)
            {
                mQuery=null;
                updateUI();
                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item)
            {
                return true;
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_ftp:
                FTP ftp = new FTP();
                FTPLab.get(getActivity()).addFTP(ftp,"");
                Intent intent = FTPEditActivity.newIntent(getActivity(), ftp.getId());
                startActivityForResult(intent, ADD_FTP);
                return true;
            /*
            case R.id.menu_item_export_contacts:
                ExportDatabaseCSVTask task=new ExportDatabaseCSVTask();
                task.execute();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        FTPLab ftpLab = FTPLab.get(getActivity());

        int ftpSize = ftpLab.getFTPs().size();
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural, ftpSize, ftpSize);

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        if(activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        FTPLab ftpLab = FTPLab.get(getActivity());
        List<FTP> FTPs;
        if(mQuery==null)
            FTPs = ftpLab.getFTPs();
        else
            FTPs = ftpLab.searchFTPByName(mQuery);

        if (mAdapter == null) {
            mAdapter = new FTPAdapter(FTPs);
            mFTPRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setFTPs(FTPs);
            mAdapter.notifyDataSetChanged();
            //mAdapter.notifyItemChanged(mAdapter.getPosition());
        }

        int ftpSize = ftpLab.getFTPs().size();

        if (ftpSize==0) {
            mFTPRecyclerView.setVisibility(View.GONE);
            mFTPTextView.setVisibility(View.VISIBLE);
        }
        else {
            mFTPRecyclerView.setVisibility(View.VISIBLE);
            mFTPTextView.setVisibility(View.GONE);
        }

        updateSubtitle();
    }

    private class FTPHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mConnectionNameTextView;
        private FTP mFTP;
        private FTPAdapter mAdapter;
        int imageViewWidth=0;
        int imageViewHeight=0;



        public FTPHolder(View itemView ) {
            super(itemView);
            mConnectionNameTextView = (TextView)
                    itemView.findViewById(R.id.list_item_ftp_connection_name);

            itemView.setOnClickListener(this);
        }

        public void bindContact(FTP ftp) {
            mFTP = ftp;
            mConnectionNameTextView.setText(mFTP.getConnectionName());
        }

        @Override
        public void onClick(View v) {

            Intent intent = FTPViewActivity.newIntent(getActivity(), mFTP.getId());
            startActivity(intent);
        }
    }

    private class FTPAdapter extends RecyclerView.Adapter<FTPHolder> {

        private List<FTP> mFTPs;

        private int position;

        public FTPAdapter(List<FTP> ftps) {
            mFTPs = ftps;
        }

        @Override
        public FTPHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_ftp, parent, false);
            return new FTPHolder(view);
        }

        @Override
        public void onBindViewHolder(FTPHolder holder, int position) {
            FTP ftp = mFTPs.get(position);
            holder.bindContact(ftp);
        }

        @Override
        public int getItemCount() {
            return mFTPs.size();
        }

        public void setFTPs(List<FTP> ftps) {
            mFTPs = ftps;
        }


    }

	public static Intent newResultIntent(Context packageContext, boolean returnState, UUID ftpId) {
        Intent intent = new Intent(packageContext, FTPListFragment.class);
        intent.putExtra(EXTRA_RETURN_STATE, returnState);
        intent.putExtra(EXTRA_FTP_ID, ftpId);
        return intent;
    }
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == ADD_FTP) {

            boolean returnState = (boolean) data.getSerializableExtra(EXTRA_RETURN_STATE);
            if(!returnState) {
               Toast.makeText(getActivity(), "deleted",
                        Toast.LENGTH_LONG).show();
				UUID ftpId = (UUID) data.getSerializableExtra(EXTRA_FTP_ID);		
                FTPLab.get(getActivity()).deleteFTP
                        (FTPLab.get(getActivity()).getFTP(ftpId));
            }
        }

        updateUI();
    }

    public void checkPermissions() {
        int result;
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,

        };

        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(getActivity(),p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{p}, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! continue

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Permission Not Granted", Toast.LENGTH_SHORT).show();
                    try {Thread.sleep(1000);} catch (Exception e) {Log.e(TAG, e.getMessage());}
                    //callback.finishActivity();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public interface MyListener {
        public void finishActivity();
    }

}

