package com.rahulrvr.spotbikephl.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.rahulrvr.spotbikephl.R;

/**
 *
 *
 */
public class BikeIndicator extends View {

    Paint mPaint;
    RectF mRect;
    private float mBikePercentage = 50;
    private int mBikeAvaiable = 10;
    private int mFreeDocks = 10;
    private Typeface mTypeface;
    private float mCircleWidth = 50.0f;

    public BikeIndicator(Context context) {
        super(context, null, 0);
        initialize(context, null, 0);

    }

    public BikeIndicator(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
        initialize(context, attributeSet, 0);
    }


    public BikeIndicator(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        initialize(context, attributeSet, defStyle);
    }


    private void initialize(Context context, AttributeSet attributeSet, int defStyle) {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mCircleWidth);
        mTypeface = Typeface.createFromAsset(context.getAssets(),
                String.format("font/%s.ttf", context.getString(R.string.typeface_roboto_light)));

        mRect = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int x = getWidth();
        int y = getHeight();
        int radius = getHeight()/2 - (int)mCircleWidth;
        //Example values
        // mRect.set(getWidth()/2- radius, getHeight()/2 - radius, getWidth()/2 + radius, getHeight()/2 + radius);
        mRect.set(getWidth() / 2 - radius, getHeight() / 2 - radius, getWidth() / 2 + radius, getHeight() / 2 + radius);

        //canvas.drawCircle(x / 2, y / 2, radius, mPaint);

        float firstAngle = (mBikePercentage / 100.0f) * 360;
        mPaint.setColor(getContext().getResources().getColor(getArcColor(mBikePercentage)));
        canvas.drawArc(mRect, 90, firstAngle, false, mPaint);
        mPaint.setColor(getContext().getResources().getColor(R.color.secondary_text));
        canvas.drawArc(mRect, firstAngle + 90, 360 - firstAngle, false, mPaint);
//        mPaint.setColor(getContext().getResources().getColor(R.color.icons));
//        //canvas.drawLine(getWidth() / 2, getHeight() / 2 - radius, getWidth() / 2, getHeight() / 2 + radius, mPaint);
//        mPaint.setTextSize(50);
//        mPaint.setColor(getContext().getResources().getColor(R.color.green));
//        mPaint.setTypeface(mTypeface);
//        canvas.drawText(Integer.toString(mBikeAvaiable) + "Bikes", getWidth() / 4 + radius, getHeight() / 4, mPaint);
//        mPaint.setColor(getContext().getResources().getColor(R.color.yellow));
//        canvas.drawText(Integer.toString(mFreeDocks) + "Docks", getWidth() / 4 + radius, getHeight() / 2 + 50, mPaint);
    }

    public float getBikePercentage() {
        return mBikePercentage;
    }

    public void setBikePercentage(int bikePercentage) {
        this.mBikePercentage = bikePercentage;
        invalidate();
    }

    private int getArcColor(float percentage) {
        if (percentage > 40) {
            return R.color.green;
        } else if (percentage > 10) {
            return R.color.yellow;
        } else {
            return R.color.red;
        }
    }
}
