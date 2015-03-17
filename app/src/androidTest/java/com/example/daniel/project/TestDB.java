package com.example.daniel.project;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.daniel.project.data.PackagesContract.PackageEntry;
import com.example.daniel.project.data.PackagesContract.PreferencesEntry;
import com.example.daniel.project.data.PackagesDbHelper;

import java.util.Map;
import java.util.Set;

/**
 * Created by daniel on 3/02/15.
 */
public class TestDB extends AndroidTestCase {

    public static final String LOG_TAG = TestDB.class.getSimpleName();
    static final String TEST_PACKAGE = "com.test.blabla";
    static final String TEST_DATE = "20141205";

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(PackagesDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new PackagesDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        PackagesDbHelper dbHelper = new PackagesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = createFakePackageValues();

        long packageRowId;
        packageRowId = db.insert(PackageEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(packageRowId != -1);
        Log.d(LOG_TAG, "New row id: " + packageRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                PackageEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, testValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues packageValues = createFakePreferenceValues(packageRowId);

        long weatherRowId = db.insert(PreferencesEntry.TABLE_NAME, null, packageValues);
        assertTrue(weatherRowId != -1);

        // A cursor is your primary interface to the query results.
        Cursor preferencesCursor = db.query(
                PreferencesEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        validateCursor(preferencesCursor, packageValues);

        dbHelper.close();
    }

    static ContentValues createFakePreferenceValues(long packageRowId) {
        ContentValues preferenceValues = new ContentValues();
        preferenceValues.put(PreferencesEntry.COLUMN_PACKAGE_ID, packageRowId);
        preferenceValues.put(PreferencesEntry.COLUMN_FORWARD_METHOD, PreferencesEntry.MAIL_METHOD);
        preferenceValues.put(PreferencesEntry.COLUMN_EXTRA_PARAM, "blabla@bleble.com");
        return preferenceValues;
    }

    static ContentValues createSecondFakePreferenceValues(long packageRowId) {
        ContentValues preferenceValues = new ContentValues();
        preferenceValues.put(PreferencesEntry.COLUMN_PACKAGE_ID, packageRowId);
        preferenceValues.put(PreferencesEntry.COLUMN_FORWARD_METHOD, PreferencesEntry.WEB_METHOD);
        preferenceValues.put(PreferencesEntry.COLUMN_EXTRA_PARAM, "www.blabla.com");
        return preferenceValues;
    }

    static ContentValues createFakePackageValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(PackageEntry.COLUMN_PACKAGE_INFO, TEST_PACKAGE);
        testValues.put(PackageEntry.COLUMN_PACKAGE_ACTIVE, PackageEntry.ACTIVE_STRING);

        return testValues;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        Log.d(LOG_TAG, "valueCursor length: "+valueCursor.getCount());
        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            Log.d(LOG_TAG, "looking at column " + columnName);
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            Log.d(LOG_TAG, "comparing " + expectedValue + " with " + valueCursor.getString(idx));
            assertEquals(expectedValue, valueCursor.getString(idx));
            Log.d(LOG_TAG,"assert successful");
        }
        Log.d(LOG_TAG,"about to close cursor");
        valueCursor.close();
        Log.d(LOG_TAG,"cursor closed");
    }

    static void validateCursor(Cursor valueCursor, ContentValues[] expectedValues) {

        Log.d(LOG_TAG, "valueCursor length: "+valueCursor.getCount());
        assertTrue(valueCursor.moveToFirst());

        for(int i=0; i<expectedValues.length; i++) {
            Set<Map.Entry<String, Object>> valueSet = expectedValues[i].valueSet();
            for (Map.Entry<String, Object> entry : valueSet) {
                String columnName = entry.getKey();
                Log.d(LOG_TAG, "looking at column " + columnName);
                int idx = valueCursor.getColumnIndex(columnName);
                assertFalse(idx == -1);
                String expectedValue = entry.getValue().toString();
                Log.d(LOG_TAG, "comparing " + expectedValue + " with " + valueCursor.getString(idx));
                assertEquals(expectedValue, valueCursor.getString(idx));
                Log.d(LOG_TAG, "assert successful");
            }
            valueCursor.moveToNext();
        }
        Log.d(LOG_TAG,"about to close cursor");
        valueCursor.close();
        Log.d(LOG_TAG,"cursor closed");
    }
}