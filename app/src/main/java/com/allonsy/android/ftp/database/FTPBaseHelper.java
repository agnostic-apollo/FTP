package com.allonsy.android.ftp.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.allonsy.android.ftp.database.FTPDbSchema.ConnectionTable;
import com.allonsy.android.ftp.database.FTPDbSchema.SourceTable;

public class FTPBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "ftpBase.db";

    public FTPBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ConnectionTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                ConnectionTable.Cols.UUID + ", " +
                ConnectionTable.Cols.CONNECTION_NAME + ", " +
                ConnectionTable.Cols.Server_IP + ", " +
                ConnectionTable.Cols.Server_PORT + ", " +
                ConnectionTable.Cols.Server_USERNAME + ", " +
                ConnectionTable.Cols.Server_DESTINATION +
                ")"
        );
        db.execSQL("create table " + SourceTable.NAME + "(" +
                " _id integer primary key not null, " +
                SourceTable.Cols.UUID + ", " +
                SourceTable.Cols.SOURCE + ", " +
                "foreign key (" + SourceTable.Cols.UUID + ") " +
                "references " + ConnectionTable.NAME + "(" + ConnectionTable.Cols.UUID + ") " +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}