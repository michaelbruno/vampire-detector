package com.apollonarius.vampiredetector;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;

/**
 *
 * @author Michael Bruno
 *
 */

public class DetectionThread extends Thread {

    private boolean running = false;
    private int detectionRun = 0;
    private int faceScore = 0;
    private float scaleFactor;
    private ApplicationControl ac;
    private long totaltime;

    public DetectionThread() {
        running = true;
        ac = ApplicationControl.getInstance();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        Debuglog.d(this, "starting run");
        Bitmap bufferBitmap = Bitmap.createBitmap(
                (ac.bounds[2] - ac.bounds[0]),
                (ac.bounds[3] - ac.bounds[1]),
                Bitmap.Config.RGB_565);


        FaceDetector faceDetector = new FaceDetector(
                (ac.bounds[2] - ac.bounds[0]),
                (ac.bounds[3] - ac.bounds[1]), 1);
        Face[] faces = new Face[1];

        scaleFactor = (float) ac.sheight / (float) ac.cheight;

        while (running) {

            synchronized(ac.pixelLock){
                try {
                    ac.pixelLock.wait();
                }catch(InterruptedException ex){
                    // nothing to do here i suppose
                }
            }

            bufferBitmap.setPixels(ac.detectionPixels, 0,
                    bufferBitmap.getWidth(),
                    0, 0, bufferBitmap.getWidth(), bufferBitmap.getHeight());

            long t1 = System.currentTimeMillis();

            if (faceDetector.findFaces(bufferBitmap, faces) > 0) {
                if (faceScore < 3) {
                    faceScore++;
                }
                //Log.d(getClass().getSimpleName(), "Face Found");
            } else {

                faceScore--;

                if (faceScore < 0) {
                    faceScore = 0;
                }
                //Log.d(getClass().getSimpleName(), "Face not found");
            }

            if (faceScore >= 3) {
                processFace(faces[0]);
                ac.detected = true;
            } else if (faceScore == 0) {
                ac.detected = false;
            }

            long t2 = System.currentTimeMillis();

            totaltime = totaltime + (t2 - t1);
            detectionRun++;
            //try {
            //    sleep(50);
            //} catch (InterruptedException ex) {
            //    Log.d(getClass().getSimpleName(), "Sleep interrupted");
           // }

            //applicationControl.detectionState = 1;
            //Log.d(getClass().getSimpleName(), "faceScore:" + faceScore);


        }

        Debuglog.d(this, "Detection run count:" + detectionRun);

        Debuglog.d(this, "avg time:" + (float)totaltime/(float)detectionRun);
    }


    private void processFace(Face face) {

        ac.dist = face.eyesDistance();
        PointF point = new PointF();
        face.getMidPoint(point);
        ac.dx = (point.x + ac.bounds[0]) * scaleFactor;
        ac.dy = (point.y + ac.bounds[1]) * scaleFactor;

        //Log.d(getClass().getSimpleName(), "processFace:" + point.x + ":" + ac.dx
        //        + "," + point.y + ":" + ac.dy);

    }

}
