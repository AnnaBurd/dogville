package com.example.dogvillev2.infopanels;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.example.dogvillev2.GameLoop;
import com.example.dogvillev2.R;

/**
 * Performance class is responsible for drawing current UPS and FPS on screen.
 * Average UPS and FPS values are calculated in the GameLoop.
 */
public class Performance {
    private final GameLoop gameLoop;
    private final Context context;

    public Performance(Context context, GameLoop gameLoop) {
        this.context = context;
        this.gameLoop = gameLoop;
    }

    public void draw(Canvas canvas) {
        drawUPS(canvas);
        drawFPS(canvas);
    }

    public void drawUPS(Canvas canvas) {
        //String averageUPS = Double.toString(gameLoop.getAverageUPS());
        String averageUPS = String.format("%04.2f", gameLoop.getAverageUPS());
        Paint paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.magenta);
        paint.setColor(color);
        paint.setTextSize(20);
        canvas.drawText("UPS: " + averageUPS, 350, 30, paint);
    }

    public void drawFPS(Canvas canvas) {
        //String averageFPS = Double.toString(gameLoop.getAverageFPS());
        String averageFPS = String.format("%04.2f", gameLoop.getAverageFPS());

        Paint paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.magenta);
        paint.setColor(color);
        paint.setTextSize(20);
        canvas.drawText("FPS: " + averageFPS, 350, 50, paint);
    }
}
