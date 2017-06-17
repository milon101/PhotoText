package com.tag.phototext;

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
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.takeimage.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

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

//        if (TextClass.sbitmap.getHeight() < TextClass.sbitmap.getWidth())
//            TextClass.sbitmap = RotateBitmap(TextClass.sbitmap, 90);

        imageView.setImageBitmap(TextClass.sbitmap);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav_ocr2);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.actionOcr:
                        imageProcess();
                       startActivity(new Intent(getApplicationContext(),TextViewActivity.class));
                        break;
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

    public void imageProcess() {

        // imageBitmap is the Bitmap image you're trying to process for text
        if (TextClass.sbitmap != null) {

            TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();

            if (!textRecognizer.isOperational()) {

                Log.w(TAG, "Detector dependencies are not yet available.");

                // Check for low storage.  If there is low storage, the native library will not be
                // downloaded, so detection will not become operational.
                IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

                if (hasLowStorage) {
                    Toast.makeText(this, "Low Storage", Toast.LENGTH_LONG).show();
                    Log.w(TAG, "Low Storage");
                }
            }


            Frame imageFrame = new Frame.Builder()
                    .setBitmap(TextClass.sbitmap)
                    .build();

            SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
            TextClass.stringBuilder = new StringBuilder();
            for (int i = 0; i < textBlocks.size(); i++) {

                TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));

                String text = textBlock.getValue();

                TextClass.stringBuilder.append(text);
                TextClass.stringBuilder.append("\n");

            }

        }

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


    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}