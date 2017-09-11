package com.example.liuzhige.mynbview;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * CircleProgressBar.java
 * Author: liuzhige
 * Date: 2017/9/6
 */

public class CircleProgressBar extends View {

  private static final String TAG = "CircleProgressBar";
  private static final int SEEK_BAR_START = 0;
  private static final int SEEK_BAR_STARTED = 1;
  private static final int SEEK_BAR_COMPLETED = 2;
  int mLastX;
  int mLastY;
  private RectF mRectF;
  private Paint mOutCirclePaint; //外层圆画笔
  private Paint mInCirclePaint;  //内层圆画笔
  private Paint mProgressPaint;  //内层圆画笔
  private Paint mTextPaint;
  private float mOutCircleRadius;  //外圈半径
  private float mInCircleRadius;   //内圈半径
  private int mMaxProgress = 100;   //进度最大值
  private int mMinProgress = 0;     //进度最小值
  private float mCircleOfPointX;    //圆心 X坐标
  private float mCircleOfPointY;    //圆心 Y坐标
  private int startPointColor = getResources().getColor(R.color.chrysophoron50);   //默认进度条起始点颜色
  private int endPointColor = getResources().getColor(R.color.chrysophoron500);    //默认进度条终点颜色
  private int curColor = startPointColor; //当前进度条颜色
  private int curEndColor;          //当前进度的终点颜色
  private int curProgress;          //当前进度值
  private int textSize;             //中间数值区域字体大写
  private String progressStr = "0";            //中间数值
  private int progressBarColor;                   //进度条颜色
  private onProgressStateListener mListener;   //进度监听

  public CircleProgressBar(Context context) {
    this(context, null);
  }

  public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  public int getProgressBarColor() {
    return progressBarColor;
  }

  public void setProgressBarColor(int progressBarColor) {
    this.progressBarColor = progressBarColor;
    invalidate();
  }

  public onProgressStateListener getmListener() {
    return mListener;
  }

  public void setmListener(
      onProgressStateListener mListener) {
    this.mListener = mListener;
  }

  public void setCurProgress(int curProgress) {
    this.curProgress = curProgress;
    invalidate();
  }

  public void setProgressStr(String progressStr) {
    this.progressStr = progressStr;
    invalidate();
  }

  public void setTextSize(int textSize) {
    this.textSize = textSize;
    invalidate();
  }

  private void init(Context context, @Nullable AttributeSet attrs) {
    mRectF = new RectF();
    TypedArray a = context.getTheme()
        .obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, 0, 0);
    textSize = a.getDimensionPixelSize(R.styleable.CircleProgressBar_in_circle_textSize,
        PxUtils.sp2px(context, 64));
    a.recycle();
    mOutCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mOutCirclePaint.setStyle(Style.FILL);
    mOutCirclePaint.setColor(getResources().getColor(R.color.gray300));

    mInCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mInCirclePaint.setStyle(Style.FILL);
    mInCirclePaint.setColor(getResources().getColor(R.color.blueA100));

    mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mProgressPaint.setStyle(Style.STROKE);
    mProgressPaint.setStrokeCap(Cap.ROUND);
    mProgressPaint.setColor(getResources().getColor(R.color.purple300));

    mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mTextPaint.setStyle(Style.STROKE);
    mTextPaint.setTextSize(textSize);
    mTextPaint.setColor(getResources().getColor(R.color.gray800));
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
    int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

    int resultWidth;
    int resultHeight;

    if (heightSpecMode == MeasureSpec.AT_MOST && widthSpecMode == MeasureSpec.AT_MOST) {
      resultHeight = 200;
      resultWidth = 200;
      setMeasuredDimension(200, 200);
    } else if (heightSpecMode == MeasureSpec.AT_MOST) {
      resultWidth = widthSpecSize;
      resultHeight = 200;
      setMeasuredDimension(widthSpecSize, 200);
    } else if (widthSpecMode == MeasureSpec.AT_MOST) {
      resultHeight = heightSpecSize;
      resultWidth = 200;
      setMeasuredDimension(200, heightSpecSize);
    } else {
      resultWidth = widthSpecSize;
      resultHeight = heightSpecSize;
      setMeasuredDimension(widthSpecSize, heightSpecSize);
    }
    final int min = Math.min(resultWidth, resultHeight);

