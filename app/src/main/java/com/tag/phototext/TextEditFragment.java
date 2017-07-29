package com.tag.phototext;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
        editText = (EditText) view.findViewById(R.id.editText);
        editText.setText(TextClass.stringBuilder.toString());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                TextClass.stringBuilder = new StringBuilder();
                TextClass.stringBuilder.append(editText.getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

}
