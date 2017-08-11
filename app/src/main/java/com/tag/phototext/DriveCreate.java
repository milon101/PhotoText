package com.tag.phototext;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveId;

/**
 * Created by MILON on 8/6/2017.
 */

public class DriveCreate extends BaseDemoActivity {
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        showMessage("Onconnected");

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
        try {
            connectionResult.startResolutionForResult((Activity) context, REQUEST_CODE_RESOLUTION);
            //new DriveCreate(context);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }
}
