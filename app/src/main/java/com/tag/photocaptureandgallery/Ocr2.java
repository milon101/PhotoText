package com.tag.photocaptureandgallery;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.takeimage.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Ocr2 extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    Button next;
    String TAG = "MAIN ACTIVITY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr2);

        imageProcess();
        next = (Button) findViewById(R.id.upload_btn1);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TextActivity.class);
                startActivity(intent);
            }
        });


    }

    public void imageProcess() {
        Uri uri = TextClass.sUri;

        Bitmap bitmap = TextClass.sbitmap;
        if(bitmap.getHeight()<bitmap.getWidth())
            bitmap= RotateBitmap(bitmap,90);

        ImageView imageView = (ImageView) findViewById(R.id.imgView);

        imageView.setImageBitmap(bitmap);

        // imageBitmap is the Bitmap image you're trying to process for text
        if (bitmap != null) {

            TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();

            if (!textRecognizer.isOperational()) {
                // Note: The first time that an app using a Vision API is installed on a
                // device, GMS will download a native libraries to the device in order to do detection.
                // Usually this completes before the app is run for the first time.  But if that
                // download has not yet completed, then the above call will not detect any text,
                // barcodes, or faces.
                // isOperational() can be used to check if the required native libraries are currently
                // available.  The detectors will automatically become operational once the library
                // downloads complete on device.
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
                    .setBitmap(bitmap)
                    .build();

            SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
            TextClass.stringBuilder = new StringBuilder();
            for (int i = 0; i < textBlocks.size(); i++) {

                TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));

                String text = textBlock.getValue();

                TextClass.stringBuilder.append(text);
                TextClass.stringBuilder.append("\n");

//                        List<? extends Text> textComponents = textBlock.getComponents();
//                        for(Text currentText : textComponents) {
//                            TextClass.stringBuilder.append(currentText.getValue());
//                            TextClass.stringBuilder.append("\n");
//                        }
//
//                        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

//
//                    if (textBlocks.size() != 0) {
//                        //textView.post(new Runnable() {
//                        //@Override
//                        // public void run() {
//                        TextClass.stringBuilder = new StringBuilder();
//                        for (int i = 0; i < textBlocks.size(); ++i) {
//                            TextBlock item = textBlocks.valueAt(i);
//                            TextClass.stringBuilder.append(item.getValue());
//                            TextClass.stringBuilder.append("\n");
//                        }
//                        //textView.setText(TextClass.stringBuilder.toString());
            }

        }

    }


    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}