package com.example.daniel.project;

import android.annotation.TargetApi;import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;import android.preference.Preference;import android.preference.PreferenceActivity;import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;import android.util.Log;import android.widget.Button;
import android.widget.EditText;

import java.util.List; /**
 * Created by daniel on 28/02/15.
 */
public class SettingsActivity extends PreferenceActivity {
    private static String LOG_TAG = SettingsActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Populate the activity with the top-level headers.
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    /**
     * This fragment shows the preferences for the first header.
     */
    public static class Prefs1Fragment extends PreferenceFragment  implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Make sure default values are applied.  In a real app, you would
            // want this in a shared function that is used to retrieve the
            // SharedPreferences wherever they are needed.
            PreferenceManager.setDefaultValues(getActivity(),
                    R.xml.preferences, false);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }


        @Override
        public void onResume() {
            super.onResume();
            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
                Preference preference = getPreferenceScreen().getPreference(i);
                if (preference instanceof PreferenceGroup) {
                    PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
                    for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
                        updatePreference(preferenceGroup.getPreference(j));
                    }
                } else {
                    updatePreference(preference);
                }
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePreference(findPreference(key));
        }

        private void updatePreference(Preference preference) {
            Log.v(LOG_TAG,"updatingPreference: "+preference.getClass().getSimpleName());

            if (preference instanceof EditTextPreference) {
                EditTextPreference editTextPreference =(EditTextPreference) preference;
                EditText edit = editTextPreference.getEditText();
                Log.v(LOG_TAG,"preference: "+editTextPreference.getText());
                if(edit.getTransformationMethod() != null) {
                    String pref = edit.getTransformationMethod().getTransformation(editTextPreference.getText(), edit).toString();
                    preference.setSummary(pref);
                }else{
                    preference.setSummary(editTextPreference.getText());
                }

            }
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        //Log.v(LOG_TAG,"getName(): "+Prefs1Fragment.class.getName()+" fragmentName: "+fragmentName);
        return Prefs1Fragment.class.getName().equals(fragmentName);
    }
}