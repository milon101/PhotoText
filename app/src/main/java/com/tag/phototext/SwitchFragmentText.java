package com.tag.phototext;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by MILON on 7/13/2017.
 */

public class SwitchFragmentText extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //Inflate the layout for this fragment

        View view = inflater.inflate(
                R.layout.switch_fragment_text, container, false);
        TextView textView = (TextView) view.findViewById(R.id.switchTextVieww);
        textView.setText(TextClass.stringBuilder.toString());
        return view;
    }
}
