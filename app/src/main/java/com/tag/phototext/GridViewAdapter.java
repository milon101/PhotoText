package com.tag.phototext;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter implements Filterable {

    Context context;
    LayoutInflater inflater;
    ArrayList<PDFDoc> pdfDocs;
    ArrayList<PDFDoc> filterList;
    CustomFilter filter;
    private SparseBooleanArray mSelectedItemsIds;
    int flag = 1, count = 0;

    public GridViewAdapter(Context context, int resourceId, ArrayList<PDFDoc> pdfDocs) {
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.pdfDocs = pdfDocs;
        this.filterList = pdfDocs;
        inflater = LayoutInflater.from(context);
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

    public View getView(int position, View view, ViewGroup parent) {
        //final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.model, null);
        }

        final TextView nameTxt = (TextView) view.findViewById(R.id.nameTxt);
        final ImageView img = (ImageView) view.findViewById(R.id.pdfImage);
        final CardView cardView = (CardView) view.findViewById(R.id.cardView);
        final PDFDoc pdfDoc = (PDFDoc) this.getItem(position);

        nameTxt.setText(pdfDoc.getName().substring(0, pdfDoc.getName().lastIndexOf(".")));
        if (pdfDoc.getType().equalsIgnoreCase("pdf"))
            img.setImageResource(R.drawable.lpdf_icon_updated);
        else if (pdfDoc.getType().equalsIgnoreCase("txt"))
            img.setImageResource(R.drawable.txt_icon_updated);

//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (pdfDoc.getChecked()) {
//                    cardView.setCardBackgroundColor(Color.WHITE);
//                    pdfDoc.setChecked(false);
//                    return;
//                } else {
//                    pdfDoc.setChecked(true);
//                    cardView.setCardBackgroundColor(0x555535);
//                    Toast.makeText(context, "Clicked" + getSelectedCount(), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
//        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (pdfDoc.getChecked()) {
                    cardView.setCardBackgroundColor(Color.WHITE);
                    pdfDoc.setChecked(false);
                    return false;
                } else {
                    pdfDoc.setChecked(true);
                    cardView.setCardBackgroundColor(0x555535);
                    Toast.makeText(context, "Clicked" + getSelectedCount(), Toast.LENGTH_SHORT).show();

                    return false;
                }
            }
        });

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