    mInCircleRadius = min / 2 - min / 10;
    mOutCircleRadius = min / 2;
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    float mLeft = getWidth() / 2 - mOutCircleRadius + mOutCircleRadius / 10;
    float mTop = getHeight() / 2 - mOutCircleRadius + mOutCircleRadius / 10;
    float mRight = getWidth() / 2 + mOutCircleRadius - mOutCircleRadius / 10;
    float mBottom = getHeight() / 2 + mOutCircleRadius - mOutCircleRadius / 10;
    mCircleOfPointX = getWidth() / 2;
    mCircleOfPointY = getHeight() / 2;
    mRectF.set(mLeft, mTop, mRight, mBottom);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    //外层圆
    canvas.drawCircle(getWidth() / 2, getHeight() / 2, mOutCircleRadius, mOutCirclePaint);
    //内层圆
    canvas.drawCircle(getWidth() / 2, getHeight() / 2, mInCircleRadius, mInCirclePaint);

    //progressbar 区域
    mProgressPaint.setColor(progressBarColor);
    mProgressPaint.setStrokeWidth(mOutCircleRadius - mInCircleRadius);
    canvas.drawArc(mRectF, -90,
        360 * (curProgress - mMinProgress) / (mMaxProgress - mMinProgress), false,
        mProgressPaint);

