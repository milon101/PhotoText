package com.tag.photocaptureandgallery;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.takeimage.R;
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
    ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        editText = (EditText) findViewById(R.id.editText);
        editText.setText(null);
        button = (ImageButton) findViewById(R.id.saveButton);
        len = TextClass.stringBuilder.toString().length();
        editText.setText(TextClass.stringBuilder.toString());
        editText.setSelection(editText.getText().length());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SaveActivity.class);
                startActivity(intent);
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
