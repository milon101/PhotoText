package com.tag.phototext;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;

public class MultipleActivity extends AppCompatActivity {


    GridView gv;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainnn);

        gv = (GridView) findViewById(R.id.gv);
        customAdapter = new CustomAdapter(MultipleActivity.this, getPDFs());
        gv.setAdapter(customAdapter);
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
}
