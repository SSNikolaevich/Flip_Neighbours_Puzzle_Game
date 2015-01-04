package com.github.ssnikolaevich.flipgame;

import android.content.Context;
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

    private final static int BACKGROUND_COLOR = Color.rgb(220, 220, 220);
    private final static int FRONT_COLOR = Color.rgb(170, 255, 170);
    private final static int BACK_COLOR = Color.rgb(255, 170, 170);
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
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public void setFront(boolean isFront) {
        this.isFront = isFront;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        drawBackground(canvas, paint);
        drawTile(canvas, paint);
        drawValue(canvas, paint);
    }

    private void drawBackground(Canvas canvas, Paint paint) {
        final RectF rect = new RectF(0, 0, getWidth(), getHeight());
        paint.setColor(BACKGROUND_COLOR);
        canvas.drawRect(rect, paint);
    }

    private void drawTile(Canvas canvas, Paint paint) {
        final int w = getWidth();
        final int h = getHeight();
        final float r = Math.min(w, h) / 10.0f;
        final float r2 = r / 4;

        // Shadow
        RectF rect = new RectF(
                r2 + shadowOffset,
                r2 + shadowOffset,
                w - r2 + shadowOffset,
                h - r2 + shadowOffset
        );
        paint.setColor(SHADOW_COLOR);
        canvas.drawRoundRect(rect, r, r, paint);

        // Tile
        rect.left -= shadowOffset;
        rect.right -= shadowOffset;
        rect.top -= shadowOffset;
        rect.bottom -= shadowOffset;
        final int tileColor = isFront? FRONT_COLOR : BACK_COLOR;
        paint.setColor(tileColor);
        canvas.drawRoundRect(rect, r, r, paint);
    }

    private void drawValue(Canvas canvas, Paint paint) {
        if (value.isOther()) {
            drawValueOther(canvas, paint);
        } else {
            if (value.isLeft()) {
                drawValueLeft(canvas, paint);
            }
            if (value.isTop()) {
                drawValueTop(canvas, paint);
            }
            if (value.isBottom()) {
                drawValueBottom(canvas, paint);
            }
            if (value.isRight()) {
                drawValueRight(canvas, paint);
            }
        }
    }

    private void drawValueOther(Canvas canvas, Paint paint) {
        final int w = getWidth();
        final int h = getHeight();
        final float s = Math.min(w, h) / 2.0f;
        final float r = s / 10.0f;

        // shadow
        RectF rect = new RectF(
                (w - s) / 2 + shadowOffset,
                (h - s) / 2 + shadowOffset,
                (w + s) / 2 + shadowOffset,
                (h + s) / 2 + shadowOffset
        );
        paint.setColor(SHADOW_COLOR);
        canvas.drawRoundRect(rect, r, r, paint);

        // value
        rect.left -= shadowOffset;
        rect.right -= shadowOffset;
        rect.top -= shadowOffset;
        rect.bottom -= shadowOffset;
        paint.setColor(VALUE_COLOR);
        canvas.drawRoundRect(rect, r, r, paint);
    }

    private void drawValueTop(Canvas canvas, Paint paint) {
        final int w = getWidth();
        final int h = getHeight();
        final float s = Math.min(w, h) / 8.0f;
        final float r = s / 4.0f;

        // Shadow
        Path path = new Path();
        path.moveTo((w - r) / 2 + shadowOffset, s + r / 2 + shadowOffset);
        path.rQuadTo(r / 2, -r / 2, r, 0);
        path.rLineTo(s, s);
        path.rQuadTo(r, r, -r, r);
        path.lineTo((w + r) / 2 - s + shadowOffset, 2 * s + r * 1.5f + shadowOffset);
        path.rQuadTo(-r * 2, 0, -r, -r);
        path.close();
        paint.setColor(SHADOW_COLOR);
        canvas.drawPath(path, paint);

        // Value
        path = new Path();
        path.moveTo((w - r) / 2, s + r / 2);
        path.rQuadTo(r / 2, -r / 2, r, 0);
        path.rLineTo(s, s);
        path.rQuadTo(r, r, -r, r);
        path.lineTo((w + r) / 2 - s, 2 * s + r * 1.5f);
        path.rQuadTo(-r * 2, 0, -r, -r);
        path.close();
        paint.setColor(VALUE_COLOR);
        canvas.drawPath(path, paint);
    }

    private void drawValueBottom(Canvas canvas, Paint paint) {
        final int w = getWidth();
        final int h = getHeight();
        final float s = Math.min(w, h) / 8.0f;
        final float r = s / 4.0f;

        // Shadow
        Path path = new Path();
        path.moveTo((w - r) / 2 + shadowOffset, h - (s + r / 2) + shadowOffset);
        path.rQuadTo(r / 2, r / 2, r, 0);
        path.rLineTo(s, -s);
        path.rQuadTo(r, -r, -r, -r);
        path.lineTo((w + r) / 2 - s + shadowOffset, h - (2 * s + r * 1.5f) + shadowOffset);
        path.rQuadTo(-r * 2, 0, -r, r);
        path.close();
        paint.setColor(SHADOW_COLOR);
        canvas.drawPath(path, paint);

        // Value
        path = new Path();
        path.moveTo((w - r) / 2, h - (s + r / 2));
        path.rQuadTo(r / 2, r / 2, r, 0);
        path.rLineTo(s, -s);
        path.rQuadTo(r, -r, -r, -r);
        path.lineTo((w + r) / 2 - s, h - (2 * s + r * 1.5f));
        path.rQuadTo(-r * 2, 0, -r, r);
        path.close();
        paint.setColor(VALUE_COLOR);
        canvas.drawPath(path, paint);
    }

    private void drawValueLeft(Canvas canvas, Paint paint) {
        final int w = getWidth();
        final int h = getHeight();
        final float s = Math.min(w, h) / 8.0f;
        final float r = s / 4.0f;

        // Shadow
        Path path = new Path();
        path.moveTo(s + r / 2 + shadowOffset, (h - r) / 2 + shadowOffset);
        path.rQuadTo(-r / 2, r / 2, 0, r);
        path.rLineTo(s, s);
        path.rQuadTo(r, r, r, -r);
        path.lineTo(2 * s + r * 1.5f + shadowOffset, (h + r) / 2 - s + shadowOffset);
        path.rQuadTo(0, -r * 2, -r, -r);
        path.close();
        paint.setColor(SHADOW_COLOR);
        canvas.drawPath(path, paint);

        // Value
        path = new Path();
        path.moveTo(s + r / 2, (h - r) / 2);
        path.rQuadTo(-r / 2, r / 2, 0, r);
        path.rLineTo(s, s);
        path.rQuadTo(r, r, r, -r);
        path.lineTo(2 * s + r * 1.5f, (h + r) / 2 - s);
        path.rQuadTo(0, -r * 2, -r, -r);
        path.close();
        paint.setColor(VALUE_COLOR);
        canvas.drawPath(path, paint);
    }

    private void drawValueRight(Canvas canvas, Paint paint) {
        final int w = getWidth();
        final int h = getHeight();
        final float s = Math.min(w, h) / 8.0f;
        final float r = s / 4.0f;

        // Shadow
        Path path = new Path();
        path.moveTo(w - s - r / 2 + shadowOffset, (h - r) / 2 + shadowOffset);
        path.rQuadTo(r / 2, r / 2, 0, r);
        path.rLineTo(-s, s);
        path.rQuadTo(-r, r, -r, -r);
        path.lineTo(w - 2 * s - 1.5f * r + shadowOffset, (h + r) / 2 - s + shadowOffset);
        path.rQuadTo(0, -r * 2, r, -r);
        path.close();
        paint.setColor(SHADOW_COLOR);
        canvas.drawPath(path, paint);

        // Value
        path = new Path();
        path.moveTo(w - s - r / 2, (h - r) / 2);
        path.rQuadTo(r / 2, r / 2, 0, r);
        path.rLineTo(-s, s);
        path.rQuadTo(-r, r, -r, -r);
        path.lineTo(w - 2 * s - 1.5f * r, (h + r) / 2 - s);
        path.rQuadTo(0, -r * 2, r, -r);
        path.close();
        paint.setColor(VALUE_COLOR);
        canvas.drawPath(path, paint);
    }
}
