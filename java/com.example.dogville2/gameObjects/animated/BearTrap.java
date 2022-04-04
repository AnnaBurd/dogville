package com.example.dogvillev2.gameObjects.animated;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.dogvillev2.GameLoop;
import com.example.dogvillev2.gameObjects.GameObject;
import com.example.dogvillev2.layout.GameView;
import com.example.dogvillev2.sounds.GameSounds;

/**
 * Bear Trap object represents a single trap that can be placed anywhere on the game level.
 * It is activated by player.
 */
public class BearTrap extends GameObject {

    // Rate of changing animation frames
    private static final double FRAMES_PER_SECOND = 10;
    private static final int UPDATES_PER_FRAME = (int) (GameLoop.MAX_UPS / FRAMES_PER_SECOND);
    // Bitmap frames of the trap
    private final int frameWidth;
    private final int frameHeight;
    private final Rect frameRect;
    private final Bitmap[] frames;
    // Trap state is always unactivated on creation
    private final int UNACTIVATED = 0;
    private final int IN_ACTION = 2;
    private final int USED = 3; // the last frame in source bitmap, can add more frames
    GameSounds gameSounds;
    private int updatesTillNextFrame = UPDATES_PER_FRAME;
    private int currentFrameUsed = UNACTIVATED;

    public BearTrap(GameView gameView, double positionOnLevelX, double positionOnLevelY, Bitmap sourceImage, GameSounds gameSounds) {
        super(gameView, positionOnLevelX, positionOnLevelY);

        // Fill the array of frames from the source image ("cut" source image into squares)
        frameWidth = sourceImage.getWidth() / (USED + 1);
        frameHeight = sourceImage.getHeight();
        frameRect = new Rect(0, 0, frameWidth, frameHeight);
        frames = new Bitmap[USED + 1];
        for (int col = 0; col < (USED + 1); col++) {
            frames[col] = Bitmap.createBitmap(
                    sourceImage,
                    col * frameWidth,
                    0,
                    frameWidth,
                    frameHeight
            );
        }

        this.gameSounds = gameSounds;
    }


    /**
     * Draw BearTrap on canvas, called by the Game (inside GameLevelLayout.update)
     *
     * @param canvas - drawing surface
     */
    @Override
    public void draw(Canvas canvas) {

        // Get the rect of the frame in coordinates of the game view window (place to put the frame on the canvas)
        Rect inGameWindowRect = new Rect(
                (int) (this.getPositionX()),
                (int) (this.getPositionY()),
                (int) (this.getPositionX() + frameWidth),
                (int) (this.getPositionY() + frameHeight)
        );

        // Draw frame on canvas
        canvas.drawBitmap(
                frames[currentFrameUsed],
                frameRect,
                inGameWindowRect,
                null);
    }

    /**
     * Update trap frame, called by the Game (inside GameLevelLayout.update)
     * Updates only for traps in the activated state.
     */
    @Override
    public void update() {

        // The trap is not yet activated or already triggered, frame does not change
        if (currentFrameUsed == UNACTIVATED || currentFrameUsed == USED) {
            return;
        }

        // The trap was activated, animate its movement
        updatesTillNextFrame--;
        if (updatesTillNextFrame > 0) {
            return; // Wait until the number game loop iterations passes until changing frame
        }
        currentFrameUsed++; // Next frame
        updatesTillNextFrame = UPDATES_PER_FRAME;
    }

    /**
     * Try to trigger trap (called when player collides with the trap)
     */
    public void triggerTrap() {
        if (currentFrameUsed == UNACTIVATED) {
            currentFrameUsed++;

            gameSounds.playSoundClang();
        }
    }

    /**
     * Check if trap is activated already but animation is not finished yet.
     */
    public boolean isInAction() {
        return currentFrameUsed == IN_ACTION;
    }

    /**
     * Get frame with (used for collision calculations)
     */
    public int getFrameWidth() {
        return frameWidth;
    }

    /**
     * Get frame height (used for collision calculations)
     */
    public int getFrameHeight() {
        return frameHeight;
    }
}
