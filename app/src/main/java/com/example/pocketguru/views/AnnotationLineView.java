package com.example.pocketguru.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class AnnotationLineView extends View {

    private Paint linePaint;
    private Paint dotPaint;
    private List<float[]> lines = new ArrayList<>(); // each float[]: {startX, startY, endX, endY}

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

    public void addLine(float startX, float startY, float endX, float endY) {
        lines.add(new float[]{startX, startY, endX, endY});
        invalidate();
    }

    public void clearLines() {
        lines.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (float[] line : lines) {
            float sX = line[0];
            float sY = line[1];
            float eX = line[2];
            float eY = line[3];
            
            canvas.drawLine(sX, sY, eX, eY, linePaint);
            canvas.drawCircle(sX, sY, 6f, dotPaint);
            drawArrowhead(canvas, sX, sY, eX, eY);
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
