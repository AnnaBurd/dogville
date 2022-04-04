package com.example.dogvillev2.gameObjects.characters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.dogvillev2.GameLoop;
import com.example.dogvillev2.gameObjects.GameObject;
import com.example.dogvillev2.layout.GameView;
import com.example.dogvillev2.sounds.GameSounds;
import com.example.dogvillev2.utilities.Utils;

/**
 * Represents game characters and player, each sprite is animated with the characterSpriteSheet object
 */
public abstract class Sprite extends GameObject {

    // Health
    public static final int MAX_HEALTH_POINTS = 100;
    // Rate of changing animation frames
    private static final double FRAMES_PER_SECOND = 10;
    private static final int UPDATES_PER_FRAME = (int) (GameLoop.MAX_UPS / FRAMES_PER_SECOND);
    protected final CharacterSpriteSheet characterSpriteSheet;
    public int updatesTillNextFrame = UPDATES_PER_FRAME;
    public boolean isSpriteDead = false;
    public boolean playedLastSound = false;
    protected int rowUsingFrame; // current type of action
    protected int colUsingFrame; // current frame of movement
    protected boolean isLockedAnimation = false;
    protected GameSounds gameSounds;
    private int healthPoints = MAX_HEALTH_POINTS;

    public Sprite(GameView gameView, double positionX, double positionY, CharacterSpriteSheet spriteSheet, GameSounds gameSounds) {
        super(gameView, positionX, positionY);
        this.characterSpriteSheet = spriteSheet;
        this.gameSounds = gameSounds;
    }

    /**
     * Distance between sprites is calculated as distance center points of the sprite frame rectangles.
     */
    public static double getDistanceBetweenSprites(Sprite sprite1, Sprite sprite2) {
        return Utils.getDistanceBetweenPoints(
                sprite2.getPositionX() + sprite2.getCharacterFrameWidth() / 2,
                sprite2.getPositionY() + sprite2.getCharacterFrameHeight() / 2,
                sprite1.getPositionX() + sprite1.getCharacterFrameWidth() / 2,
                sprite1.getPositionY() + sprite1.getCharacterFrameHeight() / 2
        );
    }

    public int getHealthPoints() {
        return this.healthPoints;
    }

    public void changeHealthPoint(int points) {

        healthPoints += points;

        if (healthPoints <= 0) {
            healthPoints = 0;
            setAnimation(CharacterSpriteSheet.ROW_DEATH, true);
            playLastSound();
            return;
        }
        setAnimation(CharacterSpriteSheet.ROW_HURT, true);
    }

    protected abstract void playLastSound();

    public boolean isDead() {
        return this.healthPoints == 0;
    }

    @Override
    public void update() {

        // Don't update statement of the dead characters
        if (isSpriteDead) {
            return;
        }

        // Update current animation frame
        updatesTillNextFrame--;
        if (updatesTillNextFrame <= 0) {
            updatesTillNextFrame = UPDATES_PER_FRAME;
            colUsingFrame++;
            if (colUsingFrame == characterSpriteSheet.ROW_MAX_FRAMES[rowUsingFrame]) {
                colUsingFrame = 0;
                // Release animation lock after finishing one row of animation
                isLockedAnimation = false;

                // Make sure that death animation only played once
                if (rowUsingFrame == CharacterSpriteSheet.ROW_DEATH) {
                    isSpriteDead = true;
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap currentFrame;

        if (isSpriteDead) {
            currentFrame = characterSpriteSheet.getCurrentMoveFrame(CharacterSpriteSheet.ROW_DEATH, characterSpriteSheet.ROW_MAX_FRAMES[CharacterSpriteSheet.ROW_DEATH] - 1, directionX < 0);
        } else {
            currentFrame = characterSpriteSheet.getCurrentMoveFrame(rowUsingFrame, colUsingFrame, directionX < 0);
        }

        Rect inGameWindowRectangle = new Rect(
                (int) (this.getPositionX()),
                (int) (this.getPositionY()),
                (int) (this.getPositionX() + characterSpriteSheet.frameWidth),
                (int) (this.getPositionY() + characterSpriteSheet.frameHeight)
        );

        canvas.drawBitmap(
                // Flip the bitmap if the direction of movement is reversed
                currentFrame,
                characterSpriteSheet.frameRect,
                inGameWindowRectangle,
                null
        );
    }

    public void setAnimation(int animationRow) {
        if (rowUsingFrame != animationRow && !isLockedAnimation) {
            rowUsingFrame = animationRow;
            colUsingFrame = 0;
        }
    }

    /**
     * Set row of animation frames of a sprite. Locked animations can not be interrupted,
     * except death animation
     */
    public void setAnimation(int animationRow, boolean lock) {
        // Set locked animation (can not be interrupted).
        if (rowUsingFrame != animationRow && (!isLockedAnimation || animationRow == CharacterSpriteSheet.ROW_DEATH)) {
            rowUsingFrame = animationRow;
            isLockedAnimation = true;
            colUsingFrame = 0;
        }
    }

    public int getCharacterFrameWidth() {
        return characterSpriteSheet.frameWidth;
    }

    public int getCharacterFrameHeight() {
        return characterSpriteSheet.frameHeight;
    }
}
