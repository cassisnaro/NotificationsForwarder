package com.example.daniel.project;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.daniel.project.data.PackagesContract;

/**
 * Created by daniel on 9/02/15.
 */
public class PreferencesAdapter extends CursorAdapter {
    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_MAIL = 0;
    private static final int VIEW_TYPE_WEB = 1;

    private static String PREFERENCE_ID = "preference_id";

    private static String LOG_TAG = PackageAdapter.class.getSimpleName();

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final TextView preferenceName;
        public final TextView preferenceExtra;
        public final TextView preferenceExtra2;
        public final Button deleteButton;


        public ViewHolder(View view) {
            preferenceName = (TextView) view.findViewById(R.id.preferenceName);
            preferenceExtra = (TextView) view.findViewById(R.id.preferenceExtra);
            preferenceExtra2 = (TextView) view.findViewById(R.id.preferenceExtra2);
            deleteButton = (Button) view.findViewById(R.id.delete_method_button);
        }
    }

    public PreferencesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        String method = cursor.getString(PreferencesFragment.COL_FORWARD_METHOD);
        int viewType=1;
        if (method.compareTo(PackagesContract.PreferencesEntry.MAIL_METHOD)==0){
            viewType=0;
        } else if (method.compareTo(PackagesContract.PreferencesEntry.WEB_METHOD)==0){
            viewType=1;
        }
        int layoutId = -1;
        switch (viewType) {
            case VIEW_TYPE_MAIL: {
                layoutId = R.layout.list_item_preference_mail;
                break;
            }
            case VIEW_TYPE_WEB: {
                layoutId = R.layout.list_item_preference_web;
                break;
            }
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }



    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String preferenceName = cursor.getString(PreferencesFragment.COL_FORWARD_METHOD);
        String preferenceExtra = cursor.getString(PreferencesFragment.COL_EXTRA_PARAM);

        String method = cursor.getString(PreferencesFragment.COL_FORWARD_METHOD);
        if (method.compareTo(PackagesContract.PreferencesEntry.MAIL_METHOD)==0){
            viewHolder.preferenceName.setText(preferenceName);
            MailMethodSettings mailMethodSettings = StringInterface.readMailMethod(preferenceExtra);
            viewHolder.preferenceExtra.setText(mailMethodSettings.getMailTo());
            viewHolder.preferenceExtra2.setText(context.getResources().getString(R.string.mail_subject_label)+": "+mailMethodSettings.getSubject());
        } else if (method.compareTo(PackagesContract.PreferencesEntry.WEB_METHOD)==0){
            viewHolder.preferenceName.setText(preferenceName);
            viewHolder.preferenceExtra.setText(StringInterface.readURLMethod(preferenceExtra));
        }
        viewHolder.deleteButton.setTag(cursor.getString(PreferencesFragment.COL_PREFERENCES_ID));
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String preferenceId = (String) v.getTag();
                String[] valuesForWhere = {preferenceId};
                String packageId = cursor.getString(PreferencesFragment.COL_PACKAGE_ID);
                context.getContentResolver().delete(
                        PackagesContract.PreferencesEntry.CONTENT_URI.buildUpon().appendQueryParameter(PackagesContract.PreferencesEntry.COLUMN_PACKAGE_ID, packageId).build(),
                        PackagesContract.PreferencesEntry._ID + "= ? ",
                        valuesForWhere
                );
            }
        });
    }



    private int getItemViewType(Cursor cursor) {
        String method = cursor.getString(PreferencesFragment.COL_FORWARD_METHOD);
        int viewType=1;
        if (method.compareTo(PackagesContract.PreferencesEntry.MAIL_METHOD)==0){
            viewType=0;
        } else if (method.compareTo(PackagesContract.PreferencesEntry.WEB_METHOD)==0){
            viewType=1;
        }
        return viewType;
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return getItemViewType(cursor);
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}