package com.allonsy.android.ftp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
	private ExtendedEditText mConnectionName;
	private ExtendedEditText mServerIP;
	private ExtendedEditText mServerPort;
	private ExtendedEditText mServerUsername;
	private ExtendedEditText mServerPassword;
	private ExtendedEditText mDestination;


    private String connectionName;
    private String serverIP;
    private String serverPort;
    private String serverUsername;
    private String serverPassword;
    private String destination;

    private List<ExtendedEditText> mSourcesEditTexts;
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
    public static final String FTP_OBJECT = "ftpObject";
    public static final String FTP_PASSWORD = "ftpPassword";
    public static final int ADD_FTP = 0;
    public static final int UPDATE_FTP = 1;
    public static final String RETURN_STATE = "ftpState";
    private static final int REQUEST_PHOTO= 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mFTP = (FTP) getArguments().getSerializable(ARG_FTP_ID);
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
    public void onResume() {
        super.onResume();
        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    save();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ftp_edit, container, false);

        mConnectionName = (ExtendedEditText) v.findViewById(R.id.edit_ftp_connection_name);
        connectionName = mFTP.getConnectionName();
        mConnectionName.setText(connectionName);
        setEditTextKeyImeChangeListener(mConnectionName);
		
		mServerIP = (ExtendedEditText) v.findViewById(R.id.edit_ftp_server_ip);
        serverIP = mFTP.getServerIP();
        mServerIP.setText(serverIP);
        setEditTextKeyImeChangeListener(mServerIP);

		mServerPort = (ExtendedEditText) v.findViewById(R.id.edit_ftp_server_port);
        serverPort = mFTP.getServerPort();
        mServerPort.setText(serverPort);
        setEditTextKeyImeChangeListener(mServerPort);

		mServerUsername = (ExtendedEditText) v.findViewById(R.id.edit_ftp_server_username);
        serverUsername = mFTP.getServerUsername();
        mServerUsername.setText(serverUsername);
        setEditTextKeyImeChangeListener(mServerUsername);

        mServerPassword = (ExtendedEditText) v.findViewById(R.id.edit_ftp_server_password);
        mServerPassword.setText(serverPassword);
        setEditTextKeyImeChangeListener(mServerPassword);

		mDestination = (ExtendedEditText) v.findViewById(R.id.edit_ftp_server_destination);
        destination = mFTP.getDestination();
        mDestination.setText(destination);
        setEditTextKeyImeChangeListener(mDestination);
		
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
                save();
                return true;
            case R.id.menu_item_cancel:
                cancel();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void save() {
        boolean valid=true;
        boolean validBefore=true;

        //check connection name validity
        //p{L} matches any kind of letter from any language.
        if (connectionName.isEmpty() || (!connectionName.isEmpty() && !connectionName.matches("^[\\p{L} .'-{0-9}]+$")))
        {
            mConnectionName.setError("valid characters include (a-z)(0-9)-_'.()");
            valid = false;
        }
        else if (connectionName.length() > 25)
        {
            mConnectionName.setError("connection name can only be 25 chars");
            valid = false;
        }
        else
            mConnectionName.setError(null);

        if(!valid && validBefore) {
            validBefore=false;
            Toast.makeText(getActivity(), "enter a valid connection name", Toast.LENGTH_SHORT).show();
            //mConnectionName.requestFocus();
        }

        //check server ip validity
        String validIpAddressRegex = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
        String validHostnameRegex = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";

        if (serverIP.isEmpty() || (!serverIP.isEmpty() && (!serverIP.matches(validIpAddressRegex) && !serverIP.matches(validHostnameRegex))))
        {
            mServerIP.setError("only standard formats of ip or hostname are valid");
            valid = false;
        }
        else
            mServerIP.setError(null);

        if(!valid && validBefore) {
            validBefore=false;
            Toast.makeText(getActivity(), "enter a valid ip or hostname", Toast.LENGTH_SHORT).show();
            //mServerIP.requestFocus();
        }

        //check server port validity
        String validPortRegex = "^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$";


        if (serverPort.isEmpty() || (!serverPort.isEmpty() && !serverPort.matches(validPortRegex)))
        {
            mServerPort.setError("valid port range is 0-65535");
            valid = false;
        }
        else
            mServerPort.setError(null);

        if(!valid && validBefore) {
            validBefore=false;
            Toast.makeText(getActivity(), "enter a valid port", Toast.LENGTH_SHORT).show();
            //mServerPort.requestFocus();
        }

        //check server username validity
        //string can contain only ASCII letters and digits, with hyphens, underscores and spaces as internal separators.
        // the first and last character are not separators, and that there's never more than one separator in a row
        if (serverUsername.isEmpty() || (!serverUsername.isEmpty() && !serverUsername.matches("^[A-Za-z0-9]+(?:[ ._-][A-Za-z0-9]+)*$")))
        {
            mServerUsername.setError("valid characters include (a-z)(0-9) .-_");
            valid = false;
        }
        else if (serverUsername.length() > 25)
        {
            mServerUsername.setError("server username can only be 25 chars");
            valid = false;
        }
        else
            mServerUsername.setError(null);

        if(!valid && validBefore) {
            validBefore=false;
            Toast.makeText(getActivity(), "enter a valid server username", Toast.LENGTH_SHORT).show();
            //mServerUsername.requestFocus();
        }

        //check password validity
        if (!serverPassword.isEmpty() && (serverPassword.length() > 50 || !serverPassword.matches("^\\p{ASCII}+$")))
        {
            mServerPassword.setError("password can only be 50 ASCII chars");
            valid = false;
        }
        else
            mServerPassword.setError(null);

        if(!valid && validBefore) {
            validBefore=false;
            Toast.makeText(getActivity(), "enter a valid password", Toast.LENGTH_SHORT).show();
            //mServerPassword.requestFocus();
        }

        //check destination validity
        //String validUnixPathRegex = "\([^\0 !$`&*()+]\|\\\(\ |\!|\$|\`|\&|\*|\(|\)|\+\)\)\+";
        String validUnixPathRegex = "^\\/$|(^(?=\\/)|^\\.|^\\.\\.)(\\/(?=[^/\0])[^/\0]+)*\\/?$";
        //String validWindowsPathRegex ="([a-zA-Z]:)?(\\\\[a-zA-Z0-9_.-]+)+\\\\?";

        if (!destination.isEmpty() && !destination.matches(validUnixPathRegex))
        {
            mDestination.setError("only unix paths are valid");
            valid = false;
        }
        else if (destination.length() > 200)
        {
            mDestination.setError("path can only be 200 chars");
            valid = false;
        }
        else
            mDestination.setError(null);

        if(!valid && validBefore) {
            validBefore=false;
            Toast.makeText(getActivity(), "enter a valid destination", Toast.LENGTH_SHORT).show();
            //mDestination.requestFocus();
        }

        //int errorIndex = mSourcesEditTexts.size();
        for(int i = 0; i!= mSourcesEditTexts.size(); i++)
        {
            String source = sources.get(i);
            //check source validity
            if (!source.isEmpty() && !source.matches(validUnixPathRegex))
            {
                if(mSourcesEditTexts.get(i)!=null) {
                    mSourcesEditTexts.get(i).setError("only unix paths are valid");
                    valid = false;
                    //errorIndex = i;
                }
            }
            else if (source.length() > 200)
            {
                if(mSourcesEditTexts.get(i)!=null)
                {
                    mSourcesEditTexts.get(i).setError("path can only be 200 chars");
                    valid = false;
                    //errorIndex = i;
                }
            }
            else
            if(mSourcesEditTexts.get(i)!=null)
                mSourcesEditTexts.get(i).setError(null);

        }

        if(!valid && validBefore) {
            validBefore=false;
            Toast.makeText(getActivity(), "enter valid source paths", Toast.LENGTH_SHORT).show();
            //if(errorIndex >= 0 && errorIndex < mSourcesEditTexts.size())
                //mSourcesEditTexts.get(errorIndex).requestFocus();
        }


        if(valid) {

            mFTP.setConnectionName(connectionName);
            mFTP.setServerIP(serverIP);
            mFTP.setServerPort(serverPort);
            mFTP.setServerUsername(serverUsername);
            mFTP.setDestination(destination);

            List<String> newSources = new ArrayList<>();
            for (int i = 0; i != sources.size(); i++) {
                if(!sources.get(i).isEmpty())
                    newSources.add(sources.get(i));
            }
            mFTP.setSources(newSources);

            Intent resultIntent1 = new Intent();
            resultIntent1.putExtra(RETURN_STATE, "1");
            resultIntent1.putExtra(FTP_OBJECT, mFTP);
            resultIntent1.putExtra(FTP_PASSWORD, serverPassword);
            getActivity().setResult(Activity.RESULT_OK, resultIntent1);
            getActivity().finish();
        }

    }


    private void cancel() {
        Intent resultIntent2 = new Intent();
        resultIntent2.putExtra(RETURN_STATE, "0");
        getActivity().setResult(Activity.RESULT_OK, resultIntent2);
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_PHOTO) {


        }
    }

    private void setEditTextKeyImeChangeListener(final ExtendedEditText extendedEditText)
    {
        extendedEditText.setKeyImeChangeListener(new ExtendedEditText.KeyImeChange(){
            @Override
            public boolean onKeyIme(int keyCode, KeyEvent event)
            {
                if (event.getAction()==KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK )
                {
                    extendedEditText.clearFocus();
                    save();
                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    //Toast.makeText(getActivity(), "back", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else
                    return false;
            }});
    }

    private void addSourceField(String text)
    {
		LinearLayout sourceLinearLayout = new LinearLayout(getContext());
        sourceLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        sourceLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        mSourcesLinearLayouts.add(sourceLinearLayout);

        int i = mSourcesLinearLayouts.size()-1;

        ExtendedEditText sourceEditText = new ExtendedEditText(getContext());
        mSourcesEditTexts.add(sourceEditText);
        mSourcesEditTexts.get(i).setText(text);
        mSourcesEditTexts.get(i).setId(i);
        mSourcesEditTexts.get(i).addTextChangedListener(new SourceTextWatcher(mSourcesEditTexts.get(i)));
        setEditTextKeyImeChangeListener(mSourcesEditTexts.get(i));
		
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
        private ExtendedEditText mEditText;

        public SourceTextWatcher(ExtendedEditText e) {
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



    public static FTPEditFragment newInstance(FTP ftp) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FTP_ID, ftp);
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
