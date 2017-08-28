package com.tag.phototext;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TextViewActivity extends AppCompatActivity {

    TextView textView;
    BottomNavigationView bottomNavigationView;
    String name, flag, rename;
    private Context mContext;
    TextToSpeech textToSpeech;
    RelativeLayout relativeLayout;
    boolean check, isCheck = false;
    MenuItem menuItem;
    File file;
    boolean aBoolean;
    private SharedPreferences mSharedPreferences;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);

        mContext = getApplicationContext();
        textView = (TextView) findViewById(R.id.switchTextVieww);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_TextView_nav);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (!TextClass.stringBuilder.toString().isEmpty())
            name = TextClass.stringBuilder.toString().substring(0, 5);
        flag = mSharedPreferences.getString("outputType", "1");
        rename = mSharedPreferences.getString("rename", "Auto");
        aBoolean = mSharedPreferences.getBoolean("keepPhoto", false);
        if (!TextClass.stringBuilder.toString().isEmpty())
            textView.setText(TextClass.stringBuilder.toString());
        else
            textView.setText("Sorry !!! Nothing to show !!!");

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.textEdt:
                        textToSpeech.stop();
                        menuItem.setIcon(R.drawable.ic_action_speech);
                        isCheck = false;
                        fragment = new TextEditFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction frgmentTransaction = fragmentManager.beginTransaction();
                        frgmentTransaction.replace(R.id.fragment_place, fragment);
                        frgmentTransaction.commit();
                        break;
                    case R.id.textDne:
                        if (TextClass.stringBuilder.toString().isEmpty()) {
                            startActivity(new Intent(getApplicationContext(), CameraTestActivity.class));
                            break;
                        }
                        textToSpeech.stop();
                        menuItem.setIcon(R.drawable.ic_action_speech);
                        isCheck = false;
                        if (!aBoolean)
                            fileDelete(TextClass.filepath);
                        if (rename.equalsIgnoreCase("Auto")) {
                            if (flag.equalsIgnoreCase("1")) {
                                createPdf();
                                startActivity(new Intent(getApplicationContext(), CameraTestActivity.class));
                            } else if (flag.equalsIgnoreCase("2")) {
                                writeToFile(TextClass.stringBuilder.toString(), getApplicationContext());
                                startActivity(new Intent(getApplicationContext(), CameraTestActivity.class));
                            }
                        } else {
                            startActivity(new Intent(getApplicationContext(), SaveActivity.class));
                            Toast.makeText(getApplicationContext(), "pressed", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.switchImage:
                        textToSpeech.stop();
                        menuItem.setIcon(R.drawable.ic_action_speech);
                        isCheck = false;
                        if (check == false) {
                            fragment = new SwitchFragmentImage();
                            FragmentManager fm = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fm.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_place, fragment);
                            fragmentTransaction.commit();
                            item.setIcon(R.drawable.ic_action_text);
                            item.setTitle("Text");
                            check = true;
                            break;
                        }
                        if (check == true) {
                            fragment = new SwitchFragmentText();
                            FragmentManager fm = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fm.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_place, fragment);
                            fragmentTransaction.commit();
                            item.setIcon(R.drawable.ic_action_image);
                            item.setTitle("Image");
                            check = false;
                            break;
                        }
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!TextClass.stringBuilder.toString().isEmpty()) {
            getMenuInflater().inflate(R.menu.share_menu, menu);
            menuItem = menu.findItem(R.id.textToSpeech);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                textToSpeech.stop();
                menuItem.setIcon(R.drawable.ic_action_speech);
                isCheck = false;
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyText = TextClass.stringBuilder.toString();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));
                return true;

            case R.id.textToSpeech:
                if (isCheck == false) {
                    textToSpeech.speak(TextClass.stringBuilder.toString(), TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
                    item.setIcon(R.drawable.ic_action_volume_off);
                    isCheck = true;
                    return true;
                }
                if (isCheck == true) {
                    textToSpeech.stop();
                    item.setIcon(R.drawable.ic_action_speech);
                    isCheck = false;
                    return true;
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createPdf() {
        Document doc = new Document();
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Photo Text");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String outpsth = "/sdcard/Photo Text/" + name + "_" + timeStamp + ".pdf";
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(outpsth));
            doc.open();
            doc.add(new Paragraph(TextClass.stringBuilder.toString()));
            doc.close();
            Toast.makeText(getApplicationContext(), "Saved to Photo Text", Toast.LENGTH_SHORT).show();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, Ocr2.class));
    }

    private void writeToFile(String data, Context context) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File myFile = new File("/sdcard/Photo Text/" + name + "_" + timeStamp + ".txt");
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter =
                    new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            myOutWriter.close();
            fOut.close();
            Toast.makeText(getBaseContext(),
                    "Done writing SD 'mysdfile.txt'",
                    Toast.LENGTH_SHORT).show();

//            Toast.makeText(getBaseContext(), "Deleted" +
//                    fileDelete(TextClass.filepath), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void fileDelete(String path) {
        File fdelete = new File(path);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Toast.makeText(getApplicationContext(), "file Deleted :" + path, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "file not Deleted :" + path, Toast.LENGTH_SHORT).show();
            }
        }
    }


}
