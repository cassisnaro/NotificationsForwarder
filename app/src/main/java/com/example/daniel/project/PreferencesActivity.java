package com.example.daniel.project;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.daniel.project.data.PackagesContract;

/**
 * Created by daniel on 7/02/15.
 */
public class PreferencesActivity extends ActionBarActivity implements PreferencesFragment.Callback{

    public static final String PACKAGE_KEY ="package_key";
    public static final String PACKAGE_INFO ="package_info";
    public static final String APP_NAME ="app_name";
    private static String LOG_TAG = PreferencesActivity.class.getSimpleName();
    private Integer package_id;
    private String packageName;
    private String appName;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PACKAGE_INFO, packageName);
        outState.putString(APP_NAME,appName);
        outState.putInt(PACKAGE_KEY, package_id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_preferences);

        if (savedInstanceState == null) {
            // Create the preferences fragment and add it to the activity
            // using a fragment transaction.
            packageName = getIntent().getStringExtra(PACKAGE_INFO);
            appName = getIntent().getStringExtra(APP_NAME);
            package_id = getIntent().getIntExtra(PACKAGE_KEY,-1);
            //package_id = new Integer(packageName);
            //Log.v(LOG_TAG, "package_id: " + package_id);
            Bundle arguments = getIntent().getExtras();

            PreferencesFragment fragment = new PreferencesFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.preferences_container, fragment)
                    .commit();
        }else{
            packageName = savedInstanceState.getString(PACKAGE_INFO);
            appName = savedInstanceState.getString(APP_NAME);
            package_id = savedInstanceState.getInt(PACKAGE_KEY,-1);

            PreferencesFragment fragment = new PreferencesFragment();
            fragment.setArguments(savedInstanceState);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.preferences_container, fragment)
                    .commit();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.insert_preference) {
            ContentValues firstPreferences = new ContentValues();
            firstPreferences.put(PackagesContract.PreferencesEntry.COLUMN_PACKAGE_ID, package_id.toString());
            firstPreferences.put(PackagesContract.PreferencesEntry.COLUMN_FORWARD_METHOD, PackagesContract.PreferencesEntry.WEB_METHOD);
            firstPreferences.put(PackagesContract.PreferencesEntry.COLUMN_EXTRA_PARAM, "www.blabla.com");

            Uri preferencesInsertUri = getApplicationContext().getContentResolver()
                    .insert(PackagesContract.PreferencesEntry.CONTENT_URI, firstPreferences);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAddingMethod(Bundle bundle) {
        Intent intent = new Intent(this, NewPreferenceActivity.class)
                .putExtra(NewPreferenceActivity.APP_NAME,appName)
                .putExtra(NewPreferenceActivity.PACKAGE_INFO,packageName)
                .putExtra(NewPreferenceActivity.PACKAGE_KEY,package_id)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onUpdatingMethod(Bundle bundle) {
        Intent intent = new Intent(this, EditPreferenceActivity.class)
                .putExtra(NewPreferenceActivity.APP_NAME,appName)
                .putExtra(NewPreferenceActivity.PACKAGE_INFO,packageName)
                .putExtra(NewPreferenceActivity.PACKAGE_KEY,package_id)
                .putExtras(bundle)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onResetToPreferenceScreen(Bundle bundle) {

    }
}