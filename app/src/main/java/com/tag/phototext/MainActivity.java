package com.tag.phototext;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.kyo.imagecrop.CropUtils;

public class MainActivity extends Activity {

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private ImageButton btnSelect;
    private ImageView ivImage;
    private String userChoosenTask;
    Intent CrIntent, GalIntent;

    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSelect = (ImageButton) findViewById(R.id.btnSelectPhoto);
        btnSelect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        //ivImage = (ImageView) findViewById(R.id.ivImage);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        try {
                            cameraIntent();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(MainActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        try {
                            cameraIntent();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        //galleryIntent();
                        GalleryOpen();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void GalleryOpen() {
        GalIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(GalIntent, "Select Image from Gallery"), SELECT_FILE);
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    private void cameraIntent() throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(createImageFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//        startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        try {
            TextClass.sUri = Uri.parse(mCurrentPhotoPath);
            TextClass.sbitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), TextClass.sUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        startCropper(REQUEST_CAMERA, data);

        if (TextClass.sbitmap.getHeight() < TextClass.sbitmap.getWidth())
            TextClass.sbitmap = RotateBitmap(TextClass.sbitmap, 90);

        //ivImage.setImageBitmap(TextClass.sbitmap);

//        Intent intent = new Intent(getApplicationContext(), Ocr2.class);
//        startActivity(intent);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        if (data != null) {
            try {
                Log.w("no", "Null");
                TextClass.sUri = data.getData();
                TextClass.sbitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                startCropper(SELECT_FILE, data);
                //CropImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//
//		ivImage.setImageBitmap(bitmap);
//        Intent intent = new Intent(getApplicationContext(), Ocr2.class);
//        startActivity(intent);
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void CropImage() {

        try {
            CrIntent = new Intent("com.android.camera.action.CROP");
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


    private void startCropper(int requestCode, Intent data) {
        Uri uri = null;
        if (requestCode == REQUEST_CAMERA) {
            uri = TextClass.sUri;
        } else if (data != null && data.getData() != null) {
            uri = TextClass.sUri;
        } else {
            return;
        }
        Intent intent = new Intent(this, CropActivity.class);
        intent.setData(uri);
        intent.putExtra("outputX", CropUtils.dip2px(this, 300));
        intent.putExtra("outputY", CropUtils.dip2px(this, 150));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivity(intent);
    }

}
