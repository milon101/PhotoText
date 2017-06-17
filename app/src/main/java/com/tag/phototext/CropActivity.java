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
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Display;
import android.view.MenuItem;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.takeimage.R;
import com.kyo.imagecrop.CropLayout;
import com.kyo.imagecrop.CropUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class CropActivity extends Activity {

    BottomNavigationView bottomNavigationView;
    private CropLayout mCropLayout;
    Intent cropIntent;
    String outputFormat;
    public static int mOrientation;
    private CameraUtils mCamUtils = null;
    private LinearLayout parentView;
    RelativeLayout.LayoutParams fullScreenParams;
    int fullWidth = 0;
    int fullHeight = 0;
    public boolean flag=false;
    private static String LOGTAG = "CameraActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_crop);

        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        mOrientation = this.getResources().getConfiguration().orientation;
        Display display = getWindowManager().getDefaultDisplay();
        final int width = display.getWidth();
        final int height = display.getHeight();

        final Uri sourceUri = intent.getData();
        int outputX = intent
                .getIntExtra("outputX", CropUtils.dip2px(this, width));
        int outputY = intent
                .getIntExtra("outputY", CropUtils.dip2px(this, height));
        outputFormat = intent.getStringExtra("outputFormat");
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav_crop);


        // bellow
        mCropLayout = (CropLayout) this.findViewById(R.id.crop);
        mCropLayout.setOnCropListener(mOnCropListener);
        mCropLayout.startCropImage(sourceUri, outputX, outputY);
        mCropLayout.setOutputFormat(outputFormat);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.cropOrigianal:
                        mCropLayout.startCropImage(sourceUri, width, height);
                        mCropLayout.setOutputFormat(outputFormat);
                        break;

                    case R.id.crop169:
                        if(flag==false) {
                            item.setIcon(R.drawable.ic_action_crop169);
                            item.setTitle("16:9");
                            mCropLayout.startCropImage(sourceUri, 720, 1280);
                            mCropLayout.setOutputFormat(outputFormat);

                            flag=true;
                            break;
                        }
                        if (flag==true) {
                            item.setIcon(R.drawable.ic_action_crop_portrait);
                            item.setTitle("Portrait");
                            mCropLayout.startCropImage(sourceUri, 1280, 720);
                            mCropLayout.setOutputFormat(outputFormat);
                            flag=false;
                            break;
                        }
                        break;

                    case R.id.cropDone:
                        mCropLayout.requestCropResult();
                        break;

                    case R.id.cropSquare:
                        mCropLayout.startCropImage(sourceUri, 1280, 1280);
                        mCropLayout.setOutputFormat(outputFormat);
                        break;
                }
                return true;
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.cropOrigianal:
                        mCropLayout.startCropImage(sourceUri, width, height);
                        mCropLayout.setOutputFormat(outputFormat);
                        break;

                    case R.id.crop169:
                        if(flag==false) {
                            item.setIcon(R.drawable.ic_action_crop169);
                            item.setTitle("16:9");
                            mCropLayout.startCropImage(sourceUri, 720, 1280);
                            mCropLayout.setOutputFormat(outputFormat);

                            flag=true;
                            break;
                        }
                        if (flag==true) {
                            item.setIcon(R.drawable.ic_action_crop_portrait);
                            item.setTitle("Portrait");
                            mCropLayout.startCropImage(sourceUri, 1280, 720);
                            mCropLayout.setOutputFormat(outputFormat);
                            flag=false;
                            break;
                        }
                        break;

                    case R.id.cropDone:
                        mCropLayout.requestCropResult();
                        break;

                    case R.id.cropSquare:
                        mCropLayout.startCropImage(sourceUri, 1280, 1280);
                        mCropLayout.setOutputFormat(outputFormat);
                        break;
                }
                return true;
            }
        });

    }

//    private OnClickListener mOnClickListener = new OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.done: {
//                    mCropLayout.requestCropResult();
//                    break;
//                }
//                default:
//                    break;
//            }
//        }
//    };

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
//            if (mDoneButton != null) {
//                mDoneButton.setEnabled(!isLoading);
//            }
        }
    };
}
