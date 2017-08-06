package com.tag.phototext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
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
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import static android.app.Activity.RESULT_OK;

/**
 * Created by MILON on 8/6/2017.
 */

public class DriveCreate extends BaseDemoActivity{
    Context context;
    private GoogleApiClient googleApiClient;
    private static final int REQUEST_CODE_RESOLUTION = 2;
    private DriveId driveId;
    private static final String TAG = "drive-quickstarto";

    public DriveCreate(Context c) {
        this.context = c;

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(c)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        googleApiClient.connect();
    }

    public void onConnected() {
        searc("Photo Text");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
//                .setTitle("Photo Text").build();
//        Drive.DriveApi.getRootFolder(googleApiClient).createFolder(
//                googleApiClient, changeSet).setResultCallback(callback);
        onConnected();
        showMessage("Onconnected");

    }

    final ResultCallback<DriveFolder.DriveFolderResult> callback = new ResultCallback<DriveFolder.DriveFolderResult>() {
        @Override
        public void onResult(DriveFolder.DriveFolderResult result) {
            if (!result.getStatus().isSuccess()) {
                //showMessage("Error while trying to create the folder");
                return;
            }
            //showMessage("Created a folder: " + result.getDriveFolder().getDriveId());
            saveData(result.getDriveFolder().getDriveId().toString());
            Log.i("Folder", result.getDriveFolder().getDriveId().toString());
        }
    };

    public void searc(final String name) {
        Query query = new Query.Builder()
                .addFilter(Filters.and(Filters.eq(
                        SearchableField.TITLE, name),
                        Filters.eq(SearchableField.TRASHED, false)))
                .build();
        Drive.DriveApi.query(googleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(DriveApi.MetadataBufferResult result) {
                        if (!result.getStatus().isSuccess()) {
                            showMessage("Cannot create folder in the root.");
                        } else {
                            boolean isFound = false;
                            for (Metadata m : result.getMetadataBuffer()) {
                                if (m.getTitle().equals(name)) {
                                    showMessage("Folder exists");
                                    driveId = m.getDriveId();
                                    saveData(m.getDriveId().toString());
                                    isFound = true;
                                    break;
                                }
                            }
                            if (!isFound) {
                                showMessage("Folder not found; creating it.");
                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .setTitle(name)
                                        .build();
                                Drive.DriveApi.getRootFolder(googleApiClient)
                                        .createFolder(googleApiClient, changeSet)
                                        .setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
                                            @Override
                                            public void onResult(DriveFolder.DriveFolderResult result) {
                                                if (!result.getStatus().isSuccess()) {
                                                    showMessage("Error while trying to create the folder");
                                                } else {
                                                    showMessage("Created a folder");
                                                    saveData(result.getDriveFolder().getDriveId().toString());
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    public void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    private void saveData(String data) {
        SharedPreferences sp =
                context.getSharedPreferences("MyPrefs",
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("level", data);
        editor.apply();
        editor.commit();
        Toast.makeText(context, "Shared", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection failed: " + connectionResult.toString());
        if (!connectionResult.hasResolution()) {
            // show the localized error dialog.
            //GoogleApiAvailability.getInstance().getErrorDialog(, result.getErrorCode(), 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
        try {
            connectionResult.startResolutionForResult((Activity) context, REQUEST_CODE_RESOLUTION);
            //new DriveCreate(context);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        showMessage("onactivity");
        if (requestCode == REQUEST_CODE_RESOLUTION) {
//            boolean mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!googleApiClient.isConnecting() &&
                        !googleApiClient.isConnected()) {
                    googleApiClient.connect();
                }
            }
        }
    }
}
