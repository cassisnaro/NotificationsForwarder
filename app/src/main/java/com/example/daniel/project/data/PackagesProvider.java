package com.example.daniel.project.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.example.daniel.project.PackagesFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by daniel on 3/02/15.
 */
public class PackagesProvider extends ContentProvider{
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PackagesDbHelper mPackagesHelper;
    private static final SQLiteQueryBuilder queryBuilderJoinned;
    private static final SQLiteQueryBuilder queryBuilder;

    private static final int PACKAGES = 100;
    private static final int PACKAGE_SELECTED = 101;
    private static final int PACKAGE = 102;
    private static final int PREFERENCES = 200;
    private static final int PREFERENCES_PACKAGE = 201;
    private static final int PREFERENCES_PACKAGE_STRING = 202;
    private static final int PREFERENCE = 202;

    private static String LOG_TAG = PackagesProvider.class.getSimpleName();
    static{
        queryBuilderJoinned = new SQLiteQueryBuilder();
        queryBuilderJoinned.setTables(
                PackagesContract.PreferencesEntry.TABLE_NAME + " INNER JOIN " +
                        PackagesContract.PackageEntry.TABLE_NAME +
                        " ON " + PackagesContract.PreferencesEntry.TABLE_NAME +
                        "." + PackagesContract.PreferencesEntry.COLUMN_PACKAGE_ID +
                        " = " + PackagesContract.PackageEntry.TABLE_NAME +
                        "." + PackagesContract.PackageEntry._ID);
        queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(PackagesContract.PackageEntry.TABLE_NAME);
    }

    private static final String sPackageSettingSelectionName =
            PackagesContract.PackageEntry.TABLE_NAME+
                    "." + PackagesContract.PackageEntry.COLUMN_PACKAGE_INFO + " = ? ";
    private static final String sPackageSettingSelectionId =
            PackagesContract.PackageEntry.TABLE_NAME+
                    "." + PackagesContract.PackageEntry.COLUMN_PACKAGE_ID + " = ? ";
    private static final String sPackageStringSettingSelectionId =
            PackagesContract.PackageEntry.TABLE_NAME+
                    "." + PackagesContract.PackageEntry.COLUMN_PACKAGE_INFO + " = ? ";


