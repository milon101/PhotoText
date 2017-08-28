package com.tag.phototext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter implements Filterable {

    Context c;
    ArrayList<PDFDoc> pdfDocs;
    ArrayList<PDFDoc> filterList;
    CustomFilter filter;
    String string1, string, result;
    File newFile, currentFile;
    private AlertDialog.Builder firstDialogBuilder;
    private AlertDialog firstDialog;
    private AlertDialog.Builder secondDialogBuilder;
    private AlertDialog secondDialog;

    private SparseBooleanArray mSelectedItemsIds;

    public CustomAdapter(Context c, ArrayList<PDFDoc> pdfDocs) {
        mSelectedItemsIds = new SparseBooleanArray();
        this.c = c;
        this.pdfDocs = pdfDocs;
        this.filterList = pdfDocs;
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
        nameTxt.setText(pdfDoc.getName().substring(0, pdfDoc.getName().lastIndexOf(".")));
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
                final CharSequence[] items = {"View", "Rename", "Delete", "Share",
                        "Cancel"};
                //Toast.makeText(c,pdfDoc.getName().substring(0, pdfDoc.getName().lastIndexOf(".")),Toast.LENGTH_SHORT).show();

                firstDialogBuilder = new AlertDialog.Builder(c);
                firstDialogBuilder.setTitle(nameTxt.getText().toString());
                firstDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (items[which].equals("Share")) {
                            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                            Uri uri = Uri.parse(pdfDoc.getPath());
                            File file = new File(pdfDoc.getPath());
                            uri = Uri.fromFile(file);
                            sharingIntent.setType("application/pdf");
                            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            c.startActivity(Intent.createChooser(sharingIntent, "Choose sharing method"));
                        }

                        if (items[which].equals("Delete")) {
                            File file = new File(pdfDoc.getPath());
                            boolean deleted = file.delete();
                            if (deleted) {
                                updateList(getPDFs());
                                Toast.makeText(c, "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        }

                        if (items[which].equals("View")) {
                            if (pdfDoc.getType().equalsIgnoreCase("pdf")) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(new File(pdfDoc.getPath())), "application/pdf");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                                Intent intent1 = Intent.createChooser(intent, "Open File");
                                c.startActivity(intent1);
                            } else if (pdfDoc.getType().equalsIgnoreCase("txt")) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(new File(pdfDoc.getPath())), "text/plain");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                                Intent intent1 = Intent.createChooser(intent, "Open File");
                                c.startActivity(intent1);
                            }
                        }


                        if (items[which].equals("Rename")) {
                            currentFile = new File(pdfDoc.getPath());
                            string = pdfDoc.getPath().substring(0, pdfDoc.getPath().lastIndexOf(File.separator));
                            secondDialogBuilder = new AlertDialog.Builder(c);
                            secondDialogBuilder.setTitle("Rename File");
                            final EditText input = new EditText(c);

                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(lp);
                            secondDialogBuilder.setView(input);
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
                                                updateList(getPDFs());
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

                    }
                });
                firstDialog = firstDialogBuilder.create();
                firstDialog.show();
                return true;
            }
        });


        return view;
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
                    pdfDoc = new PDFDoc(file.getName(),file.getAbsolutePath(),"pdf");
                    pdfDoc.setName(file.getName());
                    pdfDoc.setPath(file.getAbsolutePath());
                    pdfDoc.setType("pdf");

                    pdfDocs.add(pdfDoc);
                } else if (file.getPath().endsWith("txt")) {
                    pdfDoc = new PDFDoc(file.getName(),file.getAbsolutePath(),"txt");
                    pdfDoc.setName(file.getName());
                    pdfDoc.setPath(file.getAbsolutePath());
                    pdfDoc.setType("txt");

                    pdfDocs.add(pdfDoc);
                }


            }
        }

        return pdfDocs;
    }

    public void updateList(ArrayList<PDFDoc> pdfDocss) {
        pdfDocs.clear();
        pdfDocs.addAll(pdfDocss);
        this.notifyDataSetChanged();
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
        } else if (type.equalsIgnoreCase("txt")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(path)), "text/plain");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent intent1 = Intent.createChooser(intent, "Open File");
            c.startActivity(intent1);
        }
    }

    public void remove(PDFDoc object) {
        pdfDocs.remove(object);
        notifyDataSetChanged();
    }

    public ArrayList<PDFDoc> getPdf() {
        return pdfDocs;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value) {
            mSelectedItemsIds.put(position, value);
        } else {
            mSelectedItemsIds.delete(position);
        }
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CustomFilter();
        }
        return filter;
    }

    class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                constraint = constraint.toString().toUpperCase();
                ArrayList<PDFDoc> filters = new ArrayList<>();

                for (int i = 0; i < filterList.size(); i++) {
                    if (filterList.get(i).getName().toUpperCase().contains(constraint)) {
                        PDFDoc p = new PDFDoc(filterList.get(i).getName(), filterList.get(i).getPath(), filterList.get(i).getType());
                        filters.add(p);
                    }
                }
                filterResults.count = filters.size();
                filterResults.values = filters;
            } else {
                filterResults.count = filterList.size();
                filterResults.values = filterList;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            pdfDocs = (ArrayList<PDFDoc>) results.values;
            notifyDataSetChanged();
        }
    }
}
