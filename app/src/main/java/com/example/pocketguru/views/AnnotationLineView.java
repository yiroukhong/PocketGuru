package com.example.pocketguru.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class AnnotationLineView extends View {

    private Paint linePaint;
    private Paint dotPaint;
    private float startX, startY, endX, endY;
    private boolean coordinatesSet = false;
    private boolean drawArrow = true;

    public AnnotationLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor("#1A1A1A")); // near black
        linePaint.setStrokeWidth(4f);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setColor(Color.parseColor("#1A1A1A"));
        dotPaint.setStyle(Paint.Style.FILL);
    }

    public void setCoordinates(float startX, float startY, float endX, float endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.coordinatesSet = true;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!coordinatesSet) return;
        
        canvas.drawLine(startX, startY, endX, endY, linePaint);
        canvas.drawCircle(startX, startY, 6f, dotPaint);

        if (drawArrow) {
            drawArrowhead(canvas, startX, startY, endX, endY);
        } else {
            canvas.drawCircle(endX, endY, 6f, dotPaint);
        }
    }

    private void drawArrowhead(Canvas canvas, float fromX, float fromY, float toX, float toY) {
        float angle = (float) Math.atan2(toY - fromY, toX - fromX);
        float arrowSize = 30f;
        float arrowAngle = (float) Math.toRadians(30);

        float x1 = (float) (toX - arrowSize * Math.cos(angle - arrowAngle));
        float y1 = (float) (toY - arrowSize * Math.sin(angle - arrowAngle));
        float x2 = (float) (toX - arrowSize * Math.cos(angle + arrowAngle));
        float y2 = (float) (toY - arrowSize * Math.sin(angle + arrowAngle));

        canvas.drawLine(toX, toY, x1, y1, linePaint);
        canvas.drawLine(toX, toY, x2, y2, linePaint);
    }
}
