package com.allonsy.android.ftp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;


public class FTPEditActivity extends SingleFragmentActivity {

    private static final String EXTRA_FTP_ID =
            "com.allonsy.android.ftp.id";
	private static final String RETURN_STATE = "ftpState";

    public static Intent newIntent(Context packageContext, UUID ftpId) {
        Intent intent = new Intent(packageContext, FTPEditActivity.class);
        intent.putExtra(EXTRA_FTP_ID, ftpId);
        return intent;
    }



	
    @Override
    protected Fragment createFragment() {
        UUID ftpId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_FTP_ID);
        return FTPEditFragment.newInstance(ftpId);
    }

}
