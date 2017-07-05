package com.tag.phototext;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;

public class MultipleActivity extends AppCompatActivity {


    GridView gvMul;
    MultipleAdapter multipleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple);

        gvMul = (GridView) findViewById(R.id.gvMul);
        multipleAdapter = new MultipleAdapter(MultipleActivity.this, getPDFs());
        gvMul.setAdapter(multipleAdapter);
        gvMul.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);


        gvMul.setMultiChoiceModeListener(new MultiChoiceModeListener());
    }


    public class MultiChoiceModeListener implements
            GridView.MultiChoiceModeListener {

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            final int checkedCount = gvMul.getCheckedItemCount();
            mode.setTitle(checkedCount + " Selected");
            multipleAdapter.toggleSelection(position);
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.deleteeee:
                    SparseBooleanArray selected = multipleAdapter.getSelectedIds();

                    for (int i = (selected.size() - 1); i >= 0; i--) {
                        if (selected.valueAt(i)) {
                            Object selecteditem = multipleAdapter.getItem(selected.keyAt(i));
                            multipleAdapter.remove(selecteditem);
                        }
                    }
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multipleAdapter.removeSelection();
        }
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
