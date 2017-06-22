package com.tag.phototext;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class TextActivity extends AppCompatActivity {

    EditText editText;
    int len;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        editText = (EditText) findViewById(R.id.editText);
        editText.setText(null);

        len = TextClass.stringBuilder.toString().length();
        editText.setText(TextClass.stringBuilder.toString());
        editText.setSelection(editText.getText().length());

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.saveNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.editDone:
                        TextClass.stringBuilder = new StringBuilder();
                        TextClass.stringBuilder.append(editText.getText().toString());
                        startActivity(new Intent(getApplicationContext(), TextViewActivity.class));
                        break;
                }
                return true;
            }
        });

    }

    public void createPdf(View view) {
        Document doc = new Document();
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "TollCulator");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String outpsth = "/sdcard/OCR/mine1.pdf";
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
}
