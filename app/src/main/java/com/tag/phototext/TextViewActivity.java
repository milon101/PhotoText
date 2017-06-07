package com.tag.phototext;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.takeimage.R;

public class TextViewActivity extends AppCompatActivity {

    TextView textView;
    ImageButton editButtonn, nextSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);

        textView = (TextView) findViewById(R.id.textVieww);
        editButtonn = (ImageButton) findViewById(R.id.editButt);
        nextSave = (ImageButton) findViewById(R.id.nextSave);
        textView.setText(TextClass.stringBuilder.toString());

        editButtonn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),TextActivity.class));
            }
        });

        nextSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SaveActivity.class));
            }
        });
    }
}
