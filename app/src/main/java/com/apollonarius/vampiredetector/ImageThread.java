package com.apollonarius.vampiredetector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.SurfaceHolder;

/**
 * @author Michael Bruno
 */

public class ImageThread extends Thread {

    private VisionView vv;
    private boolean running = false;
    private SurfaceHolder holder;
    private ApplicationControl ac;
    private final float focusRate = 5;

    public ImageThread(VisionView view) {
        vv = view;
        holder = view.getHolder();
        running = true;
        ac = ApplicationControl.getInstance();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }


    @Override
    public void run() {

        running = true;

        Canvas c;
        Bitmap bufferBitmap = Bitmap.createBitmap(ac.cwidth,
                ac.cheight, Bitmap.Config.RGB_565);

        float lastx = ac.swidth/2;
        float lasty = ac.sheight/2;
        float curx = 0;
        float cury = 0;

        Paint facePaint = new Paint();
        Paint textPaint = new Paint();
        facePaint.setStyle(Paint.Style.STROKE);
        facePaint.setStrokeWidth(5);
        facePaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.createFromAsset(vv.getContext().getAssets(), "enochian-v7.ttf"));
        textPaint.setColor(Color.WHITE);
        //textPaint.setTextSize(100);

        float multi = (float) ac.sheight / (float) ac.cheight;

        Debuglog.d(this, ac.sheight + "..." + ac.cheight + "..." + multi);

        Rect rect = new Rect(0, 0, (int)(multi * ac.cwidth), ac.sheight);

        while (running) {

            // Don't try to display camera frame whilst its being transformed
            synchronized (ac.pixelLock) {
                try {
                    ac.pixelLock.wait();
                } catch (InterruptedException ex) {
                    // nothing to do here i suppose
                }
            }

            c = holder.lockCanvas(null);

            if (c != null) {

                bufferBitmap.setPixels(ac.pixels, 0, bufferBitmap.getWidth(),
                        0, 0, bufferBitmap.getWidth(), bufferBitmap.getHeight());

                c.drawBitmap(bufferBitmap, null, rect, null);
                vv.draw(c);

                if (ac.detectionState < 2) {
                    ac.detectionState = 2;
                }

                if (ac.detected) {
                    // try to smooth detection a little bit
                    //curx = ((ac.dx - lastx) * (-.2f)) + ac.dx;
                    //cury = ((ac.dy - lasty) * (-.2f)) + ac.dy;
                    if((ac.dx - lastx)<=-focusRate){
                        curx = lastx - focusRate;
                    }else if(ac.dx - lastx>=focusRate){
                        curx = lastx + focusRate;
                    }else{
                        curx = ac.dx;
                    }

                    if((ac.dy - lasty)<=-focusRate){
                        cury = lasty - focusRate;
                    }else if(ac.dy - lasty>=focusRate){
                        cury = lasty + focusRate;
                    }else{
                        cury = ac.dy;
                    }

                    c.drawCircle(curx, cury, 5.0f, facePaint);
                    c.drawCircle(curx, cury,
                            (ac.dist * 3), facePaint);

                    textPaint.setTextSize(ac.dist);
                    if(Math.abs(ac.dx - curx)<(2*focusRate) &&
                            Math.abs(ac.dy - cury)<(2*focusRate)) {
                        // because there is no such thing as vampires... right?
                        c.drawText("HUMAN", (curx - ac.dist), (cury + (ac.dist * 4)), textPaint);
                    }
                    lastx = curx;
                    lasty = cury;
                }

                holder.unlockCanvasAndPost(c);

            }
            ac.displayState = 1;

        }

    }

}
