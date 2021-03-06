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

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Android DriveCreate Quickstart activity. This activity takes a photo and saves it
 * in Google DriveCreate. The user is prompted with a pre-made dialog which allows
 * them to choose the file location.
 */
public class MyDrive implements ConnectionCallbacks,
        OnConnectionFailedListener {

    private static final String TAG = "drive-quickstart";
    private static final int REQUEST_CODE_RESOLUTION = 3;
    private DriveId mFolderDriveId;
    ArrayList<PDFDoc> pdfDocs;
    DriveFolder folder;
    boolean flag = true;
    ArrayList<String> fileName;
    String mime_pdf = "application/pdf", mime_text = "text/plain";
    public String Level = null;
    Context c;

    private GoogleApiClient mGoogleApiClient;

    public MyDrive(Context c) {
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

        //Toast.makeText(c, "Connected", Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();

        Toast.makeText(c, "Connected", Toast.LENGTH_SHORT).show();
    }

    /**
     * Create a new file and save it to DriveCreate.
     */


    private void saveFileToDrive(final String Path, final String Name, final String MIME_TYPE) {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveContentsResult>() {

                    @Override
                    public void onResult(DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything
                        // and must
                        // fail.
                        if (!result.getStatus().isSuccess()) {
                            Log.i(TAG, "Failed to create new contents.");
                            return;
                        }

                        for (int i = 0; i < fileName.size(); i++) {
                            Log.i("suspended", Name + "Nmae\n");
                            Log.i("suspended", fileName.get(i) + "fileNmae\n");
                            if (Name.equalsIgnoreCase(fileName.get(i))) {
                                return;
                            }
                        }

                        // Otherwise, we can write our data to the new contents.
                        Log.i(TAG, "New contents created.");

                        // Get an output stream for the contents.
                        OutputStream outputStream = result.getDriveContents().getOutputStream();
                        String path = Path;
                        File f = new File(path);

                        byte[] buf = new byte[8192];

                        InputStream is = null;
                        try {
                            is = new FileInputStream(f);

                            int c = 0;
                            while ((c = is.read(buf, 0, buf.length)) > 0) {
                                outputStream.write(buf, 0, c);
                                outputStream.flush();
                            }

                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        // Create the initial metadata - MIME type and title.
                        // Note that the user will be able to change the title later.
                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                .setMimeType(MIME_TYPE).setTitle(Name).build();

                        folder.createFile(mGoogleApiClient, metadataChangeSet, result.getDriveContents())
                                .setResultCallback(fileCallback);
                    }

                });

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


    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback =
            new ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        //showMessage("Error while trying to create the file");
                        return;
                    }
                    //showMessage("Created a file: " + result.getDriveFile().getDriveId());
                    //c.startActivity(new Intent(c, MainnnActivity.class));
                    Toast.makeText(c, "Success", Toast.LENGTH_SHORT).show();
                }
            };


//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (mGoogleApiClient == null) {
//            // Create the API client and bind it to an instance variable.
//            // We use this instance as the callback for connection and connection
//            // failures.
//            // Since no account name is passed, the user is prompted to choose.
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addApi(DriveCreate.API)
//                    .addScope(DriveCreate.SCOPE_FILE)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .build();
//        }
//        // Connect the client. Once connected, the camera is launched.
//        mGoogleApiClient.connect();
//    }

    private ArrayList<PDFDoc> getPDFs()

    {
        ArrayList<PDFDoc> pdfDocs = new ArrayList<>();
        //TARGET FOLDER
        File downloadsFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "Photo Text");

        PDFDoc pdfDoc;

        if (downloadsFolder.exists()) {
            //GET ALL FILES IN DOWNLOAD FOLDER
            File[] files = downloadsFolder.listFiles();

            //LOOP THRU THOSE FILES GETTING NAME AND URI

            for (int i = 0; i < files.length; i++) {
                File file = files[i];

                if (file.getPath().endsWith("pdf")) {
                    pdfDoc = new PDFDoc(file.getName(), file.getAbsolutePath(), "pdf");
                    pdfDocs.add(pdfDoc);
                } else if (file.getPath().endsWith("txt")) {
                    pdfDoc = new PDFDoc(file.getName(), file.getAbsolutePath(), "txt");
                    pdfDocs.add(pdfDoc);
                }


            }
        }

        return pdfDocs;
    }

