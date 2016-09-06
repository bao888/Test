package com.xiaobao.mywuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/29.
 */
public class WuziqiView extends View {

    private int mPanelWidth;
    private float mLineHeight;
    private int MAX_LINE = 12;
    private int MAX_COUNT_LINE = 5;
    private Paint mPaint = new Paint();
    private Bitmap mWhitePic;//白棋子图片
    private Bitmap mBlackPic;

    private float radioPicOfLineHeight = 3 * 1.0f / 4;//棋子占行高的比例

    //白子先走
    private boolean isWhite = true;
    private List<Point> mWhiteArray = new ArrayList<>();//存放白棋子
    private List<Point> mBlackArray = new ArrayList<>();

    private boolean mIsGameOver;
    private boolean mIsWhiteWin;

    public WuziqiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setBackgroundColor(0x44ff0000);
        init();
    }

    private void init() {
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

        //棋子图片
        mWhitePic = BitmapFactory.decodeResource(getResources(), R.drawable.white);
        mBlackPic = BitmapFactory.decodeResource(getResources(), R.drawable.black);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, widthSize);
        setMeasuredDimension(width, width);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;

        int picWidth = (int) (mLineHeight * radioPicOfLineHeight);
        mWhitePic = Bitmap.createScaledBitmap(mWhitePic, picWidth, picWidth, false);
        mBlackPic = Bitmap.createScaledBitmap(mBlackPic, picWidth, picWidth, false);
    }

    @Override
    //落子
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsGameOver)
            return false;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getRightPoint(x, y);
            //相同位置只能落一子
            if (mWhiteArray.contains(p) || mBlackArray.contains(p)) {
                return false;
            }
            if (isWhite) {
                mWhiteArray.add(p);
            } else {
                mBlackArray.add(p);
            }
            invalidate();
            isWhite = !isWhite;
        }
        return true;
    }

    private Point getRightPoint(int x, int y) {
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPics(canvas);
        checkGameOver();
    }

    private void checkGameOver() {
        boolean whiteWin = checkFiveInLine(mWhiteArray);
        boolean blackWin = checkFiveInLine(mBlackArray);
        if (whiteWin || blackWin) {
            mIsGameOver = true;
            mIsWhiteWin = whiteWin;
            String text = mIsWhiteWin ? "白棋胜利" : "黑棋胜利";
            Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkFiveInLine(List<Point> points) {
        for (Point p : points) {
            int x = p.x;
            int y = p.y;
            boolean win = checkHorizontal(x, y, points);
            if (win)
                return true;

            win = checkVertical(x, y, points);
            if (win)
                return true;

            win = checkLeftDiagonal(x, y, points);
            if (win)
                return true;

            win = checkRightDiagonal(x, y, points);
            if (win)
                return true;
        }
        return false;
    }

    /**
     * 判断横向是否有五个棋子连在一起
     *
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        //左
        for (int i = 1; i < MAX_COUNT_LINE; i++) {
            if (points.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_LINE)
            return true;
        //右
        for (int i = 1; i < MAX_COUNT_LINE; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_LINE)
            return true;

        return false;
    }

    /**
     * 判断纵向是否有五个棋子连在一起
     *
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkVertical(int x, int y, List<Point> points) {
        int count = 1;
        //上
        for (int i = 1; i < MAX_COUNT_LINE; i++) {
            if (points.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_LINE)
            return true;
        //下
        for (int i = 1; i < MAX_COUNT_LINE; i++) {
            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_LINE)
            return true;

        return false;
    }

    /**
     * 判断左斜方向是否有五个棋子连在一起
     *
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        //左斜上
        for (int i = 1; i < MAX_COUNT_LINE; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_LINE)
            return true;
        //左斜下
        for (int i = 1; i < MAX_COUNT_LINE; i++) {
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_LINE)
            return true;

        return false;
    }

    /**
     * 判断右斜方向是否有五个棋子连在一起
     *
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkRightDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        //右斜上
        for (int i = 1; i < MAX_COUNT_LINE; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_LINE)
            return true;
        //右斜下
        for (int i = 1; i < MAX_COUNT_LINE; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_LINE)
            return true;

        return false;
    }

    /**
     * 绘制棋子
     *
     * @param canvas
     */
    private void drawPics(Canvas canvas) {
        for (int i = 0; i < mWhiteArray.size(); i++) {
            Point whitePoint = mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePic, (whitePoint.x + (1 - radioPicOfLineHeight) / 2) * mLineHeight,
                    (whitePoint.y + (1 - radioPicOfLineHeight) / 2) * mLineHeight, null);
        }

        for (int i = 0; i < mBlackArray.size(); i++) {
            Point blackPoint = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPic, (blackPoint.x + (1 - radioPicOfLineHeight) / 2) * mLineHeight,
                    (blackPoint.y + (1 - radioPicOfLineHeight) / 2) * mLineHeight, null);
        }
    }

    /**
     * 绘制棋盘
     *
     * @param canvas
     */
    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineHeight = mLineHeight;
        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);
            int y = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX, y, endX, y, mPaint);
        }
        for (int i = 0; i < MAX_LINE; i++) {
            int startY = (int) (lineHeight / 2);
            int endY = (int) (w - lineHeight / 2);
            int x = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(x, startY, x, endY, mPaint);
        }
    }

    //再来一局
    public void again() {
        mWhiteArray.clear();
        mBlackArray.clear();
        mIsGameOver = false;
        mIsWhiteWin = false;
        invalidate();
    }
}
