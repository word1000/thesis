package com.example.xiazhituo.ecase;

import android.graphics.Canvas;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.graphics.RectF;
import android.graphics.Paint;
import android.content.Context;
import android.content.res.TypedArray;


/**
 * Created by xiazhituo on 16/7/17.
 */
public class CircleProgressView extends View {

    private static final String TAG = "CircleProgressBar";

    private int mMaxProgress = 100;

    private int mProgress = 30;

    private boolean mSetProgressTextDrawFlag = false;

    private final int mCircleLineStrokeWidth = 24;

    private final int mTxtStrokeWidth = 2;

    private String mMainText = "";

    // 画圆所在的距形区域
    private final RectF mRectF;

    private final Paint mPaint;

    private final Context mContext;

    private String mTxtHint1;

    private String mTxtHint2;

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.CircleProgressView);

        mMainText = typedArray.getString(R.styleable.CircleProgressView_mainText)   ;
        mSetProgressTextDrawFlag = typedArray.getBoolean(R.styleable.CircleProgressView_progressDrawFlag, true);

        typedArray.recycle();

        mContext = context;
        mRectF = new RectF();
        mPaint = new Paint();
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgressView, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.CircleProgressView_mainText:
                    this.mMainText = a.getString(attr);
                    break;
                case R.styleable.CircleProgressView_progressDrawFlag:
                    this.mSetProgressTextDrawFlag = a.getBoolean(attr, true);
                    Log.v(CircleProgressView.TAG, ""+mSetProgressTextDrawFlag);
                    break;
            }

        }
        a.recycle();

        mContext = context;
        mRectF = new RectF();
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }

        // 设置画笔相关属性
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.rgb(0xe9, 0xe9, 0xe9));
        canvas.drawColor(Color.TRANSPARENT);
        mPaint.setStrokeWidth(mCircleLineStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        // 位置
        mRectF.left = mCircleLineStrokeWidth / 2; // 左上角x
        mRectF.top = mCircleLineStrokeWidth / 2; // 左上角y
        mRectF.right = width - mCircleLineStrokeWidth / 2; // 左下角x
        mRectF.bottom = height - mCircleLineStrokeWidth / 2; // 右下角y

        // 绘制圆圈，进度条背景
        canvas.drawArc(mRectF, -90, 360, false, mPaint);
        mPaint.setColor(Color.rgb(0x16, 0x96, 0x88));
        canvas.drawArc(mRectF, -90, ((float) mProgress / mMaxProgress) * 360, false, mPaint);

        String text;
        int textHeight;
        int textWidth;
        // 绘制进度文案显示
        if (mSetProgressTextDrawFlag) {
            mPaint.setStrokeWidth(mTxtStrokeWidth);
            text = mProgress + "%";
            textHeight = height / 4;
            mPaint.setTextSize(textHeight);
            textWidth = (int) mPaint.measureText(text, 0, text.length());
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(text, width / 2 - textWidth / 2, height / 2 + textHeight / 2, mPaint);
        } else {
            mPaint.setStrokeWidth(mTxtStrokeWidth);
            textHeight = height / 6;
            mPaint.setTextSize(textHeight);
            textWidth = (int) mPaint.measureText(mMainText, 0, mMainText.length());
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(mMainText, width / 2 - textWidth / 2, height / 2 + textHeight / 2, mPaint);
            mProgress = 100;
        }

        if (!TextUtils.isEmpty(mTxtHint1)) {
            mPaint.setStrokeWidth(mTxtStrokeWidth);
            text = mTxtHint1;
            textHeight = height / 8;
            mPaint.setTextSize(textHeight);
            mPaint.setColor(Color.rgb(0x99, 0x99, 0x99));
            textWidth = (int) mPaint.measureText(text, 0, text.length());
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(text, width / 2 - textWidth / 2, height / 4 + textHeight / 2, mPaint);
        }

        if (!TextUtils.isEmpty(mTxtHint2)) {
            mPaint.setStrokeWidth(mTxtStrokeWidth);
            text = mTxtHint2;
            textHeight = height / 8;
            mPaint.setTextSize(textHeight);
            textWidth = (int) mPaint.measureText(text, 0, text.length());
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(text, width / 2 - textWidth / 2, 3 * height / 4 + textHeight / 2, mPaint);
        }
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.mMaxProgress = maxProgress;
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        this.invalidate();
    }

    public void setProgressNotInUiThread(int progress) {
        this.mProgress = progress;
        this.postInvalidate();
    }

    public String getmTxtHint1() {
        return mTxtHint1;
    }

    public void setmTxtHint1(String mTxtHint1) {
        this.mTxtHint1 = mTxtHint1;
    }

    public String getmTxtHint2() {
        return mTxtHint2;
    }

    public void setmTxtHint2(String mTxtHint2) {
        this.mTxtHint2 = mTxtHint2;
    }

    public void setProgressTextDrawFlag(boolean flag) {
        this.mSetProgressTextDrawFlag = flag;
    }

    public void setmMainText(String mainText) {
        this.mMainText = mainText;
    }
}
