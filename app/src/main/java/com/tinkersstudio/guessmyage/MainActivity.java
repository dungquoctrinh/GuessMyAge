package com.tinkersstudio.guessmyage;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.DetectedFaces;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.Face;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualRecognitionOptions;
import android.widget.TextView;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    //Window w = getWindow();

    /*Debugging TAG. Use in log*/
    private static final String TAG = Activity.class.getName();

    /**UI Component*/
    //Image Component
    protected ImageView avatarImage;
    protected ImageButton shareButton;

    //AgeBox Component
    protected TextView labelMinAge, labelMaxAge, labelGender, labelAge;
    protected TextView t_minAge, t_maxAge, t_age, t_gender;

    //Button Group
    protected Button cameraButton, analyzeButton;

    private CameraHelper cameraHelper;
    private GalleryHelper galleryHelper;
    private VisualRecognition visualService;

    boolean checkFlag = false;
    int resultCode;

    //Progress Bar
    //Ad Window
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initLayout();
        initListener();
        this.cameraHelper = new CameraHelper(this);
        this.galleryHelper = new GalleryHelper(this);
        visualService = initVisualRecognitionService();
    }

    /**
     * Init the layout component. Don't set anything else in here
     */
    protected void initLayout() {
        //changeFrameLayout();
        setContentView(R.layout.activity_main);

        this.avatarImage = (ImageView) findViewById(R.id.image_view);
        this.shareButton = (ImageButton) findViewById(R.id.share_button);

        this.analyzeButton = (Button) findViewById(R.id.analyze_button);
        this.cameraButton = (Button) findViewById(R.id.camera_button);

        this.labelAge = (TextView) findViewById(R.id.label_estimate_age);
        this.labelGender = (TextView) findViewById(R.id.label_gender);
        this.labelMaxAge = (TextView) findViewById(R.id.label_age_max);
        this.labelMinAge = (TextView) findViewById(R.id.label_min_age);

        this.t_age = (TextView) findViewById(R.id.age_text_view);
        this.t_maxAge = (TextView) findViewById(R.id.max_age_text_view);
        this.t_minAge = (TextView) findViewById(R.id.min_age_text_view);
        this.t_gender = (TextView) findViewById(R.id.gender_text_view);

        /**Set font*/
        setFont();
    }


    protected void initListener() {
        //init all of the listener
        this.analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                if (checkFlag == true) {
                    new VisualTask().execute(resultCode);
                }
            }
        });

        //trigger camera
        this.cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraHelper.dispatchTakePictureIntent();
            }
        });
    }

    private VisualRecognition initVisualRecognitionService() {
        return new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20,
                getString(R.string.visual_recognition_api_key));
    }

    private class VisualTask extends AsyncTask<Integer, Void, String> {
        @Override protected String doInBackground(Integer... integers) {
            VisualRecognitionOptions options = new VisualRecognitionOptions.Builder()
                    .images(cameraHelper.getFile(integers[0]))
                    .build();

            DetectedFaces faces = visualService.detectFaces(options).execute();
            Random rn = new Random();
            if (!faces.getImages().get(0).getFaces().isEmpty()) {
                Face face = faces.getImages().get(0).getFaces().get(0);
                Face.Age age = faces.getImages().get(0).getFaces().get(0).getAge();
                showAge(Integer.toString(rn.nextInt((age.getMax()+1) - age.getMin()) + age.getMin()));
                showAgeMin(Integer.toString(age.getMin()));
                showAgeMax(Integer.toString(age.getMax()));
                showGender(face.getGender().getGender());
            }

            return "Did visual";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CameraHelper.REQUEST_IMAGE_CAPTURE) {
            avatarImage.setImageBitmap(cameraHelper.getBitmap(resultCode));
            //code is execute in here
            checkFlag = true;
            this.resultCode = resultCode;

        }

        if (requestCode == GalleryHelper.PICK_IMAGE_REQUEST) {
            avatarImage.setImageBitmap(galleryHelper.getBitmap(resultCode, data));
        }
    }


    //----------UI Method -------
    /**
     * Set all the font to the custom
     */
    private void setFont() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                labelMinAge.setTypeface(EasyFonts.robotoMedium(MainActivity.this));
                labelGender.setTypeface(EasyFonts.robotoMedium(MainActivity.this));
                labelAge.setTypeface(EasyFonts.robotoMedium(MainActivity.this));
                labelMaxAge.setTypeface(EasyFonts.robotoMedium(MainActivity.this));

                //set the font
                t_gender.setTypeface(EasyFonts.robotoMedium(MainActivity.this));
                t_maxAge.setTypeface(EasyFonts.robotoMedium(MainActivity.this));
                t_minAge.setTypeface(EasyFonts.robotoMedium(MainActivity.this));
                t_age.setTypeface(EasyFonts.robotoMedium(MainActivity.this));
            }
        });
    }

    /**
     * Set the max age
     * @param ageMax
     */
    private void showAgeMax(final String ageMax) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                t_maxAge.setText(ageMax);
            }
        });
    }

    /**
     * Set the min age
     * @param ageMin
     */
    private void showAgeMin(final String ageMin) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                t_minAge.setText(ageMin);
            }
        });
    }

    /**
     * Show the age
     * @param age
     */
    private void showAge(final String age) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                t_age.setText(age);
            }
        });
    }

    private void showGender(final String gender) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                t_gender.setText(gender);
            }
        });
    }
    /**
     * Method to modify the color of the frame
     */
    protected void changeFrameLayout() {
        //pass
        //w.setStatusBarColor(Color.parseColor("#4CAF50"));
    }


}
