package com.tag.phototext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

import com.kyo.imagecrop.CropUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MainnnActivity extends AppCompatActivity {

    private int SELECT_FILE = 1, REQUEST_CAMERA = 0;
    Intent GalIntent;
    GridView gv;
    CustomAdapter customAdapter;
    Context context;
    private SharedPreferences mSharedPreferences;
    String flag;
    int num;
    ArrayList<PDFDoc> pdfDocs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainnn);

        gv = (GridView) findViewById(R.id.gv);
        customAdapter = new CustomAdapter(MainnnActivity.this, getPDFs());
        gv.setAdapter(customAdapter);
        pdfDocs = new ArrayList<PDFDoc>();
        pdfDocs = getPDFs();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        flag = mSharedPreferences.getString("gridValues", "1");
        num = Integer.parseInt(flag);
        gv.setNumColumns(num);
        gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                Toast.makeText(MainnnActivity.this, "LONG PRESS", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openPDFView(pdfDocs.get(position).getPath(), pdfDocs.get(position).getType());
                Toast.makeText(MainnnActivity.this, "Short PRESS", Toast.LENGTH_SHORT).show();
            }
        });
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

    private void openPDFView(String path, String type) {

        if (type.equalsIgnoreCase("pdf")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(path)), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent intent1 = Intent.createChooser(intent, "Open File");
            startActivity(intent1);
//        Intent i=new Intent(c,PDF_Activity.class);
//        i.putExtra("PATH",path);
//        c.startActivity(i);
        } else if (type.equalsIgnoreCase("txt")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(path)), "text/plain");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent intent1 = Intent.createChooser(intent, "Open File");
            startActivity(intent1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_more, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                customAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionMore:
                final CharSequence[] items = {"Select", "Remove",
                        "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(MainnnActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        boolean result = Utility.checkPermission(MainnnActivity.this);

                        if (items[item].equals("Select")) {
                            startActivity(new Intent(getApplicationContext(), MultipleActivity.class));
                            Toast.makeText(getApplicationContext(), "Menu", Toast.LENGTH_SHORT).show();
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams abc = dialog.getWindow().getAttributes();
                abc.width = WindowManager.LayoutParams.MATCH_PARENT;
                abc.gravity = Gravity.TOP | Gravity.LEFT;
                abc.x = 100;   //x position
                abc.y = 100;   //y position
                dialog.show();
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
                    pdfDoc = new PDFDoc(file.getName(),file.getAbsolutePath(),"pdf");
                    pdfDocs.add(pdfDoc);
                } else if (file.getPath().endsWith("txt")) {
                    pdfDoc = new PDFDoc(file.getName(),file.getAbsolutePath(),"txt");
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
