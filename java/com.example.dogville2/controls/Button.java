package com.example.dogvillev2.controls;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Button class represents a single bitmap button with two states: pressed or released.
 * It is used in Game to control player jumps / attacks.
 */
public class Button {

    private final Bitmap[] buttonBitmaps;
    private final Rect buttonBitmapRect;
    private final Rect buttonOnScreenRect;
    private final int PRESSED = 1;
    private final int RELEASED = 0;
    double sizeMultiplier = 3; // Resize button bitmap on the screen
    private boolean isPressed = false;
    private int buttonFrameUsed = 0;
    private int updatesTillRelease; // Number of game loop updates per second is controlled in game loop

    public Button(int centerPositionX, int centerPositionY, Bitmap releasedBitmap, Bitmap pressedBitmap) {

        buttonBitmaps = new Bitmap[2];
        buttonBitmaps[RELEASED] = releasedBitmap;
        buttonBitmaps[PRESSED] = pressedBitmap;

        buttonBitmapRect = new Rect(
                0,
                0,
                releasedBitmap.getWidth(),
                releasedBitmap.getHeight()
        );

        // Position of button on the game screen
        buttonOnScreenRect = new Rect(
                (int) (centerPositionX - buttonBitmapRect.width() * sizeMultiplier / 2),
                (int) (centerPositionY - buttonBitmapRect.height() * sizeMultiplier / 2),
                (int) (centerPositionX - buttonBitmapRect.width() * sizeMultiplier / 2 + buttonBitmapRect.width() * sizeMultiplier),
                (int) (centerPositionY - buttonBitmapRect.height() * sizeMultiplier / 2 + buttonBitmapRect.height() * sizeMultiplier)
        );
    }

    /**
     * Check for button press - pressed if touch event coordinates are within the button rectangle
     */
    public boolean isPressed(int touchPositionX, int touchPositionY) {
        isPressed = buttonOnScreenRect.contains(touchPositionX, touchPositionY);
        return isPressed;
    }

    /**
     * Update button state, called each iteration of the game loop
     */
    public void update() {

        // Start press animation
        if (buttonFrameUsed != PRESSED && isPressed) {
            buttonFrameUsed = PRESSED;
            updatesTillRelease = 10;
        }

        // Start release animation
        if (buttonFrameUsed == PRESSED && !isPressed) {
            buttonFrameUsed = RELEASED;
        }

        // Release button after press
        if (updatesTillRelease > 0) {
            updatesTillRelease--;
            return;
        }
        // At this point, press animation is finished, release button
        isPressed = false;
    }

    /**
     * Draw button on canvas, called by the game loop
     *
     * @param canvas surface to draw on
     */
    public void draw(Canvas canvas) {

        canvas.drawBitmap(
                buttonBitmaps[buttonFrameUsed],
                buttonBitmapRect,
                buttonOnScreenRect,
                null);
    }
}

