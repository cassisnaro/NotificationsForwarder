package com.example.daniel.project;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.daniel.project.data.PackagesContract;

/**
 * Created by daniel on 3/02/15.
 */
public class PackageAdapter extends CursorAdapter {

    private static String LOG_TAG = PackageAdapter.class.getSimpleName();

    // Flag to determine if we want to use a separate view for "today".
    private boolean mUseTodayLayout = true;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView packageNameView;
        public final Switch packageForwardSwitch;

        public String getPackageName() {
            return packageName;
        }

        public void setPackageInfo(String packageName) {
            this.packageName = packageName;
        }

        private String packageName;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.packageIcon);
            packageNameView = (TextView) view.findViewById(R.id.packageName);
            packageForwardSwitch = (Switch) view.findViewById(R.id.forwardActivated);
        }
    }

    public PackageAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        layoutId = R.layout.list_item_package;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        Switch packageSwitch = (Switch)  view.findViewById(R.id.forwardActivated);
        packageSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                String packageName = (String) buttonView.getTag();

                ContentValues updatedValues = new ContentValues();
                updatedValues.put(PackagesContract.PackageEntry.COLUMN_PACKAGE_ACTIVE, isChecked ? PackagesContract.PackageEntry.ACTIVE_STRING:PackagesContract.PackageEntry.INACTIVE_STRING);

                int count = mContext.getContentResolver().update(
                        PackagesContract.PackageEntry.CONTENT_URI, updatedValues, PackagesContract.PackageEntry.COLUMN_PACKAGE_INFO + "= ?",
                        new String[] { packageName});
            }
        });

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);



        return view;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String packageInfo = cursor.getString(PackagesFragment.COL_PACKAGE_INFO);

        viewHolder.setPackageInfo(packageInfo);
        viewHolder.packageNameView.setText(cursor.getString(cursor.getColumnIndex(PackagesContract.PackageEntry.COLUMN_PACKAGE_NAME)));

        IconLoader iconLoader = new IconLoader(context, viewHolder.iconView);
        iconLoader.execute(packageInfo);

        viewHolder.packageForwardSwitch.setTag(packageInfo);
        viewHolder.packageForwardSwitch.setChecked(cursor.getString(PackagesFragment.COL_PACKAGE_ACTIVE).compareTo(PackagesContract.PackageEntry.ACTIVE_STRING)==0);
    }
}