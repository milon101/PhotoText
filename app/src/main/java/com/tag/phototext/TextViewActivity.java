package com.tag.phototext;

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
import android.view.View;
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
    String name, flag;
    private Context mContext;
    TextToSpeech textToSpeech;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);

        mContext = getApplicationContext();
        textView = (TextView) findViewById(R.id.textVieww);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_TextView_nav);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (!TextClass.stringBuilder.toString().isEmpty())
            name = TextClass.stringBuilder.toString().substring(0, 5);
        flag = mSharedPreferences.getString("outputType", "1");

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
                        startActivity(new Intent(getApplicationContext(), TextActivity.class));
                        break;
                    case R.id.textDne:
                        if (flag.equalsIgnoreCase("1")) {
                            createPdf();
                            startActivity(new Intent(getApplicationContext(), CameraTestActivity.class));
                        } else if (flag.equalsIgnoreCase("2")) {
                            writeToFile(TextClass.stringBuilder.toString(), getApplicationContext());
                            startActivity(new Intent(getApplicationContext(), CameraTestActivity.class));
                        }
                        break;
                }
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyText = TextClass.stringBuilder.toString();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));
                return true;

            case R.id.textToSpeech:
                textToSpeech.speak(TextClass.stringBuilder.toString(), TextToSpeech.QUEUE_FLUSH, null);
                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
                return true;

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
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

}
