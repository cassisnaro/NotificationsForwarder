package com.example.daniel.project.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.daniel.project.data.PackagesContract.PackageEntry;
import com.example.daniel.project.data.PackagesContract.PreferencesEntry;

/**
 * Created by daniel on 3/02/15.
 */
public class PackagesDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "packages.db";

    public PackagesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_PACKAGES_TABLE = "CREATE TABLE " + PackageEntry.TABLE_NAME + " (" +
                PackageEntry._ID + " INTEGER PRIMARY KEY," +
                PackageEntry.COLUMN_PACKAGE_INFO + " TEXT UNIQUE NOT NULL, " +
                PackageEntry.COLUMN_PACKAGE_ACTIVE + " TEXT NOT NULL, " +
                "UNIQUE (" + PackageEntry.COLUMN_PACKAGE_INFO +") ON CONFLICT IGNORE"+
                " );";
        final String SQL_CREATE_PREFERENCES_TABLE = "CREATE TABLE " + PreferencesEntry.TABLE_NAME + " (" +
                PreferencesEntry._ID + " INTEGER PRIMARY KEY," +
                PreferencesEntry.COLUMN_PACKAGE_ID + " INTEGER NOT NULL, " +
                PreferencesEntry.COLUMN_FORWARD_METHOD + " TEXT NOT NULL, " +
                PreferencesEntry.COLUMN_EXTRA_PARAM + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + PreferencesEntry.COLUMN_PACKAGE_ID + ") REFERENCES " +
                PackageEntry.TABLE_NAME + " (" + PackageEntry._ID + ") " +
                //", UNIQUE (" + PreferencesEntry.COLUMN_PACKAGE_ID +","+PreferencesEntry.COLUMN_FORWARD_METHOD+") ON CONFLICT REPLACE"+
                " );";
        db.execSQL(SQL_CREATE_PACKAGES_TABLE);
        db.execSQL(SQL_CREATE_PREFERENCES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PreferencesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PackageEntry.TABLE_NAME);
        onCreate(db);
    }
}
