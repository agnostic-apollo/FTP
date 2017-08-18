package com.allonsy.android.ftp;


import android.database.Cursor;
import android.database.CursorWrapper;

import com.allonsy.android.ftp.database.FTPDbSchema.ConnectionTable;
import com.allonsy.android.ftp.database.FTPDbSchema.SourceTable;


public class FTPCursorWrapper extends CursorWrapper {
    public FTPCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public String getFTPConnectionUUID() {
        return getString(getColumnIndex(ConnectionTable.Cols.UUID));

    }

    public String getFTPConnectionConnectionName() {
        return getString(getColumnIndex(ConnectionTable.Cols.CONNECTION_NAME));
    }

    public String getFTPConnectionConnectionServerIP() {
        return getString(getColumnIndex(ConnectionTable.Cols.Server_IP));
    }

    public String getFTPConnectionConnectionServerPort() {
        return getString(getColumnIndex(ConnectionTable.Cols.Server_PORT));
    }

    public String getFTPConnectionConnectionServerUsername() {
        return getString(getColumnIndex(ConnectionTable.Cols.Server_USERNAME));
    }

    public String getFTPConnectionConnectionServerDestination() {
        return getString(getColumnIndex(ConnectionTable.Cols.Server_DESTINATION));
    }

    public String getFTPSourceUUID() {
        return getString(getColumnIndex(SourceTable.Cols.UUID));
    }

    public String getFTPSourceSource() {
        return getString(getColumnIndex(SourceTable.Cols.SOURCE));
    }

}