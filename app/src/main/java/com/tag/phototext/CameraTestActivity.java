package com.tag.phototext;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kyo.imagecrop.CropUtils;

import java.io.File;
import java.io.IOException;

public class CameraTestActivity extends AppCompatActivity {

    private static String LOGTAG = "CameraActivity";
    Intent data;
    boolean boo = false;
    RelativeLayout.LayoutParams fullScreenParams;
    int fullWidth = 0;
    int fullHeight = 0;
    Intent GalIntent;
    BottomNavigationView bottomNavigation;
    private int SELECT_FILE = 1, REQUEST_CAMERA = 0, SELECT_CAMERA_FILE = 1;

    FloatingActionButton floatingActionButton;

    private CameraUtils mCamUtils = null;
    private ImageButton flipCamera = null;
    private ImageButton cameraFlash = null;
    private RelativeLayout cameraLayout = null;
    private Toolbar toolbar;

    private LinearLayout parentView;

    public static int mOrientation;

    /**
     * Action to be performed when image is capture is clicked
     */
    private OnClickListener OnCapture = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCamUtils.clickPicture();
        }
    };

    /**
     * Action to be performed when image is capture is clicked
     */
    private OnClickListener OnFlashClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCamUtils.toggleFlashSettings();

        }
    };

    /**
     * Action to be performed when flip camera is clicked
     */
    private OnClickListener OnFlip = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCamUtils.flipCamera();

            double ar = mCamUtils.getHighestAspectRatio();
            ScreenDimensions screen = getScreenDimensions(mOrientation, ar);
            calculateLayoutParams(screen);
            mCamUtils.setPreviewLayoutParams(fullScreenParams);
        }
    };

    /**
     * Callback called by the camera Utils when image file has been created
     */
    private CameraUtils.ImageClicked onImageClick = new CameraUtils.ImageClicked() {
        @Override
        public void imageClicked(File pictureFile) {

            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{pictureFile.getPath()},
                    null, null);
        }

        @Override
        public void flashSet(String flashMode) {
            if (flashMode.equalsIgnoreCase("on"))
                cameraFlash.setImageResource(R.drawable.flash);
            else if (flashMode.equalsIgnoreCase("off"))
                cameraFlash.setImageResource(R.drawable.flashoff);
            else if (flashMode.equalsIgnoreCase("auto"))
                cameraFlash.setImageResource(R.drawable.automaticflash);


        }

        @Override
        public void hideFlipButton() {
            flipCamera.setVisibility(View.INVISIBLE);
        }

        @Override
        public void enableFlashButton(boolean flag) {
            if (flag) {
                cameraFlash.setVisibility(View.VISIBLE);
            } else {
                cameraFlash.setVisibility(View.GONE);
            }

        }

        @Override
        public void CameraUnAvailable() {
            // TODO Auto-generated method stub

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        if (null != actionBar)
            actionBar.hide();

        setContentView(R.layout.activity_test);

        flipCamera = (ImageButton) findViewById(R.id.button_flip);
        flipCamera.setVisibility(View.VISIBLE);
        flipCamera.setOnClickListener(OnFlip);

        cameraFlash = (ImageButton) findViewById(R.id.button_flash);
        cameraFlash.setVisibility(View.VISIBLE);
        cameraFlash.setOnClickListener(OnFlashClick);
        cameraFlash.setImageResource(R.drawable.flash);


        mOrientation = this.getResources().getConfiguration().orientation;

        ScreenDimensions fullscreen = getScreenDimensions(mOrientation, (double) 16 / (double) 9);
        calculateLayoutParams(fullscreen);

        cameraLayout = (RelativeLayout) findViewById(R.id.full_camera_content);
        cameraLayout.setVisibility(View.VISIBLE);

        mCamUtils = new CameraUtils(getApplicationContext(), onImageClick, cameraLayout);

        parentView = (LinearLayout) findViewById(R.id.picture_content_parent_view_host);

        LinearLayout.LayoutParams parentLayout = new LinearLayout.LayoutParams(fullscreen.getDisplayWidth(), fullscreen.getDisplayHeight());
        parentLayout.gravity = Gravity.CENTER;
        parentView.setLayoutParams(parentLayout);
        parentView.setGravity(Gravity.CENTER);

		/* Hide flip camera button if only one camera a available */
        mCamUtils.handleFlipVisibility();

        floatingActionButton = (FloatingActionButton) findViewById(R.id.surface_float_test);
        floatingActionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamUtils.clickPicture();
            }
        });

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigationView4);


        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_Gal_surf:
                        GalleryOpen();
                        break;

                    case R.id.action_Histotry_surf:
                        startActivity(new Intent(CameraTestActivity.this, MainnnActivity.class));
                        break;

                    case R.id.action_settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        break;
                }
                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    private void GalleryOpen() {
        GalIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(GalIntent, "Select Image from Gallery"), SELECT_FILE);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        if (data != null) {
            try {
                Log.w("no", "Null");
                TextClass.sUri = data.getData();
                TextClass.sbitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                startCropper(SELECT_FILE, data);
                //CropImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//
//		ivImage.setImageBitmap(bitmap);
//        Intent intent = new Intent(getApplicationContext(), Ocr2.class);
//        startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
        }
    }

    private void startCropper(int requestCode, Intent data) {
        Uri uri = null;
        uri = TextClass.sUri;
        if (requestCode == REQUEST_CAMERA) {
            uri = TextClass.sUri;
        } else if (uri != null) {
            uri = TextClass.sUri;
        }
        Intent intent = new Intent(getApplicationContext(), CropActivity.class);
        intent.setData(uri);
        intent.putExtra("outputX", CropUtils.dip2px(this, 300));
        intent.putExtra("outputY", CropUtils.dip2px(this, 150));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivity(intent);

        Log.w(LOGTAG, "Uri Found starto");

    }


    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mOrientation = this.getResources().getConfiguration().orientation;

        mCamUtils.resetCamera();

        double ar = mCamUtils.getHighestAspectRatio();
        ScreenDimensions screen = getScreenDimensions(mOrientation, ar);

        calculateLayoutParams(screen);
        mCamUtils.setPreviewLayoutParams(fullScreenParams);

        mCamUtils.setFlashParams(mCamUtils.getFlashMode());

        mCamUtils.setCameraDisplayOrientation(this);

        ScreenDimensions fullscreen = getScreenDimensions(mOrientation, (double) 16 / (double) 9);
        LinearLayout.LayoutParams parentLayout = new LinearLayout.LayoutParams(fullscreen.getDisplayWidth(), fullscreen.getDisplayHeight());
        parentLayout.gravity = Gravity.CENTER;
        parentView.setLayoutParams(parentLayout);

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mOrientation = newConfig.orientation;

        double ar = mCamUtils.getHighestAspectRatio();
        ScreenDimensions screen = getScreenDimensions(mOrientation, ar);
        calculateLayoutParams(screen);
        mCamUtils.setPreviewLayoutParams(fullScreenParams);

        mCamUtils.setCameraDisplayOrientation(this);

        ScreenDimensions fullscreen = getScreenDimensions(mOrientation, (double) 16 / (double) 9);
        LinearLayout.LayoutParams parentLayout = new LinearLayout.LayoutParams(fullscreen.getDisplayWidth(), fullscreen.getDisplayHeight());
        parentLayout.gravity = Gravity.CENTER;
        parentView.setLayoutParams(parentLayout);

    }


    private void calculateLayoutParams(ScreenDimensions screen) {

        fullHeight = screen.getDisplayHeight();
        fullWidth = screen.getDisplayWidth();

        int left_margins = 0;
        int top_margins = 0;

        ScreenDimensions max = getScreenDimensions(screen.orientation, (double) 16 / (double) 9);

        if (screen.aspectratio < max.aspectratio) {
            if (Configuration.ORIENTATION_LANDSCAPE == screen.orientation) {
                fullHeight = max.getDisplayHeight();
                fullWidth = (int) (screen.aspectratio * (double) fullHeight);
                left_margins = max.getDisplayWidth() - fullWidth;

            } else if (Configuration.ORIENTATION_PORTRAIT == screen.orientation) {
                fullWidth = max.getDisplayWidth();
                fullHeight = (int) (screen.aspectratio * (double) fullWidth);
                top_margins = max.getDisplayHeight() - fullHeight;
            }
        }

        fullScreenParams = new RelativeLayout.LayoutParams(fullWidth, fullHeight);
        fullScreenParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        fullScreenParams.setMargins(left_margins, top_margins, 0, 0);

    }


    @Override
    public void onPause() {
        super.onPause();
        mCamUtils.releaseCamera();
    }


    protected ScreenDimensions getScreenDimensions(int orientation, double ar) {

        ScreenDimensions screen = null;

        Display display = getWindowManager().getDefaultDisplay();
        if (null != display) {
            screen = new ScreenDimensions();

            int mSurfaceHeight = 0;
            int mSurfaceWidth = 0;
            int mdisplayWidth = 0;
            int mdisplayHeight = 0;

            Point size = new Point();
            display.getSize(size);


            mdisplayWidth = size.x;
            mdisplayHeight = size.y;

            if (Configuration.ORIENTATION_LANDSCAPE == orientation) {

                if (mdisplayWidth < mdisplayHeight) {
                    int temp = mdisplayHeight;
                    mdisplayHeight = mdisplayWidth;
                    mdisplayWidth = temp;
                }
                   /*
                    * Surface view should be like this
				    * 		___________________________
				    * 	   |		    16			   |
				    * 	   |						   |
				    *      |						   | 9
				    *      |						   |
				    *      |						   |
				    *      |___________________________|
				    */

                if (ar <= ((double) mdisplayWidth) / ((double) mdisplayHeight)) {
                    mSurfaceWidth = mdisplayWidth;
                    mSurfaceHeight = (int) ((double) mSurfaceWidth / ar);
                } else {
                    mSurfaceHeight = mdisplayHeight;
                    mSurfaceWidth = (int) ((double) mSurfaceHeight * ar);
                }

                screen.setDisplayHeight(mSurfaceHeight);
                screen.setDisplayWidth(mSurfaceWidth);
                screen.setOrientation(Configuration.ORIENTATION_LANDSCAPE);
            } else {

                if (mdisplayWidth > mdisplayHeight) {
                    int temp = mdisplayHeight;
                    mdisplayHeight = mdisplayWidth;
                    mdisplayWidth = temp;
                }

				   /*
                    * Surface view should be like this
				    * 		_______________
				    * 	   |		9  	   |
				    * 	   |			   |
				    *      |			   | 
				    *      |			   |
				    *      |			   |
				    *      |			   | 16
				    *      |			   |
				    *      |			   |
				    *      |			   |
				    *      |			   |
				    *      |_______________|
				    */

                if (ar >= ((double) mdisplayHeight / (double) mdisplayWidth)) {
                    mSurfaceWidth = mdisplayWidth;
                    mSurfaceHeight = (int) ((double) mSurfaceWidth * ar);
                } else {
                    mSurfaceHeight = mdisplayHeight;
                    mSurfaceWidth = (int) ((double) mSurfaceHeight / ar);
                }

                screen.setDisplayHeight(mSurfaceHeight);
                screen.setDisplayWidth(mSurfaceWidth);
                screen.setOrientation(Configuration.ORIENTATION_PORTRAIT);
            }
        }

        screen.aspectratio = ar;

        Log.i(LOGTAG, "screen aspect ratio h = " + screen.getDisplayHeight());
        Log.i(LOGTAG, "screen aspect ratio w = " + screen.getDisplayWidth());

        return screen;
    }


}


