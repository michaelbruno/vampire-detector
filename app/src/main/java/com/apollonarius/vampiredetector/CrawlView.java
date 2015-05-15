package com.apollonarius.vampiredetector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

/**
 *
 * @author Michael Bruno
 *
 */

public class CrawlView extends TextView {

    public CrawlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CrawlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public CrawlView(Context context) {
        super(context);
        init();
    }

    // do this in xml?
    private void init() {

        //ApplicationControl applicationControl = ApplicationControl.getInstance();
        //setTypeface(applicationControl.typeface);

        Typeface ttf = Typeface.createFromAsset(getContext().getAssets(), "enochian-v7.ttf");
        setTypeface(ttf);
        Log.d(getClass().getName(),"font loaded:"+ttf.toString());


       // setMarqueeRepeatLimit(1);
        setMaxLines(1);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
       // setSelected(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (getHeight() * 0.80));
        int padding = (int)(getHeight()*0.15);
        setPadding(padding,padding,padding,0);
        super.onDraw(canvas);
    }

}
