package com.tag.phototext;

/**
 * Created by MILON on 6/22/2017.
 */

import android.app.NotificationManager;
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
                    new DriveSync(getContext());
//                    Notification notification = new Notification(R.drawable.ic_action_ocr,
//                            "Syncing",
//                            System.currentTimeMillis());
//                    notification.flags |= Notification.FLAG_NO_CLEAR
//                            | Notification.FLAG_ONGOING_EVENT;
//                    NotificationManager notifier = (NotificationManager)
//                            getContext().getSystemService(Context.NOTIFICATION_SERVICE);
//                    notifier.notify(1, notification);
                    Toast.makeText(getContext(), "DriveFile", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        final CheckBoxPreference driveInitial = (CheckBoxPreference) findPreference("pref_key_auto_delete");

        driveInitial.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()

        {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (driveInitial.isChecked()) {
                        new DriveCreate(getContext());
                        Toast.makeText(getContext(), "DriveCreate", Toast.LENGTH_SHORT).show();
                    } else if (!driveInitial.isChecked()) {
                        new ReConnect(getContext());
                        Toast.makeText(getContext(), "DriveNot", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
    }

}