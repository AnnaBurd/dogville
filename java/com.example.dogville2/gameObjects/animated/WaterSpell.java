package com.example.dogvillev2.gameObjects.animated;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.dogvillev2.GameLoop;
import com.example.dogvillev2.gameObjects.GameObject;
import com.example.dogvillev2.gameObjects.characters.FilthySlime;
import com.example.dogvillev2.gameObjects.characters.Sprite;
import com.example.dogvillev2.layout.GameView;
import com.example.dogvillev2.utilities.Utils;

/**
 * Spell cast represents an flying object casted by enemy.
 * Destroyed on collision with player
 */
public class WaterSpell extends GameObject {

    // Maximum speed of the spell
    public static final double SPEED_PIXELS_PER_SECOND = 100; // Spell flows fast
    private static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS; // pixels per update

    // The rate of changing frames
    private static final double FRAMES_PER_SECOND = 10;
    private static final int UPDATES_PER_FRAME = (int) (GameLoop.MAX_UPS / FRAMES_PER_SECOND);
    protected int rowUsingAnimationFrame;
    protected int colUsingAnimationFrame;
    SpellSpriteSheet spellSpriteSheet;
    private int updatesTillNextFrame = UPDATES_PER_FRAME;
    private boolean isFinishedAnimation = false;
    private boolean countedHit = false;


    /**
     * Cast single spell, every game object with directionX, directionY can cast spells.
     */
    public WaterSpell(SpellSpriteSheet spellSpriteSheet, GameView gameView, FilthySlime spellCaster) {
        super(
                gameView,
                spellCaster.getPositionOnLevelX(),
                spellCaster.getPositionOnLevelY() + spellCaster.getCharacterFrameWidth() / 2 - spellSpriteSheet.frameHeight / 2
        );

        this.spellSpriteSheet = spellSpriteSheet;

        this.rowUsingAnimationFrame = spellSpriteSheet.ROW_LAUNCH;
        this.colUsingAnimationFrame = 0;

        // Constant direction from launch
        this.directionX = spellCaster.getDirectionX();
        this.directionY = spellCaster.getDirectionY();

        // Constant velocity from launch
        velocityX = spellCaster.getDirectionX() * MAX_SPEED;
        velocityY = spellCaster.getDirectionY() * MAX_SPEED;
    }


    /**
     * Update spell state (position on screen, animation frame).
     * Called inside the Game.
     */
    @Override
    public void update() {
        // ------------------------ Manage X Y coordinates---------------------------//
        positionOnLevelX += velocityX;
        positionOnLevelY += velocityY;

        // ------------------------ Manage animation ------------------------------//
        updatesTillNextFrame--;

        // Wait until updates pass before switching frame
        if (updatesTillNextFrame > 0) {
            return;
        }

        // Change animation frame
        colUsingAnimationFrame++;
        if (rowUsingAnimationFrame == spellSpriteSheet.ROW_LAUNCH && colUsingAnimationFrame >= SpellSpriteSheet.ROW_MAX_FRAMES[spellSpriteSheet.ROW_LAUNCH]) {
            // Transition from launch to flight animation
            colUsingAnimationFrame = 0;
            rowUsingAnimationFrame = spellSpriteSheet.ROW_FLIGHT;
        } else if (rowUsingAnimationFrame == spellSpriteSheet.ROW_FLIGHT && colUsingAnimationFrame >= SpellSpriteSheet.ROW_MAX_FRAMES[spellSpriteSheet.ROW_FLIGHT]) {
            // Repeat flight animation
            colUsingAnimationFrame = 0;
        } else if (rowUsingAnimationFrame == spellSpriteSheet.ROW_HIT && colUsingAnimationFrame == SpellSpriteSheet.ROW_MAX_FRAMES[spellSpriteSheet.ROW_HIT]) {
            colUsingAnimationFrame--;
            // Set animation status finished on end of hit
            isFinishedAnimation = true;
        }

        // Number of updates before next frame change
        updatesTillNextFrame = UPDATES_PER_FRAME;
    }

    /**
     * Draw spell on the game canvas, called by the draw() method of the Game
     */
    @Override
    public void draw(Canvas canvas) {

        Bitmap currentFrame;

        // Get current frame bitmap and rotate it
        if (directionX != 1 || directionY != directionX) {
            currentFrame = spellSpriteSheet.getFrame(rowUsingAnimationFrame, colUsingAnimationFrame, directionX, directionY);
        } else {
            currentFrame = spellSpriteSheet.getFrame(rowUsingAnimationFrame, colUsingAnimationFrame);
        }

        Rect inGameWindowRectangle = new Rect(
                (int) (this.getPositionX()),
                (int) (this.getPositionY()),
                (int) (this.getPositionX() + spellSpriteSheet.frameWidth),
                (int) (this.getPositionY() + spellSpriteSheet.frameHeight)
        );

        // Draw current frame bitmap on canvas
        canvas.drawBitmap(
                currentFrame,
                spellSpriteSheet.frameRect,
                inGameWindowRectangle,
                null);
    }

    /**
     * Trigger hit animation, called after check for collision with the player in Game
     */
    public void setAnimationHit() {
        if (this.rowUsingAnimationFrame != spellSpriteSheet.ROW_HIT) {
            this.rowUsingAnimationFrame = spellSpriteSheet.ROW_HIT;
            colUsingAnimationFrame = 0;
        }
        this.velocityX = 0;
        this.velocityY = 0;
    }

    /**
     * Check if the spell object is ready to be removed from the game.
     */
    public boolean isFinishedAnimation() {
        return isFinishedAnimation;
    }

    /**
     * Check for the collision of the spell with the sprite (player, enemy or friend).
     */
    public boolean isHit(Sprite object) {
        double centerX = this.getPositionX() + spellSpriteSheet.frameWidth / 2;
        double centerY = this.getPositionY() + spellSpriteSheet.frameHeight / 2;

        double centerObjectX = object.getPositionX() + object.getCharacterFrameWidth() / 2;
        double centerObjectY = object.getPositionY() + object.getCharacterFrameHeight() / 2;

        double objectRadius = object.getCharacterFrameWidth();

        return (Utils.getDistanceBetweenPoints(
                centerX,
                centerY,
                centerObjectX,
                centerObjectY
        ) < objectRadius - 27);
    }

    /**
     * Hit animation takes multiple updates, the countedHit flag helps to count damage only once.
     * Called by the Game before taking health points.
     */
    public void countHit() {
        this.countedHit = true;
    }

    /**
     * Is spell damage already counted.
     */
    public boolean isCountedHit() {
        return countedHit;
    }
}
