package com.allonsy.android.ftp;


import android.support.v4.app.Fragment;

public class FTPListActivity extends SingleFragmentActivity  implements FTPListFragment.MyListener {

	
    @Override
    protected Fragment createFragment() {
        return new FTPListFragment();
    }

    public void finishActivity(){
        finish();
    }
}