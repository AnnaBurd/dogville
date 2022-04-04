package com.example.dogvillev2.controls;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.dogvillev2.R;
import com.example.dogvillev2.utilities.BitmapLoader;
import com.example.dogvillev2.utilities.Utils;

/**
 * Joystick class creates a virtual joystick that controls movement of player
 */
public class Joystick {

    private final int outerCircleRadius;
    private final int outerCircleCenterPositionX;
    private final int outerCircleCenterPositionY;
    private final int MIDDLE = 1;
    private final int RIGHT = 2;
    private final int LEFT = 0;
    private final int UP = 3;
    private final int DOWN = 4;
    private final Bitmap[] joystickBitmaps;
    private final Rect joystickBitmapRect;
    private final Rect joystickOnScreenRect;
    private boolean isPressed;
    private double actuatorX;
    private double actuatorY;
    private int joystickFrameUsed = 1;

    public Joystick(int centerPositionX, int centerPositionY, BitmapLoader bitmapLoader) {

        // Radius of the circles
        outerCircleRadius = 25 + 20;

        // Bitmap size multiplier
        double sizeMultiplier = 3;

        // Outer and inner circles of the joystick (hidden behind bitmap)
        outerCircleCenterPositionX = centerPositionX;
        outerCircleCenterPositionY = centerPositionY;

        // Joystick frames
        joystickBitmaps = new Bitmap[5];
        joystickBitmaps[LEFT] = bitmapLoader.loadBitmap(R.drawable.joystick_2);
        joystickBitmaps[MIDDLE] = bitmapLoader.loadBitmap(R.drawable.joystick_0);
        joystickBitmaps[RIGHT] = bitmapLoader.loadBitmap(R.drawable.joystick_1);
        joystickBitmaps[UP] = bitmapLoader.loadBitmap(R.drawable.joystick_up);
        joystickBitmaps[DOWN] = bitmapLoader.loadBitmap(R.drawable.joystick_down);

        joystickBitmapRect = new Rect(
                0,
                0,
                joystickBitmaps[MIDDLE].getWidth(),
                joystickBitmaps[MIDDLE].getHeight()
        );

        joystickOnScreenRect = new Rect(
                (int) (centerPositionX - joystickBitmapRect.width() * sizeMultiplier / 2),
                (int) (centerPositionY - joystickBitmapRect.height() * sizeMultiplier / 2),
                (int) (centerPositionX - joystickBitmapRect.width() * sizeMultiplier / 2 + joystickBitmapRect.width() * sizeMultiplier),
                (int) (centerPositionY - joystickBitmapRect.height() * sizeMultiplier / 2 + joystickBitmapRect.height() * sizeMultiplier)
        );

    }

    /**
     * Update joystick position and frame used, called by the game loop.
     */
    public void update() {

        // Update joystick frame
        if (actuatorX > 0.5) {
            joystickFrameUsed = RIGHT;
        } else if (actuatorX < -0.5) {
            joystickFrameUsed = LEFT;
        } else if (actuatorY > 0.5) {
            joystickFrameUsed = DOWN;
        } else if (actuatorY < -0.5) {
            joystickFrameUsed = UP;
        } else {
            joystickFrameUsed = MIDDLE;
        }
    }

    /**
     * Draw joystick on the canvas, called by the game loop
     *
     * @param canvas main surface to draw
     */
    public void draw(Canvas canvas) {

        // Draw joystick from bitmap
        canvas.drawBitmap(joystickBitmaps[joystickFrameUsed],
                joystickBitmapRect,
                joystickOnScreenRect,
                null);
    }

    /**
     * Check if joystick is tapped on by the touch event
     *
     * @param touchPositionX of the event
     * @param touchPositionY of the event
     * @return true if touch position is within the joystick radius
     */
    public boolean isPressed(double touchPositionX, double touchPositionY) {

        double joystickCenterToTouchDistance = Utils.getDistanceBetweenPoints(
                outerCircleCenterPositionX,
                outerCircleCenterPositionY,
                touchPositionX,
                touchPositionY
        );
        return joystickCenterToTouchDistance < outerCircleRadius;
    }

    public boolean getIsPressed() {
        return isPressed;
    }

    public void setIsPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }

    /**
     * Actuator helps to convert movement of the joystick inner circle into the velocityX, velocityY
     * of the player
     */
    public void setActuator(double touchPositionX, double touchPositionY) {
        double deltaX = touchPositionX - outerCircleCenterPositionX;
        double deltaY = touchPositionY - outerCircleCenterPositionY;
        double deltaDistance = Utils.getDistanceBetweenPoints(0, 0, deltaX, deltaY);

        if (deltaDistance < outerCircleRadius) {
            actuatorX = deltaX / outerCircleRadius;
            actuatorY = deltaY / outerCircleRadius;
        } else {
            actuatorX = deltaX / deltaDistance;
            actuatorY = deltaY / deltaDistance;
        }
    }

    /**
     * On release of the joystick reset values of actuator.
     */
    public void resetActuator() {
        actuatorX = 0.0;
        actuatorY = 0.0;
    }

    /**
     * Return value of actuator on X axis,
     * used by the player object (or any other object that the joystick should control).
     */
    public double getActuatorX() {
        return actuatorX;
    }

    /**
     * Return value of actuator on Y axis,
     * not used in this game.
     */
    public double getActuatorY() {
        return actuatorY;
    }
}
