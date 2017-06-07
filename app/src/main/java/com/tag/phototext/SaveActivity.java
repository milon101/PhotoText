package com.tag.phototext;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.takeimage.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class SaveActivity extends AppCompatActivity {

    EditText save;
    ImageButton finalSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        save = (EditText) findViewById(R.id.editText);
        finalSave = (ImageButton) findViewById(R.id.finalSave);
        finalSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPdf(view);
                Toast.makeText(getApplicationContext(), "Saved",
                        Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

    }

    public void createPdf(View view) {
        Document doc = new Document();
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "OCR");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String outpsth = "/sdcard/OCR/"+save.getText()+".pdf";
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
