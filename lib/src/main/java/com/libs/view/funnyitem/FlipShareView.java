package com.libs.view.funnyitem;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import com.libs.view.funnyitem.actmode.ItemVertical;
import com.libs.view.funnyitem.interfaces.ItemBehaviorListener;
import com.libs.view.utils.BarUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * 作者： 巴掌 on 16/9/1 21:38
 * Github: https://github.com/JeasonWong
 */
public class FlipShareView extends View {

    private static final int STATUS_NONE = 0;
    private static final int STATUS_DOWN = 1;
    private static final int STATUS_UP = 2;

    public static final int TYPE_VERTICLE = 0;
    public static final int TYPE_HORIZONTAL = 1;
    public static final int TYPE_SLIDE = 1 << 1;

    private int mStatus = STATUS_NONE;

    private Paint mPaint;
    private Path mPath;
    private Camera mCamera;
    private Matrix mMatrix;
    //item's flip degree
    private float mDegree;
    //item's shake degree
    private float mItemShakeDegree;
    //current item which is showing
    private int mCurrentItem = 0;
    //everyItem's animation duration
    private int mDuration = 300;
    //total window view's background color
    private int mBackgroundColor = 0x00FFFFFF;
    //separate line's color分隔线颜色
    private int mSeparateLineColor = Color.TRANSPARENT;
    //separate line's height分隔线高度
    private int mSeparateLineHeight = dip2px(0.4f);
    //background color area
    private RectF mBgRect;
    //shareItem's width  //消息框宽度
    private int mItemWidth = dip2px(140);
    //shareItem's height //消息框高度
    private int mItemHeight = dip2px(50);
    //shareItem's left x position//消息框左侧x坐标
    private int mItemLeft = dip2px(0);
    //shareItem's top y position消息框顶部y坐标
    private int mItemTop = dip2px(50);
    //item's corner
    private int mCorner = dip2px(6);
    //triangle's height
    private int mTriangleHeight = dip2px(4);
    //border's margin默认距屏幕的边距
    private int mBorderMargin = dip2px(5);
    //the first shown item's top y coordinate
    private int mFirstItemTop;
    //    //screen width
//    private int mScreenWidth;
//    //screen height
//    private int mScreenHeight;
    //parent view is used to position
//    private View mParentView;
    //shareItem lists
    private List<ShareItem> mItemList = new ArrayList<>();
    private List<RectF> mItemRectList = new ArrayList<>();

    private int mAnimType = TYPE_VERTICLE;
    private final List<AnimatorSet> mTotalAnimList = new ArrayList<>();
    private ObjectAnimator mAlphaAnim;

    private OnFlipClickListener onFlipClickListener;

    private ItemBehaviorListener mItemBehavior;
    /**
     * Target中心X坐标
     */
    private int pMiddleX;

    public ItemBehaviorListener getmItemBehavior() {
        return mItemBehavior;
    }

    public void setmItemBehavior(ItemBehaviorListener mItemBehavior) {
        this.mItemBehavior = mItemBehavior;
    }

    public FlipShareView(Context context, Window window, View parentView) {
        super(context);
        float mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        float mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        mBgRect = new RectF(0, 0, mScreenWidth, mScreenHeight);
        setWindow(window);
        initView();
        beforeDraw(parentView);
        mItemBehavior = new ItemVertical();
    }

