package com.tag.phototext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Oclemy on 7/28/2016 for ProgrammingWizards Channel and http://www.camposha.com.
 */
public class CustomAdapter extends BaseAdapter {

    Context c;
    ArrayList<PDFDoc> pdfDocs;
    String string1, string, result;
    File newFile, currentFile;
    private AlertDialog.Builder firstDialogBuilder;
    private AlertDialog firstDialog;
    private AlertDialog.Builder secondDialogBuilder;
    private AlertDialog secondDialog;

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
            public boolean onLongClick(final View v) {
                final CharSequence[] items = {"Rename", "Delete", "Share",
                        "Cancel"};

                firstDialogBuilder = new AlertDialog.Builder(c);
                firstDialogBuilder.setTitle(nameTxt.getText().toString());
                firstDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items[which].equals("Rename")) {
                            currentFile = new File(pdfDoc.getPath());
                            string = pdfDoc.getPath().substring(0, pdfDoc.getPath().lastIndexOf(File.separator));
                            secondDialogBuilder = new AlertDialog.Builder(c);
                            secondDialogBuilder.setTitle("Rename File");
//                            secondDialogBuilder.setMessage("Enter Password");
                            final EditText input = new EditText(c);

                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(lp);
                            secondDialogBuilder.setView(input);
//                            alertDialog.setIcon(R.drawable.key);
                            result = pdfDoc.getName().substring(0, pdfDoc.getName().lastIndexOf("."));
                            input.setText(result);
                            input.selectAll();


                            secondDialogBuilder.setPositiveButton("RENAME",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            string1 = input.getText().toString();
                                            if (string1.isEmpty())
                                                string1 = result;
                                            if (pdfDoc.getType().equalsIgnoreCase("pdf")) {
                                                newFile = new File(string + "/" + string1 + ".pdf");
                                            } else if (pdfDoc.getType().equalsIgnoreCase("txt")) {
                                                newFile = new File(string + "/" + string1 + ".txt");
                                            }
                                            if (rename(currentFile, newFile)) {
                                                //Success
                                                c.startActivity(new Intent(c, MainnnActivity.class));
                                            } else {
                                                //Fail
                                                Log.i("TAG", "Fail");
                                            }
                                            secondDialog.dismiss();
                                        }
                                    });

                            secondDialogBuilder.setNegativeButton("CANCEL",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            firstDialog.cancel();
                            secondDialog = secondDialogBuilder.create();
                            secondDialog.setCanceledOnTouchOutside(false);
                            secondDialog.show();

                        }

                        //Toast.makeText(c, "Rename" + string, Toast.LENGTH_SHORT).show();

                    }
                });
                firstDialog = firstDialogBuilder.create();
                firstDialog.show();
                return true;
            }
        });


        return view;
    }


    private boolean rename(File from, File to) {
        return from.getParentFile().exists() && from.exists() && from.renameTo(to);
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
