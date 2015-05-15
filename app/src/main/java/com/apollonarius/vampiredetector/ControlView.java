package com.apollonarius.vampiredetector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 *
 * @author Michael Bruno
 *
 */

public class ControlView extends SurfaceView implements SurfaceHolder.Callback{


	public ControlView(Context context) {
		super(context);
	}
	
	public ControlView(Context context, AttributeSet set) {
		super(context,set);
		getHolder().addCallback(this);
		setKeepScreenOn(true);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

        // TODO Auto-generated method stub
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		
	}




}
