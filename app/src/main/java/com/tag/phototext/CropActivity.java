/**
 * Copyright 2015, KyoSherlock
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tag.phototext;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.takeimage.R;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.codec.Base64;
import com.kyo.imagecrop.CropLayout;
import com.kyo.imagecrop.CropUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class CropActivity extends Activity {

    private CropLayout mCropLayout;
    private ImageButton mDoneButton;
    Intent cropIntent;
    String outputFormat;
    public static int mOrientation;
    private CameraUtils mCamUtils = null;
    private LinearLayout parentView;
    RelativeLayout.LayoutParams fullScreenParams;
    int fullWidth = 0;
    int fullHeight = 0;
    private static String LOGTAG = "CameraActivity";
    ImageButton imageButton11, imageButton43, imageButton169;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_crop);

        imageButton11 = (ImageButton) findViewById(R.id.imageButton11);
        imageButton43 = (ImageButton) findViewById(R.id.imageButton43);
        imageButton169 = (ImageButton) findViewById(R.id.imageButton169);

        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        mOrientation = this.getResources().getConfiguration().orientation;

        final Uri sourceUri = intent.getData();
        int outputX = intent
                .getIntExtra("outputX", CropUtils.dip2px(this, 200));
        int outputY = intent
                .getIntExtra("outputY", CropUtils.dip2px(this, 200));
        outputFormat = intent.getStringExtra("outputFormat");

        mDoneButton = (ImageButton) this.findViewById(R.id.done);
        mDoneButton.setOnClickListener(mOnClickListener);


        // bellow
        mCropLayout = (CropLayout) this.findViewById(R.id.crop);
        mCropLayout.setOnCropListener(mOnCropListener);
        mCropLayout.startCropImage(sourceUri, outputX, outputY);
        mCropLayout.setOutputFormat(outputFormat);

        imageButton11.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCropLayout.startCropImage(sourceUri, 720, 720);
                mCropLayout.setOutputFormat(outputFormat);
            }
        });

        imageButton43.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCropLayout.startCropImage(sourceUri, 1280, 960);
                mCropLayout.setOutputFormat(outputFormat);
            }
        });

        imageButton169.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCropLayout.startCropImage(sourceUri, 1280, 720);
                mCropLayout.setOutputFormat(outputFormat);
            }
        });


    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.done: {
                    mCropLayout.requestCropResult();
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), CameraTestActivity.class));
    }


    private CropLayout.OnCropListener mOnCropListener = new CropLayout.OnCropListener() {

        @Override
        public void onCropResult(Uri data) {
//			Intent intent = new Intent(CropActivity.this, ResultActivity.class);
//			intent.setData(data);
//			startActivity(intent);
            Intent intent = new Intent(CropActivity.this, Ocr2.class);
            TextClass.sUri = data;
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(TextClass.sUri);
                TextClass.sbitmap = BitmapFactory.decodeStream(inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            intent.setData(data);
            startActivity(intent);
        }

        @Override
        public void onCropFailed(String errmsg) {

        }

        @Override
        public void onLoadingStateChanged(boolean isLoading) {
            if (mDoneButton != null) {
                mDoneButton.setEnabled(!isLoading);
            }
        }
    };
}
