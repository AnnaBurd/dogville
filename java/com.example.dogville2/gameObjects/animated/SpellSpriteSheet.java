package com.example.dogvillev2.gameObjects.animated;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;

/**
 * Sprite sheet for the flying spell,
 * a single sprite sheet is used by multiple spell objects
 */
public class SpellSpriteSheet {

    // Number of frames in each row (inconsistent value in the source)
    protected static int[] ROW_MAX_FRAMES = {7, 14, 15};
    // Single frame size
    protected final int frameWidth;
    protected final int frameHeight;
    protected final Rect frameRect;
    // Number of row in a source image starting from 0
    protected final int ROW_LAUNCH = 0;
    protected final int ROW_FLIGHT = 1;
    protected final int ROW_HIT = 2;
    // Array of frames for different status
    private final Bitmap[] launchBitmaps;
    private final Bitmap[] flightBitmaps;
    private final Bitmap[] hitBitmaps;
    // For rotation of frames
    protected Matrix matrixOfBitmap = new Matrix();
    protected Matrix matrixFlipped = new Matrix();

    /**
     * Initialize sprite sheet (called by Game constructor)
     */
    public SpellSpriteSheet(Bitmap sourceImage, int colCountInSourceImage, int rowCountInSourceImage) {

        frameWidth = sourceImage.getWidth() / colCountInSourceImage;
        frameHeight = sourceImage.getHeight() / rowCountInSourceImage;
        this.frameRect = new Rect(0, 0, frameWidth, frameHeight);

        matrixFlipped.postScale(-1, 1);

        // Fill arrays with bitmap frames (sub-images of the source image)
        launchBitmaps = new Bitmap[ROW_MAX_FRAMES[ROW_LAUNCH]];
        flightBitmaps = new Bitmap[ROW_MAX_FRAMES[ROW_FLIGHT]];
        hitBitmaps = new Bitmap[ROW_MAX_FRAMES[ROW_HIT]];

        for (int col = 0; col < colCountInSourceImage; col++) {
            if (col < ROW_MAX_FRAMES[ROW_LAUNCH]) {
                launchBitmaps[col] = createFrameImageAt(sourceImage, ROW_LAUNCH, col);
            }
            if (col < ROW_MAX_FRAMES[ROW_FLIGHT]) {
                flightBitmaps[col] = createFrameImageAt(sourceImage, ROW_FLIGHT, col);
            }
            if (col < ROW_MAX_FRAMES[ROW_HIT]) {
                hitBitmaps[col] = createFrameImageAt(sourceImage, ROW_HIT, col);
            }
        }
    }

    /**
     * Get a single frame from the bitmap source (to fill the bitmap frames array)
     */
    private Bitmap createFrameImageAt(Bitmap image, int row, int col) {
        return Bitmap.createBitmap(
                image,
                col * frameWidth,
                row * frameHeight,
                frameWidth,
                frameHeight
        );
    }

    /**
     * Get frame from the array of frames that corresponds to current animation type (launch - flight - hit)
     */
    public Bitmap getFrame(int rowFrameUsing, int colFrameUsing) {
        Bitmap currentFrame;
        if (rowFrameUsing == ROW_LAUNCH) {
            currentFrame = launchBitmaps[colFrameUsing];
        } else if (rowFrameUsing == ROW_FLIGHT) {
            currentFrame = flightBitmaps[colFrameUsing];
        } else {
            currentFrame = hitBitmaps[colFrameUsing];
        }
        return currentFrame;
    }

    /**
     * Get current animation frame of the spell
     * (rotated direction is towards the position of the player when the spell was casted)
     */
    public Bitmap getFrame(int rowFrameUsing, int colFrameUsing, double directionX, double directionY) {
        Bitmap currentFrame = getFrame(rowFrameUsing, colFrameUsing);
        double angle = Math.atan2(directionY, directionX) * 180 / Math.PI;
        matrixOfBitmap.setRotate((float) angle);
        return Bitmap.createBitmap(currentFrame, 0, 0, frameWidth, frameHeight, matrixOfBitmap, true);
    }
}
