package com.example.daniel.project;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.daniel.project.data.PackagesContract;

public class MainActivity  extends ActionBarActivity implements PackagesFragment.Callback, PreferencesFragment.Callback{

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String FIST_OPEN = "firstOpen";
    private final String OTHER_ACTION = "otherAction";

    private boolean mTwoPane;
    private boolean firstOpen;
    private boolean otherAction;
    private NotificationReceiver nReceiver;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FIST_OPEN,firstOpen);
        outState.putBoolean(OTHER_ACTION,otherAction);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.daniel.project.NOTIFICATION_LISTENER_EXAMPLE");
        registerReceiver(nReceiver,filter);

        mTwoPane = false;
        if (findViewById(R.id.preferences_container) != null) {
            // The preferences container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the preferences view in this activity by
            // adding or replacing the preferences fragment using a
            // fragment transaction.
            if(savedInstanceState!=null){
                firstOpen=savedInstanceState.getBoolean(FIST_OPEN);
                otherAction=savedInstanceState.getBoolean(OTHER_ACTION);
            }else{
                firstOpen=false;
                otherAction=false;
            }

        } else {
            mTwoPane = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }





       /* PackagesFragment forecastFragment =  ((PackagesFragment)getSupportFragmentManager()
                .findFragmentById(R.id.packages_forecast));*/
        //forecastFragment.setUseTodayLayout(!mTwoPane);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nReceiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings){
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOpenFirst(Bundle bundle) {
        if (mTwoPane) {
            if (!(firstOpen || otherAction)) {
                firstOpen = true;

                int packageKey = bundle.getInt(PackagesFragment.Callback.Package_ID);
                String appName = bundle.getString(PackagesFragment.Callback.Package_NAME);
                String packageInfo = bundle.getString(PackagesFragment.Callback.Package_INFO);

                final Bundle bundleForFragment = new Bundle();
                bundleForFragment.putInt(PreferencesActivity.PACKAGE_KEY, packageKey);
                bundleForFragment.putString(PreferencesActivity.PACKAGE_INFO, packageInfo);
                bundleForFragment.putString(PreferencesActivity.APP_NAME, appName);


                PreferencesFragment fragment = new PreferencesFragment();
                fragment.setArguments(bundleForFragment);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.preferences_container, fragment)
                        .commitAllowingStateLoss();


            }
        }
    }

    @Override
    public void onAddingMethod(Bundle bundle) {
        if (mTwoPane) {
            otherAction = true;
            int packageKey = bundle.getInt(NewPreferenceActivity.PACKAGE_KEY,-1);
            String appName = bundle.getString(NewPreferenceActivity.APP_NAME);
            String packageInfo = bundle.getString(NewPreferenceActivity.PACKAGE_INFO);

            Bundle arguments = new Bundle();
            arguments.putInt(NewPreferenceActivity.PACKAGE_KEY, packageKey);
            arguments.putString(NewPreferenceActivity.APP_NAME, appName);
            arguments.putString(NewPreferenceActivity.PACKAGE_INFO, packageInfo);

            NewPreferenceFragment fragment = new NewPreferenceFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.preferences_container, fragment)
                    .commit();
        } else {

            String packageKey = bundle.getString(PreferencesFragment.Callback.Package_ID);
            String appName = bundle.getString(PreferencesFragment.Callback.Package_NAME);
            String packageInfo = bundle.getString(PreferencesFragment.Callback.Package_INFO);
            Intent intent =  new Intent(this, NewPreferenceActivity.class);
            intent.putExtra(NewPreferenceActivity.PACKAGE_INFO,packageInfo)
                    .putExtra(NewPreferenceActivity.PACKAGE_KEY, packageKey)
                    .putExtra(NewPreferenceActivity.APP_NAME,appName)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
    }

    @Override
         public void onItemSelected(Bundle bundle) {
             if (mTwoPane) {
                 // In two-pane mode, show the preferences view in this activity by
                 // adding or replacing the preferences fragment using a
                 // fragment transaction.
                 int packageKey = bundle.getInt(PackagesFragment.Callback.Package_ID);
                 String appName = bundle.getString(PackagesFragment.Callback.Package_NAME);
                 String packageInfo = bundle.getString(PackagesFragment.Callback.Package_INFO);

                 Bundle bundleForFragment = new Bundle();
                 bundleForFragment.putInt(PreferencesActivity.PACKAGE_KEY, packageKey);
                 bundleForFragment.putString(PreferencesActivity.PACKAGE_INFO,packageInfo);
                 bundleForFragment.putString(PreferencesActivity.APP_NAME,appName);


                 PreferencesFragment fragment = new PreferencesFragment();
                 fragment.setArguments(bundleForFragment);

                 getSupportFragmentManager().beginTransaction()
                         .replace(R.id.preferences_container, fragment)
                         .commit();
             } else {
                 int packageKey = bundle.getInt(PackagesFragment.Callback.Package_ID);
                 String appName = bundle.getString(PackagesFragment.Callback.Package_NAME);
                 String packageInfo = bundle.getString(PackagesFragment.Callback.Package_INFO);
                 Intent intent = new Intent(this, PreferencesActivity.class)
                         .putExtra(PreferencesActivity.PACKAGE_INFO, packageInfo)
                         .putExtra(PreferencesActivity.PACKAGE_KEY, packageKey)
                         .putExtra(PreferencesActivity.APP_NAME, appName);
                 startActivity(intent);
             }
         }

    @Override
         public void onUpdatingMethod(Bundle bundle) {
             if(mTwoPane){
                 otherAction = true;
                 EditPreferenceFragment fragment = new EditPreferenceFragment();
                 fragment.setArguments(bundle);

                 getSupportFragmentManager().beginTransaction()
                         .replace(R.id.preferences_container, fragment)
                         .commit();
             }
         }


    @Override
         public void onResetToPreferenceScreen(Bundle bundle) {
             otherAction = false;
             final Bundle bundleForFragment = new Bundle();
             bundleForFragment.putInt(PreferencesActivity.PACKAGE_KEY, bundle.getInt(PreferencesActivity.PACKAGE_KEY));
             bundleForFragment.putString(PreferencesActivity.PACKAGE_INFO, bundle.getString(PreferencesActivity.PACKAGE_INFO));
             bundleForFragment.putString(PreferencesActivity.APP_NAME, bundle.getString(PreferencesActivity.APP_NAME));


             PreferencesFragment fragment = new PreferencesFragment();
             fragment.setArguments(bundleForFragment);

             getSupportFragmentManager().beginTransaction()
                     .replace(R.id.preferences_container, fragment)
                     .commit();

         }

    class NotificationReceiver extends BroadcastReceiver{

             @Override
             public void onReceive(Context context, Intent intent) {
                 String temp = intent.getStringExtra("notification_event") + "\n";
                 //temp += txtView.getText();
                 //txtView.setText(temp);
             }
         }
}
