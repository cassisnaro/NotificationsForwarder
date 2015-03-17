package com.example.daniel.project;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;

import com.example.daniel.project.data.PackagesContract;
import com.example.daniel.project.data.PackagesContract.PackageEntry;

/**
 * Created by daniel on 4/02/15.
 */
public class PackagesFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private PackageAdapter mPackageAdapter;

    private static String LOG_TAG = PackagesFragment.class.getSimpleName();

    private String mLocation;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private boolean mUseTodayLayout;
    private Bundle argumentsForFragment;
    private static String BUNDLE = "bundle";

    private static final String SELECTED_KEY = "selected_position";

    private static final int PACKAGE_LOADER = 0;

    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] PACKAGES_COLUMNS = {
        // In this case the id needs to be fully qualified with a table name, since
        // the content provider joins the location & weather tables in the background
        // (both have an _id column)
        // On the one hand, that's annoying.  On the other, you can search the weather table
        // using the location set by the user, which is only in the Location table.
        // So the convenience is worth it.
        PackageEntry.TABLE_NAME + "." + PackageEntry._ID,
            PackageEntry.COLUMN_PACKAGE_INFO,
            PackageEntry.COLUMN_PACKAGE_ACTIVE
        };


    // These indices are tied to PACKAGES_COLUMNS.  If PACKAGES_COLUMNS changes, these
    // must change.
    public static final int COL_PACKAGE_ID = 0;
    public static final int COL_PACKAGE_INFO = 1;
    public static final int COL_PACKAGE_ACTIVE = 2;


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public static String Package_ID = "Package_ID";
        public static String Package_NAME = "Package_NAME";
        public static String Package_INFO = "Package_INFO";
        public void onItemSelected(Bundle bundle);
        public void onOpenFirst(Bundle bundle);
    }

    public PackagesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(false);
    }

/*    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }   */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ArrayAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mPackageAdapter = new PackageAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_forecast);

        mListView.setAdapter(mPackageAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            mPosition = position;
            Cursor cursor = mPackageAdapter.getCursor();
            if (cursor != null && cursor.moveToPosition(position)) {
                Bundle bundle = new Bundle();
                int packageID = cursor.getInt(COL_PACKAGE_ID);
                bundle.putInt(Callback.Package_ID, packageID);
                bundle.putString(Callback.Package_NAME, cursor.getString(cursor.getColumnIndex(PackagesContract.PackageEntry.COLUMN_PACKAGE_NAME)));
                String packageInfo = cursor.getString(COL_PACKAGE_INFO);
                bundle.putString(Callback.Package_INFO, packageInfo);
                Activity activity = getActivity();
                ((Callback)getActivity())
                        .onItemSelected(bundle);
            }
            }
        });



        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }else{
            mPosition=0;
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(PACKAGE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(PACKAGE_LOADER, null, this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        Uri packagesUri = PackageEntry.CONTENT_URI_LIST;

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        Loader<Cursor> retCursor = new CursorLoader(
                getActivity(),
                packagesUri,
                null,
                null,
                null,
                null
        );
        return retCursor;
    }

    private Handler handlerOnOpenFirst = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle=msg.getData();
            ((PackagesFragment.Callback)getActivity()).onOpenFirst(bundle);
        }
    };

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPackageAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mListView.smoothScrollToPosition(mPosition);
        }
        if (data != null && data.moveToPosition(mPosition)) {
            Bundle bundle = new Bundle();
            int packageID = data.getInt(COL_PACKAGE_ID);
            bundle.putInt(Callback.Package_ID, packageID);
            bundle.putString(Callback.Package_NAME, data.getString(data.getColumnIndex(PackageEntry.COLUMN_PACKAGE_NAME)));
            String packageInfo = data.getString(COL_PACKAGE_INFO);
            bundle.putString(Callback.Package_INFO, packageInfo);
            Activity activity = getActivity();
            Message message = new Message();
            message.setData(bundle);
            handlerOnOpenFirst.handleMessage(message);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPackageAdapter.swapCursor(null);
    }



    /*public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
        if (mPackageAdapter != null) {
            mPackageAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }*/
}