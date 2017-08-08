package com.tag.phototext;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by MILON on 8/6/2017.
 */

public class DriveSync extends BaseDemoActivity {

    Context context;
    GoogleApiClient mGoogleApiClient;
    DriveId driveId;
    DriveFolder driveFolder;
    String mime_pdf = "application/pdf", mime_text = "text/plain";
    ArrayList<PDFDoc> pdfDocs;
    ArrayList<String> fileName;
    private static final String TAG = "drive-quickstart";
    NotificationManager mNotifyMgr;
    int j;


    public DriveSync(Context c) {
        this.context = c;

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    public void searc(final String name) {
        final Query query = new Query.Builder()
                .addFilter(Filters.and(Filters.eq(
                        SearchableField.TITLE, name),
                        Filters.eq(SearchableField.TRASHED, false)))
                .build();
        Drive.DriveApi.query(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(DriveApi.MetadataBufferResult result) {
//                        result = Drive.DriveApi.query(mGoogleApiClient, query).await();
                        if (!result.getStatus().isSuccess()) {
                            showMessag("Cannot create folder in the root.");
                        } else {

                            boolean isFound = false;
                            for (Metadata m : result.getMetadataBuffer()) {
                                if (m.getTitle().equals(name)) {
                                    showMessag("Folder exists");
                                    driveId = m.getDriveId();
                                    driveFolder = driveId.asDriveFolder();
                                    driveFolder.listChildren(mGoogleApiClient)
                                            .setResultCallback(metadataResult);
                                    isFound = true;
                                    break;
                                }
                            }
                            if (!isFound) {
                                showMessag("Folder not found; creating it.");
                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .setTitle(name)
                                        .build();
                                Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                        .createFolder(mGoogleApiClient, changeSet)
                                        .setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
                                            @Override
                                            public void onResult(DriveFolder.DriveFolderResult result) {
                                                if (!result.getStatus().isSuccess()) {
                                                    showMessag("Error while trying to create the folder");
                                                } else {
                                                    showMessag("Created a folder");
                                                    driveId = result.getDriveFolder().getDriveId();
                                                    driveFolder = driveId.asDriveFolder();
                                                    driveFolder.listChildren(mGoogleApiClient)
                                                            .setResultCallback(metadataResult);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

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

                        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Photo Text" + File.separator + metadata.getTitle());
                        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "Photo Text");
                        DownloadFile(metadata.getDriveId(), file, folder);
                        //showMessag(metadata.getTitle());
                    }
                    for (j = 0; j < pdfDocs.size(); j++) {
                        if (pdfDocs.get(j).getType().equals("pdf"))
                            saveFileToDrive(pdfDocs.get(j).getPath(), pdfDocs.get(j).getName(), mime_pdf);
                        else
                            saveFileToDrive(pdfDocs.get(j).getPath(), pdfDocs.get(j).getName(), mime_text);
                    }


                    //showMessag("" + j +"    "+ pdfDocs.size());
                    if (j == pdfDocs.size()) {
                        mNotifyMgr.cancel(33);
                    }

                }

            };

    public void DownloadFile(final DriveId driveId, final File filename, final File fileFolder) {
        Toast.makeText(context, "DownloadFile", Toast.LENGTH_SHORT).show();
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (!fileFolder.exists()){
                        fileFolder.mkdir();
                }

                if (!filename.exists()) {
                    try {
                        filename.createNewFile();

                        Toast.makeText(context, "Filecreated", Toast.LENGTH_SHORT).show();
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


    private void saveFileToDrive(final String Path, final String Name, final String MIME_TYPE) {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
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

                        driveFolder.createFile(mGoogleApiClient, metadataChangeSet, result.getDriveContents())
                                .setResultCallback(fileCallback);
                    }

                });

    }

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback =
            new ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessag("Error while trying to create the file");
                        return;
                    }
                    showMessag("Success");
                }
            };

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        showMessag("DriveSync");
        applyStatusBar(33);
        pdfDocs = new ArrayList<PDFDoc>();
        pdfDocs = getPDFs();
        fileName = new ArrayList<>();

        Drive.DriveApi.requestSync(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                //search query performs here
                searc("Photo Text");
            }
        });

    }


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

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void showMessag(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    private void applyStatusBar(int notificationId) {
        NotificationCompat.Builder mBuilder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle("Syncing");

            Intent resultIntent = null;

            resultIntent = new Intent(context, SettingsActivity.class);

            PendingIntent resultPendingIntent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            mBuilder.setContentIntent(resultPendingIntent);
            Notification notification = mBuilder.build();
            notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

            mNotifyMgr = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            }
            mNotifyMgr.notify(notificationId, notification);
        }
    }
}
