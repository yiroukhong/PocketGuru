package com.example.pocketguru.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class DrawingView extends View {

    private Paint paint;
    private Path path;
    private boolean isTracking = false;
    private boolean isLocked = false;
    private float currentAlpha = 1.0f;
    private OnLineCompleteListener listener;

    public interface OnLineCompleteListener {
        void onLineComplete();
    }

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.parseColor("#4FC3F7"));
        paint.setStrokeWidth(convertDpToPx(8));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);

        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setAlpha((int) (currentAlpha * 255));
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isLocked) return false;

        float x = event.getX();
        float y = event.getY();
        float height = getHeight();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Check if starting at roots (bottom 25%)
                if (y > height * 0.75) {
                    isTracking = true;
                    currentAlpha = 1.0f;
                    path.reset();
                    path.moveTo(x, y);
                    invalidate();
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                if (isTracking) {
                    path.lineTo(x, y);
                    invalidate();
                }
                return true;

            case MotionEvent.ACTION_UP:
                if (isTracking) {
                    isTracking = false;
                    // Check if ending at leaves (top 30%)
                    if (y < height * 0.3) {
                        if (listener != null) {
                            listener.onLineComplete();
                        }
                    } else {
                        fadeOutPath();
                    }
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void fadeOutPath() {
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0f);
        animator.setDuration(400);
        animator.addUpdateListener(animation -> {
            currentAlpha = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                path.reset();
                currentAlpha = 1.0f;
                invalidate();
            }
        });
        animator.start();
    }

    public void setOnLineCompleteListener(OnLineCompleteListener listener) {
        this.listener = listener;
    }

    public void lockDrawing() {
        this.isLocked = true;
    }

    private float convertDpToPx(int dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }
}