    //中心数值区域
    mTextPaint.setTextSize(mInCircleRadius * 2 / 3);
    mTextPaint.setTextAlign(Align.CENTER);
    Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
    canvas.drawText(progressStr + "%", getWidth() / 2,
        getHeight() / 2 - fontMetrics.top / 2 - fontMetrics.bottom / 2, mTextPaint);
  }

  public void seekToProgress(int progress) {
    setProgressWithAnimation(progress, true);
  }

  private void setProgressWithAnimation(int progress, boolean isNeedDuration) {
    if (progress == curProgress) {
      return;
    } else if (progress > mMaxProgress) {
      progress = mMaxProgress;
    } else if (progress < mMinProgress) {
      progress = mMinProgress;
    }
    ObjectAnimator progressAnimator = ObjectAnimator
        .ofInt(this, "curProgress", curProgress, progress);
    progressAnimator.setInterpolator(new AccelerateInterpolator());
    final int[] state = {0};
    final int finalProgress = progress;
    progressAnimator.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
        progressStr = String.valueOf(valueAnimator.getAnimatedValue("curProgress"));
        if (progressStr.equals(mMinProgress + "") && (state[0] == SEEK_BAR_START
            || state[0] == SEEK_BAR_COMPLETED)) {
          mListener.onStart(Integer.parseInt(progressStr));
          state[0] = SEEK_BAR_STARTED;
        } else if (progressStr.equals(mMaxProgress + "") && state[0] == SEEK_BAR_STARTED) {
          state[0] = SEEK_BAR_COMPLETED;
          curProgress = finalProgress;
          mListener.onComplete(Integer.parseInt(progressStr));
        }
      }
    });
    //计算此次动画的终点颜色
    curEndColor = getCurrentEndColor(progress, startPointColor, endPointColor);
    ObjectAnimator colorAnimator = ObjectAnimator
        .ofObject(this, "progressBarColor", new ArgbEvaluator(), curColor, curEndColor);
    colorAnimator.setInterpolator(new AccelerateInterpolator());
    colorAnimator.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float fraction = valueAnimator.getAnimatedFraction();
        progressBarColor = getCurrentColor(fraction, curColor, curEndColor);
        if (progressBarColor == curEndColor) {
          curColor = curEndColor;
        }
      }
    });
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playTogether(progressAnimator, colorAnimator);
    if (isNeedDuration) {
      animatorSet.setDuration(computeDuration(curProgress, progress));
    }
    animatorSet.start();
  }

  private int computeDuration(int oldPro, int newPro) {
    int duration;
    if (Math.abs(oldPro - newPro) <= 30) {
      duration = 400;
    } else if (Math.abs(oldPro - newPro) <= 60) {
      duration = 700;
    } else {
      duration = 1000;
    }
    return duration;
  }

  /**
   * 根据fraction值来计算当前的颜色。 fraction值范围  0f-1f
   */
  private int getCurrentColor(float fraction, int startColor, int endColor) {
    int redCurrent;
    int blueCurrent;
    int greenCurrent;
    int alphaCurrent;

    int redStart = Color.red(startColor);
    int blueStart = Color.blue(startColor);
    int greenStart = Color.green(startColor);
    int alphaStart = Color.alpha(startColor);

    int redEnd = Color.red(endColor);
    int blueEnd = Color.blue(endColor);
    int greenEnd = Color.green(endColor);
    int alphaEnd = Color.alpha(endColor);

    int redDifference = redEnd - redStart;
    int blueDifference = blueEnd - blueStart;
    int greenDifference = greenEnd - greenStart;
    int alphaDifference = alphaEnd - alphaStart;

    redCurrent = (int) (redStart + fraction * redDifference);
    blueCurrent = (int) (blueStart + fraction * blueDifference);
    greenCurrent = (int) (greenStart + fraction * greenDifference);
    alphaCurrent = (int) (alphaStart + fraction * alphaDifference);

    return Color.argb(alphaCurrent, redCurrent, greenCurrent, blueCurrent);
  }

  /**
   * 计算本次Seek终点颜色值
   *
   * @param progress 终点进度
   * @param startColor 0点颜色值
   * @param endColor 100点颜色值
   * @return progress位置处的颜色值
   */
  private int getCurrentEndColor(float progress, int startColor, int endColor) {
    int redCurrent;
    int blueCurrent;
    int greenCurrent;
    int alphaCurrent;

    int redStart = Color.red(startColor);
    int blueStart = Color.blue(startColor);
    int greenStart = Color.green(startColor);
    int alphaStart = Color.alpha(startColor);

    int redEnd = Color.red(endColor);
    int blueEnd = Color.blue(endColor);
    int greenEnd = Color.green(endColor);
    int alphaEnd = Color.alpha(endColor);

    int redDifference = redEnd - redStart;
    int blueDifference = blueEnd - blueStart;
    int greenDifference = greenEnd - greenStart;
    int alphaDifference = alphaEnd - alphaStart;

    float percent = progress / mMaxProgress;
    redCurrent = redStart + (int) (percent * redDifference);
    blueCurrent = blueStart + (int) (percent * blueDifference);
    greenCurrent = greenStart + (int) (percent * greenDifference);
    alphaCurrent = alphaStart + (int) (percent * alphaDifference);

    return Color.argb(alphaCurrent, redCurrent, greenCurrent, blueCurrent);
  }

  @Override
  public boolean onTouchEvent(@NonNull MotionEvent event) {
    final int x = (int) event.getX();
    final int y = (int) event.getY();
    if (!isContainInCircleBar(x, y)) {
      return true;
    }
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        mLastX = x;
        mLastY = y;
        float a = deltaDegree(mCircleOfPointX, mCircleOfPointY, x, y);
        Log.d(TAG, "onTouchEvent: " + a);
        break;
      case MotionEvent.ACTION_MOVE:
        post(new Runnable() {
          @Override
          public void run() {
            float a = deltaDegree(mCircleOfPointX, mCircleOfPointY, x, y);
            float progress = (a / 360) * 100;
            if (progress > 100) {
              progress = 100;
            }
            setProgressWithAnimation((int) progress, false);
          }
        });
        break;
      case MotionEvent.ACTION_UP:
        break;
    }
    return true;
  }

  /**
   * 判断触摸点是否在progressbar中
   *
   * @param x Touch 的 x坐标
   * @param y Touch 的 y坐标
   * @return True 包含， false 不包含
   */
  private boolean isContainInCircleBar(int x, int y) {
    //r^2 = （X1-X0)^2 + （Y1- Y0)^2
    int touchRadius = (int) (Math.pow((x - mCircleOfPointX), 2) + Math
        .pow((y - mCircleOfPointY), 2));
    //在小于外圈大于内圈
    return touchRadius <= (mOutCircleRadius * mOutCircleRadius) && touchRadius >= (mInCircleRadius
        * mInCircleRadius);
  }

  private float deltaDegree(float src_x, float src_y, float target_x, float target_y) {
    float deltaX = target_x - src_x;
    float deltaY = target_y - src_y;
    double d;
    if (deltaY != 0) {
      if (deltaY < 0) {
        //一、四象限
        if (deltaX > 0) {
          //第一象限
          d = Math.atan(Math.abs(deltaX / deltaY)) / Math.PI * 180;
        } else {
          d = Math.atan(Math.abs(deltaY / deltaX)) / Math.PI * 180 + 270;
        }
      } else {
        //二、三象限了
        if (deltaX > 0) {
          d = Math.atan(Math.abs(deltaY / deltaX)) / Math.PI * 180 + 90;
        } else {
          d = Math.atan(Math.abs(deltaX / deltaY)) / Math.PI * 180 + 180;
        }
      }
    } else {
      if (deltaX > 0) {
        d = 90;
      } else {
        d = 270;
      }
    }
    return (float) d;
  }

  interface onProgressStateListener {

    void onStart(int progressStart);

    void onPause(int progressPause);

    void onComplete(int progressEnd);
  }
}
