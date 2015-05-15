package com.apollonarius.vampiredetector;

import android.content.res.Resources;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.util.Log;
import java.util.List;

/**
 *
 * @author Michael Bruno
 *
 */

public class ApplicationControl {

    private static ApplicationControl instance=null;
	public int[] pixels;
    public int[] detectionPixels;
    public int cwidth;
    public int cheight;
    public int swidth;
    public int sheight;
    public int colorMode = 1;
    public boolean effectMode = false;
    public int displayState = 0;
    public int detectionState = 0;
    public boolean lightOn = false;
    public boolean lightAvailable = false;
    public int bufferSize;
    public float dx = 0;
    public float dy = 0;
    public float dist = 0;
    public boolean detected = false;
    public static final float FDCLIP_RIGHT = 0.4f;
    public static final float FDCLIP_LEFT = 0.25f;
    public static final float FDCLIP_TOP = 0.20f;
    public static final float FDCLIP_BOTTOM = 0.20f;
    public final int[] bounds = new int[4];
    public final Object pixelLock = new Object();

    private ApplicationControl(){

    }

    private void init(){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        swidth = metrics.widthPixels;
        sheight = metrics.heightPixels;

        float aspect = (float)swidth/(float)sheight;

        Log.d(getClass().getName(), "Screen aspect is " + aspect + ", with:"
                + swidth + " x height:" + sheight);

        Camera camera = Camera.open();
        Camera.Parameters params = camera.getParameters();



        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        Camera.Size previewSize = null;

        List<Integer> formats = params.getSupportedPreviewFormats();
        for(Integer f:formats){
            Log.d(getClass().getName(), "Prewview Format:"+f+"");
        }

        for(int k=0;k<sizes.size();k++){

            float ap = (float)sizes.get(k).width/(float)sizes.get(k).height;
            Log.d(getClass().getName(),  + sizes.get(k).width + "x" + sizes.get(k).height + ":" + ap);
            if(swidth > sizes.get(k).width
                    && sheight > sizes.get(k).height
                    && ap>aspect){

                previewSize = sizes.get(k);
                break;
            }
        }

        if(previewSize==null){
            previewSize=sizes.get(0);
        }

        cwidth = previewSize.width;
        cheight = previewSize.height;

        Log.d(getClass().getSimpleName(), "Camera preview set to "+cwidth +"x"+cheight);


        int previewFormat = ImageFormat.getBitsPerPixel(camera.getParameters().getPreviewFormat());
        int frameSize = cwidth * cheight;
        pixels = new int[frameSize];
        bounds[0] = (int)(cwidth*FDCLIP_LEFT);
        bounds[1] = (int)(cheight*FDCLIP_TOP);
        bounds[2] = (int)(cwidth*(1 - FDCLIP_RIGHT));
        bounds[3] = (int)(cheight*(1 - FDCLIP_BOTTOM));

        Log.d(getClass().getSimpleName(),"Detection bounds: " +bounds[0] +"," +bounds[1] +
        " " +bounds[2]+","+bounds[3]);
        detectionPixels = new int[(bounds[2] - bounds[0]) * (bounds[3] - bounds[1])];
        bufferSize = ((frameSize * previewFormat)/8);

        camera.release();

    }

    public static ApplicationControl getInstance(){
        if(instance==null){
            instance = new ApplicationControl();
            instance.init();
        }
        return instance;
    }
}
