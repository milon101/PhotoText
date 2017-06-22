package com.tag.phototext;

/**
 * Created by MILON on 6/22/2017.
 */

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        /*
            public void addPreferencesFromResource (int preferencesResId)
                Inflates the given XML resource and adds the preference hierarchy to the
                current preference hierarchy.

            Parameters
                preferencesResId : The XML resource ID to inflate.
        */
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}