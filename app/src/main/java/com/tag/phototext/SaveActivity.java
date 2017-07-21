package com.tag.phototext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class SaveActivity extends AppCompatActivity {

    EditText save;
    BottomNavigationView bottomNavigationView;
    TextInputLayout til;
    SharedPreferences sharedPreferences;
    String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        save = (EditText) findViewById(R.id.edit_text_save);
        til = (TextInputLayout) findViewById(R.id.text_input_layout);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav_saveFinal);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        flag = sharedPreferences.getString("outputType", "1");

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.saveFinal:
                        if (flag.equalsIgnoreCase("1")) {
                            if (save.getText().toString().isEmpty()) {
                                til.setError("You need to enter a name");
                            } else {
                                createPdf();
                                startActivity(new Intent(getApplicationContext(), CameraTestActivity.class));
                            }
                        } else if (flag.equalsIgnoreCase("2")) {
                            if (save.getText().toString().isEmpty()) {
                                til.setError("You need to enter a name");
                            } else {
                                writeToFile(TextClass.stringBuilder.toString(), getApplicationContext());
                                startActivity(new Intent(getApplicationContext(), CameraTestActivity.class));
                            }
                        }
                        break;
                }
                return false;
            }
        });

    }

    public void createPdf() {
        Document doc = new Document();
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Photo Text");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String outpsth = "/sdcard/Photo Text/" + save.getText() + ".pdf";
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(outpsth));
            doc.open();
            doc.add(new Paragraph(TextClass.stringBuilder.toString()));
            doc.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void writeToFile(String data, Context context) {
        try {

            File myFile = new File("/sdcard/Photo Text/" + save.getText() + ".txt");
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
