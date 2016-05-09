package renderer.suntabu.com.dimondprogressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by gouzhun on 2016/5/9.
 */
public class DimondProgressBar extends View {
    private static final String TAG = "DimondProgress";

    private Path path;

    private static String ptsBaseStr = "0.5,1,1,0.5,0.5,0,0,0.5";

    private Vector2[] outerBasePos;
    private Vector2[] innerBasePos;
    private Vector2 centerPos;
    private boolean isDataInit;
    private int lineWidth = 40;
    private int borderWidth = 8;
    private Vector2 endPos;
    private ArrayList<Vector2> pathArray;
    private Vector2[] outBorderPos;
    private Path borderPath;

    private Path pathBg;

    /**
     * 分段颜色
     */
    private static final int[] SECTION_COLORS = {0xfec500, 0x656366, Color.RED};
    /**
     * 进度条最大值
     */
    private float maxCount = 100f;
    /**
     * 进度条当前值
     */
    private float currentCount = 33f;
    /**
     * 画笔
     */
    private Paint mPaint;
    private int mWidth, mHeight;

    public DimondProgressBar(Context context, AttributeSet attrs,
                             int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public DimondProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DimondProgressBar(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        isDataInit = false;
        pathArray = new ArrayList<>();

    }

    private void initData() {
        if (!isDataInit) {
            outerBasePos = new Vector2[4];
            innerBasePos = new Vector2[4];
            outBorderPos = new Vector2[4];
            centerPos = new Vector2(mWidth / 2, mHeight / 2);
            String[] ptsStr = ptsBaseStr.split(",");

            float x, y;
            for (int i = 0; i < ptsStr.length; i++) {
                if (i % 2 == 0) {
                    x = Float.parseFloat(ptsStr[i]) * mWidth;
                    y = Float.parseFloat(ptsStr[i + 1]) * mHeight;
                    Log.d(TAG, x + "," + y + "    " + Float.parseFloat(ptsStr[i]) + "," + Float.parseFloat(ptsStr[i + 1]) + "    " + mWidth + "," + mHeight + "       " + ptsStr[i] + "," + ptsStr[i + 1]);
                    outBorderPos[i / 2] = new Vector2();
                    outBorderPos[i / 2].x = x;
                    outBorderPos[i / 2].y = y;


                    outerBasePos[i / 2] = centerPos.minus(outBorderPos[i / 2]).normalize().scale(lineWidth + borderWidth).plus(outBorderPos[i / 2]);

                    innerBasePos[i / 2] = new Vector2();
                    innerBasePos[i / 2] = new Vector2(centerPos.x, centerPos.y).minus(outerBasePos[i / 2]).normalize().scale(lineWidth).plus(outerBasePos[i / 2]);

                }
            }

            isDataInit = true;
        }
    }


    public float getPercent() {
        return currentCount / maxCount;
    }

    private void updatePath() {
        pathArray.clear();

        float percent = getPercent();
        float scale = 0;
        Vector2 newOut = null, newIn = null;
        if (percent < 0.25f) {
            scale = percent / 0.25f;
            newOut = outerBasePos[1].minus(outerBasePos[0]).scale(scale).plus(outerBasePos[0]);
            newIn = innerBasePos[1].minus(innerBasePos[0]).scale(scale).plus(innerBasePos[0]);


            pathArray.add(innerBasePos[0]);
            pathArray.add(outerBasePos[0]);
            pathArray.add(newOut);
            pathArray.add(newIn);

        } else if (percent < 0.50f) {
            scale = (percent - 0.25f) / 0.25f;

            newOut = outerBasePos[2].minus(outerBasePos[1]).scale(scale).plus(outerBasePos[1]);
            newIn = innerBasePos[2].minus(innerBasePos[1]).scale(scale).plus(innerBasePos[1]);

            pathArray.add(innerBasePos[0]);
            pathArray.add(outerBasePos[0]);
            pathArray.add(outerBasePos[1]);
            pathArray.add(newOut);
            pathArray.add(newIn);
            pathArray.add(innerBasePos[1]);


        } else if (percent < 0.75f) {
            scale = (percent - 0.50f) / 0.25f;

            newOut = outerBasePos[3].minus(outerBasePos[2]).scale(scale).plus(outerBasePos[2]);
            newIn = innerBasePos[3].minus(innerBasePos[2]).scale(scale).plus(innerBasePos[2]);

            pathArray.add(innerBasePos[0]);
            pathArray.add(outerBasePos[0]);
            pathArray.add(outerBasePos[1]);
            pathArray.add(outerBasePos[2]);
            pathArray.add(newOut);
            pathArray.add(newIn);
            pathArray.add(innerBasePos[2]);
            pathArray.add(innerBasePos[1]);
        } else {
            scale = (percent - 0.75f) / 0.25f;

            newOut = outerBasePos[0].minus(outerBasePos[3]).scale(scale).plus(outerBasePos[3]);
            newIn = innerBasePos[0].minus(innerBasePos[3]).scale(scale).plus(innerBasePos[3]);

            pathArray.add(innerBasePos[0]);
            pathArray.add(outerBasePos[0]);
            pathArray.add(outerBasePos[1]);
            pathArray.add(outerBasePos[2]);
            pathArray.add(outerBasePos[3]);
            pathArray.add(newOut);
            pathArray.add(newIn);
            pathArray.add(innerBasePos[3]);
            pathArray.add(innerBasePos[2]);
            pathArray.add(innerBasePos[1]);
        }
        endPos = newOut.plus(newIn).scale(0.5f);
        endPos.y = mHeight - endPos.y;
        path = new Path();
        for (int i = 0; i < pathArray.size(); i++) {
            float x = pathArray.get(i).x;
            float y = mHeight - pathArray.get(i).y;
            Log.d(TAG, x + " " + y);
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        path.close();


        pathBg = new Path();
        pathBg.moveTo(innerBasePos[0].x, innerBasePos[0].y);
        pathBg.lineTo(outerBasePos[0].x, outerBasePos[0].y);
        pathBg.lineTo(outerBasePos[1].x, outerBasePos[1].y);
        pathBg.lineTo(outerBasePos[2].x, outerBasePos[2].y);
        pathBg.lineTo(outerBasePos[3].x, outerBasePos[3].y);
        pathBg.lineTo(outerBasePos[0].x, outerBasePos[0].y);
        pathBg.lineTo(innerBasePos[0].x, innerBasePos[0].y);
        pathBg.lineTo(innerBasePos[3].x, innerBasePos[3].y);
        pathBg.lineTo(innerBasePos[2].x, innerBasePos[2].y);
        pathBg.lineTo(innerBasePos[1].x, innerBasePos[1].y);
        pathBg.close();


        borderPath = new Path();
        borderPath.moveTo(outBorderPos[0].x, outBorderPos[0].y);
        borderPath.lineTo(outBorderPos[1].x, outBorderPos[1].y);
        borderPath.lineTo(outBorderPos[2].x, outBorderPos[2].y);
        borderPath.lineTo(outBorderPos[3].x, outBorderPos[3].y);
        borderPath.close();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initData();
        updatePath();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        // 边框
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(borderWidth);
        canvas.drawPath(borderPath, mPaint);

        //  进度条底
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.GRAY);
        canvas.drawPath(pathBg, mPaint);

        // 进度条
        Shader gradient = new SweepGradient(centerPos.x, centerPos.y, Color.GREEN, Color.RED);
        float rotate = -90f;
        Matrix gradientMatrix = new Matrix();
        gradientMatrix.preRotate(rotate, 0, 0);
        gradient.setLocalMatrix(gradientMatrix);
        mPaint.setShader(gradient);
        canvas.drawPath(path, mPaint);

        // rect
        RectF rect = new RectF(endPos.x - lineWidth, endPos.y - lineWidth, endPos.x + lineWidth, endPos.y + lineWidth);
        canvas.drawRect(rect, mPaint);
//        canvas.drawCircle(endPos.x, endPos.y, lineWidth + 2, mPaint);


        // effect动画

    }

    private int dipToPx(int dip) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /***
     * 设置最大的进度值
     *
     * @param maxCount
     */
    public void setMaxCount(float maxCount) {
        this.maxCount = maxCount;
    }

    /***
     * 设置当前的进度值
     *
     * @param currentCount
     */
    public void setCurrentCount(float currentCount) {
        this.currentCount = currentCount > maxCount ? maxCount : currentCount;
        invalidate();
    }

    public float getMaxCount() {
        return maxCount;
    }

    public float getCurrentCount() {
        return currentCount;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.EXACTLY || widthSpecMode == MeasureSpec.AT_MOST) {
            mWidth = widthSpecSize;
        } else {
            mWidth = 0;
        }
        if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            mHeight = dipToPx(15);
        } else {
            mHeight = heightSpecSize;
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    public static Path createPath(int width, int height, String ptStr) {
        width = width - 20;
        height = height - 20;
        Path path = new Path();
        String[] ptsStr = ptStr.split(",");
        float x, y;
        float[] positions = new float[ptsStr.length];
        for (int i = 0; i < ptsStr.length; i++) {
            if (i % 2 == 0) {
                x = Float.parseFloat(ptsStr[i]) * width;
                y = Float.parseFloat(ptsStr[i + 1]) * height;
                Log.d(TAG, x + "," + y + "    " + Float.parseFloat(ptsStr[i]) + "," + Float.parseFloat(ptsStr[i + 1]) + "    " + width + "," + height + "       " + ptsStr[i] + "," + ptsStr[i + 1]);
                if (i == 0) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
                positions[i + 0] = x;
                positions[i + 1] = y;
            }
        }
        path.close();
        return path;
    }
}
