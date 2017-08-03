package com.tag.phototext;

/**
 * Created by MILON on 6/22/2017.
 */

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
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

        Preference button = findPreference(getString(R.string.myCoolButton));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    new MyDriveActivity(getContext());
                    startActivity(new Intent(getContext(),DriveActivity.class));
                    Toast.makeText(getContext(), "DriveFile", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getContext(), "pressed", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        final CheckBoxPreference driveInitial = (CheckBoxPreference) findPreference("pref_key_auto_delete");

        driveInitial.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (driveInitial.isChecked()) {
                        //startActivity(new Intent(getContext(), CreateFolderActivity.class));
                        new CreateFolderActivity(getContext());
                        Toast.makeText(getContext(), "Drive", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getContext(), "presseddddd", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });

        Preference reConnect = findPreference("pref_key_sms_delete_limit");
        reConnect.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(getContext(), "Reconnect", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getContext(), "pressed", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

    }
}