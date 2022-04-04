package com.example.dogvillev2.infopanels;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.dogvillev2.R;
import com.example.dogvillev2.layout.GameDisplay;

/**
 * GameIntro is a panel that draws welcoming text above main game view.
 * Fades gradually.
 */
public class GameIntro {

    private final String text = "Welcome to Dogville!"; // Text shown on the screen
    private final Paint paint;
    private final Paint backPaint;
    private final int gameWindowX;
    private final int gameWindowY;

    public int opacity = 255;
    private boolean isFading = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public GameIntro(Context context) {

        // Place text on screen
        gameWindowX = GameDisplay.GAME_WIDTH / 2;
        gameWindowY = GameDisplay.GAME_HEIGHT / 2;

        // Set back paint properties
        backPaint = new Paint();
        backPaint.setColor(Color.argb(255, 211, 210, 157));

        // Set text paint properties
        paint = new Paint();
        paint.setColor(Color.argb(255, 68, 76, 78));
        Typeface typeface = context.getResources().getFont(R.font.bitpotion);
        paint.setTypeface(typeface);
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    public void setFading(boolean fading) {
        isFading = fading;
    }

    /**
     * Draw panel on canvas and update state, called by the game loop.
     *
     * @param canvas - drawing surface
     */
    public void draw(Canvas canvas) {

        // Draw background color
        canvas.drawRect(0, 0, GameDisplay.GAME_WIDTH, GameDisplay.GAME_HEIGHT, backPaint);

        // Draw text
        canvas.drawText(text, gameWindowX, gameWindowY, paint);

        // Update background opacity
        backPaint.setAlpha(opacity);
        paint.setAlpha(opacity);
        if (isFading) {
            opacity -= 2;
            if (opacity < 170) {
                opacity -= 5;
            }
            if (opacity < 100) {
                opacity -= 10;
            }
        }
    }
}
