package com.tinkersstudio.guessmyage;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    /*Generic font use for text*/
    Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/NotoMono-Regilar.ttf");
    Window w = getWindow();
    Resources resources = getResources();

    /*Debugging TAG. Use in log*/
    private static final String TAG = Activity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();
        initListener();
    }

    /**
     * Init the layout component. Don't set anything else in here
     */
    protected void initLayout() {
        //changeFrameLayout();
        setContentView(R.layout.activity_main);
    }

    protected void initListener() {
        //init all of the listener
    }

    /**
     * Method to modify the color of the frame
     */
    protected void changeFrameLayout() {
        //pass
        //w.setStatusBarColor(Color.parseColor("#4CAF50"));

    }
}
