package com.tag.phototext;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
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
    ImageButton finalSave;
    RadioButton radioButtonTxt, radioButtonPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        save = (EditText) findViewById(R.id.saveName);
        finalSave = (ImageButton) findViewById(R.id.finalSave);
        radioButtonTxt = (RadioButton) findViewById(R.id.radioButtonTxt);
        radioButtonPdf = (RadioButton) findViewById(R.id.radioButtonPdf);


        finalSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioButtonPdf.isChecked()) {
                    createPdf(view);
                    Toast.makeText(getApplicationContext(), "Saved to Photo Text",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), CameraTestActivity.class));
                } else if (radioButtonTxt.isChecked()) {
                    writeToFile(TextClass.stringBuilder.toString(), getApplicationContext());
                    startActivity(new Intent(getApplicationContext(), CameraTestActivity.class));
                }
            }
        });

    }

    public void createPdf(View view) {
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
