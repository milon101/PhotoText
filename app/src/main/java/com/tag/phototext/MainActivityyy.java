package com.tag.phototext;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class MainActivityyy extends ActionBarActivity {

    GridView gridView;
    GridViewAdapter gridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityyy_main);
        gridView = (GridView) findViewById(R.id.gridViewMultiple);
        gridViewAdapter = new GridViewAdapter(this, R.layout.model, getPDFs());
        gridView.setAdapter(gridViewAdapter);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);

        gridView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

            @Override
            public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode arg0) {
                gridViewAdapter.removeSelection();
            }

            @Override
            public boolean onCreateActionMode(ActionMode arg0, Menu arg1) {
                arg0.getMenuInflater().inflate(R.menu.main, arg1);

                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode arg0, MenuItem arg1) {
                switch (arg1.getItemId()) {
                    case R.id.deleteeee:
                        SparseBooleanArray selected = gridViewAdapter.getSelectedIds();

                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                PDFDoc selecteditem = gridViewAdapter.getItem(selected.keyAt(i));
                                File file = new File(selecteditem.getPath());
                                boolean deleted = file.delete();
                                gridViewAdapter.remove(selecteditem);
                                if (deleted) {
                                    Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        arg0.finish();
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
            }
        });

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
