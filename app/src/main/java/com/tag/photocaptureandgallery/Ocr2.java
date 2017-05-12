package com.tag.photocaptureandgallery;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.net.Uri;
import com.example.takeimage.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Ocr2 extends AppCompatActivity {

    Button next, rotateButton, leftButton, rightButton, crop;
    String TAG = "MAIN ACTIVITY";
    ImageView imageView;
    Uri uri;
    Intent CrIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr2);

        leftButton = (Button) findViewById(R.id.leftButton);
        rightButton = (Button) findViewById(R.id.rightButton);
        crop = (Button) findViewById(R.id.crop);
        next = (Button) findViewById(R.id.upload_btn1);
        rotateButton = (Button) findViewById(R.id.rotateButton);
        imageView = (ImageView) findViewById(R.id.imgView);

        if (TextClass.sbitmap.getHeight() < TextClass.sbitmap.getWidth())
            TextClass.sbitmap = RotateBitmap(TextClass.sbitmap, 90);

        imageView.setImageBitmap(TextClass.sbitmap);

        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextClass.sbitmap = RotateBitmap(TextClass.sbitmap, 90);
                imageView.setImageBitmap(TextClass.sbitmap);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageProcess();
                Intent intent = new Intent(getApplicationContext(), TextActivity.class);
                startActivity(intent);
            }
        });

        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uri=TextClass.sUri;
                CropImage();
            }
        });

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextClass.sbitmap = RotateBitmap(TextClass.sbitmap, -1.0f);
                imageView.setImageBitmap(TextClass.sbitmap);
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextClass.sbitmap = RotateBitmap(TextClass.sbitmap, 1.0f);
                imageView.setImageBitmap(TextClass.sbitmap);
            }
        });

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
            Log.w("no00",TextClass.sUri.toString());
            if (data != null) {
                Log.w("no00",TextClass.sUri.toString());
                Log.w("h","ok");
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

    private void CropImage() {

        try {
            CrIntent = new Intent("com.android.camera.action.CROP");
            if (TextClass.sUri!=null)
                Log.w("no",TextClass.sUri.toString());
            CrIntent.setDataAndType(TextClass.sUri, "image/*");

            CrIntent.putExtra("crop", "true");
            CrIntent.putExtra("outputX", 900);
            CrIntent.putExtra("outputY", 900);
//            CropIntent.putExtra("aspectX", 3);
//            CropIntent.putExtra("aspectY", 4);
            //CropIntent.putExtra("scaleUpIfNeeded", true);
            CrIntent.putExtra("return-data", true);
            CrIntent.putExtra(MediaStore.EXTRA_OUTPUT, TextClass.sUri);
            startActivityForResult(CrIntent, 1);
        } catch (ActivityNotFoundException ex) {

        }

    }


    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}