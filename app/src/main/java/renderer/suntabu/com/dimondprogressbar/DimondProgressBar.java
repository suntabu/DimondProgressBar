package renderer.suntabu.com.dimondprogressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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
    private int lineWidth = 20;
    private Vector2 endPos;

    private ArrayList<Vector2> pathArray;
    private ArrayList<Vector2> colorPosArray;

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
    private float currentCount = 24f;
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
        colorPosArray = new ArrayList<>();

    }

    private void initData() {
        if (!isDataInit) {
            outerBasePos = new Vector2[4];
            innerBasePos = new Vector2[4];
            centerPos = new Vector2(mWidth / 2, mHeight / 2);
            String[] ptsStr = ptsBaseStr.split(",");

            float x, y;
            for (int i = 0; i < ptsStr.length; i++) {
                if (i % 2 == 0) {
                    x = Float.parseFloat(ptsStr[i]) * mWidth;
                    y = Float.parseFloat(ptsStr[i + 1]) * mHeight;
                    Log.d(TAG, x + "," + y + "    " + Float.parseFloat(ptsStr[i]) + "," + Float.parseFloat(ptsStr[i + 1]) + "    " + mWidth + "," + mHeight + "       " + ptsStr[i] + "," + ptsStr[i + 1]);
                    outerBasePos[i / 2] = new Vector2();
                    outerBasePos[i / 2].x = x;
                    outerBasePos[i / 2].y = y;
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
        colorPosArray.clear();

        float percent = getPercent();
        float scale = 0;
        if (percent < 0.25f) {
            scale = percent / 0.25f;
            Vector2 newOut = outerBasePos[1].minus(outerBasePos[0]).scale(scale).plus(outerBasePos[0]);
            Vector2 newIn = innerBasePos[1].minus(innerBasePos[0]).scale(scale).plus(innerBasePos[0]);


            pathArray.add(innerBasePos[0]);
            pathArray.add(outerBasePos[0]);
            pathArray.add(newOut);
            pathArray.add(newIn);
            endPos = newOut.plus(newIn).scale(0.5f);

        } else if (percent < 0.50f) {

        } else if (percent < 0.75f) {

        } else {

        }

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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initData();
        updatePath();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.GREEN);


        Shader gradient = new SweepGradient(centerPos.x, centerPos.y, Color.RED, Color.GREEN);

        mPaint.setShader(gradient);
        canvas.drawPath(path, mPaint);

        RectF rect = new RectF(endPos.x - lineWidth, endPos.y - lineWidth * 2, endPos.x + lineWidth, endPos.y);
        canvas.drawRect(rect, mPaint);
//        canvas.drawCircle(centerPos.x, centerPos.y, mWidth / 2, mPaint);
        //canvas.drawRoundRect(rectProgressBg, round, round, mPaint);
//        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(40);
        Path newPath = createPath(mWidth, mHeight, ptsBaseStr);
        canvas.drawPath(newPath, mPaint);
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
