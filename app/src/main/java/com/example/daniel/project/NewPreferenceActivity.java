package com.example.daniel.project;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.daniel.project.data.PackagesContract;import static android.support.v4.app.NavUtils.navigateUpFromSameTask;

/**
 * Created by daniel on 18/02/15.
 */
public class NewPreferenceActivity extends ActionBarActivity implements PreferencesFragment.Callback{

    public static final String PACKAGE_KEY ="package_key";
    public static final String PACKAGE_INFO ="package_info";
    public static final String APP_NAME ="app_name";
    private static String LOG_TAG = PreferencesActivity.class.getSimpleName();
    private String packageInfo;
    private int packageKey;
    private String appName;


    @Override
    public Intent getSupportParentActivityIntent() {
        Intent intent = new Intent(this, PreferencesActivity.class)
                .putExtra(PreferencesActivity.PACKAGE_INFO,packageInfo)
                .putExtra(PreferencesActivity.PACKAGE_KEY, packageKey)
                .putExtra(PreferencesActivity.APP_NAME,appName);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_preferences);

        if (savedInstanceState == null) {
            // Create the preferences fragment and add it to the activity
            // using a fragment transaction.
            packageKey = getIntent().getIntExtra(PACKAGE_KEY,-1);
            appName = getIntent().getStringExtra(APP_NAME);
            packageInfo = getIntent().getStringExtra(PACKAGE_INFO);
            //Log.v(LOG_TAG,"packega)

            Bundle arguments = getIntent().getExtras();

            NewPreferenceFragment fragment = new NewPreferenceFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.preferences_container, fragment)
                    .commit();
        } else {
            Bundle arguments = new Bundle();
            arguments.putInt(PACKAGE_KEY, packageKey);
            arguments.putString(APP_NAME, appName);
            arguments.putString(PACKAGE_INFO, packageInfo);
            NewPreferenceFragment fragment = new NewPreferenceFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.preferences_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAddingMethod(Bundle bundle) {

    }
    @Override
    public void onUpdatingMethod(Bundle bundle) {

    }@Override
     public void onResetToPreferenceScreen(Bundle bundle) {
        Intent intent = getSupportParentActivityIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        NavUtils.navigateUpTo(this, intent);
    }
}
