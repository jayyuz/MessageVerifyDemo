package com.jaesoon.messageverifydemo.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 功能说明：<br>
 * <ul>
 * <li>当用户输入文字之后，产生两个动画：
 * <ol>
 * <li>文字透明度变化：文字透明度由透明度100%到0%</li>
 * <li>底部标线颜色变化：底部标线激活颜色由水平中心扩展到两端</li>
 * </ol>
 * </li>
 * <p>
 * <li>当用户清除了文字之后，产生两个动画：
 * <ol>
 * <li>文字透明度变化：文字透明度由透明度0%到100%</li>
 * <li>底部标线颜色变化：底部标线激活颜色由两端收缩到中心，然后不可见</li>
 * </ol>
 * </li>
 * </ul>
 */
public class SingleNumberView extends View {
    private static final String TAG = SingleNumberView.class.getSimpleName();
    /**
     * 相关动画：文字颜色动画、底部标线动画
     */
    private Animation lineExpenseAnimation;
    private Animation lineShrinkAnimation;

    /**
     * 动画周期 单位：ms
     */
    private int mDuration = 500;

    /**
     * 动画百分比（不是动画消逝时间百分比） InterpolatorFraction
     */
    private float mInterpolatorFraction = 0;

    /**
     * 当前数字
     */
    private String mNumber = "";
    /**
     * 文本颜色
     */
    private int textColor = Color.BLACK;
    /**
     * 文本字体大小
     */
    private int textSize = (int) (Resources.getSystem().getDisplayMetrics().density * 25);
    /**
     * 文本为空底部文字颜色
     */
    private int mBottomLineEmptyColor = Color.parseColor("#47b4db");

    /**
     * 文本为激活状态文字颜色
     */
    private int mBottomLineActiveColor = Color.parseColor("#6ae1ff");

    /**
     * 底部线的宽窄
     */
    private int mBottomLineWidth = (int) (Resources.getSystem().getDisplayMetrics().density * 1.5);

    /**
     * 文本画笔
     */
    private Paint mTextPaint;

    /**
     * 标线画笔
     */
    private Paint mBottomLinePaint;

    public SingleNumberView(Context context) {
        super(context);
        init();
    }

    public SingleNumberView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SingleNumberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {
        //初始化动画对象
        lineExpenseAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                ensureInterpolator();
                mInterpolatorFraction = getInterpolator().getInterpolation(interpolatedTime);
//                Log.e("SingleNumberView", mInterpolatorFraction + " .");
                mTextPaint.setAlpha((int) (mInterpolatorFraction * 255));
                invalidate();
            }
        };
        lineExpenseAnimation.setDuration(mDuration);


        lineShrinkAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                ensureInterpolator();
                mInterpolatorFraction = getInterpolator().getInterpolation(1 - interpolatedTime);
//                Log.e("SingleNumberView", mInterpolatorFraction + " ;");
                mTextPaint.setAlpha((int) (mInterpolatorFraction * 255));
                invalidate();
            }
        };
        lineShrinkAnimation.setDuration(mDuration);

        lineShrinkAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                mNumber = "";
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //初始化画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(textColor);

        mBottomLinePaint = new Paint();
        mBottomLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mBottomLinePaint.setStrokeWidth(mBottomLineWidth);
    }

    /**
     * 开始绘制
     */
    public void onDraw(Canvas canvas) {
        //开始绘制文字
        if (!TextUtils.isEmpty(mNumber)) {
            //绘制文字
            //仔细推导一下，就会找到合适的居中工具（可参考引文书写四线三格）
            int baseline = getTextBaseline(getPaddingTop());
            canvas.drawText(mNumber, getPaddingLeft() + mTextPaint.measureText("8") / 4, baseline, mTextPaint);
        } else {
            //不需要绘制文字
        }
        //开始绘制底部基础线框
        int lineY = (int) (getMeasuredHeight() - mBottomLinePaint.getStrokeWidth() - getPaddingBottom());
        int lineLength = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int lineStart = getPaddingLeft();
        mBottomLinePaint.setColor(mBottomLineEmptyColor);
        canvas.drawLine(lineStart,
                lineY,
                lineStart + lineLength,
                lineY, mBottomLinePaint);

        //开始绘制底部激活线框
        mBottomLinePaint.setColor(mBottomLineActiveColor);
        lineLength = (int) (mInterpolatorFraction * (getMeasuredWidth() - getPaddingLeft() - getPaddingRight()));
        lineStart = (getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - lineLength) / 2 + getPaddingLeft();
        if (lineLength > 0f && lineStart > 0f) {
            canvas.drawLine(lineStart,
                    lineY,
                    lineStart + lineLength,
                    lineY, mBottomLinePaint);
        }
    }

    private int getTextBaseline(int top) {
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(mNumber, 0, mNumber.length(), bounds);
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        int center = top + bounds.height() / 2;
        int baseline = center + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
//        Log.e(TAG, "baseline = " + baseline);
        return baseline;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        mTextPaint.setColor(textColor);
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        mTextPaint.setTextSize(textSize);
    }

    public void setActiveColor(int color) {
        mBottomLineActiveColor = color;
    }

    public void setInactiveColor(int color) {
        mBottomLineEmptyColor = color;
    }

    public void setBottomLineWidth(int width) {
        mBottomLineWidth = width;
        mBottomLinePaint.setStrokeWidth(mBottomLineWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = measureWidth(widthMeasureSpec);
        int measureHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(measureWidth, measureHeight);
    }

    private int measureWidth(int pWidthMeasureSpec) {
        int result = 0;
        int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式
        int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸

        switch (widthMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
//                Log.e(TAG, "我被测量啦,width ：" + getPaddingLeft() + "|" + getPaddingRight());
                result = (int) (mTextPaint.measureText("8") * 1.5f + getPaddingLeft() + getPaddingRight());
                break;
            case MeasureSpec.EXACTLY:
                // match_parent或具体的值如：60dp
                result = widthSize;
                break;
        }
        return result;
    }

    private int measureHeight(int pHeightMeasureSpec) {
        int result = 0;

        int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
        int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);

        switch (heightMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
//                Log.e(TAG, "我被测量啦,height ：" + getPaddingTop() + "|" + getPaddingBottom());
                Rect bounds = new Rect();
                mTextPaint.getTextBounds("8", 0, 1, bounds);
                result = bounds.height() + getPaddingTop() + getPaddingBottom();
                //线宽
                result += mBottomLinePaint.getStrokeWidth();
                //这个是文字与下划线的间隔
                result += getPaddingBottom();
                break;
            case MeasureSpec.EXACTLY:
                // match_parent或具体的值如：60dp
                result = heightSize;
                break;
        }
        return result;
    }

    public void setNumber(String mNumber) {
        if (lineShrinkAnimation != null) {
            lineShrinkAnimation.cancel();
        }
        if (lineExpenseAnimation != null) {
            lineExpenseAnimation.cancel();
        }
        if (TextUtils.isEmpty(mNumber)) {
            startAnimation(lineShrinkAnimation);
        } else {
            this.mNumber = mNumber;
            startAnimation(lineExpenseAnimation);
        }
    }
}