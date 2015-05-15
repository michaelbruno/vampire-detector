package com.apollonarius.vampiredetector;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 *
 * @author Michael Bruno
 *
 */

public class VisionView extends SurfaceView implements SurfaceHolder.Callback{

	
	public VisionView(Context context) {
		super(context);
        init();

	}
	
	public VisionView(Context context, AttributeSet set) {

		super(context,set);
        init();

	}

    private void init(){
        getHolder().addCallback(this);
        setKeepScreenOn(true);
    }

    @Override
	public void draw(Canvas c){


	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {


	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

}
