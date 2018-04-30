package com.markfeldman.theweatheroutside.fragments;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.markfeldman.theweatheroutside.R;
import com.markfeldman.theweatheroutside.services.WeatherSyncTask;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferencesxml);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();//So to get count of total # of preferences
        int numberOfPrefs = preferenceScreen.getPreferenceCount();

        for (int i = 0; i < numberOfPrefs; i++){
            Preference preference = preferenceScreen.getPreference(i);//Iterate through each preference in list
            if (!(preference instanceof CheckBoxPreference)){
                String prefValue = sharedPreferences.getString(preference.getKey(),"");
                setPreferenceSummary(preference,prefValue);
            }
        }
    }

    private void setPreferenceSummary (Preference pref, String value){
        if (pref instanceof ListPreference){
            ListPreference listPreference = (ListPreference) pref;
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex>=0){
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }

        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_units_key))){
            WeatherSyncTask.syncWeatherDB(getActivity());
        }
        Preference preference = findPreference(key);
        if (preference!=null){
            if (!(preference instanceof CheckBoxPreference)){
                String prefValue = sharedPreferences.getString(preference.getKey(),"");
                setPreferenceSummary(preference,prefValue);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
