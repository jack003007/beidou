package com.libs.view.loadingView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by ty on 2016/10/22.
 */

public class CircleZoom extends View {
    private Paint mPaint;

    private float mWidth = 0f;
    private float mHigh = 0f;


    private float mMaxRadius = 8;
    private int circleCount = 5;
    private float mAnimatedValue = 1.0f;
    private int mJumpValue = 0;


    public CircleZoom(Context context) {
        this(context, null);
    }

    public CircleZoom(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleZoom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#2E3741"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //wrap_content OR match_content
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (wMode == MeasureSpec.EXACTLY) {//like match_content
//            mWidth = widthSize;
        } else {
            mWidth = widthSize / 2;
        }
        if (hMode == MeasureSpec.EXACTLY) {
//            mHigh = heightSize;
        } else {
            mHigh = mWidth / 2;
        }
        setMeasuredDimension((int) mWidth, (int) mHigh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float mCircleX = mWidth / circleCount;//第一个圆心的位置

        if ((mCircleX - mMaxRadius) < 0) {

        }
        for (int i = 0; i < circleCount; i++) {
            if (i == mJumpValue % circleCount) {
                canvas.drawCircle((i + 1) * mCircleX - mCircleX / 2,
                        mHigh / 2,
                        mMaxRadius * mAnimatedValue, mPaint);
            } else {
                canvas.drawCircle((i + 1) * mCircleX - mCircleX / 2,
                        mHigh / 2,
                        mMaxRadius, mPaint);
            }
        }
    }

    public void startAnim() {
        stopAnim();
        startViewAnim(0f, 1f, 500);
    }

    public void stopAnim() {
        if (valueAnimator != null) {
            clearAnimation();
            mAnimatedValue = 0f;
            mJumpValue = 0;
            valueAnimator.setRepeatCount(0);
            valueAnimator.cancel();
            valueAnimator.end();
        }
    }

    ValueAnimator valueAnimator = null;

    private ValueAnimator startViewAnim(float startF, final float endF, long time) {
        valueAnimator = ValueAnimator.ofFloat(startF, endF);
        valueAnimator.setDuration(time);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                mAnimatedValue = (float) valueAnimator.getAnimatedValue();

                if (mAnimatedValue < 0.2) {
                    mAnimatedValue = 0.2f;
                }
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                mJumpValue++;
            }
        });
        if (!valueAnimator.isRunning()) {
            valueAnimator.start();
        }

        return valueAnimator;
    }
}
