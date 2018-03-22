package com.bryonnicoson.clippingexample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by bryon on 3/22/18.
 */

public class ClippedView extends View {

    private Paint mPaint;
    private Path mPath;

    // dimension vars
    private int mClipRectRight = (int) getResources().getDimension(R.dimen.clipRectRight);
    private int mClipRectBottom = (int) getResources().getDimension(R.dimen.clipRectBottom);
    private int mClipRectLeft = (int) getResources().getDimension(R.dimen.clipRectLeft);
    private int mClipRectTop = (int) getResources().getDimension(R.dimen.clipRectTop);
    private int mRectInset = (int) getResources().getDimension(R.dimen.rectInset);
    private int mSmallRectOffset = (int) getResources().getDimension(R.dimen.smallRectOffset);

    private int mCircleRadius = (int) getResources().getDimension(R.dimen.circleRadius);

    private int mTextOffset = (int) getResources().getDimension(R.dimen.textOffset);
    private int mTextSize = (int) getResources().getDimension(R.dimen.textSize);

    // row and column coordinates
    private int mColumnOne = mRectInset;
    private int mColumnTwo = mColumnOne + mRectInset + mClipRectRight;

    private int mRowOne = mRectInset;
    private int mRowTwo = mRowOne + mRectInset + mClipRectBottom;
    private int mRowThree = mRowTwo + mRectInset + mClipRectBottom;
    private int mRowFour = mRowThree + mRectInset + mClipRectBottom;
    private int mTextRow = mRowFour + (int)(1.5 * mClipRectBottom);

    // RectF
    private final RectF mRectF;

    public ClippedView(Context context){
        this(context, null);
    }

    public ClippedView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setFocusable(true);
        mPaint = new Paint();
        // smooth edges
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth((int) getResources().getDimension(R.dimen.strokeWidth));
        mPaint.setTextSize((int) getResources().getDimension(R.dimen.textSize));
        mPath = new Path();

        mRectF = new RectF(new Rect(mRectInset, mRectInset,
                mClipRectRight - mRectInset, mClipRectBottom - mRectInset));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.GRAY);
        // save drawing state so can be restored
        canvas.save();

        // CLIP 1: translate origin of canvas
        canvas.translate(mColumnOne, mRowOne);
        // draw first rectangle
        drawClippedRectangle(canvas);
        // restore previous state
        canvas.restore();

        // CLIP 2: draw a rectangle that uses difference btw two clipping rects for frame effect
        canvas.save();
        canvas.translate(mColumnTwo, mRowOne);
        canvas.clipRect(2 * mRectInset, 2 * mRectInset,
                mClipRectRight - 2 * mRectInset, mClipRectBottom - 2 * mRectInset);
        canvas.clipRect( 4 * mRectInset, 4 * mRectInset,
                mClipRectRight - 4 * mRectInset, mClipRectBottom - 4 * mRectInset,
                Region.Op.DIFFERENCE);
        drawClippedRectangle(canvas);
        canvas.restore();

        // CLIP 3: with circular clipping region
        canvas.save();
        canvas.translate(mColumnOne, mRowTwo);
        // clear any lines and curves from the path, but, unlike reset(), keep data
        mPath.rewind();
        mPath.addCircle(mCircleRadius, mClipRectBottom - mCircleRadius, mCircleRadius,
                Path.Direction.CCW);
        canvas.clipPath(mPath, Region.Op.DIFFERENCE);
        drawClippedRectangle(canvas);
        canvas.restore();

        // CLIP 4: intersection of two rectangles
        canvas.save();
        canvas.translate(mColumnTwo, mRowTwo);
        canvas.clipRect(mClipRectLeft, mClipRectTop, mClipRectRight - mSmallRectOffset,
                mClipRectBottom - mSmallRectOffset);
        canvas.clipRect(mClipRectLeft + mSmallRectOffset, mClipRectTop + mSmallRectOffset,
                mClipRectRight, mClipRectBottom, Region.Op.INTERSECT);
        drawClippedRectangle(canvas);
        canvas.restore();

        // CLIP 5: combined shapes
        canvas.save();
        canvas.translate(mColumnOne, mRowThree);
        mPath.rewind();
        mPath.addCircle(mClipRectLeft + mRectInset + mCircleRadius,
                mClipRectTop + mCircleRadius + mRectInset,
                mCircleRadius, Path.Direction.CCW);
        mPath.addRect(mClipRectRight / 2 - mCircleRadius, mClipRectTop + mCircleRadius + mRectInset,
                mClipRectRight / 2 + mCircleRadius, mClipRectBottom - mRectInset,
                Path.Direction.CCW);
        canvas.clipPath(mPath);
        drawClippedRectangle(canvas);
        canvas.restore();

        // CLIP 6: rounded rectangle
        canvas.save();
        canvas.translate(mColumnTwo, mRowThree);
        mPath.rewind();
        mPath.addRoundRect(mRectF, (float)mClipRectRight / 4, (float)mClipRectRight / 4,
                Path.Direction.CCW);
        canvas.clipPath(mPath);
        drawClippedRectangle(canvas);
        canvas.restore();

        // CLIP 7: clip outside around rectangle
        canvas.save();
        canvas.translate(mColumnOne, mRowFour);
        canvas.clipRect(2 * mRectInset, 2 * mRectInset, mClipRectRight - 2 * mRectInset,
                mClipRectBottom - 2 * mRectInset);
        drawClippedRectangle(canvas);
        canvas.restore();

        // CLIP 8: translated text
        canvas.save();
        mPaint.setColor(Color.CYAN);
        mPaint.setTextAlign(Paint.Align.LEFT);
        canvas.translate(mColumnTwo, mTextRow);
        canvas.drawText(getContext().getString(R.string.translated), 0, 0, mPaint);
        canvas.restore();

        // CLIP 9: skewed text
        canvas.save();
        mPaint.setTextSize(mTextSize);
        mPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.translate(mColumnTwo, mTextRow);
        canvas.skew(0.2f, 0.3f);
        canvas.drawText(getContext().getString(R.string.skewed), 0, 0, mPaint);
        canvas.restore();
    } // End of onDraw()

    public void drawClippedRectangle(Canvas canvas) {
        // set boundaries
        canvas.clipRect(mClipRectLeft, mClipRectTop, mClipRectRight, mClipRectBottom);
        // fill
        canvas.drawColor(Color.WHITE);
        // draw line
        mPaint.setColor(Color.RED);
        canvas.drawLine(mClipRectLeft, mClipRectTop, mClipRectRight, mClipRectBottom, mPaint);
        // draw circle
        mPaint.setColor(Color.GREEN);
        canvas.drawCircle(mCircleRadius, mClipRectBottom - mCircleRadius, mCircleRadius, mPaint);
        // draw text - align right side of text with the origin
        mPaint.setColor(Color.BLUE);
        mPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(getContext().getString(R.string.clipping), mClipRectRight, mTextOffset, mPaint);
    }

}