    private void setWindow(Window window) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.addContentView(this, params);
    }

    private void initAlphaAnim() {
        mAlphaAnim = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0f);//透明背景关闭动画
        mAlphaAnim.setDuration(200);
        mAlphaAnim.addListener(new MyAnimListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeView();
                if (onFlipClickListener != null) {
                    onFlipClickListener.dismiss();
                }
            }
        });
    }


    private void initView() {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(sp2px(14));
        mCamera = new Camera();
        mMatrix = new Matrix();
        initAlphaAnim();
    }

    private void beforeDraw(View target) {
        //点击触发动画的View的参数，均为像素值
        int pWidth = target.getMeasuredWidth();
        int pHeight = target.getMeasuredHeight();
        int pLeft = target.getLeft();
        int[] location = new int[2];
        //这两种方法都包含状态栏的高度
        target.getLocationInWindow(location);
//        mParentView.getLocationOnScreen(location);
        //状态栏的高度
        int statusBarHight = BarUtils.getStatusBarHeight(getContext());
        Logger.d("statusBarHight:" + statusBarHight);

        location[1] -= statusBarHight;
        Logger.d("Location x: " + location[0] + "  y: " + location[1]
                + "parent W: " + pWidth + " H: " + pHeight);

        int pTop = location[1];
        int pBottom = location[1] + pHeight;
        pMiddleX = pLeft + pWidth / 2;
        Logger.d(
                "getWidth:" + getWidth() +
                        "getHeight:" + getHeight() +
                        "getLeft:" + getLeft() +
                        "getTop:" + getTop()
        );
        // 三角形角标高度 px值
        int mTriangleHeightPx = dip2px(mTriangleHeight);
        if (pTop < getResources().getDisplayMetrics().heightPixels / 2) {
            //目标View顶部在屏幕的上半部分
            mStatus = STATUS_DOWN;
            mItemTop = pBottom + mTriangleHeightPx;
            mFirstItemTop = mItemTop + mTriangleHeightPx;
        } else {
            mStatus = STATUS_UP;
            mItemTop = pTop - mTriangleHeightPx;
            mFirstItemTop = mItemTop - mTriangleHeightPx;
        }
        mItemLeft = getItemXLeft(pMiddleX);
        Logger.d("ItemTop: " + mItemTop + "  mFirstItemTop: " + mFirstItemTop + " mItemLeft: " + mItemLeft);
    }

    /**
     * 获取Item左侧x坐标
     *
     * @param pMiddleX
     * @return
     */
    private int getItemXLeft(int pMiddleX) {
        int result;
        int mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        if (pMiddleX + mItemWidth / 2 > mScreenWidth) {
            //分享框右侧超出屏幕
            result = mScreenWidth - mItemWidth - mBorderMargin;
        } else if (pMiddleX - mItemWidth / 2 < 0) {
            //分享框左侧超出屏幕
            result = mBorderMargin;
        } else {
            result = pMiddleX - mItemWidth / 2;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        switch (mStatus) {
            case STATUS_NONE:

                break;
            default:
                drawFlipItem(canvas);
                break;
        }

    }

    /**
     * 绘制透明背景
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        mPaint.setColor(mBackgroundColor);
        canvas.drawRect(mBgRect, mPaint);
    }


    /**
     * 下拉式
     *
     * @param canvas
     */
    private void drawFlipItem(Canvas canvas) {

        mItemRectList.clear();
        for (int i = 0; i < mItemList.size(); i++) {
            canvas.save();
            mCamera.save();
            if (i <= mCurrentItem) {
                if (i == mCurrentItem) {
                    //当前框旋转
                    mCamera = mItemBehavior.rotate(mCamera, mDegree);
                    Logger.d("mDegree:" + mDegree + "  mMatrix:" + mMatrix.toString());
                } else {
                    //其余框抖动
//                    mCamera = mItemBehavior.shake(mCamera);
                }
                mCamera.getMatrix(mMatrix);
//                if (mAnimType == TYPE_SLIDE) {
//                    mMatrix.preTranslate(-(mItemLeft + mItemWidth / 2), -mItemHeight / 2);
//                    mMatrix.postTranslate(mItemLeft + mItemWidth / 2, mItemHeight / 2);
//                } else {
                //Camera中心点（0，0），将目标中心点移到（0，0）再回来。这里的y值为每一项的顶部坐标
                float mCenterX = mItemLeft + mItemWidth / 2;
                float mCenterY = getRotateAxis(i);
                mMatrix.preTranslate(-mCenterX, -mCenterY);
                mMatrix.postTranslate(mCenterX, mCenterY);
                canvas.concat(mMatrix);

                mPaint.setColor(mItemList.get(i).bgColor);

                if (i == 0) {//第一个
                    mPath.reset();
                    drawTriangleMark(canvas);//绘制三角图标
//                    float xLeft = mItemLeft;
//                    float xRight = mItemLeft + mItemWidth;
//                    float yTop = isUpper ? mFirstItemTop : mFirstItemTop + i * mItemHeight;
//                    float yBottom = isUpper ? mFirstItemTop - mItemHeight : mFirstItemTop + (i + 1) * mItemHeight;

//                mPath.moveTo(xLeft, yBottom);//左下角
//                mPath.lineTo(xRight, yBottom);//右下角
//                mPath.lineTo(xRight, yTop + mCorner);//右上角垂直方向减去圆角
//                mPath.quadTo(xRight, yTop, xRight - mCorner, yTop);//（右上角，右上角水平方向减去圆角）
//                mPath.lineTo(xLeft + mCorner, yTop);//左上角水平方向加上圆角
//                mPath.quadTo(xLeft, yTop, xLeft, yTop + mCorner);//（左上角，左上角垂直方向减去圆角）
//                mPath.lineTo(xLeft, yBottom);//此处可省略，自动封闭曲线

//                    canvas.drawRect(xLeft, yTop, xRight, yBottom, mPaint);
                }
//                else {
                float xLeft = mItemLeft;
                float xRight = mItemLeft + mItemWidth;
                float yTop = getItemTop(i);
                float yBottom = getItemBottom(i);
                canvas.drawRect(xLeft, yTop, xRight, yBottom, mPaint);
//                }
                mItemRectList.add(new RectF(xLeft, yTop, xRight, yBottom));

                drawTextContent(canvas, i);
                drawContentIcon(canvas, i);
            }
            mCamera.restore();
            canvas.restore();
        }
    }

    /**
     * 获取转轴y坐标
     *
     * @param index
     * @return
     */
    private float getRotateAxis(int index) {
        if (mStatus == STATUS_DOWN) {
            return getItemTop(index);
        } else {
            return getItemBottom(index);
        }
    }

    /**
     * 获取指定项的顶部坐标
     * 注意方向
     *
     * @param index
     * @return
     */
    private float getItemTop(int index) {
        if (mStatus == STATUS_DOWN) {
            return mFirstItemTop + index * mItemHeight;
        } else {
            return mFirstItemTop - (index + 1) * mItemHeight;
        }
    }

    /**
     * 获取指定项的底部坐标
     *
     * @param index
     * @return
     */
    private float getItemBottom(int index) {
        if (mStatus == STATUS_DOWN) {
            return mFirstItemTop + (index + 1) * mItemHeight;
        } else {
            return mFirstItemTop - index * mItemHeight;
        }
    }

    /**
     * 绘制三角
     *
     * @param canvas
     */
    private void drawTriangleMark(Canvas canvas) {
        mPath.moveTo(pMiddleX, mItemTop);//上三角顶角
        mPath.lineTo(pMiddleX - mTriangleHeight, mFirstItemTop);
        mPath.lineTo(pMiddleX + mTriangleHeight, mFirstItemTop);
        canvas.drawPath(mPath, mPaint);
    }

    //绘制文字
    private void drawTextContent(Canvas canvas, int position) {
        ShareItem shareItem = mItemList.get(position);
        mPaint.setColor(shareItem.titleColor);
        canvas.drawText(shareItem.title
                , mItemLeft + dip2px(8)
                , getTextHeight(shareItem.title, mPaint) / 2 + mItemHeight / 2 + getItemTop(position)
                , mPaint);
    }

    //绘制图标
    private void drawContentIcon(Canvas canvas, int position) {
        ShareItem shareItem = mItemList.get(position);
        if (shareItem.icon != null) {
            float left = mItemLeft + mItemWidth - mItemHeight / 2 - dip2px(6);
            canvas.drawBitmap(shareItem.icon, null, new RectF(
                    left
                    , getItemTop(position) + mItemHeight / 4
                    , left + mItemHeight / 2, getItemTop(position) + mItemHeight * 3 / 4), mPaint);

        }
    }

    /**
     * 动画设置
     */
    public void startAnim() {

        if (mItemList.size() == 0) {
            throw new RuntimeException("At least set one shareItem");
        }

        mTotalAnimList.clear();

        for (int i = 0; i < mItemList.size(); i++) {
            final Collection<Animator> animList = new ArrayList<>();

            ValueAnimator itemShakeAnim;//抖动动画
            switch (mStatus) {
                case STATUS_DOWN:
                    itemShakeAnim = ValueAnimator.ofFloat(0, 3, 0);//0°~3°~0°
                    break;
                case STATUS_UP:
                    itemShakeAnim = ValueAnimator.ofFloat(0, -3, 0);
                    break;
                default:
                    itemShakeAnim = ValueAnimator.ofFloat(0);
                    break;
            }
            itemShakeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mItemShakeDegree = (float) animation.getAnimatedValue();
                }
            });

            ValueAnimator itemFlipAnim = ValueAnimator.ofFloat(-90, 8, 0);//翻转动画
            final int index = i;
            itemFlipAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mDegree = (float) animation.getAnimatedValue();
                    mCurrentItem = index;
                    invalidate();//重新调用onDraw方法
                }
            });
            animList.add(itemFlipAnim);
            animList.add(itemShakeAnim);
            AnimatorSet set = new AnimatorSet();
            set.setDuration(mDuration);
            set.setInterpolator(new LinearInterpolator());
            set.playTogether(animList);
            mTotalAnimList.add(set);
            set.addListener(new MyAnimListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    //前一个动画结束开始下一个动画
                    if (index + 1 < mItemList.size()) {
                        mTotalAnimList.get(index + 1).start();
                    }
                }
            });

        }

        mTotalAnimList.get(0).start();

    }

    public void setShareItemList(final List<ShareItem> list) {
        mItemList.clear();
        List<ShareItem> items = list;
        for (ShareItem item : items) {
            if (!TextUtils.isEmpty(item.title)) {
                item.title = updateTitle(item.title, item.icon != null);
            } else {
                item.title = "";
            }

            mItemList.add(item);
        }
    }

    /**
     * 根据是否有图标，获取最终文字形式
     * 由分享框长度决定
     *
     * @param title
     * @param hasIcon
     * @return
     */
    private String updateTitle(String title, boolean hasIcon) {
        int textLength = title.length();
        String suffix = "";
        //保证icon的显示，过长的文字以...结尾   分享框宽度-文字右边距-（如果有图标[图文间距+分享框的一半]|0）
        while (getTextWidth(title.substring(0, textLength) + "...", mPaint) > mItemWidth - dip2px(10) - (hasIcon ? (dip2px(6) + mItemHeight / 2) : 0)) {
            textLength--;
            suffix = "...";
        }
        return title.substring(0, textLength) + suffix;
    }

    public void setItemDuration(int mils) {
        mDuration = mils;
    }

    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
    }

    public void setSeparateLineColor(int separateLineColor) {
        mSeparateLineColor = separateLineColor;
    }

    private void dismiss() {
        for (Animator animator : mTotalAnimList) {
            animator.cancel();
        }

        if (!mAlphaAnim.isRunning()) {
            mAlphaAnim.start();
        }
    }

    /**
     * 关闭当前view时移除
     */
    private void removeView() {
        ViewGroup vg = (ViewGroup) this.getParent();
        if (vg != null) {
            vg.removeView(this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                return true;
            case MotionEvent.ACTION_UP:
                for (int i = 0; i < mItemRectList.size(); i++) {
                    //如果点击目标，执行相应逻辑
                    if (onFlipClickListener != null && isPointInRect(new PointF(event.getX(), event.getY()), mItemRectList.get(i))) {
                        onFlipClickListener.onItemClick(i);
                    }
                }
                dismiss();
                return true;
        }
        return true;
    }

    /**
     * 判断点击区域是否有效
     *
     * @param pointF
     * @param targetRect
     * @return
     */
    private boolean isPointInRect(PointF pointF, RectF targetRect) {
        if (pointF.x < targetRect.left) {
            return false;
        }
        if (pointF.x > targetRect.right) {
            return false;
        }
        if (pointF.y < targetRect.top) {
            return false;
        }
        if (pointF.y > targetRect.bottom) {
            return false;
        }
        return true;
    }

    private int dip2px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取文字高度
     *
     * @param text
     * @param paint
     * @return
     */
    private float getTextHeight(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height() / 1.1f;
    }

    private float getTextWidth(String text, Paint paint) {
        return paint.measureText(text);
    }

    @IntDef(flag = true, value = {TYPE_VERTICLE, TYPE_HORIZONTAL, TYPE_SLIDE})
    public @interface AnimType {
    }

    @AnimType
    public void setAnimType(@AnimType int animType) {
        mAnimType = animType;
    }

    public void setOnFlipClickListener(OnFlipClickListener onFlipClickListener) {
        this.onFlipClickListener = onFlipClickListener;
    }

    public interface OnFlipClickListener {
        void onItemClick(int position);

        void dismiss();
    }

    private abstract class MyAnimListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    public static class Builder {

        private Activity mActivity;
        private View mParentView;//Target视图
        private List<ShareItem> mShareItemList = new ArrayList<>();//Bean
        private int mMilliSecond = 300;//动画时间
        private int mBgColor = 0x00FFFFFF;//背景颜色
        private int mAnimType = FlipShareView.TYPE_VERTICLE;//动画的方向
        private int mSeparateLineColor = Color.TRANSPARENT;//分隔线颜色
        private ItemBehaviorListener mItemBehaviorListener;

        public void setmItemBehaviorListener(ItemBehaviorListener mItemBehaviorListener) {
            this.mItemBehaviorListener = mItemBehaviorListener;
        }

        public Builder(Activity activity, View parentView) {
            mActivity = activity;
            mParentView = parentView;
        }

        public Builder addItem(ShareItem shareItem) {
            mShareItemList.add(shareItem);
            return this;
        }

        public Builder addItems(List<ShareItem> list) {
            mShareItemList.addAll(list);
            return this;
        }

        public Builder setItemDuration(int mils) {
            mMilliSecond = mils;
            return this;
        }

        public Builder setAnimType(@AnimType int animType) {
            mAnimType = animType;
            return this;
        }

        public Builder setBackgroundColor(int color) {
            mBgColor = color;
            return this;
        }

        public Builder setSeparateLineColor(int color) {
            mSeparateLineColor = color;
            return this;
        }

        public FlipShareView create() {
            FlipShareView flipShare = new FlipShareView(mActivity.getBaseContext(), mActivity.getWindow(), mParentView);
            flipShare.setShareItemList(mShareItemList);
            flipShare.setItemDuration(mMilliSecond);
            flipShare.setBackgroundColor(mBgColor);
            flipShare.setAnimType(mAnimType);
            flipShare.setSeparateLineColor(mSeparateLineColor);
            flipShare.startAnim();//先配置好动画效果
            return flipShare;
        }

    }

}
