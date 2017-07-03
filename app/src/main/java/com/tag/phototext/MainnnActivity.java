package com.tag.phototext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.kyo.imagecrop.CropUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MainnnActivity extends AppCompatActivity {

    private int SELECT_FILE = 1, REQUEST_CAMERA = 0;
    Intent GalIntent;
    CustomAdapter customAdapter;
    Context context;
    private SharedPreferences mSharedPreferences;
    String flag;
    int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainnn);

        final GridView gv = (GridView) findViewById(R.id.gv);
        customAdapter = new CustomAdapter(MainnnActivity.this, getPDFs());
        gv.setAdapter(customAdapter);
        gv.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        flag = mSharedPreferences.getString("gridValues", "1");
        num = Integer.parseInt(flag);
        gv.setNumColumns(num);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_Gal:
                        GalleryOpen();
                        break;

                    case R.id.action_settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        break;

                    case R.id.action_Cam:
                        startActivity(new Intent(MainnnActivity.this, CameraTestActivity.class));
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_more, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionMore:


                final CharSequence[] items = {"Take Photo", "Choose from Library",
                        "Cancel"};


                Toast.makeText(getApplicationContext(),"Menu",Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ArrayList<PDFDoc> getPDFs()

    {
        ArrayList<PDFDoc> pdfDocs = new ArrayList<>();
        //TARGET FOLDER
        File downloadsFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "Photo Text");

        PDFDoc pdfDoc;

        if (downloadsFolder.exists()) {
            //GET ALL FILES IN DOWNLOAD FOLDER
            File[] files = downloadsFolder.listFiles();

            //LOOP THRU THOSE FILES GETTING NAME AND URI
            for (int i = 0; i < files.length; i++) {
                File file = files[i];

                if (file.getPath().endsWith("pdf")) {
                    pdfDoc = new PDFDoc();
                    pdfDoc.setName(file.getName());
                    pdfDoc.setPath(file.getAbsolutePath());
                    pdfDoc.setType("pdf");

                    pdfDocs.add(pdfDoc);
                } else if (file.getPath().endsWith("txt")) {
                    pdfDoc = new PDFDoc();
                    pdfDoc.setName(file.getName());
                    pdfDoc.setPath(file.getAbsolutePath());
                    pdfDoc.setType("txt");

                    pdfDocs.add(pdfDoc);
                }


            }
        }

        return pdfDocs;
    }


    private void GalleryOpen() {
        GalIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(GalIntent, "Select Image from Gallery"), SELECT_FILE);
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
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
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
