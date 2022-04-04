package com.example.dogvillev2.controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.dogvillev2.R;
import com.example.dogvillev2.layout.GameDisplay;

/**
 * Game panel that draws buttons on the canvas after game is ended,
 * provides options to repeat or exit the game.
 */
public class GameExitRepeatMenu {

    private final Button exit;
    private final Button restart;

    private final int FONT_SIZE = 30;
    private final int BUTTON_WIDTH = 130;
    private final int BUTTON_HEIGHT = 30;

    // The same paint is shared by all buttons
    private final Paint textPaint;
    private final Paint rectPaint;

    private final Context context;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public GameExitRepeatMenu(Context context) {

        this.context = context;

        // Set main paint properties
        rectPaint = new Paint();
        rectPaint.setStrokeWidth(4);

        textPaint = new Paint();
        textPaint.setTypeface(context.getResources().getFont(R.font.bitpotion));
        textPaint.setTextSize(FONT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);

        // Create and position button Exit on the screen
        int exitGameWindowX = GameDisplay.GAME_WIDTH_BUFF_CANVAS - BUTTON_WIDTH - 20;
        int exitGameWindowY = 250;
        RectF exitRect = new RectF(
                exitGameWindowX,
                exitGameWindowY,
                exitGameWindowX + BUTTON_WIDTH,
                exitGameWindowY + BUTTON_HEIGHT
        );
        exit = new Button("Exit game", exitRect);

        // Create and position button Restart on the screen
        int restartGameWindowX = 20;
        int restartGameWindowY = 250;
        RectF restartRect = new RectF(
                restartGameWindowX,
                restartGameWindowY,
                restartGameWindowX + BUTTON_WIDTH,
                restartGameWindowY + BUTTON_HEIGHT
        );
        restart = new Button("Restart game", restartRect);
    }


    /**
     * Draw restart and exit buttons on the game canvas
     *
     * @param canvas - drawing surface of the game
     */
    public void draw(Canvas canvas) {

        exit.draw(canvas);
        restart.draw(canvas);

    }

    /**
     * Check exit button state (called by the event handler)
     */
    public boolean isExitPressed(int touchPositionX, int touchPositionY) {
        exit.isPressed = exit.isPressed(touchPositionX, touchPositionY);
        return exit.isPressed;
    }

    /**
     * Check restart button state (called by the event handler)
     *
     * @param touchPositionX - touch event coordinate X  converted into game coordinates
     * @param touchPositionY - touch event coordinate Y converted into game coordinates
     */
    public boolean isRestartPressed(int touchPositionX, int touchPositionY) {
        restart.isPressed = restart.isPressed(touchPositionX, touchPositionY);
        return restart.isPressed;
    }


    /**
     * Private class represents a single button from the exit menu.
     */
    private class Button {
        private final String btnText;
        private final RectF btnRect;
        private final int textX;
        private final int textY;
        private boolean isPressed = false;

        Button(String text, RectF rect) {
            this.btnText = text;
            this.btnRect = rect;

            this.textX = (int) (rect.left + (BUTTON_WIDTH - textPaint.measureText(text)) / 2);
            this.textY = (int) (rect.top + BUTTON_HEIGHT - 10);
        }

        public boolean isPressed(int touchPositionX, int touchPositionY) {
            return btnRect.contains(touchPositionX, touchPositionY);
        }

        public void draw(Canvas canvas) {

            // Draw background rectangle
            rectPaint.setColor(Color.argb(255, 211, 210, 157));
            rectPaint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(
                    btnRect,
                    10,
                    10,
                    rectPaint
            );

            // Draw rectangle borders
            rectPaint.setStyle(Paint.Style.STROKE);
            rectPaint.setColor(Color.BLACK);
            canvas.drawRoundRect(btnRect, 10, 10, rectPaint);

            // Draw text
            if (isPressed) {
                textPaint.setColor(ContextCompat.getColor(context, R.color.gameOver));
            } else {
                textPaint.setColor(ContextCompat.getColor(context, R.color.black));
            }
            canvas.drawText(btnText, textX, textY, textPaint);
        }
    }
}
