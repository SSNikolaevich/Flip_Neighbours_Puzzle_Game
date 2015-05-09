package com.github.ssnikolaevich.flipgame;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.github.ssnikolaevich.flipgame.game.Tile;
import com.github.ssnikolaevich.flipgame.game.Value;


public class TileView extends View {
    private Tile tile;
    private boolean isFront;
    private Bitmap frontBitmap;
    private Bitmap backBitmap;
    private Paint paint;

    private AnimatorSet flipInAnimator;
    private AnimatorSet flipOutAnimator;

    private Animator.AnimatorListener animatorListener;

    private int maxTileSize;

    private final static int FRONT_COLOR = Color.rgb(115, 210, 22);
    private final static int BACK_COLOR = Color.rgb(245, 121, 0);
    private final static int VALUE_COLOR = Color.rgb(255, 255, 255);
    private final static int SHADOW_COLOR = Color.argb(64, 0, 0, 0);

    private final static float shadowOffset = 4.0f;

    public TileView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public TileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        tile = new Tile();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        frontBitmap = null;
        backBitmap = null;

        animatorListener = null;
        flipOutAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(
                context,
                R.anim.flipout
        );
        flipInAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(
                context,
                R.anim.flipin
        );
        flipInAnimator.setTarget(this);
        flipOutAnimator.setTarget(this);
        flipInAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                animatorListener.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isFront = !isFront;
                postInvalidate();
                flipOutAnimator.start();
            }
        });
        flipOutAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorListener.onAnimationEnd(animation);
            }
        });

        maxTileSize = 0;
    }

    public void setAnimatorListener(Animator.AnimatorListener listener) {
        animatorListener = listener;
    }

    public void setMaxTileSize(int size) {
        maxTileSize = size;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
        isFront = tile.getVisibleSide() == Tile.FRONT;
    }

    public void flip() {
        flipInAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int size = Math.min(maxTileSize, (width > height)? height : width);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        makeBitmaps(w, h);
    }

    private void makeBitmaps(int w, int h) {
        frontBitmap = makeBitmap(w, h, tile.getFront(), true);
        backBitmap = makeBitmap(w, h, tile.getBack(), false);
    }

    private Bitmap makeBitmap(int w, int h, Value value, boolean isFront) {
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawTile(canvas, w, h, isFront);
        drawValue(canvas, w, h, value);
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap = isFront? frontBitmap : backBitmap;
        if (bitmap != null)
            canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    private void drawTile(Canvas canvas, int w, int h, boolean isFront) {
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

    private void drawValue(Canvas canvas, int w, int h, Value value) {
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
