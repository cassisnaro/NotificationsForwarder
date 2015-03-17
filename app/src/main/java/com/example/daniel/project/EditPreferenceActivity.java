package com.example.daniel.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by daniel on 24/02/15.
 */
public class EditPreferenceActivity extends ActionBarActivity implements PreferencesFragment.Callback{

    public static final String PACKAGE_KEY ="package_key";
    public static final String PACKAGE_INFO ="package_info";
    public static final String APP_NAME ="app_name";
    public static final String BUNDLE ="bundle";
    private static String LOG_TAG = EditPreferenceActivity.class.getSimpleName();
    private String packageInfo;
    private int packageKey;
    private String appName;
    private Bundle argumentsForFragment;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PACKAGE_KEY, packageKey);
        outState.putString(PACKAGE_INFO, packageInfo);
        outState.putString(APP_NAME, appName);
        outState.putBundle(BUNDLE,argumentsForFragment);
    }

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

            packageKey = getIntent().getIntExtra(PACKAGE_KEY,-1);
            appName = getIntent().getStringExtra(APP_NAME);
            packageInfo = getIntent().getStringExtra(PACKAGE_INFO);

            argumentsForFragment = getIntent().getExtras();

            EditPreferenceFragment fragment = new EditPreferenceFragment();
            fragment.setArguments(argumentsForFragment);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.preferences_container, fragment)
                    .commit();
        } else {
            packageKey = savedInstanceState.getInt(PACKAGE_KEY);
            packageInfo = savedInstanceState.getString(PACKAGE_INFO);
            appName = savedInstanceState.getString(APP_NAME);
            argumentsForFragment = savedInstanceState.getBundle(BUNDLE);

            EditPreferenceFragment fragment = new EditPreferenceFragment();
            fragment.setArguments(argumentsForFragment);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.preferences_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.preferences, menu);
        return true;
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
    }}

