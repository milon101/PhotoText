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

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;

/**
 * An activity illustrates how to list files in a folder. For an example of
 * pagination and displaying results, please see {@link ListFilesActivity}.
 */
public class ListFilesInFolderActivity extends BaseDemoActivity {

    public String Level;
    private DriveId mFolderDriveId = DriveId.decodeFromString(loadData());

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_listfiles);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);

        DriveId driveId = mFolderDriveId;
        DriveFolder folder = driveId.asDriveFolder();
        folder.listChildren(getGoogleApiClient())
                .setResultCallback(metadataResult);

    }

    private String loadData() {
        SharedPreferences sp =
                getSharedPreferences("MyPrefs",
                        Context.MODE_PRIVATE);
        Level = sp.getString("level", Level);
        return Level;
    }


    final private ResultCallback<MetadataBufferResult> metadataResult = new
            ResultCallback<MetadataBufferResult>() {
                @Override
                public void onResult(MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Problem while retrieving files");
                        return;
                    }
                    for (int i = 0; i < result.getMetadataBuffer().getCount(); i++) {
                        Metadata metadata = result.getMetadataBuffer().get(i);
                        metadata.getTitle();
                        showMessage(metadata.getTitle());
                    }
                    showMessage("Successfully listed files.");
                }
            };

}
