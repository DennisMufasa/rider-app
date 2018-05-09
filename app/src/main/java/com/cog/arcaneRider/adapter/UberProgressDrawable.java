package com.cog.arcaneRider.adapter;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.widget.SeekBar;

/**
 * Created by test on 2/7/17.
 */

public class UberProgressDrawable extends Drawable {

    private static final String TAG = "CustomDrawable";
    private final SeekBar mySlider;
    private final Drawable myBase;
    private final int myDots;
    private Paint unSelectLinePaint;

    public UberProgressDrawable(Drawable base, SeekBar slider, int dots, int color) {
        mySlider = slider;
        myBase = base;
        myDots = dots;

        unSelectLinePaint = new Paint();
        unSelectLinePaint.setColor(color);
        unSelectLinePaint.setStrokeWidth(toPix(3));
    }

    @Override
    public final void draw(Canvas canvas) {
        float height = toPix(20) / 2;
        float width = getBounds().width();
        float radius = toPix((int) height / 2);
        float mY = 45;
        height = 45;
        //        start x1,y1      end x2,y2
        canvas.drawLine(0, height, width, height, unSelectLinePaint);
        float mX = 0;
        canvas.drawCircle(5, mY, 13, unSelectLinePaint); // First Circle
        canvas.drawCircle((mX+width)/2, mY, 13, unSelectLinePaint); // Mid Circle
        canvas.drawCircle(width-5, mY, 13, unSelectLinePaint); // Last circle

        /*For Four circles*/
        /*for (int i = 0; i < 1; i++) {
            mX = mX + width / 3;
            canvas.drawCircle(mX, mY, radius, unSelectLinePaint);
        }*/
    }

    private float toPix(int size) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, mySlider.getContext().getResources().getDisplayMetrics());
    }


    @Override
    public final int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }
}
