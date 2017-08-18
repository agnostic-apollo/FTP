package com.allonsy.android.ftp;


import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {


    public static String getPassword(Context context, String uuid) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(uuid, "");
    }

    public static void setPassword(Context context, String uuid, String password) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(uuid, password)
                .apply();
    }
    public static void deletePassword(Context context, String uuid) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .remove(uuid)
                .apply();
    }


}