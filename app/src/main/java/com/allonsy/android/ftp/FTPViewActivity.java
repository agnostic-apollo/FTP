package com.allonsy.android.ftp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;


public class FTPViewActivity extends SingleFragmentActivity {

    private static final String EXTRA_FTP_ID =
            "com.allonsy.android.ftp.crime_id";

    public static Intent newIntent(Context packageContext, UUID ftpId) {
        Intent intent = new Intent(packageContext, FTPViewActivity.class);
        intent.putExtra(EXTRA_FTP_ID, ftpId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID ftpId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_FTP_ID);
        return FTPViewFragment.newInstance(ftpId);
    }

}
