package com.allonsy.android.ftp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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

public class FTPListFragment extends Fragment {

    private RecyclerView mFTPRecyclerView;
    private TextView mFTPTextView;
    private FTPAdapter mAdapter;
    private String mQuery;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private static final int ADD_FTP = 0;
	private static final String EXTRA_FTP_ID = "ftpId";
	private static final String EXTRA_RETURN_STATE = "ftpState";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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


    /*private class ExportDatabaseCSVTask extends AsyncTask<String ,String, String> {
        //private final ProgressDialog dialog = new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            //this.dialog.setMessage("Exporting database...");
            //this.dialog.show();
        }

        protected String doInBackground(final String... args){
            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            Date date = new Date() ;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss") ;

            File file = new File(exportDir, dateFormat.format(date) + ".csv");
            try {

                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

                //data
                FTPLab ftpLab = FTPLab.get(getActivity());
                List<FTP> FTPs;
                List<String> phones;
                List<String> emails;
                FTPs = ftpLab.getContacts();
                int max_phones=0;
                int max_emails=0;

                //get max number of phone and emails in database
                for (int i = 0; i != FTPs.size(); i++) {
                    phones = FTPs.get(i).getSources();
                    if(phones.size()>max_phones)
                        max_phones=phones.size();
                    emails = FTPs.get(i).getEmails();
                    if(emails.size()>max_emails)
                        max_emails=emails.size();
                }

                //write headers
                ArrayList<String> header= new ArrayList<String>();
                header.add("Name");
                for (int j = 0; j != max_phones; j++) {
                    header.add("Phone "+ String.valueOf(j+1));
                }
                for (int j = 0; j != max_emails; j++) {
                    header.add("Email "+ String.valueOf(j+1));
                }

                String[] arr1 = header.toArray(new String[header.size()]);
                csvWrite.writeNext(arr1);

                //write data
                for (int i = 0; i != FTPs.size(); i++) {

                    ArrayList<String> data= new ArrayList<String>();
                    data.add(FTPs.get(i).getName());


                    phones = FTPs.get(i).getSources();
                    int j;
                    for (j = 0; j != phones.size(); j++) {
                        data.add(phones.get(j));
                    }
                    for (; j< max_phones; j++) {
                        data.add("");
                    }

                    emails = FTPs.get(i).getEmails();
                    for (j = 0; j != emails.size(); j++) {
                        data.add(emails.get(j));
                    }
                    for (; j< max_emails; j++) {
                        data.add("");
                    }

                    String[] arr = data.toArray(new String[data.size()]);
                    csvWrite.writeNext(arr);

                }
                csvWrite.close();

                if(FTPs.size()==0)
                    return "0";

                return "1";
            }
            catch (IOException e){
                Log.e("allonsy.contacts", e.getMessage(), e);
                return "2";
            }
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(final String success) {

           // if (this.dialog.isShowing()){
                //this.dialog.dismiss();
            //}
            if (success!=null && success.equals("0")){
                Toast.makeText(getActivity(), "No Contacts Saved", Toast.LENGTH_SHORT).show();
            }
            else if (success!=null && success.equals("1")){
                Toast.makeText(getActivity(), "Export successful!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(), "Export failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    */

}

