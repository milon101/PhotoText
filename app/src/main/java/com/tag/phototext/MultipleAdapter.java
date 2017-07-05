package com.tag.phototext;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by MILON on 7/5/2017.
 */

public class MultipleAdapter extends BaseAdapter {


    Context c;
    ArrayList<PDFDoc> pdfDocs;
    private SparseBooleanArray mSelectedItemsIds;
    PDFDoc pdfDoc;

    public MultipleAdapter(Context c, ArrayList<PDFDoc> pdfDocs) {
        mSelectedItemsIds = new SparseBooleanArray();
        this.c = c;
        this.pdfDocs = pdfDocs;
    }

    private class ViewHolder {
        TextView nameTxt;
        ImageView img;
    }

    @Override
    public int getCount() {
        return pdfDocs.size();
    }

    @Override
    public Object getItem(int position) {
        return pdfDocs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(c).inflate(R.layout.model, parent, false);
            convertView.setLongClickable(true);
            pdfDoc = (PDFDoc) this.getItem(position);
            holder.nameTxt = (TextView) convertView.findViewById(R.id.nameTxt);
            holder.img = (ImageView) convertView.findViewById(R.id.pdfImage);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.nameTxt.setText(pdfDoc.getName());
        if (pdfDoc.getType().equalsIgnoreCase("pdf"))
            holder.img.setImageResource(R.drawable.lpdf_icon_updated);
        else if (pdfDoc.getType().equalsIgnoreCase("txt"))
            holder.img.setImageResource(R.drawable.txt_icon_updated);
        return convertView;
    }

    public void remove(Object object) {
        pdfDocs.remove(object);
        notifyDataSetChanged();
    }


    public ArrayList<PDFDoc> getPdfDocs() {
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
}
