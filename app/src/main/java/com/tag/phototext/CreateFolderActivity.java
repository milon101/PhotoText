/**
 * Copyright 2013 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tag.phototext;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveFolder.DriveFolderResult;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

/**
 * An activity to illustrate how to create a new folder.
 */
public class CreateFolderActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    Context c;

    public CreateFolderActivity(Context c) {
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

    public void searc(final String name) {
        Query query = new Query.Builder()
                .addFilter(Filters.and(Filters.eq(
                        SearchableField.TITLE, name),
                        Filters.eq(SearchableField.TRASHED, false)))
                .build();
        Drive.DriveApi.query(mGoogleApiClient, query)
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
                                    isFound = true;
                                    break;
                                }
                            }
                            if (!isFound) {
                                showMessage("Folder not found; creating it.");
                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .setTitle(name)
                                        .build();
                                Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                        .createFolder(mGoogleApiClient, changeSet)
                                        .setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
                                            @Override
                                            public void onResult(DriveFolder.DriveFolderResult result) {
                                                if (!result.getStatus().isSuccess()) {
                                                    showMessage("Error while trying to create the folder");
                                                } else {
                                                    showMessage("Created a folder");
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    public void showMessage(String message) {
        Toast.makeText(c, message, Toast.LENGTH_LONG).show();
    }


    public void onConnected() {

        //if (loadData() == 2) {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle("Photo Text").build();
        Drive.DriveApi.getRootFolder(mGoogleApiClient).createFolder(
                mGoogleApiClient, changeSet).setResultCallback(callback);
        //} else if (loadData() == 1)
        //{
//            searc();
        //  return;
        //}
        searc("Photo Text");
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        onConnected();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    final ResultCallback<DriveFolderResult> callback = new ResultCallback<DriveFolderResult>() {
        @Override
        public void onResult(DriveFolderResult result) {
            if (!result.getStatus().isSuccess()) {
                //showMessage("Error while trying to create the folder");
                return;
            }
            //showMessage("Created a folder: " + result.getDriveFolder().getDriveId());
            saveData(result.getDriveFolder().getDriveId().toString(), 1);
            Log.i("Folder", result.getDriveFolder().getDriveId().toString());
        }
    };

    private void saveData(String data, int int_data) {
        SharedPreferences sp =
                c.getSharedPreferences("MyPrefs",
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("level", data);
        editor.putInt("bool", int_data);
        editor.apply();
        editor.commit();
        Toast.makeText(c, "Shared", Toast.LENGTH_SHORT).show();
    }

    private int loadData() {
        SharedPreferences sharedPreferences =
                c.getSharedPreferences("MyPrefs",
                        Context.MODE_PRIVATE);
        int l = sharedPreferences.getInt("bool", 2);
        if (l == 2) {
            Toast.makeText(c, "Loaded", Toast.LENGTH_SHORT).show();
        }
        return l;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
