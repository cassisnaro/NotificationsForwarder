package com.example.daniel.project;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by daniel on 10/02/15.
 */
public class IconLoader extends AsyncTask<String, Void, Drawable> {
    Context context;
    WeakReference<ImageView> iconViewReference;

    public IconLoader(Context context, ImageView iconView) {
        this.context = context;
        this.iconViewReference =  new WeakReference<ImageView>(iconView);
    }

    @Override
    protected Drawable doInBackground(String... packageName) {
        try {
            return context.getPackageManager().getApplicationIcon(packageName[0]);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Void... progress) {}

    @Override
    protected void onPostExecute(Drawable result) {
        final ImageView iconView = iconViewReference.get();
        if (iconView != null) {
            iconView.setImageDrawable(result);
        }
    }
}
