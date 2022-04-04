package com.example.dogvillev2.layout;

import android.content.res.Resources;

/**
 * GameDisplay object is responsible for scaling game to the device window.
 * All events and movements are calculated based on standard game window 480x320,
 * the window is scaled depending on the devise resolution.
 * Image resources used for game are also based on resolution 480x320.
 * For a better practice, resources for different screen resolutions should be provided.
 */
public class GameDisplay {

    // Standard game window size
    public static final int GAME_WIDTH = 480;
    public static final int GAME_HEIGHT = 320;

    // For transition from 3x2 to 16x9 screens
    public static int GAME_WIDTH_BUFF_CANVAS;
    public static int GAME_HEIGHT_BUFF_CANVAS;

    // Maximum resolution of the device
    public static int deviceWidthCapped;
    public static int deviceHeightCapped;

    // Scaling from game window size to device capped size
    public static double scalingFactor;

    // For converting events coordinates on the capped screen devices
    public static int DEVICE_WIDTH_REAL;
    public static int DEVICE_HEIGHT_REAL;

    public GameDisplay(int deviceWidthPixelsCapped, int deviceHeightPixelsCapped) {

        deviceWidthCapped = deviceWidthPixelsCapped;
        deviceHeightCapped = deviceHeightPixelsCapped;

        GAME_WIDTH_BUFF_CANVAS = GAME_WIDTH;
        GAME_HEIGHT_BUFF_CANVAS = GAME_HEIGHT;

        double scalingFactorX = (double) deviceWidthPixelsCapped / GAME_WIDTH;
        double scalingFactorY = (double) deviceHeightPixelsCapped / GAME_HEIGHT;

        scalingFactor = Math.min(scalingFactorX, scalingFactorY);

        // Resize game window for 16x9 screens
        if (scalingFactorX != scalingFactorY && scalingFactorX > scalingFactorY) {
            GAME_WIDTH_BUFF_CANVAS = GAME_WIDTH / (3 / 2) * (16 / 9);
        }

        DEVICE_WIDTH_REAL = Resources.getSystem().getDisplayMetrics().widthPixels;
        DEVICE_HEIGHT_REAL = Resources.getSystem().getDisplayMetrics().heightPixels;

        // Make sure that width from display metrics is bigger than height (in case if screen is rotated)
        if (DEVICE_WIDTH_REAL < DEVICE_HEIGHT_REAL) {
            int temp = DEVICE_WIDTH_REAL;
            DEVICE_WIDTH_REAL = DEVICE_HEIGHT_REAL;
            DEVICE_HEIGHT_REAL = temp;
        }
    }

    /**
     * Recalculate touch coordinate X to the standard game window coordinates (devise width -> game window width)
     *
     * @param touchPositionX returned by touch event
     * @return X coordinate relative to game window
     */
    public double realToGameWindowPositionX(double touchPositionX) {
        return touchPositionX / DEVICE_WIDTH_REAL * GAME_WIDTH;
    }

    /**
     * Recalculate touch coordinate Y to the standard game window coordinates (devise height -> game window height)
     *
     * @param touchPositionY returned by touch event
     * @return Y coordinate relative to game window
     */
    public double realToGameWindowPositionY(double touchPositionY) {
        return touchPositionY / DEVICE_HEIGHT_REAL * GAME_HEIGHT;
    }
}
