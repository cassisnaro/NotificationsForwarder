package com.example.daniel.project;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.daniel.project.data.PackagesContract;

import java.util.ArrayList;

/**
 * Created by daniel on 24/02/15.
 */
public class EditPreferenceFragment extends Fragment {

    private static final String LOG_TAG = EditPreferenceFragment.class.getSimpleName();

    private static final int DETAIL_LOADER = 0;

    private static String MailState="MAIL_TO";
    private static String SubjectState="SUBJECT";
    private static String UrlState="URL";
    private static String MethodState="METHOD";
    private static String PrefereceIDState="PreferenceID";
    private static String ExtraState="EXTRA";

    private ArrayList<View> availableMethods;

    private int mPackage;
    private int mPreferenceID;
    private String mAppName;
    private String mPackageInfo;
    private String mMailTo;
    private String mSubject;
    private String mUrl;
    private int mMethod;
    private Bundle mBundle;

    private Spinner spinner;
    private TextView mailTextView;
    private TextView subjectTextView;
    private TextView urlTextView;



    public EditPreferenceFragment() {

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(PreferencesActivity.PACKAGE_KEY, mPackage);
        outState.putString(PreferencesActivity.APP_NAME, mAppName);
        outState.putString(PreferencesActivity.PACKAGE_INFO,mPackageInfo);
        outState.putInt(MethodState, mMethod);
        outState.putString(MailState, mailTextView.getText().toString());
        outState.putString(SubjectState, subjectTextView.getText().toString());
        outState.putString(UrlState, urlTextView.getText().toString());
        outState.putInt(PrefereceIDState, mPreferenceID);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mPackage = savedInstanceState.getInt(PreferencesActivity.PACKAGE_KEY);
            mAppName = savedInstanceState.getString(PreferencesActivity.APP_NAME);
            mPackageInfo = savedInstanceState.getString(PreferencesActivity.PACKAGE_INFO);
            mMailTo = savedInstanceState.getString(MailState);
            mSubject = savedInstanceState.getString(SubjectState);
            mUrl = savedInstanceState.getString(UrlState);
            mMethod = savedInstanceState.getInt(MethodState);
            mPreferenceID = savedInstanceState.getInt(PrefereceIDState);
        } else {
            if(getArguments()!=null){
                mPackage = getArguments().getInt(PreferencesFragment.Callback.Package_ID);
                mAppName = getArguments().getString(PreferencesFragment.Callback.Package_NAME);
                mPackageInfo = getArguments().getString(PreferencesFragment.Callback.Package_INFO);
                if (getArguments().getString(PreferencesFragment.Callback.Preference_Method).compareTo(PackagesContract.PreferencesEntry.MAIL_METHOD)==0){
                    mMethod = 0;
                    MailMethodSettings mailMethodSettings = StringInterface.readMailMethod(getArguments().getString(PreferencesFragment.Callback.Preference_Extra));
                    mMailTo = mailMethodSettings.getMailTo();
                    mSubject = mailMethodSettings.getSubject();
                    mUrl = "";
                } else if (getArguments().getString(PreferencesFragment.Callback.Preference_Method).compareTo(PackagesContract.PreferencesEntry.WEB_METHOD)==0){
                    mMethod = 1;
                    mUrl = StringInterface.readURLMethod(getArguments().getString(PreferencesFragment.Callback.Preference_Extra));
                    mMailTo = "";
                    mSubject = "";
                }
                mPreferenceID = getArguments().getInt(PreferencesFragment.Callback.Preference_ID);
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.edit_preference_fragment, container, false);

        availableMethods = new ArrayList<>();
        availableMethods.add(rootView.findViewById(R.id.mail_settings));
        availableMethods.add(rootView.findViewById(R.id.web_settings));

        for(int i=0; i<availableMethods.size(); i++){
            if (i!=mMethod){
                availableMethods.get(i).setVisibility(View.GONE);
            } else {
                availableMethods.get(i).setVisibility(View.VISIBLE);
            }
        }

        mailTextView = (TextView) rootView.findViewById(R.id.emailTextView);
        mailTextView.setText(mMailTo);
        subjectTextView = (TextView) rootView.findViewById(R.id.subjectTextView);
        subjectTextView.setText(mSubject);
        urlTextView = (TextView) rootView.findViewById(R.id.urlTextView);
        urlTextView.setText(mUrl);

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
    }

    private void addMethod(){
        String method="";
        String params="";
        switch (mMethod) {
            case 0:
                method= PackagesContract.PreferencesEntry.MAIL_METHOD;
                params=StringInterface.descriptMailMethod(mailTextView.getText().toString(),subjectTextView.getText().toString());
                break;
            case 1:
                method=PackagesContract.PreferencesEntry.WEB_METHOD;
                params=StringInterface.descriptURLMethod(urlTextView.getText().toString());
                break;

        }

        ContentValues preferences = new ContentValues();
        preferences.put(PackagesContract.PreferencesEntry.COLUMN_PACKAGE_ID, Integer.toString(mPackage));
        preferences.put(PackagesContract.PreferencesEntry._ID, Integer.toString(mPreferenceID));
        preferences.put(PackagesContract.PreferencesEntry.COLUMN_FORWARD_METHOD, method);
        preferences.put(PackagesContract.PreferencesEntry.COLUMN_EXTRA_PARAM, params);

        String[] valuesForWhere = {Integer.toString(mPreferenceID)};

        int rowsUpdate = getActivity().getContentResolver()
                .update(PackagesContract.PreferencesEntry.CONTENT_URI.buildUpon().appendQueryParameter(PackagesContract.PreferencesEntry.COLUMN_PACKAGE_ID, Integer.toString(mPackage)).build(),
                        preferences,
                        PackagesContract.PreferencesEntry._ID + "= ? ",
                        valuesForWhere);

        Bundle cancelBundle = new Bundle();
        cancelBundle.putString(PreferencesActivity.PACKAGE_INFO,mPackageInfo);
        cancelBundle.putInt(PreferencesActivity.PACKAGE_KEY,mPackage);
        cancelBundle.putString(PreferencesActivity.APP_NAME, mAppName);

        ((PreferencesFragment.Callback)getActivity()).onResetToPreferenceScreen(cancelBundle);
    }
}