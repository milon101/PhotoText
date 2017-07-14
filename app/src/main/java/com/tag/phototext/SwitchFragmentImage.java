package com.tag.phototext;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by MILON on 7/13/2017.
 */

public class SwitchFragmentImage extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.switch_fragment_image, container, false);

        //Inflate the layout for this fragment
        ImageView imageView = (ImageView) view.findViewById(R.id.switchImgView);
        imageView.setImageBitmap(TextClass.sbitmap);

        return view;
    }
}
