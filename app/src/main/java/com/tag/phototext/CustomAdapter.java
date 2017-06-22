package com.tag.phototext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Oclemy on 7/28/2016 for ProgrammingWizards Channel and http://www.camposha.com.
 */
public class CustomAdapter extends BaseAdapter {

    Context c;
    ArrayList<PDFDoc> pdfDocs;

    public CustomAdapter(Context c, ArrayList<PDFDoc> pdfDocs) {
        this.c = c;
        this.pdfDocs = pdfDocs;
    }

    @Override
    public int getCount() {
        return pdfDocs.size();
    }

    @Override
    public Object getItem(int i) {
        return pdfDocs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            //INFLATE CUSTOM LAYOUT
            view = LayoutInflater.from(c).inflate(R.layout.model, viewGroup, false);
        }

        final PDFDoc pdfDoc = (PDFDoc) this.getItem(i);

        final TextView nameTxt = (TextView) view.findViewById(R.id.nameTxt);
        ImageView img = (ImageView) view.findViewById(R.id.pdfImage);

        //BIND DATA
        nameTxt.setText(pdfDoc.getName());
        if (pdfDoc.getType().equalsIgnoreCase("pdf"))
            img.setImageResource(R.drawable.lpdf_icon_updated);
        else if (pdfDoc.getType().equalsIgnoreCase("txt"))
            img.setImageResource(R.drawable.txt_icon_updated);

        //VIEW ITEM CLICK
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPDFView(pdfDoc.getPath(), pdfDoc.getType());
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final CharSequence[] items = {"Rename", "Delete",
                        "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setTitle(nameTxt.getText().toString());
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                return true;
            }
        });
        return view;
    }

    //OPEN PDF VIEW
    private void openPDFView(String path, String type) {

        if (type.equalsIgnoreCase("pdf")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(path)), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent intent1 = Intent.createChooser(intent, "Open File");
            c.startActivity(intent1);
//        Intent i=new Intent(c,PDF_Activity.class);
//        i.putExtra("PATH",path);
//        c.startActivity(i);
        } else if (type.equalsIgnoreCase("txt")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(path)), "text/plain");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent intent1 = Intent.createChooser(intent, "Open File");
            c.startActivity(intent1);
        }
    }
}