    private static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PackagesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, PackagesContract.PATH_PACKAGES, PACKAGES);
        matcher.addURI(authority, PackagesContract.PATH_PACKAGE, PACKAGE);
        matcher.addURI(authority, PackagesContract.PATH_PACKAGES + "/*", PACKAGE_SELECTED);
        matcher.addURI(authority, PackagesContract.PATH_PREFERENCE, PREFERENCE);
        matcher.addURI(authority, PackagesContract.PATH_PREFERENCES, PREFERENCES);
        Log.v(LOG_TAG, "path_preferences: " + PackagesContract.PATH_PREFERENCE);
        matcher.addURI(authority, PackagesContract.PATH_PREFERENCES + "/*", PREFERENCES_PACKAGE);
        matcher.addURI(authority, PackagesContract.PATH_PREFERENCES_STRING + "/*", PREFERENCES_PACKAGE_STRING);
        Log.v(LOG_TAG,"------path string:"+PackagesContract.PATH_PREFERENCES_STRING);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mPackagesHelper = new PackagesDbHelper(getContext());
        return true;
    }

    private Cursor getPackageById(Uri uri, String[] projection, String sortOrder) {
        String packageId = PackagesContract.PackageEntry.getPackageIdForUri(uri);
        return queryBuilder.query(mPackagesHelper.getReadableDatabase(),
                projection,
                sPackageSettingSelectionId,
                new String[]{packageId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getPackagePreferences(Uri uri, String[] projection, String sortOrder) {
        //String packageName = PackagesContract.PackageEntry.getPackageNameForUri(uri);
        String packageId = PackagesContract.PackageEntry.getPackageIdForUri(uri);
        return queryBuilderJoinned.query(mPackagesHelper.getReadableDatabase(),
                projection,
                sPackageSettingSelectionId,
                new String[]{packageId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getPackageStringPreferences(Uri uri, String[] projection, String sortOrder) {
        //String packageName = PackagesContract.PackageEntry.getPackageNameForUri(uri);
        String packageId = PackagesContract.PackageEntry.getPackageIdForUri(uri);
        Log.v(LOG_TAG,"doing the query with packageId: "+packageId);
        if(projection==null){
            Log.v(LOG_TAG,"projection null");
        }
        Log.v(LOG_TAG,"searching in tables: "+queryBuilderJoinned.getTables());
        return queryBuilderJoinned.query(mPackagesHelper.getReadableDatabase(),
                projection,
                sPackageStringSettingSelectionId,
                new String[]{packageId},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)){
            case PACKAGES: {
                Cursor tmp = mPackagesHelper.getReadableDatabase().query(
                        PackagesContract.PackageEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                List<String> columnNames = new ArrayList<>(Arrays.asList(tmp.getColumnNames()));
                String packageNameColumn = PackagesContract.PackageEntry.COLUMN_PACKAGE_NAME;
                columnNames.add(packageNameColumn);
                retCursor = new MatrixCursor(Arrays.copyOf(columnNames.toArray(), columnNames.toArray().length, String[].class));

                while(tmp.moveToNext()){
                    List<Object> newValues = new ArrayList<>();
                    for(String columnKey: Arrays.asList(tmp.getColumnNames())){
                        Log.v(LOG_TAG,"available column name: "+columnKey);
                        newValues.add(tmp.getString(tmp.getColumnIndex(columnKey)));
                    }

                    String packageName =tmp.getString(tmp.getColumnIndex(PackagesContract.PackageEntry.COLUMN_PACKAGE_INFO));
                    try {
                        Log.v(LOG_TAG,"about to search app name for :"+packageName);
                        ApplicationInfo applicationInfo = getContext().getPackageManager().getApplicationInfo(packageName,0);
                        String appName =  getContext().getPackageManager().getApplicationLabel(applicationInfo).toString();
                        Log.v(LOG_TAG,"found app:"+appName);
                        newValues.add(appName);
                    } catch (PackageManager.NameNotFoundException e) {
                        newValues.add(packageName);
                    }

                    ((MatrixCursor)retCursor).addRow(newValues);
                }
                break;
            }
            case PACKAGE_SELECTED: {
                //retCursor = getPackagePreferences(uri, projection, sortOrder);
                Log.v(LOG_TAG, "executing getPackageById");
                retCursor = getPackageById(uri, projection, sortOrder);
                break;
            }
            case PREFERENCES: {
                retCursor = mPackagesHelper.getReadableDatabase().query(
                        PackagesContract.PreferencesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PREFERENCES_PACKAGE: {
                Log.v(LOG_TAG, "executing getPreferencesPackage");
                retCursor = getPackagePreferences(uri, projection, sortOrder);
                break;
            }
            case PREFERENCES_PACKAGE_STRING: {
                retCursor = getPackageStringPreferences(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){
            case PACKAGES: {
                return PackagesContract.PackageEntry.CONTENT_TYPE;
            }
            case PACKAGE_SELECTED: {
                return PackagesContract.PackageEntry.CONTENT_ITEM_TYPE;
            }
            case PREFERENCE: {
                return PackagesContract.PreferencesEntry.CONTENT_ITEM_TYPE;
            }
            case PREFERENCES: {
                return PackagesContract.PreferencesEntry.CONTENT_TYPE;
            }
            case PREFERENCES_PACKAGE: {
                return PackagesContract.PreferencesEntry.CONTENT_TYPE;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri + " package uri: " + PackagesContract.PATH_PACKAGES);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mPackagesHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PACKAGE: {
                Log.v(LOG_TAG,"inserting package");
                long _id = db.insert(PackagesContract.PackageEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = PackagesContract.PackageEntry.buildPackageUri(_id);
                    getContext().getContentResolver().notifyChange(PackagesContract.PackageEntry.CONTENT_URI_LIST, null);
                } else
                    throw new android.database.SQLException("Failed to insert row info " + uri);
                break;
            }
            case PREFERENCE: {
                long _id = db.insert(PackagesContract.PreferencesEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = PackagesContract.PackageEntry.buildPackageUri(_id);
                    Uri uriToNotify = PackagesContract.PreferencesEntry.CONTENT_URI_LIST.buildUpon().appendPath(values.getAsString(PackagesContract.PreferencesEntry.COLUMN_PACKAGE_ID)).build();
                    Log.v(LOG_TAG,"about to notify: "+uriToNotify);
                    getContext().getContentResolver().notifyChange(uriToNotify,null);
                }else
                    throw new android.database.SQLException("Failed to insert row info " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Log.v(LOG_TAG,"notifying change at uri: "+uri);
        getContext().getContentResolver().notifyChange(uri, null);


        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mPackagesHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case PACKAGES:
                rowsDeleted = db.delete(
                        PackagesContract.PackageEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PREFERENCE:
                rowsDeleted = db.delete(
                        PackagesContract.PreferencesEntry.TABLE_NAME, selection, selectionArgs);
                String packageID= uri.getQueryParameter(PackagesContract.PreferencesEntry.COLUMN_PACKAGE_ID);
                Uri uriToNotify = PackagesContract.PreferencesEntry.CONTENT_URI_LIST.buildUpon().appendPath(packageID).build();
                Log.v(LOG_TAG,"about to notify: "+uriToNotify);
                getContext().getContentResolver().notifyChange(uriToNotify,null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri + " package uri: " + PackagesContract.PATH_PACKAGES);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mPackagesHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case PACKAGE:
                rowsUpdated = db.update(PackagesContract.PackageEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case PREFERENCE:
                rowsUpdated = db.update(PackagesContract.PreferencesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
