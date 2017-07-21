package com.tag.phototext;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.kyo.imagecrop.CropUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Ocr2 extends AppCompatActivity {

    String TAG = "MAIN ACTIVITY";
    ImageView imageView;
    Uri uri;
    Intent CrIntent;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr2);

        imageView = (ImageView) findViewById(R.id.imgView);

        imageView.setImageBitmap(TextClass.sbitmap);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav_ocr2);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.actionOcr:
                        inspectFromBitmap(TextClass.sbitmap);
                        startActivity(new Intent(getApplicationContext(), TextViewActivity.class));
                        break;
                    case R.id.reCrop:
                        startCropper();
                }
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), CameraTestActivity.class));
    }


    private void inspectFromBitmap(Bitmap bitmap) {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        try {
            if (!textRecognizer.isOperational()) {
                new AlertDialog.
                        Builder(this).
                        setMessage("Text recognizer could not be set up on your device").show();
                return;
            }

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> origTextBlocks = textRecognizer.detect(frame);
            List<TextBlock> textBlocks = new ArrayList<>();
            for (int i = 0; i < origTextBlocks.size(); i++) {
                TextBlock textBlock = origTextBlocks.valueAt(i);
                textBlocks.add(textBlock);
            }
            Collections.sort(textBlocks, new Comparator<TextBlock>() {
                @Override
                public int compare(TextBlock o1, TextBlock o2) {
                    int diffOfTops = o1.getBoundingBox().top - o2.getBoundingBox().top;
                    int diffOfLefts = o1.getBoundingBox().left - o2.getBoundingBox().left;
                    if (diffOfTops != 0) {
                        return diffOfTops;
                    }
                    return diffOfLefts;
                }
            });

            TextClass.stringBuilder = new StringBuilder();
            for (TextBlock textBlock : textBlocks) {
                if (textBlock != null && textBlock.getValue() != null) {
                    TextClass.stringBuilder.append(textBlock.getValue());
                    TextClass.stringBuilder.append("\n");
                }
            }

        } finally {
            textRecognizer.release();
        }
    }

    private void startCropper() {
        Uri uri = null;
        uri = TextClass.sUri;
//        if (requestCode == REQUEST_CAMERA) {
//            uri = TextClass.sUri;
//        } else if (uri != null) {
//            uri = TextClass.sUri;
//        }
        Intent intent = new Intent(getApplicationContext(), CropActivity.class);
        intent.setData(uri);
        intent.putExtra("outputX", CropUtils.dip2px(this, 300));
        intent.putExtra("outputY", CropUtils.dip2px(this, 150));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivity(intent);

        //Log.w(LOGTAG, "Uri Found starto");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            Log.w("no00", TextClass.sUri.toString());
            if (data != null) {
                Log.w("no00", TextClass.sUri.toString());
                Log.w("h", "ok");
                Bundle bundle = data.getExtras();
                TextClass.sbitmap = bundle.getParcelable("data");
                try {
                    TextClass.sbitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), TextClass.sUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(TextClass.sbitmap);
            }
        }
    }

 }