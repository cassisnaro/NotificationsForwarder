package com.example.daniel.project;

import android.app.LoaderManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.content.CursorLoader;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.daniel.project.data.PackagesContract;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URL;

public class NLService extends NotificationListenerService {

    private String TAG = this.getClass().getSimpleName();
    private NLServiceReceiver nlservicereciver;
    private static String LOG_TAG = NLService.class.getSimpleName();
    //private static ForwardingExecution forwardingExecution;
    private CursorLoader mCursorLoader;
    private static int LOADER_PACKAGE_PREFERENCES=0;

   /* private  class ForwardingExecution implements LoaderManager.LoaderCallbacks<Cursor> {
        private  String LOG_TAG = ForwardingExecution.class.getSimpleName();
        public String PACKAGE_NAME = "package_name";

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String packageName = args.getString(PACKAGE_NAME);
            Uri queryUri = PackagesContract.PreferencesEntry.CONTENT_URI_STRING_LIST.buildUpon().appendPath(packageName).build();
            Log.v(LOG_TAG, "uri used: " + queryUri);



            CursorLoader cursorLoader = new CursorLoader(
                    getApplicationContext(),
                    queryUri,  // Table to Query
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null // columns to group by
            );
            return cursorLoader;
        }


        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.v(LOG_TAG,"onLoadFinished");
            if (data!= null ){
                Log.v(LOG_TAG,"data is not null");
                for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                    for(String columnName: data.getColumnNames()){
                        Log.v(LOG_TAG, "one column name: "+columnName);
                    }
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
        nlservicereciver = new NLServiceReceiver();
        //Looper.prepare();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.daniel.project.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        registerReceiver(nlservicereciver,filter);
        //forwardingExecution = new ForwardingExecution();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlservicereciver);
        /*if (mCursorLoader != null) {
            mCursorLoader.unregisterListener(this);
            mCursorLoader.cancelLoad();
            mCursorLoader.stopLoading();
        }*/
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        PackageManager packageManager = getApplicationContext().getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(sbn.getPackageName(), 0);
        } catch (final PackageManager.NameNotFoundException e) {
        }
        final String title = (String) ((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : "???");
        Intent i = new Intent("com.example.daniel.project.NOTIFICATION_LISTENER_EXAMPLE");
        i.putExtra("notification_event", "onNotificationPosted :" + title + "\n");
        sendBroadcast(i);


        ContentValues newPackageValues = new ContentValues();
        newPackageValues.put(PackagesContract.PackageEntry.COLUMN_PACKAGE_INFO, sbn.getPackageName());
        newPackageValues.put(PackagesContract.PackageEntry.COLUMN_PACKAGE_ACTIVE, PackagesContract.PackageEntry.ACTIVE_STRING);


        try {
            Uri packageUri = getApplicationContext().getContentResolver().
                    insert(PackagesContract.PackageEntry.CONTENT_URI, newPackageValues);
        } catch (Exception e){

        }



        Uri queryUri = PackagesContract.PreferencesEntry.CONTENT_URI_STRING_LIST.buildUpon().appendPath(sbn.getPackageName()).build();
        Cursor preferencesCursor = getApplicationContext().getContentResolver().query(
                queryUri,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );


        for (preferencesCursor.moveToFirst(); !preferencesCursor.isAfterLast(); preferencesCursor.moveToNext()) {
            if(preferencesCursor.getString(preferencesCursor.getColumnIndex(PackagesContract.PackageEntry.COLUMN_PACKAGE_ACTIVE)).compareTo(PackagesContract.PackageEntry.ACTIVE_STRING)==0){
                String method = preferencesCursor.getString(preferencesCursor.getColumnIndex(PackagesContract.PreferencesEntry.COLUMN_FORWARD_METHOD));
                if (method.compareTo(PackagesContract.PreferencesEntry.MAIL_METHOD)==0){
                    MailMethodSettings mailMethodSettings = StringInterface.readMailMethod(preferencesCursor.getString(preferencesCursor.getColumnIndex(PackagesContract.PreferencesEntry.COLUMN_EXTRA_PARAM)));
                    try {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        if(prefs.getAll().size()!=0) {
                            String mailUser = prefs.getString("edittext_preference","");
                            String mailPassword = prefs.getString("password_preference","");
                            GMailSender sender = new GMailSender(mailUser, mailPassword);
                            sender.sendMail(mailMethodSettings.getSubject(),
                                    "",
                                    mailUser,
                                    mailMethodSettings.getMailTo());
                        }
                    } catch (Exception e) {
                        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        NotificationCompat.Builder ncomp = new NotificationCompat.Builder(this);
                        ncomp.setContentTitle(getResources().getString(R.string.app_name));
                        String message = getResources().getString(R.string.mail_error_message)+mailMethodSettings.getMailTo();
                        ncomp.setContentText(message);
                        ncomp.setTicker(message);
                        ncomp.setSmallIcon(R.drawable.ic_launcher);
                        ncomp.setAutoCancel(true);
                        nManager.notify((int) System.currentTimeMillis(), ncomp.build());
                    }
                } else if (method.compareTo(PackagesContract.PreferencesEntry.WEB_METHOD)==0){
                    String addressSettings = StringInterface.readURLMethod(preferencesCursor.getString(preferencesCursor.getColumnIndex(PackagesContract.PreferencesEntry.COLUMN_EXTRA_PARAM)));
                    String webAddress = new String(addressSettings);
                    if(!webAddress.startsWith("http://")){
                        webAddress="http://"+webAddress;
                    }
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    try {
                        HttpGet httpGet = new HttpGet(webAddress);
                        HttpResponse response = httpClient.execute(httpGet);
                        int code = response.getStatusLine().getStatusCode();
                    } catch (Exception e) {
                        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        NotificationCompat.Builder ncomp = new NotificationCompat.Builder(this);
                        ncomp.setContentTitle(getResources().getString(R.string.app_name));
                        String message = getResources().getString(R.string.url_error_message)+addressSettings;
                        ncomp.setContentText(message);
                        ncomp.setTicker(message);
                        ncomp.setSmallIcon(R.drawable.ic_launcher);
                        ncomp.setAutoCancel(true);
                        nManager.notify((int) System.currentTimeMillis(), ncomp.build());
                    }
                }
            }
        }


    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Intent i = new  Intent("com.example.daniel.project.NOTIFICATION_LISTENER_EXAMPLE");
        i.putExtra("notification_event","onNotificationRemoved :" + sbn.getPackageName() + "\n");

        sendBroadcast(i);
    }


    class NLServiceReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("command").equals("clearall")){
                    NLService.this.cancelAllNotifications();
            }
            else if(intent.getStringExtra("command").equals("list")){
                Intent i1 = new  Intent("com.example.daniel.project.NOTIFICATION_LISTENER_EXAMPLE");
                i1.putExtra("notification_event","=====================");
                sendBroadcast(i1);
                int i=1;
                for (StatusBarNotification sbn : NLService.this.getActiveNotifications()) {
                    Intent i2 = new  Intent("com.example.daniel.project.NOTIFICATION_LISTENER_EXAMPLE");
                    i2.putExtra("notification_event",i +" " + sbn.getPackageName() + "\n");
                    sendBroadcast(i2);
                    i++;
                }
                Intent i3 = new  Intent("com.example.daniel.project.NOTIFICATION_LISTENER_EXAMPLE");
                i3.putExtra("notification_event","===== Notification List ====");
                sendBroadcast(i3);

            }
        }
    }
}
