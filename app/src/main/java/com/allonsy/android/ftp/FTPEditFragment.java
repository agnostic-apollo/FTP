package com.allonsy.android.ftp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FTPEditFragment extends Fragment {

    private FTP mFTP;
	private EditText mConnectionName;
	private EditText mServerIP;
	private EditText mServerPort;
	private EditText mServerUsername;
	private EditText mServerPassword;
	private EditText mDestination;


    private String connectionName;
    private String serverIP;
    private String serverPort;
    private String serverUsername;
    private String serverPassword;
    private String destination;

    private List<EditText> mSourcesEditTexts;
    private List<ImageView> mSourcesDeleteButtons;
    private List<LinearLayout> mSourcesLinearLayouts;
    private List<String> sources;
	private Button mAddSourceButton;
	private int mSourcesLayoutsCount;


    private LinearLayout mSourcesParentLayout;



    private static final String ARG_FTP_ID = "ftp_id";
    private static final String ARG_FTP_PASSWORD = "ftp_password";
    private static final String ARG_FTP = "ftp";
    private static final String DIALOG_FTP_SOURCE = "DialogFtpSource";
    private static final String RETURN_STATE = "ftpState";
    private static final String FTP_ID = "ftpId";
    private static final int REQUEST_PHOTO= 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID ftpId = (UUID) getArguments().getSerializable(ARG_FTP_ID);
        mFTP = FTPLab.get(getActivity()).getFTP(ftpId);
        serverPassword = FTPLab.get(getActivity()).retrieveServerPassword(mFTP);

        if (savedInstanceState != null) {
            mFTP = (FTP) savedInstanceState.getSerializable(ARG_FTP);
            serverPassword = (String) savedInstanceState.getSerializable(ARG_FTP_PASSWORD);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ftp_edit, container, false);

        mConnectionName = (EditText) v.findViewById(R.id.edit_ftp_connection_name);
        connectionName = mFTP.getConnectionName();
        mConnectionName.setText(connectionName);
		
		mServerIP = (EditText) v.findViewById(R.id.edit_ftp_server_ip);
        serverIP = mFTP.getServerIP();
        mServerIP.setText(serverIP);
		
		mServerPort = (EditText) v.findViewById(R.id.edit_ftp_server_port);
        serverPort = mFTP.getServerPort();
        mServerPort.setText(serverPort);
		
		mServerUsername = (EditText) v.findViewById(R.id.edit_ftp_server_username);
        serverUsername = mFTP.getServerUsername();
        mServerUsername.setText(serverUsername);

        mServerPassword = (EditText) v.findViewById(R.id.edit_ftp_server_password);
        mServerPassword.setText(serverPassword);
		
		mDestination = (EditText) v.findViewById(R.id.edit_ftp_server_destination);
        destination = mFTP.getDestination();
        mDestination.setText(destination);

		
        mConnectionName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                connectionName=s.toString();
                connectionName = connectionName.replaceAll(System.getProperty("line.separator"),"");
            }
            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });

		mServerIP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                serverIP=s.toString();
                serverIP = serverIP.replaceAll(System.getProperty("line.separator"),"");
            }
            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });
		
		mServerPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                serverPort=s.toString();
                serverPort = serverPort.replaceAll(System.getProperty("line.separator"),"");
            }
            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });
		
		mServerUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                serverUsername=s.toString();
                serverUsername = serverUsername.replaceAll(System.getProperty("line.separator"),"");
            }
            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });

        mServerPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                serverPassword=s.toString();
                serverPassword = serverPassword.replaceAll(System.getProperty("line.separator"),"");
            }
            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });
		
		mDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                destination=s.toString();
                destination = destination.replaceAll(System.getProperty("line.separator"),"");
            }
            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });
		
		
        mSourcesParentLayout = (LinearLayout) v.findViewById(R.id.edit_ftp_source_list);
        sources = mFTP.getSources();
        mSourcesEditTexts = new ArrayList<>();
        mSourcesDeleteButtons = new ArrayList<>();
        mSourcesLinearLayouts = new ArrayList<>();
        mSourcesLayoutsCount=0;
        if(sources.size()>0) {
            for (int i = 0; i != sources.size(); i++) {
                addSourceField(sources.get(i));
            }
        }
        else
        {
            addSourceField("");
            sources.add("");
        }

        mAddSourceButton = (Button)v.findViewById(R.id.edit_ftp_add_source_button);
        mAddSourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSourcesLayoutsCount<=20) {
                    addSourceField("");
                    sources.add("");
					//start activity here
                }
                else
                    Toast.makeText(getActivity(), "Max limit is 20",
                            Toast.LENGTH_LONG).show();
            }
        });


 

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_ftp_edit, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_save:
                boolean valid=true;

                //check connection name validity
                if (connectionName.isEmpty() || !connectionName.matches("^[\\p{L} .'-]+$"))
                {
                    mConnectionName.setError("enter a valid connection name");
                    valid = false;
                }
                else if (connectionName.length() > 25)
                {
                    mConnectionName.setError("connection name can only be 25 chars");
                    valid = false;
                }
                else
                    mConnectionName.setError(null);

				//check server ip validity
				String validIpAddressRegex = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
				String validHostnameRegex = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";
				
                if (serverIP.isEmpty() || (!serverIP.matches(validIpAddressRegex) && !serverIP.matches(validHostnameRegex)))
                {
                    mServerIP.setError("enter a valid ip or hostname");
                    valid = false;
                }
                else
                    mServerIP.setError(null);
				
				
				//check server port validity
				String validPortRegex = "^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$";
				
				
                if (serverPort.isEmpty() || !serverPort.matches(validPortRegex))
                {
                    mServerPort.setError("enter a valid port");
                    valid = false;
                }
                else
                    mServerPort.setError(null);
				

				//check server username validity
				//string can contain only ASCII letters and digits, with hyphens, underscores and spaces as internal separators.
				// the first and last character are not separators, and that there's never more than one separator in a row
				if (serverUsername.isEmpty() || !serverUsername.matches("^[A-Za-z0-9]+(?:[ _-][A-Za-z0-9]+)*$"))
                {
                    mServerUsername.setError("enter a valid server username");
                    valid = false;
                }
				else if (serverUsername.length() > 25)
                {
                    mServerUsername.setError("server username can only be 25 chars");
                    valid = false;
                }
                else
                    mServerUsername.setError(null);
				
				//check password validity
				if (!serverPassword.isEmpty() && (serverPassword.length() > 50 || !serverPassword.matches("^\\p{ASCII}+$")))
                {
                    mServerPassword.setError("password can only be 50 ASCII chars");
                    valid = false;
                }
                else
                    mServerPassword.setError(null);
				
				//check destination validity
				//String validUnixPathRegex = "\([^\0 !$`&*()+]\|\\\(\ |\!|\$|\`|\&|\*|\(|\)|\+\)\)\+";
				String validUnixPathRegex = "^\\/$|(^(?=\\/)|^\\.|^\\.\\.)(\\/(?=[^/\0])[^/\0]+)*\\/?$";
				//String validWindowsPathRegex ="([a-zA-Z]:)?(\\\\[a-zA-Z0-9_.-]+)+\\\\?";

               if (!destination.isEmpty() && !destination.matches(validUnixPathRegex))
                {
                    mDestination.setError("enter a valid unix destination path");
                    valid = false;
                }
				else if (destination.length() > 200)
                {
                    mDestination.setError("destination path can only be 200 chars");
                    valid = false;
                }
                else
                    mDestination.setError(null);
				
				
				
                for(int i = 0; i!= mSourcesEditTexts.size(); i++)
                {
                    String source = sources.get(i);
                    //check source validity
                    if (!source.isEmpty() && !source.matches(validUnixPathRegex))
                    {
                        if(mSourcesEditTexts.get(i)!=null) {
                            mSourcesEditTexts.get(i).setError("enter a valid unix source path");
                            valid = false;
                        }
                    }
                    else if (source.length() > 200)
                    {
                        if(mSourcesEditTexts.get(i)!=null)
                        {
                            mSourcesEditTexts.get(i).setError("source can only be 200 chars");
                            valid = false;
                        }
                    }
                    else
                        if(mSourcesEditTexts.get(i)!=null)
                             mSourcesEditTexts.get(i).setError(null);

                }

              

                if(valid) {
					
					mFTP.setConnectionName(connectionName);
					mFTP.setServerIP(serverIP);
					mFTP.setServerPort(serverPort);
					mFTP.setServerUsername(serverUsername);
					mFTP.setDestination(destination);
					mFTP.setSources(sources);

                    FTPLab.get(getActivity()).updateFTP(mFTP,serverPassword);
                    Toast.makeText(getActivity(), "saved",
                            Toast.LENGTH_LONG).show();

                    //Intent resultIntent1 = new Intent();
                    //resultIntent1.putExtra(RETURN_STATE, "1");
                    //resultIntent1.putExtra(FTP_ID, mFTP.getId().toString());
					Intent resultIntent1 = FTPListFragment.newResultIntent(getActivity(),true, mFTP.getId());
                    getActivity().setResult(Activity.RESULT_OK, resultIntent1);

                    getActivity().finish();
                }
                return true;
            case R.id.menu_item_cancel:
                //Intent resultIntent2 = new Intent();
                //resultIntent2.putExtra(RETURN_STATE, "0");
                //resultIntent2.putExtra(FTP_ID, mFTP.getId().toString());
				Intent resultIntent2 = FTPListFragment.newResultIntent(getActivity(),false, mFTP.getId());
                getActivity().setResult(Activity.RESULT_OK, resultIntent2);
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
        if (requestCode == REQUEST_PHOTO) {


        }
    }
    private void addSourceField(String text)
    {
		LinearLayout sourceLinearLayout = new LinearLayout(getContext());
        sourceLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        sourceLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        mSourcesLinearLayouts.add(sourceLinearLayout);

        int i = mSourcesLinearLayouts.size()-1;

        EditText sourceEditText = new EditText(getContext());
        mSourcesEditTexts.add(sourceEditText);
        mSourcesEditTexts.get(i).setText(text);
        mSourcesEditTexts.get(i).setId(i);
        mSourcesEditTexts.get(i).addTextChangedListener(new SourceTextWatcher(mSourcesEditTexts.get(i)));
		
		ImageView sourceDeleteButton = new ImageView(getContext());
        mSourcesDeleteButtons.add(sourceDeleteButton);
        mSourcesDeleteButtons.get(i).setImageResource(R.drawable.ic_delete_source);
        mSourcesDeleteButtons.get(i).setId(i);
        mSourcesDeleteButtons.get(i).setOnClickListener(new SourceDeleteButtonClick(mSourcesDeleteButtons.get(i)));

        mSourcesLinearLayouts.get(i).addView(mSourcesEditTexts.get(i));
        mSourcesLinearLayouts.get(i).addView(mSourcesDeleteButtons.get(i));

        mSourcesParentLayout.addView(mSourcesLinearLayouts.get(i));

        LinearLayout.LayoutParams editTextParams = (LinearLayout.LayoutParams) mSourcesEditTexts.get(i).getLayoutParams();
        //params.setMargins(10, 0, 10, 0);
        editTextParams.weight=5;
        mSourcesEditTexts.get(i).setLayoutParams(editTextParams);

        LinearLayout.LayoutParams deleteButtonParams = (LinearLayout.LayoutParams) mSourcesDeleteButtons.get(i).getLayoutParams();
        //deleteButtonParams.setMargins(10, 0, 10, 0);
        deleteButtonParams.weight=1;
        mSourcesDeleteButtons.get(i).setLayoutParams(deleteButtonParams);

        mSourcesLayoutsCount++;
    }

   

    public class SourceTextWatcher implements TextWatcher {
        private EditText mEditText;

        public SourceTextWatcher(EditText e) {
            mEditText = e;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String source = s.toString();
            source = source.replaceAll(System.getProperty("line.separator"),"");
            sources.set(mEditText.getId(),source);
        }

        public void afterTextChanged(Editable s) {
        }
    }

    public class SourceDeleteButtonClick implements View.OnClickListener {
        private ImageView mImageView;

        public SourceDeleteButtonClick(ImageView i) {
            mImageView = i;
        }

        public void onClick(View v) {
            sources.set(mImageView.getId(),"");
            mSourcesParentLayout.removeView(mSourcesLinearLayouts.get(mImageView.getId()));

            //mSourcesLinearLayouts.get(mButton.getId()).setVisibility(LinearLayout.GONE);
            mSourcesLinearLayouts.set(mImageView.getId(),null);
            mSourcesEditTexts.set(mImageView.getId(),null);
            mSourcesDeleteButtons.set(mImageView.getId(),null);
            mSourcesLayoutsCount--;

        }

    }



    public static FTPEditFragment newInstance(UUID ftpId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FTP_ID, ftpId);
        FTPEditFragment fragment = new FTPEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        mFTP.setConnectionName(connectionName);
        mFTP.setServerIP(serverIP);
        mFTP.setServerPort(serverPort);
        mFTP.setServerUsername(serverUsername);
        mFTP.setDestination(destination);
        mFTP.setSources(sources);

        savedInstanceState.putSerializable(ARG_FTP, mFTP);
        savedInstanceState.putSerializable(ARG_FTP_PASSWORD, serverPassword);
    }
}
