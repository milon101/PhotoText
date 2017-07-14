package com.tag.phototext;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class TextEditFragment extends Fragment {

    EditText editText;
    int len;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //Inflate the layout for this fragment

        View view = inflater.inflate(
                R.layout.fragment_edit_text, container, false);
        EditText editText = (EditText) view.findViewById(R.id.editText);
        editText.setText(TextClass.stringBuilder.toString());
        editText.setSelection(editText.getText().length());
        return view;
    }

}
