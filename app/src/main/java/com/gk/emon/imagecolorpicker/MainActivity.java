package com.gk.emon.imagecolorpicker;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Bitmap bitmap;
    public static String TAG = MainActivity.class.getSimpleName();
    private ImageView imgSource;
    private View vPreviewColor, llPicker;
    private SeekBar skbBorderRadiusChanger;
    private GradientDrawable gdImageSource;
    private final int defaultBorderWidth = 20;
    private int selectedBorderColor = Color.DKGRAY;
    private int selectedBorderWidth = defaultBorderWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setupImageSource();
        setupBorderChange();
    }

    private void initView() {
        imgSource = findViewById(R.id.img_nature);
        vPreviewColor = findViewById(R.id.v_preview);
        llPicker = findViewById(R.id.ll_picker);
        skbBorderRadiusChanger = findViewById(R.id.skb_border_radius_changer);

        // When the drawing cache is enabled, the next call to getDrawingCache() or buildDrawingCache()
        // will draw the view in a bitmap.
        imgSource.setDrawingCacheEnabled(true);
        imgSource.buildDrawingCache(true);
        skbBorderRadiusChanger.setDrawingCacheEnabled(true);
        skbBorderRadiusChanger.buildDrawingCache(true);

        gdImageSource = (GradientDrawable) imgSource.getBackground();
        gdImageSource.setStroke(selectedBorderWidth,selectedBorderColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            skbBorderRadiusChanger.setMin(defaultBorderWidth);
            skbBorderRadiusChanger.setProgress(defaultBorderWidth,false);
        }
    }

    private void setupBorderChange() {

        skbBorderRadiusChanger.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int radius, boolean b) {

                if (radius <= defaultBorderWidth)
                    radius = defaultBorderWidth;
                selectedBorderWidth = radius;

                gdImageSource.setStroke(selectedBorderWidth, selectedBorderColor);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * I'm suppressing "Android button has setOnTouchListener called on it but does not override
     * performClick" warning , as we are not supporting accessibility for the blind or visually
     * impaired people (Ref - https://stackoverflow.com/a/50345118/7200133 )
     * <p>
     * My note -
     * <p>
     * "Difference between MotionEvent.getRawX and MotionEvent.getX "
     * MotionEvent will sometimes return absolute X and Y coordinates relative to the view, and sometimes relative coordinates to the previous motion event.
     * getRawX() and getRawY() that is guaranteed to return absolute coordinates, relative to the device screen.
     * While getX() and getY(), should return you coordinates, relative to the View, that dispatched them.
     * (Ref - https://stackoverflow.com/a/20636236/7200133 )
     * <p>
     * "GradientDrawable"
     * A Drawable with a color gradient for buttons, backgrounds, etc.
     * It can be defined in an XML file with the <shape> element.
     */
    @SuppressLint("ClickableViewAccessibility")

    private void setupImageSource() {
        imgSource.setOnTouchListener((v, event) -> {
            try {
                if (event.getAction() == MotionEvent.ACTION_DOWN ||
                        event.getAction() == MotionEvent.ACTION_MOVE) {

                    llPicker.setVisibility(View.VISIBLE);
                    bitmap = imgSource.getDrawingCache();
                    // get touched pixel
                    int pixel = bitmap.getPixel((int) event.getX(), (int) event.getY());
                    // get RGB values from the touched pixel
                    int r = Color.red(pixel);
                    int g = Color.green(pixel);
                    int b = Color.blue(pixel);
                    selectedBorderColor = Color.rgb(r, g, b);
                    Log.d(TAG, "Color : " + Integer.toHexString(pixel));

                    //Change the color of the floating preview rectangle
                    GradientDrawable drawable = (GradientDrawable) vPreviewColor.getBackground();
                    drawable.setColor(selectedBorderColor);

                    //This is for moving the picker icon
                    llPicker.animate()
                            .x(event.getX())
                            .y(event.getY())
                            .setDuration(0)
                            .start();


                    //Change the color of the seekbar thumb
                    gdImageSource.setStroke(selectedBorderWidth, selectedBorderColor);


                } else {
                    llPicker.setVisibility(View.GONE);
                }
            } catch (Exception exception) {
                Log.d(TAG, "Error in color picking : " + exception.getMessage());
            }

            return true;
        });
    }
}
