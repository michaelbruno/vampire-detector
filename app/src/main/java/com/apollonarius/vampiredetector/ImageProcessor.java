package com.apollonarius.vampiredetector;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Build;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.content.Context;
import java.io.IOException;

/**
 *
 * @author Michael Bruno
 *
 */

public class ImageProcessor extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback {

	private byte[] buffer;
    private SurfaceHolder holder;
    private Camera camera;
    private Camera.Parameters params;
	private ApplicationControl ac;
    private int good=0;
    private int total=0;

	
	public ImageProcessor(Context context){
        super(context);

        ac = ApplicationControl.getInstance();

		buffer = new byte[ac.bufferSize];
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        Debuglog.d(this, "surfaceCreated");
        initCamera();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Debuglog.d(this,  "surfaceDestroyed");
        try {
            releaseCamera();
        } catch (Exception ex) {
            Debuglog.d(this, "failed release camera");
        }
        float fb = (float) good / (float) total;
        Debuglog.d(this, "Camera frames"+ good + "..." + total + " ratio:"+fb);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,int height){

    }

    public void releaseCamera(){
        if(camera!=null){
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera=null;
        }
    }

    private void initCamera(){
        try {

            if(camera==null) {
                camera = Camera.open();
                camera.addCallbackBuffer(buffer);
            }

            if(params==null) {
                params = camera.getParameters();

            }

            if(ac.lightOn && ac.lightAvailable){
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            }else{
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }

            params.setPreviewSize(ac.cwidth, ac.cheight);
            if (Build.VERSION.SDK_INT >= 14) {
                //if(params.isAutoExposureLockSupported()) {
                //    params.setAutoExposureLock(true);
                //}
                if (params.isAutoWhiteBalanceLockSupported()) {
                    params.setAutoWhiteBalanceLock(true);
                }
                //params.setExposureCompensation((int) (params.getMaxExposureCompensation() * .5));
            }

            camera.setParameters(params);
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallbackWithBuffer(this);
            camera.startPreview();


        } catch (IOException ex) {
            Debuglog.d(this, "failed to create surface");
        }
    }

    public void resetCamera(){
        if(ac.lightOn){
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        }else{
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }


        if(ac.effectMode){
            params.setColorEffect("negative");
        }else{
            params.setColorEffect("none");
        }

        try{

            camera.setParameters(params);
        }catch(Exception ex){
            Debuglog.d(this,"Camera service failure:"+ex.getMessage());
        }
    }

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {

       if(ac.displayState==1 || ac.displayState==0) {
           decodeCameraFrame(data);
           ac.displayState=2;
           synchronized(ac.pixelLock){
               ac.pixelLock.notifyAll();
           }
           good++;
       }
        total++;

       camera.addCallbackBuffer(buffer);

	}

    // Convert the YUV format signals android camera uses into something more
    // usable
    private void decodeCameraFrame(final byte[] yuv420sp) {

        int r;
        int g;
        int fp=0;
        int y1192;

        for (int j = 0, yp = 0; j < ac.cheight; j++) {
            //int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < ac.cwidth; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;

                y1192 = 1192 * y;

                if(ac.colorMode==1) {
                    r = (y1192);
                    if (r < 0) {
                        r = 0;
                    } else if (r > 262143) {
                        r = 262143;
                    }
                    ac.pixels[yp] =  0xff000000 | ((r << 6) & 0xff0000);
                }else if(ac.colorMode==2){
                    g = (y1192);
                    if (g < 0) {
                        g = 0;
                    } else if (g > 262143) {
                        g = 262143;
                    }
                    ac.pixels[yp] =  0xff000000 | ((g >> 2) & 0x00ff00);

                }
                // use only center area of camera frame, for face detection is most expensive
                if((j>=ac.bounds[1]) && (j<ac.bounds[3]) &&
                        (i>=ac.bounds[0]) && (i<ac.bounds[2])) {
                    ac.detectionPixels[fp] = ac.pixels[yp];
                    fp++;
                }
            }
        }

    }
	


}
