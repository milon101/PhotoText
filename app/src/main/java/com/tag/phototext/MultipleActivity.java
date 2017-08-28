package com.tag.phototext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class MultipleActivity extends ActionBarActivity {

    GridView gridView;
    GridViewAdapter gridViewAdapter;
    ArrayList<PDFDoc> pdfDocs;
    AlertDialog.Builder alertDialog;
    CardView cardView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityyy_main);
        gridView = (GridView) findViewById(R.id.gridViewMultiple);
        gridViewAdapter = new GridViewAdapter(this, R.layout.model, getPDFs());
        pdfDocs = getPDFs();
        gridView.setAdapter(gridViewAdapter);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);

        View view = getLayoutInflater().inflate(R.layout.model, null);
        cardView1 = (CardView) view.findViewById(R.id.cardView);

        gridView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

            @Override
            public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode arg0) {
                gridViewAdapter.removeSelection();
                gridViewAdapter = new GridViewAdapter(getApplicationContext(), R.layout.model, getPDFs());
                gridView.setAdapter(gridViewAdapter);
            }

            @Override
            public boolean onCreateActionMode(ActionMode arg0, Menu arg1) {
                arg0.getMenuInflater().inflate(R.menu.main, arg1);

                return true;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode arg0, MenuItem arg1) {
                switch (arg1.getItemId()) {
                    case R.id.deleteeee:
                        final SparseBooleanArray selected = gridViewAdapter.getSelectedIds();

                        AlertDialog alertDialog = new AlertDialog.Builder(MultipleActivity.this).create();
                        alertDialog.setMessage("Do you really want to delete?");

                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (int i = (selected.size() - 1); i >= 0; i--) {
                                    if (selected.valueAt(i)) {
                                        PDFDoc selecteditem = (PDFDoc) gridViewAdapter.getItem(selected.keyAt(i));
                                        File file = new File(selecteditem.getPath());
                                        boolean deleted = file.delete();
                                        if (deleted) {
                                            gridViewAdapter.remove(selecteditem);
                                            Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                arg0.finish();
                            }
                        });

                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        alertDialog.show();

                        return true;


                    default:
                        return false;
                }
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                final int checkedCount = gridView.getCheckedItemCount();
                mode.setTitle(checkedCount + " Selected");
                gridViewAdapter.toggleSelection(position);
            }


        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gridView.setItemChecked(position, true);
//                final PDFDoc pdfDoc = (PDFDoc) pdfDocs.get(position);
//
//                if (pdfDoc.getChecked()) {
//                    cardView1.setCardBackgroundColor(Color.WHITE);
//                    pdfDoc.setChecked(false);
//                    return;
//                } else {
//                    pdfDoc.setChecked(true);
//                    cardView1.setCardBackgroundColor(0x555535);
//                    Toast.makeText(getApplicationContext(), "Clicked" + gridViewAdapter.getSelectedCount(), Toast.LENGTH_SHORT).show();
//                    return;
//                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_multiple, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search_multiple);
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                gridViewAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainnnActivity.class));
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
                    pdfDoc = new PDFDoc(file.getName(), file.getAbsolutePath(), "pdf");
                    pdfDoc.setName(file.getName());
                    pdfDoc.setPath(file.getAbsolutePath());
                    pdfDoc.setType("pdf");

                    pdfDocs.add(pdfDoc);
                } else if (file.getPath().endsWith("txt")) {
                    pdfDoc = new PDFDoc(file.getName(), file.getAbsolutePath(), "txt");
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
