package com.tag.phototext;

import android.os.Bundle;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

public class DriveActivity extends BaseDemoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                .setTitle("Photo Txt")
                .setMimeType("text/plain")
                .setStarred(true).build();

        Drive.DriveApi.getRootFolder(getGoogleApiClient())
                .createFile(getGoogleApiClient(), metadataChangeSet, null /* DriveContents */)
                .setResultCallback(fileCallback);
    }

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file");
                        return;
                    }
                    showMessage("Created an empty file: "
                            + result.getDriveFile().getDriveId());
                }
            };

}
