
package com.mediatek.factorymode;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class VUMeter extends View {
    private static final float PIVOT_RADIUS = 3.5f;
    private static final float PIVOT_Y_OFFSET = 10f;
    private static final float SHADOW_OFFSET = 2.0f;
    private static final float DROPOFF_STEP = 0.18f;
    private static final long ANIMATION_INTERVAL = 70;
    /** M: avoid magic number and unnecessary calculation @{ */
    private static final int COLOR_NUMBER = 60;
    private static final float MIN_ANGLE = (float) Math.PI / 8;
    private static final float MAX_ANGLE = (float) Math.PI * 7 / 8;
    private static final float BASE_NUMBER = 32768;
    /** @} */
    private Paint mPaint;
    private Paint mShadow;
    private Recorder mRecorder;
    /**
     * M: Use a variable to save Context, and avoid every calling will deliver a
     * Context parameter
     */
    private final Context mContext;

    public float mCurrentAngle;

    /**
     * constructor of VUMeter
     *
     * @param context
     *            the context that show VUMeter
     */
    public VUMeter(Context context) {
        super(context);
        mContext = context;
        init();
    }

    /**
     * constructor of VUMeter
     *
     * @param context
     *            the context that show VUMeter
     * @param attrs
     *            AttributeSet
     */
    public VUMeter(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    /**
     * set Recorder that bind with this VUMeter
     *
     * @param recorder
     *            Recorder to be set
     */
    public void setRecorder(Recorder recorder) {
        mRecorder = recorder;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final float minAngle = MIN_ANGLE;
        final float maxAngle = MAX_ANGLE;

        float angle = minAngle;
        if (null != mRecorder) {
            angle += (float) (maxAngle - minAngle) * mRecorder.getMaxAmplitude() / BASE_NUMBER;
        }

        if (angle > mCurrentAngle) {
            mCurrentAngle = angle;
        } else {
            mCurrentAngle = Math.max(angle, mCurrentAngle - DROPOFF_STEP);
        }

        mCurrentAngle = Math.min(maxAngle, mCurrentAngle);

        float w = getWidth();
        float h = getHeight();
        float pivotX = w / 2;
        float pivotY = h - PIVOT_RADIUS - PIVOT_Y_OFFSET;
        float l = h * 4 / 5;
        float sin = (float) Math.sin(mCurrentAngle);
        float cos = (float) Math.cos(mCurrentAngle);
        float x0 = pivotX - l * cos;
        float y0 = pivotY - l * sin;
        canvas.drawLine(x0 + SHADOW_OFFSET, y0 + SHADOW_OFFSET, pivotX + SHADOW_OFFSET, pivotY
                + SHADOW_OFFSET, mShadow);
        canvas.drawCircle(pivotX + SHADOW_OFFSET, pivotY + SHADOW_OFFSET, PIVOT_RADIUS, mShadow);
        canvas.drawLine(x0, y0, pivotX, pivotY, mPaint);
        canvas.drawCircle(pivotX, pivotY, PIVOT_RADIUS, mPaint);

        if ((null != mRecorder)
                && (Recorder.RECORDING_STATE == mRecorder.getCurrentState())) {
            postInvalidateDelayed(ANIMATION_INTERVAL);
        }
    }

    /**
     * initialize variable of VUMter
     */
    private void init() {
        Drawable background = mContext.getResources().getDrawable(R.drawable.vumeter);
        setBackgroundDrawable(background);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadow.setColor(Color.argb(COLOR_NUMBER, 0, 0, 0));
        mRecorder = null;
        mCurrentAngle = 0;
    }
}
