package com.example.daniel.project;

/**
 * Created by daniel on 5/02/15.
 */


import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.daniel.project.data.PackagesContract.PackageEntry;
import com.example.daniel.project.data.PackagesContract.PreferencesEntry;

import junit.framework.Test;


public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    // brings our database to an empty state
    public void deleteAllRecords() {
        Log.v(LOG_TAG, "preferences entry path: "+PreferencesEntry.CONTENT_URI);
        mContext.getContentResolver().delete(
                PreferencesEntry.CONTENT_URI,
                null,
                null
        );
        Log.v(LOG_TAG, "packages entry path: "+PackageEntry.CONTENT_URI_LIST);
        mContext.getContentResolver().delete(
                PackageEntry.CONTENT_URI_LIST,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                PackageEntry.CONTENT_URI_LIST,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                PreferencesEntry.CONTENT_URI_LIST,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    public void setUp() {
        deleteAllRecords();
    }

    public void testInsertReadProvider() {

        ContentValues testValues = TestDB.createFakePackageValues();

        Uri packageUri = mContext.getContentResolver().insert(PackageEntry.CONTENT_URI, testValues);
        long packageRowId = ContentUris.parseId(packageUri);

        // Verify we got a row back.
        assertTrue(packageRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                PackageEntry.CONTENT_URI_LIST,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDB.validateCursor(cursor, testValues);

        // Now see if we can successfully query if we include the row id
        cursor = mContext.getContentResolver().query(
                PackageEntry.buildPackageUri(packageRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestDB.validateCursor(cursor, testValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues preferencesValues = TestDB.createFakePreferenceValues(packageRowId);

        Uri preferencesInsertUri = mContext.getContentResolver()
                .insert(PreferencesEntry.CONTENT_URI, preferencesValues);
        assertTrue(preferencesInsertUri != null);

        // A cursor is your primary interface to the query results.
        Cursor preferencesCursor = mContext.getContentResolver().query(
                PreferencesEntry.CONTENT_URI_LIST,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDB.validateCursor(preferencesCursor, preferencesValues);


        // Add the location values in with the weather data so that we can make
        // sure that the join worked and we actually get all the values back
        addAllContentValues(preferencesValues, testValues);

        // Get the joined Weather and Location data
        preferencesCursor = mContext.getContentResolver().query(
                PreferencesEntry.buildPrefrencesPackage(TestDB.TEST_PACKAGE),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestDB.validateCursor(preferencesCursor, preferencesValues);
    }

    public void testGetType() {
        // content://com.example.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(PreferencesEntry.CONTENT_URI_LIST);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(PreferencesEntry.CONTENT_TYPE, type);

        String testLocation = TestDB.TEST_PACKAGE;
        // content://com.example.android.sunshine.app/weather/94074
        type = mContext.getContentResolver().getType(
                PreferencesEntry.buildPrefrencesPackage(testLocation));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(PreferencesEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine.app/location/
        type = mContext.getContentResolver().getType(PackageEntry.CONTENT_URI_LIST);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals(PackageEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine.app/location/1
        type = mContext.getContentResolver().getType(PackageEntry.buildPackageUri(1L));
        // vnd.android.cursor.item/com.example.android.sunshine.app/location
        assertEquals(PackageEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testUpdatePackage() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestDB.createFakePackageValues();

        Uri packageUri = mContext.getContentResolver().
                insert(PackageEntry.CONTENT_URI, values);
        long packageRowId = ContentUris.parseId(packageUri);

        // Verify we got a row back.
        assertTrue(packageRowId != -1);
        Log.d(LOG_TAG, "New row id: " + packageRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(PackageEntry._ID, packageRowId);
        updatedValues.put(PackageEntry.COLUMN_PACKAGE_INFO, "eu.bleble.test");

        int count = mContext.getContentResolver().update(
                PackageEntry.CONTENT_URI, updatedValues, PackageEntry._ID + "= ?",
                new String[] { Long.toString(packageRowId)});

        assertEquals(count, 1);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                PackageEntry.buildPackageUri(packageRowId),
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );

        TestDB.validateCursor(cursor, updatedValues);
    }

    public void testInsertMultiplePreferences() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestDB.createFakePackageValues();

        Uri packageUri = mContext.getContentResolver().
                insert(PackageEntry.CONTENT_URI, values);
        long packageRowId = ContentUris.parseId(packageUri);

        // Verify we got a row back.
        assertTrue(packageRowId != -1);
        Log.d(LOG_TAG, "New row id: " + packageRowId);

        ContentValues firstPreferences = TestDB.createFakePreferenceValues(packageRowId);
        ContentValues secondPreferences = TestDB.createSecondFakePreferenceValues(packageRowId);

        Uri preferencesInsertUri = mContext.getContentResolver()
                .insert(PreferencesEntry.CONTENT_URI, firstPreferences);
        assertTrue(preferencesInsertUri != null);

        Uri preferencesInsertUri2 = mContext.getContentResolver()
                .insert(PreferencesEntry.CONTENT_URI, secondPreferences);
        assertTrue(preferencesInsertUri2 != null);

        // A cursor is your primary interface to the query results.
        Cursor preferencesCursor = mContext.getContentResolver().query(
                PreferencesEntry.CONTENT_URI_LIST,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        ContentValues[] preferencesValues = {firstPreferences, secondPreferences};
        TestDB.validateCursor(preferencesCursor, preferencesValues);

    }

    // Make sure we can still delete after adding/updating stuff
    public void testDeleteRecordsAtEnd() {
        deleteAllRecords();
    }


    // The target api annotation is needed for the call to keySet -- we wouldn't want
    // to use this in our app, but in a test it's fine to assume a higher target.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void addAllContentValues(ContentValues destination, ContentValues source) {
        for (String key : source.keySet()) {
            destination.put(key, source.getAsString(key));
        }
    }

    static final String KALAMAZOO_LOCATION_SETTING = "kalamazoo";
    static final String KALAMAZOO_WEATHER_START_DATE = "20140625";

    long packageRowId;


    // Inserts both the location and weather data for the Kalamazoo data set.
    public void insertPackageData() {
        ContentValues packageValues = TestDB.createFakePackageValues();
        Uri packageInsertUri = mContext.getContentResolver()
                .insert(PackageEntry.CONTENT_URI_LIST, packageValues);
        assertTrue(packageInsertUri != null);

        packageRowId = ContentUris.parseId(packageInsertUri);

        ContentValues preferencesValues = TestDB.createFakePreferenceValues(packageRowId);
        Uri weatherInsertUri = mContext.getContentResolver()
                .insert(PreferencesEntry.CONTENT_URI, preferencesValues);
        assertTrue(weatherInsertUri != null);
    }
}
