package com.example.dogvillev2.infopanels;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.dogvillev2.R;
import com.example.dogvillev2.layout.GameDisplay;

/**
 * GameOver is a panel that draws text Game Over above main game view.
 */
public class GameOver {


    private final Paint paint;
    private String text = "GAME OVER";
    private float gameWindowX;
    private float gameWindowY;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public GameOver(Context context) {

        // Set text coordinates
        gameWindowX = GameDisplay.GAME_WIDTH / 2;
        gameWindowY = GameDisplay.GAME_HEIGHT / 2;

        // Set text paint properties
        paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.gameOver);
        paint.setColor(color);
        Typeface typeface = context.getResources().getFont(R.font.bitpotion);
        paint.setTypeface(typeface);
        paint.setTextSize(120);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setColor(int color) {
        this.paint.setColor(color);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void draw(Canvas canvas) {
        canvas.drawText(text, gameWindowX, gameWindowY, paint);
    }
}
