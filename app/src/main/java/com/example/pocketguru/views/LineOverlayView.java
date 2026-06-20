package com.example.pocketguru.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LineOverlayView extends View {

    private final Paint paint = new Paint();
    private final List<float[]> lines = new ArrayList<>();

    public LineOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setColor(Color.parseColor("#FFD93D"));
        paint.setStrokeWidth(convertDpToPx(4));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (float[] line : lines) {
            canvas.drawLine(line[0], line[1], line[2], line[3], paint);
        }
    }

    public void addLine(float startX, float startY, float endX, float endY) {
        lines.add(new float[]{startX, startY, endX, endY});
        invalidate();
    }

    private float convertDpToPx(int dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }
}
