package com.tag.phototext;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter<PDFDoc> {

    Context context;
    LayoutInflater inflater;
    ArrayList<PDFDoc> pdfDocs;
    private SparseBooleanArray mSelectedItemsIds;
    boolean flag = true;

    public GridViewAdapter(Context context, int resourceId, ArrayList<PDFDoc> pdfDocs) {
        super(context, resourceId, pdfDocs);

        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.pdfDocs = pdfDocs;
        inflater = LayoutInflater.from(context);
    }

    private class ViewHolder {
        TextView nameTxt;
        ImageView img;
        CardView cardView;
        View v;

    }

    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {

            holder = new ViewHolder();
            view = inflater.inflate(R.layout.model, null);
            holder.nameTxt = (TextView) view.findViewById(R.id.nameTxt);
            holder.img = (ImageView) view.findViewById(R.id.pdfImage);
            holder.cardView = (CardView) view.findViewById(R.id.cardView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (flag == false) {
                    holder.cardView.setCardBackgroundColor(Color.WHITE);
                    Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                    flag=true;
                    return false;
                } else if (flag == true) {
                    holder.cardView.setCardBackgroundColor(Color.BLUE);
                    Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                    flag=false;
                    return false;
                }
                return false;
            }
        });

        holder.nameTxt.setText(pdfDocs.get(position).getName());
        if (pdfDocs.get(position).getType().equalsIgnoreCase("pdf"))
            holder.img.setImageResource(R.drawable.lpdf_icon_updated);
        else if (pdfDocs.get(position).getType().equalsIgnoreCase("txt"))
            holder.img.setImageResource(R.drawable.txt_icon_updated);

        return view;
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
}
