package com.example.daniel.project;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.daniel.project.data.PackagesContract;

import java.util.ArrayList;

/**
 * Created by daniel on 18/02/15.
 */
public class NewPreferenceFragment extends Fragment {

    private static final String LOG_TAG = NewPreferenceFragment.class.getSimpleName();

    private static final int DETAIL_LOADER = 0;
    private ArrayList<View> availableMethods;

    private int mPackage;
    private String mAppName;
    private String mPackageInfo;
    private Spinner spinner;
    private TextView mailTextView;
    private TextView subjectTextView;
    private TextView urlTextView;

    /*private ListView mListView;

    private static final String[] PREFERENCES_PACKAGE_COLUMNS = {
            PackagesContract.PackageEntry.TABLE_NAME + "." + PackagesContract.PackageEntry._ID,
            PackagesContract.PackageEntry.COLUMN_PACKAGE_INFO,
            PackagesContract.PackageEntry.COLUMN_PACKAGE_ACTIVE,
            PackagesContract.PreferencesEntry.COLUMN_FORWARD_METHOD,
            PackagesContract.PreferencesEntry.COLUMN_EXTRA_PARAM
    };

    public static final int COL_PACKAGE_ID = 0;
    public static final int COL_PACKAGE_INFO = 1;
    public static final int COL_PACKAGE_ACTIVE = 2;
    public static final int COL_FORWARD_METHOD = 3;
    public static final int COL_EXTRA_PARAM = 4;

    private String mPackage;
    private String mAppName;
    private String mPackageInfo;
    private TextView mAppNameView;

    private PreferencesAdapter mPreferencesAdapter;*/

    public NewPreferenceFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(PreferencesActivity.PACKAGE_KEY, mPackage);
        outState.putString(PreferencesActivity.APP_NAME,mAppName);
        outState.putString(PreferencesActivity.PACKAGE_INFO,mPackageInfo);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mPackage = savedInstanceState.getInt(PreferencesActivity.PACKAGE_KEY);
            mAppName = savedInstanceState.getString(PreferencesActivity.APP_NAME);
            mPackageInfo = savedInstanceState.getString(PreferencesActivity.PACKAGE_INFO);
        } else {
            if(getArguments()!=null){

                mPackage = getArguments().getInt(PreferencesActivity.PACKAGE_KEY);
                mAppName = getArguments().getString(PreferencesActivity.APP_NAME);
                mPackageInfo = getArguments().getString(PreferencesActivity.PACKAGE_INFO);
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.new_preference_fragment, container, false);
        spinner = (Spinner) rootView.findViewById(R.id.spinner_methods);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.methods_array, R.layout.my_simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        availableMethods = new ArrayList<>();
        availableMethods.add(rootView.findViewById(R.id.mail_settings));
        availableMethods.add(rootView.findViewById(R.id.web_settings));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for(int i=0; i<availableMethods.size(); i++){
                    if (i!=position){
                        availableMethods.get(i).setVisibility(View.GONE);
                    } else {
                        availableMethods.get(i).setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int activePos = spinner.getSelectedItemPosition();

        for(int i=0; i<availableMethods.size(); i++){
            if (i!=activePos){
                availableMethods.get(i).setVisibility(View.GONE);
            } else {
                availableMethods.get(i).setVisibility(View.VISIBLE);
            }
        }
        mailTextView = (TextView) rootView.findViewById(R.id.emailTextView);
        subjectTextView = (TextView) rootView.findViewById(R.id.subjectTextView);
        urlTextView = (TextView) rootView.findViewById(R.id.urlTextView);

        rootView.findViewById(R.id.addMethodButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMethod();
            }
        });

        View cancelButton = rootView.findViewById(R.id.cancelButton);
        if (cancelButton != null){
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle cancelBundle = new Bundle();
                    cancelBundle.putString(PreferencesActivity.PACKAGE_INFO,mPackageInfo);
                    cancelBundle.putInt(PreferencesActivity.PACKAGE_KEY,mPackage);
                    cancelBundle.putString(PreferencesActivity.APP_NAME, mAppName);

                    ((PreferencesFragment.Callback)getActivity()).onResetToPreferenceScreen(cancelBundle);
                }
            });

        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        /*if (arguments != null && arguments.containsKey(PreferencesActivity.PACKAGE_KEY)) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }*/
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //inflater.inflate(R.menu.preferences, menu);

        /*// Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mForecast != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }*/
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*if (savedInstanceState != null) {
            mPackage = savedInstanceState.getString(PreferencesActivity.PACKAGE_KEY);
            Log.v(LOG_TAG,"in activityCreated found mPackage: "+mPackage);
        }*/

        Bundle arguments = getArguments();
        /*if (arguments != null && arguments.containsKey(PreferencesActivity.PACKAGE_KEY)) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }*/
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "on loader reset");
        mPreferencesAdapter.swapCursor(null);
    }*/


    private void addMethod(){
        int activePos = spinner.getSelectedItemPosition();

        String method="";
        String params="";
        boolean knownMethod=false;
        switch (activePos) {
            case 0:
                method=PackagesContract.PreferencesEntry.MAIL_METHOD;
                params=StringInterface.descriptMailMethod(mailTextView.getText().toString(),subjectTextView.getText().toString());
                knownMethod=true;
                break;
            case 1:
                method=PackagesContract.PreferencesEntry.WEB_METHOD;
                params=StringInterface.descriptURLMethod(urlTextView.getText().toString());
                knownMethod=true;
                break;

        }

        if (knownMethod) {
            ContentValues firstPreferences = new ContentValues();
            firstPreferences.put(PackagesContract.PreferencesEntry.COLUMN_PACKAGE_ID, Integer.toString(mPackage));

            firstPreferences.put(PackagesContract.PreferencesEntry.COLUMN_FORWARD_METHOD, method);
            firstPreferences.put(PackagesContract.PreferencesEntry.COLUMN_EXTRA_PARAM, params);

            Uri preferencesInsertUri = getActivity().getContentResolver()
                    .insert(PackagesContract.PreferencesEntry.CONTENT_URI, firstPreferences);
        }
        Bundle resetBundle = new Bundle();
        resetBundle.putString(PreferencesActivity.PACKAGE_INFO, mPackageInfo);
        resetBundle.putInt(PreferencesActivity.PACKAGE_KEY, mPackage);
        resetBundle.putString(PreferencesActivity.APP_NAME, mAppName);

        ((PreferencesFragment.Callback)getActivity()).onResetToPreferenceScreen(resetBundle);
    }
}