//    @Override
//    protected void onPause() {
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.disconnect();
//        }
//        super.onPause();
//    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Called whenever the API client fails to connect.
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            //GoogleApiAvailability.getInstance().getErrorDialog(, result.getErrorCode(), 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
        try {
            result.startResolutionForResult((Activity) c, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    public void onConnected() {
        Log.i(TAG, "API client connected.");
        pdfDocs = new ArrayList<PDFDoc>();
        pdfDocs = getPDFs();
        fileName = new ArrayList<>();
        mFolderDriveId = DriveId.decodeFromString(loadData());
        folder = mFolderDriveId.asDriveFolder();
        folder.getMetadata(mGoogleApiClient).setResultCallback(metadataRetrievedCallback);

    }

    final private ResultCallback<DriveResource.MetadataResult> metadataRetrievedCallback = new
            ResultCallback<DriveResource.MetadataResult>() {
                @Override
                public void onResult(DriveResource.MetadataResult result) {
                    String TAG = "hjjkjkjk";
                    if (!result.getStatus().isSuccess()) {
                        Log.v(TAG, "Problem while trying to fetch metadata.");
                        return;
                    }

                    Metadata metadata = result.getMetadata();
                    if (metadata.isTrashed()) {
                        Log.v(TAG, "Folder is trashed");
                        saveData(2);
                        new CreateFolderActivity(c);
                    } else {
                        folder.listChildren(mGoogleApiClient)
                                .setResultCallback(metadataResult);
                        Toast.makeText(c, "Connected", Toast.LENGTH_SHORT).show();
                        Log.v(TAG, "Folder is not trashed");
                    }

                }
            };

    final private ResultCallback<DriveApi.MetadataBufferResult> metadataResult = new
            ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        //showMessage("Problem while retrieving files");
                        return;
                    }

                    fileName.clear();
                    for (int i = 0; i < result.getMetadataBuffer().getCount(); i++) {
                        Metadata metadata = result.getMetadataBuffer().get(i);
                        fileName.add(metadata.getTitle());
                        File file = new File(Environment.getExternalStorageDirectory() + File.separator + metadata.getTitle());
                        DownloadFile(metadata.getDriveId(), file);
                        //showMessage(fileName.get(i));
                    }
                    for (int i = 0; i < pdfDocs.size(); i++) {
                        if (pdfDocs.get(i).getType().equals("pdf"))
                            saveFileToDrive(pdfDocs.get(i).getPath(), pdfDocs.get(i).getName(), mime_pdf);
                        else
                            saveFileToDrive(pdfDocs.get(i).getPath(), pdfDocs.get(i).getName(), mime_text);
                    }
                }
            };

    private void saveData(int int_data) {
        SharedPreferences sp =
                c.getSharedPreferences("MyPrefs",
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("bool", int_data);
        editor.apply();
        editor.commit();
        Toast.makeText(c, "Shared", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(c, "Connected", Toast.LENGTH_SHORT).show();
        onConnected();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }


    public void DownloadFile(final DriveId driveId, final File filename) {
        Toast.makeText(c, "DownloadFile", Toast.LENGTH_SHORT).show();
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (!filename.exists()) {
                    try {
                        filename.createNewFile();

                        Toast.makeText(c, "Filecreated", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);

            }

            @Override
            protected Boolean doInBackground(Void... params) {
                DriveFile file = Drive.DriveApi.getFile(
                        mGoogleApiClient, driveId);
                file.getMetadata(mGoogleApiClient)
                        .setResultCallback(metadataRetrieveCallback);
                DriveApi.DriveContentsResult driveContentsResult = file.open(
                        mGoogleApiClient,
                        DriveFile.MODE_READ_ONLY, null).await();
                DriveContents driveContents = driveContentsResult
                        .getDriveContents();
                InputStream inputstream = driveContents.getInputStream();

                try {
                    FileOutputStream fileOutput = new FileOutputStream(filename);

                    byte[] buffer = new byte[1024];
                    int bufferLength = 0;
                    while ((bufferLength = inputstream.read(buffer)) > 0) {
                        fileOutput.write(buffer, 0, bufferLength);
                    }
                    fileOutput.close();
                    inputstream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return true;
            }

        };
        task.execute();
    }

    private ResultCallback<DriveResource.MetadataResult> metadataRetrieveCallback = new ResultCallback<DriveResource.MetadataResult>() {
        @Override
        public void onResult(DriveResource.MetadataResult result) {
            if (!result.getStatus().isSuccess()) {
                return;
            }
            Metadata metadata = result.getMetadata();
        }
    };
}
