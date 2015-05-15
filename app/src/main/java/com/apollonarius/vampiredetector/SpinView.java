package com.apollonarius.vampiredetector;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;


/**
 *
 * @author Michael Bruno
 *
 */

public class SpinView extends ImageView {

    public SpinView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public SpinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public SpinView(Context context) {
        super(context);
        init(null);
    }

    // do this in xml?
    private void init(AttributeSet attrs) {
        Log.d(getClass().getName(), "init");

        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SpinView,
                0, 0);



        int direction = a.getInteger(R.styleable.SpinView_rotationDirection, 0);
        a.recycle();

        float rstart=0;
        float rend=359;

        if(direction==1){
            rstart=359;
            rend=0;
        }


        Animation anim = new RotateAnimation(rstart, rend, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        //anim.setDuration(64000);
        anim.setDuration(40000);
        anim.setRepeatCount(-1);
        anim.setInterpolator(new LinearInterpolator());
        startAnimation(anim);

    }
}
