package com.tag.photocaptureandgallery;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.takeimage.R;

import java.io.IOException;

public class Main3Activity extends AppCompatActivity {

    Button iButton, iRotate, crop, tiltLeft, tiltRight;
    ImageView iView;
    Intent CamIntent, GalIntent, CropIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        tiltLeft = (Button) findViewById(R.id.tiltLeft);
        tiltRight = (Button) findViewById(R.id.tiltRight);
        iButton = (Button) findViewById(R.id.iButton);
        iRotate = (Button) findViewById(R.id.iRotate);
        crop = (Button) findViewById(R.id.cropButton);

        iView = (ImageView) findViewById(R.id.iView);
        iView.setImageBitmap(TextClass.sbitmap);


        iRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextClass.sbitmap = RotateBitmap(TextClass.sbitmap, 90);
                iView.setImageBitmap(TextClass.sbitmap);
            }
        });

        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage();
            }
        });

        iButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Main2Activity.class));
            }
        });

        tiltLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextClass.sbitmap = RotateBitmap(TextClass.sbitmap, -1);
                iView.setImageBitmap(TextClass.sbitmap);
            }
        });

        tiltRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextClass.sbitmap = RotateBitmap(TextClass.sbitmap, 1);
                iView.setImageBitmap(TextClass.sbitmap);
            }
        });
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                TextClass.sbitmap = bundle.getParcelable("data");
                try {
                    TextClass.sbitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), TextClass.sUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                iView.setImageBitmap(TextClass.sbitmap);
            }
        }
    }

    private void CropImage() {

        try {
            CropIntent = new Intent("com.android.camera.action.CROP");
            CropIntent.setDataAndType(TextClass.sUri, "image/*");

            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 900);
            CropIntent.putExtra("outputY", 900);
//            CropIntent.putExtra("aspectX", 3);
//            CropIntent.putExtra("aspectY", 4);
            //CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);
            CropIntent.putExtra(MediaStore.EXTRA_OUTPUT, TextClass.sUri);

            startActivityForResult(CropIntent, 1);
        } catch (ActivityNotFoundException ex) {

        }

    }
}
