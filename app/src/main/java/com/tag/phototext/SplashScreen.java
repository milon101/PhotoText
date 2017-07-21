package com.tag.phototext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class SplashScreen extends AppCompatActivity {

    private static final long SPLASH_TIME_OUT = 1500;
    private SharedPreferences mSharedPreferences;
    String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        ImageView imageView = (ImageView) findViewById(R.id.my_image_view);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        flag = mSharedPreferences.getString("camera", "1");
        Glide.with(this).load(R.drawable.splashscreen2).into(imageView);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if (flag.equalsIgnoreCase("Camera")) {
                    Intent i = new Intent(SplashScreen.this, CameraTestActivity.class);
                    startActivity(i);
                } else {
                    startActivity(new Intent(getApplicationContext(), MainnnActivity.class));
                }

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
