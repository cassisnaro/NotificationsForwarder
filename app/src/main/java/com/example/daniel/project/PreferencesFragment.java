package com.example.daniel.project;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.daniel.project.data.PackagesContract;
import com.example.daniel.project.data.PackagesContract.PreferencesEntry;
import com.example.daniel.project.data.PackagesContract.PackageEntry;

/**
 * Created by daniel on 7/02/15.
 */
public class PreferencesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = PreferencesFragment.class.getSimpleName();

    private static final int DETAIL_LOADER = 0;

    private ListView mListView;

    private static final String[] PREFERENCES_PACKAGE_COLUMNS = {
            PackageEntry.TABLE_NAME + "." + PackageEntry._ID,
            PackageEntry.COLUMN_PACKAGE_INFO,
            PackageEntry.COLUMN_PACKAGE_ACTIVE,
            PreferencesEntry.COLUMN_FORWARD_METHOD,
            PreferencesEntry.COLUMN_EXTRA_PARAM,
            PreferencesEntry.TABLE_NAME+"."+PreferencesEntry._ID
    };

    public static final int COL_PACKAGE_ID = 0;
    public static final int COL_PACKAGE_INFO = 1;
    public static final int COL_PACKAGE_ACTIVE = 2;
    public static final int COL_FORWARD_METHOD = 3;
    public static final int COL_EXTRA_PARAM = 4;
    public static final int COL_PREFERENCES_ID=5;

    private int mPackage;
    private String mAppName;
    private String mPackageInfo;
    private TextView mAppNameView;
    private Button mButtonNewMethod;

    private PreferencesAdapter mPreferencesAdapter;

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public static String Package_ID = "Package_ID";
        public static String Package_NAME = "Package_NAME";
        public static String Package_INFO = "Package_INFO";
        public static String Preference_ID = "Preference_ID";
        public static String Preference_Method = "Preference_Method";
        public static String Preference_Extra = "Preference_Extra";
        public void onAddingMethod(Bundle bundle);
        public void onUpdatingMethod(Bundle bundle);
        public void onResetToPreferenceScreen(Bundle bundle);
    }

    public PreferencesFragment() {
        setHasOptionsMenu(false);
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
        setHasOptionsMenu(false);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*Bundle arguments = getArguments();
        if (arguments != null) {
            z = arguments.getString(DetailActivity.DATE_KEY);
        }*/
        mPreferencesAdapter = new PreferencesAdapter(getActivity(), null, 0);
        if (savedInstanceState != null) {
            mPackage = savedInstanceState.getInt(PreferencesActivity.PACKAGE_KEY);
            mAppName = savedInstanceState.getString(PreferencesActivity.APP_NAME);
            mPackageInfo = savedInstanceState.getString(PreferencesActivity.PACKAGE_INFO);
        }


        View rootView = inflater.inflate(R.layout.preferences_fragment, container, false);

        ImageView iconView = (ImageView) rootView.findViewById(R.id.appIconView);
        IconLoader iconLoader = new IconLoader(getActivity(),iconView);
        iconLoader.execute(mPackageInfo);

        mAppNameView = (TextView) rootView.findViewById(R.id.packageNameView);
        mButtonNewMethod = (Button) rootView.findViewById(R.id.new_preference_button);
        mButtonNewMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                Bundle bundle = new Bundle();
                bundle.putInt(NewPreferenceActivity.PACKAGE_KEY, mPackage);
                bundle.putString(NewPreferenceActivity.APP_NAME, mAppName);
                bundle.putString(NewPreferenceActivity.PACKAGE_INFO, mPackageInfo);
                ((Callback) activity).onAddingMethod(bundle);
            }
        });

        mListView = (ListView) rootView.findViewById(R.id.listview_preferences);
        mListView.setAdapter(mPreferencesAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = mPreferencesAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    Bundle bundle = new Bundle();
                    int preferenceID = cursor.getInt(COL_PREFERENCES_ID);
                    String preferenceMethod = cursor.getString(COL_FORWARD_METHOD);
                    String preferenceExtra = cursor.getString(COL_EXTRA_PARAM);

                    bundle.putInt(Callback.Preference_ID, preferenceID);
                    bundle.putString(Callback.Package_NAME, mPackageInfo);
                    bundle.putInt(Callback.Package_ID, mPackage);
                    bundle.putString(Callback.Package_INFO,mPackageInfo);
                    bundle.putString(Callback.Preference_Method, preferenceMethod);
                    bundle.putString(Callback.Preference_Extra,preferenceExtra);
                    bundle.putString(Callback.Package_NAME,mAppName);


                    ((Callback)getActivity())
                            .onUpdatingMethod(bundle);
                }

            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(PreferencesActivity.PACKAGE_KEY)) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        // Inflate the menu; this adds items to the action bar if it is present.
        //inflater.inflate(R.menu.preferences, menu);
        //super.onCreateOptionsMenu(menu,inflater);

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
        if (savedInstanceState != null) {
            mPackage = savedInstanceState.getInt(PreferencesActivity.PACKAGE_KEY);
        }

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(PreferencesActivity.PACKAGE_KEY)) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.

        Uri queryUri = PreferencesEntry.CONTENT_URI_LIST.buildUpon().appendPath(Integer.toString(mPackage)).build();

        return new CursorLoader(
                getActivity(),
                queryUri,  // Table to Query
                PREFERENCES_PACKAGE_COLUMNS, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAppNameView.setText(mAppName);
        mPreferencesAdapter.swapCursor(data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPreferencesAdapter.swapCursor(null);
    }
}
