package com.apollonarius.vampiredetector;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.util.Random;

/**
 * @author Michael Bruno
 */


public class VisionActivity extends Activity implements SensorEventListener, View.OnClickListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ImageThread imageThread;
    private DetectionThread detectionThread;
    private ImageProcessor processor;
    private final float[] currentVector = {0, 0, 0};
    private final float[] lastVector = {0, 0, 0};
    private final float[] diffVector = {0, 0, 0};
    private static final float sensor_diff = 0.5f;
    private Random random;
    private ToggleButton[] buttons;
    private VisionView visionView;
    private ApplicationControl ac;
    private long lastChange = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Maybe use a GLSurfaceView in the future?

        ac = ApplicationControl.getInstance();

        setContentView(R.layout.vision);

        processor = new ImageProcessor(this);
        visionView = (VisionView) findViewById(R.id.camera_view);
        //detectionView = (VisionView)findViewById(R.id.detection_view);
        addContentView(processor, new LayoutParams(1, 1));


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        random = new Random();
        alterText(0);
        alterText(1);
        alterText(2);

        if (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH)) {
            ac.lightAvailable = true;
        }

        //set up click listeners, xml config doesn't like this

        buttons = new ToggleButton[3];
        buttons[0] = (ToggleButton) findViewById(R.id.button_a);
        buttons[1] = (ToggleButton) findViewById(R.id.button_b);
        buttons[2] = (ToggleButton) findViewById(R.id.button_c);

        for (ToggleButton b : buttons) {
            b.setOnClickListener(this);
        }

        resizeButtons();

    }

    private void teardown() {
        sensorManager.unregisterListener(this);
        imageThread.setRunning(false);
        detectionThread.setRunning(false);
        imageThread.interrupt();
        detectionThread.interrupt();
        processor.releaseCamera();
        ac.effectMode=false;
        ac.lightOn=false;
        ac.colorMode=1;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            currentVector[0] = event.values[0];
            currentVector[1] = event.values[1];
            currentVector[2] = event.values[2];

            for (int i = 0; i < 3; i++) {

                diffVector[i] = currentVector[i] - lastVector[i];

                if (Math.abs(diffVector[i]) > sensor_diff) {
                    if ((System.currentTimeMillis() - lastChange) > 500){
                        alterText(i);
                        lastChange = System.currentTimeMillis();
                    }
                }

                lastVector[i] = currentVector[i];
            }


        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        imageThread = new ImageThread(visionView);
        imageThread.start();

        detectionThread = new DetectionThread();
        detectionThread.start();

        if(Build.VERSION.SDK_INT>=11){
            sensorManager.registerListener(this, accelerometer, 1000000);
        }else {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        ac = ApplicationControl.getInstance();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //processor.releaseCamera();
        teardown();
    }

    private void alterText(int lexicon) {

        TextView tv;
        int k = random.nextInt(4);
        String[] lex;

        if (lexicon == 0) {
            tv = (CrawlView) findViewById(R.id.crawl_view_x);
            lex = getResources().getStringArray(R.array.lexicon_x);
            //}else if(lexicon==1){
            //tv= (CrawlView)findViewById(R.id.crawl_view_y);
            //lex = getResources().getStringArray(R.array.lexicon_y);
        } else {
            tv = (CrawlView) findViewById(R.id.crawl_view_z);
            lex = getResources().getStringArray(R.array.lexicon_z);
        }
        tv.setText(lex[k]);


    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_a:
                lightButtonClick(v);
                break;

            case R.id.button_b:
                colorModeClick(v);
                break;

            case R.id.button_c:
                effectModeClick(v);
                break;

            default:
                break;
        }
    }

    public void lightButtonClick(View v) {

        ac.lightOn = ((ToggleButton) v).isChecked();
        processor.resetCamera();

    }


    public void colorModeClick(View v) {

        boolean active = ((ToggleButton) v).isChecked();

        if (active) {
            ac.colorMode = 2;

        } else {
            ac.colorMode = 1;
        }
        processor.resetCamera();

    }

    public void effectModeClick(View v) {

        ac.effectMode = ((ToggleButton) v).isChecked();
        processor.resetCamera();

    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
        teardown();
    }

    private void resizeButtons() {

        final LinearLayout buttonPane = (LinearLayout) findViewById(R.id.button_pane);

        ViewTreeObserver vto = buttonPane.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = buttonPane.getViewTreeObserver();

                //int layoutWidth = buttonPane.getWidth();
                int layoutHeight = buttonPane.getHeight();

                int buttonSide = (int) (0.30 * layoutHeight);
                int margin = (int) (.04 * layoutHeight);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }

                boolean first = true;

                for (ToggleButton button : buttons) {

                    LinearLayout.LayoutParams buttonParams =
                            (LinearLayout.LayoutParams) button.getLayoutParams();
                    buttonParams.height = buttonSide;
                    buttonParams.width = buttonSide;


                    if (first) {
                        first = false;
                    } else {
                        buttonParams.setMargins(0, margin, 0, 0);
                    }
                    button.setLayoutParams(buttonParams);

                }
            }

        });

    }

}
