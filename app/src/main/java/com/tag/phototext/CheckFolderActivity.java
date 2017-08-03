package com.tag.phototext;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;

/**
 * Created by MILON on 8/1/2017.
 */

public class CheckFolderActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    String folderId = null;
    Context c;
    private DriveId mFolderDriveId;
    DriveFolder folder;

    public CheckFolderActivity(Context c) {
        this.c = c;
        if (mGoogleApiClient == null) {
//            // Create the API client and bind it to an instance variable.
//            // We use this instance as the callback for connection and connection
//            // failures.
//            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = new GoogleApiClient.Builder(c)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        // Connect the client. Once connected, the camera is launched.
        mGoogleApiClient.connect();
    }

    public void onConnected() {

        Toast.makeText(c, "Onconnected", Toast.LENGTH_SHORT).show();

        mFolderDriveId = DriveId.decodeFromString(loadData());
        folder = Drive.DriveApi.getFolder(mGoogleApiClient, mFolderDriveId);
        folder.getMetadata(mGoogleApiClient).setResultCallback(metadataRetrievedCallback);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        onConnected();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private String loadData() {
        SharedPreferences sharedPreferences =
                c.getSharedPreferences("MyPrefs",
                        Context.MODE_PRIVATE);
        String l = sharedPreferences.getString("level", null);
        if (l == null) {
            Toast.makeText(c, "Loaded", Toast.LENGTH_SHORT).show();
        }
        return l;
    }


    private void saveData(boolean data) {
        SharedPreferences sp =
                c.getSharedPreferences("MyPrefs",
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("che", data);
        editor.apply();
        editor.commit();
        Toast.makeText(c, "Shared Boolean", Toast.LENGTH_SHORT).show();
    }

    final private ResultCallback<DriveResource.MetadataResult> metadataRetrievedCallback = new
            ResultCallback<DriveResource.MetadataResult>() {
                @Override
                public void onResult(DriveResource.MetadataResult result) {
                    String TAG = null;
                    if (!result.getStatus().isSuccess()) {
                        Log.v(TAG, "Problem while trying to fetch metadata.");
                        return;
                    }

                    Metadata metadata = result.getMetadata();
                    if (metadata.isTrashed()) {
                        Log.v(TAG, "Folder is trashed");
                        saveData(false);
                    } else {
                        Log.v(TAG, "Folder is not trashed");
                        saveData(true);
                    }

                }
            };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}