package com.github.ssnikolaevich.flipgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import com.github.ssnikolaevich.flipgame.game.Value;


public class TileSideView extends View {
    private Value value;
    private boolean isFront;
    private Bitmap mBitmap;
    private Paint paint;

    private final static int BACKGROUND_COLOR = Color.argb(0, 255, 255, 255);
    private final static int FRONT_COLOR = Color.rgb(115, 210, 22);
    private final static int BACK_COLOR = Color.rgb(245, 121, 0);
    private final static int VALUE_COLOR = Color.rgb(255, 255, 255);
    private final static int SHADOW_COLOR = Color.argb(64, 0, 0, 0);

    private final static float shadowOffset = 4.0f;

    public TileSideView(Context context) {
        super(context);
        init(null, 0);
    }

    public TileSideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TileSideView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        value = new Value();
        value.setLeft(true);
        value.setRight(true);
        value.setTop(true);
        value.setBottom(true);
        isFront = true;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public void setFront(boolean isFront) {
        this.isFront = isFront;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int size = (width > height)? height : width;
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        makeBitmap(w, h);
    }

    private void makeBitmap(int w, int h) {
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmap);
        drawBackground(canvas, w, h);
        drawTile(canvas, w, h);
        drawValue(canvas, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap != null)
            canvas.drawBitmap(mBitmap, 0, 0, paint);
    }

    private void drawBackground(Canvas canvas, int w, int h) {
        paint.setColor(BACKGROUND_COLOR);
        canvas.drawRect(0, 0, w, h, paint);
    }

    private void drawTile(Canvas canvas, int w, int h) {
        final float r = Math.min(w, h) / 5.0f;
        final float r2 = r / 8;

        // Create shape
        RectF rect = new RectF(r2, r2, w - r2, h - r2);

        // Draw shadow
        rect.offset(shadowOffset, shadowOffset);
        paint.setColor(SHADOW_COLOR);
        canvas.drawRoundRect(rect, r, r, paint);

        // Tile
        rect.offset(-shadowOffset, -shadowOffset);
        final int tileColor = isFront? FRONT_COLOR : BACK_COLOR;
        paint.setColor(tileColor);
        canvas.drawRoundRect(rect, r, r, paint);
    }

    private void drawValue(Canvas canvas, int w, int h) {
        if (value.isOther()) {
            drawValueOther(canvas, w, h);
        } else {
            if (value.isLeft()) {
                drawValueLeft(canvas, w, h);
            }
            if (value.isTop()) {
                drawValueTop(canvas, w, h);
            }
            if (value.isBottom()) {
                drawValueBottom(canvas, w, h);
            }
            if (value.isRight()) {
                drawValueRight(canvas, w, h);
            }
        }
    }

    private void drawValueOther(Canvas canvas, int w, int h) {
        final float s = Math.min(w, h) / 2.0f;
        final float r = s / 10.0f;

        // Create shape
        RectF rect = new RectF((w - s) / 2, (h - s) / 2, (w + s) / 2, (h + s) / 2);

        // Draw shadow
        rect.offset(shadowOffset, shadowOffset);
        paint.setColor(SHADOW_COLOR);
        canvas.drawRoundRect(rect, r, r, paint);

        // Draw value
        rect.offset(-shadowOffset, -shadowOffset);
        paint.setColor(VALUE_COLOR);
        canvas.drawRoundRect(rect, r, r, paint);
    }

    private void drawValueTop(Canvas canvas, int w, int h) {
        final float s = Math.min(w, h) / 8.0f;
        final float r = s / 4.0f;

        // Create shape
        Path path = new Path();
        path.moveTo((w - r) / 2, s + r / 2);
        path.rQuadTo(r / 2, -r / 2, r, 0);
        path.rLineTo(s, s);
        path.rQuadTo(r, r, -r, r);
        path.lineTo((w + r) / 2 - s, 2 * s + r * 1.5f);
        path.rQuadTo(-r * 2, 0, -r, -r);
        path.close();

        // Draw shadow
        path.offset(shadowOffset, shadowOffset);
        paint.setColor(SHADOW_COLOR);
        canvas.drawPath(path, paint);

        // Draw value
        path.offset(-shadowOffset, -shadowOffset);
        paint.setColor(VALUE_COLOR);
        canvas.drawPath(path, paint);
    }

    private void drawValueBottom(Canvas canvas, int w, int h) {
        final float s = Math.min(w, h) / 8.0f;
        final float r = s / 4.0f;

        // Create shape
        Path path = new Path();
        path.moveTo((w - r) / 2, h - (s + r / 2));
        path.rQuadTo(r / 2, r / 2, r, 0);
        path.rLineTo(s, -s);
        path.rQuadTo(r, -r, -r, -r);
        path.lineTo((w + r) / 2 - s, h - (2 * s + r * 1.5f));
        path.rQuadTo(-r * 2, 0, -r, r);
        path.close();

        // Draw shadow
        path.offset(shadowOffset, shadowOffset);
        paint.setColor(SHADOW_COLOR);
        canvas.drawPath(path, paint);

        // Draw value
        path.offset(-shadowOffset, -shadowOffset);
        paint.setColor(VALUE_COLOR);
        canvas.drawPath(path, paint);
    }

    private void drawValueLeft(Canvas canvas, int w, int h) {
        final float s = Math.min(w, h) / 8.0f;
        final float r = s / 4.0f;

        // Create shape
        Path path = new Path();
        path.moveTo(s + r / 2, (h - r) / 2);
        path.rQuadTo(-r / 2, r / 2, 0, r);
        path.rLineTo(s, s);
        path.rQuadTo(r, r, r, -r);
        path.lineTo(2 * s + r * 1.5f, (h + r) / 2 - s);
        path.rQuadTo(0, -r * 2, -r, -r);
        path.close();

        // Draw shape
        path.offset(shadowOffset, shadowOffset);
        paint.setColor(SHADOW_COLOR);
        canvas.drawPath(path, paint);

        // Draw value
        path.offset(-shadowOffset, -shadowOffset);
        paint.setColor(VALUE_COLOR);
        canvas.drawPath(path, paint);
    }

    private void drawValueRight(Canvas canvas, int w, int h) {
        final float s = Math.min(w, h) / 8.0f;
        final float r = s / 4.0f;

        // Create shape
        Path path = new Path();
        path.moveTo(w - s - r / 2, (h - r) / 2);
        path.rQuadTo(r / 2, r / 2, 0, r);
        path.rLineTo(-s, s);
        path.rQuadTo(-r, r, -r, -r);
        path.lineTo(w - 2 * s - 1.5f * r, (h + r) / 2 - s);
        path.rQuadTo(0, -r * 2, r, -r);
        path.close();

        // Draw shadow
        path.offset(shadowOffset, shadowOffset);
        paint.setColor(SHADOW_COLOR);
        canvas.drawPath(path, paint);

        // Draw value
        path.offset(-shadowOffset, -shadowOffset);
        paint.setColor(VALUE_COLOR);
        canvas.drawPath(path, paint);
    }
}
