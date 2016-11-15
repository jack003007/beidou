package com.libs.view;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class LoadingButton extends View {

    /**
     * 圆环背景的画笔
     **/
    private Paint mRingBgPaint;
    /**
     * 圆环进度条的画笔
     **/
    private Paint mProgressPaint;
    /**
     * 圆形实心背景的画笔
     **/
    private Paint mCircleBgPaint;
    /**
     * View的宽度
     **/
    private int width;
    /**
     * View的高度，这里View应该是正方形，所以宽高是一样的
     **/
    private int height;
    /**
     * View的中心坐标x
     **/
    private int centerX;
    /**
     * View的中心坐标y
     **/
    private int centerY;
    /**
     * 是否为首次绘制
     **/
    private boolean isFirstDraw = true;

    /**
     * 是否处于完成状态
     **/
    private boolean isCompleted = true;
    /**
     * 用于展示icon
     **/
    private Drawable mStartDrawable;
    /**
     * 完成时的Drawable
     **/
    private Drawable mCompleteDrawable;
    /**
     * 展示icon的默认宽度
     **/
    private int mDrawableWidth;
    int radius;
    /**
     * 画圆弧的区域
     */
    RectF mRectF;
    /**
     * 旋转的起始角度
     */
    int arcStartAngle = -90;
    /**
     * 旋转弧度
     */
    float sweepAngle;
    /**
     * 默认旋转弧度
     */
    final float defaultSweepAngle = 20;
    /**
     * 目标弧度
     */
    float targetProgress = sweepAngle = defaultSweepAngle;
    /**
     * 终止弧度
     */
    float circleAngle = 360;
    /**
     * 是否旋转
     */
    boolean isRotate = false;

    /**
     * 旋转偏移角度
     */
    int degreeOffset = 0;

    /**
     * 旋转速度
     */
    int spreadRotate = 4;

    public LoadingButton(Context context) {
        this(context, null, 0);
    }

    public LoadingButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * 配置画笔
     */
    private class Builder {
        private Paint mPaint;
        //画笔颜色
        private int color = Color.parseColor("#FFFFFFFF");
        //画笔风格
        private Paint.Style style = Paint.Style.STROKE;
        //是否抗锯齿
        private boolean isAntiAlias = true;
        //
        private int strokeWidth = 10;

        public Builder(Paint paint) {
            this.mPaint = paint;
        }

        public Builder setStyle(Paint.Style style) {
            this.style = style;
            return this;
        }

        public Builder setAntiAlias(boolean antiAlias) {
            isAntiAlias = antiAlias;
            return this;
        }

        public Builder setStrokeWidth(int strokeWidth) {
            this.strokeWidth = strokeWidth;
            return this;
        }

        // 画笔颜色，必须传带alpha值的颜色值
        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        public Paint create() {
            mPaint.setColor(color);
            mPaint.setStyle(style);
            mPaint.setStrokeWidth(strokeWidth);
            mPaint.setAntiAlias(isAntiAlias);
            return mPaint;
        }
    }

    /**
     * 点击View开始动画
     */
    OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isCompleted) {
                return;
            } else {
                ObjectAnimator animX = ObjectAnimator.ofFloat(LoadingButton.this, "scaleX", 1f, 0f, 1f);
                animX.setDuration(500);
                ObjectAnimator animY = ObjectAnimator.ofFloat(LoadingButton.this, "scaleY", 1f, 0f, 1f);
                animY.setDuration(500);
                animY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if ((float) animation.getAnimatedValue() <= 0) {
                            //正式进入旋转阶段
                            isFirstDraw = false;
                            //开始旋转
                            isRotate = true;
                            invalidate();

                        }
                    }
                });
                AnimatorSet mASet = new AnimatorSet();
                mASet.play(animX).with(animY);
                mASet.start();
            }
        }
    };

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        setPaints();
        setDrawables();
        setOnClickListener(mOnClickListener);
    }

    private void setDrawables() {
        mStartDrawable = getResources().getDrawable(R.drawable.download_text);
        mCompleteDrawable = getResources().getDrawable(R.drawable.complete);
    }

    /**
     * 设置背景、圆环、进度条的画笔
     */
    private void setPaints() {
        mRingBgPaint = new Builder(new Paint())
                .setColor(getResources().getColor(R.color.ColorRing))
                .create();
        mProgressPaint = new Builder(new Paint())
                .setColor(getResources().getColor(R.color.ColorProgress))
                .create();

        mCircleBgPaint = new Builder(new Paint())
                .setStyle(Paint.Style.FILL)
                .setColor(getResources().getColor(R.color.ColorCircle))
                .create();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
        centerX = width / 2;
        centerY = height / 2;
        //设置图像居中
        mDrawableWidth = (int) (width * 0.5);//(int) Math.round(getWidth()/10.0);
        mStartDrawable.setBounds(0, 0, mDrawableWidth, mDrawableWidth);
        mCompleteDrawable.setBounds(0, 0, mDrawableWidth, mDrawableWidth);
        radius = centerX - 10;
        mRectF = new RectF(centerX - radius, centerX - radius, centerX + radius, centerX + radius);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean isKeepDraw = false;
        if (isFirstDraw) {
            //第一次绘制，显示起始图标
            canvas = drawCircle(canvas);//背景
            canvas = translateCanvasPoint2Center(canvas);
            mStartDrawable.draw(canvas);
        } else if (isRotate) {
            //开始旋转，一直绘制下去
            isKeepDraw = true;
            //将当前扫描弧度转为设置弧度
            if (sweepAngle != targetProgress) {
                sweepAngle = Math.min(sweepAngle + 2, targetProgress);
                if (sweepAngle == circleAngle) {
                    resetConfigurations();
                }
            }
            canvas = drawRing(canvas);
            drawProgress(canvas);
        } else if (isCompleted) {
            canvas = drawCircle(canvas);
            canvas = translateCanvasPoint2Center(canvas);
            mCompleteDrawable.draw(canvas);
        }
        if (isKeepDraw) {
            invalidate();
        }
    }

    /**
     * 画进度条
     *
     * @param canvas
     * @return
     */
    private Canvas drawProgress(Canvas canvas) {
        canvas.drawArc(mRectF, arcStartAngle + getArcStartOffset(), sweepAngle, false, mProgressPaint);
        return canvas;
    }

    /**
     * 画圆环
     *
     * @param canvas
     * @return
     */
    private Canvas drawRing(Canvas canvas) {
        canvas.drawCircle(centerX, centerX, radius, mRingBgPaint);
        return canvas;
    }

    /**
     * 将画布的位置移动到View的中心
     *
     * @param canvas
     * @return
     */
    Canvas translateCanvasPoint2Center(Canvas canvas) {
        canvas.translate(centerX - mDrawableWidth / 2, centerY - mDrawableWidth / 2);
        return canvas;
    }

    /**
     * 画背景圆
     *
     * @param canvas
     */
    private Canvas drawCircle(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, radius, mCircleBgPaint);
        return canvas;
    }

    Callback callback;

    public interface Callback {
        void complete();
    }

    private void resetConfigurations() {
        isRotate = false;
        isCompleted = true;
        sweepAngle = targetProgress = 20;
        invalidate();
        invokeComplete();
    }

    private void invokeComplete() {
        if (callback != null) {
            callback.complete();
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }


    private int getArcStartOffset() {
        return degreeOffset = degreeOffset >= 360 ? 0 : degreeOffset + spreadRotate;
    }

    public void setTargetProgress(float targetProgress) {
        this.targetProgress = targetProgress;
        invalidate();
    }


}
