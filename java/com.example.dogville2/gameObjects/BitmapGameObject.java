package com.example.dogvillev2.gameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.dogvillev2.layout.GameView;

/**
 * Bitmap Game Object represents bitmap game objects (not animated)
 */
public class BitmapGameObject extends GameObject {

    public Bitmap image;
    public Rect bitmapRectangle;

    private double speedMultiplier = 1.0; // Speed relative to player (for parallax movement effect)

    public BitmapGameObject(Bitmap image, GameView gameView, double positionOnLevelX, double positionOnLevelY) {
        super(
                gameView,
                positionOnLevelX,
                positionOnLevelY
        );

        this.image = image;
        this.bitmapRectangle = new Rect(0, 0, image.getWidth(), image.getHeight());
    }

    /**
     * Set velocity (speed) relative to player movement (game window movement)
     *
     * @param speedMultiplier - multiplier to the game window velocity
     */
    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    /**
     * Update position of BitmapGameObject on the level:
     * relative to player movement, and based on the own velocity (speed).
     */
    @Override
    public void update() {

        // Update position relative to player movement
        if (gameView.isMoving()) {
            positionOnLevelX += gameView.getLevelVelocityX() * (1 - speedMultiplier);
        }
        // Update position based on own velocity
        positionOnLevelX += velocityX;
        positionOnLevelY += velocityY;
    }

    /**
     * Draw BitmapGameObject on canvas, called by the Game object
     *
     * @param canvas - any canvas from the Game class
     */
    @Override
    public void draw(Canvas canvas) {

        Rect inGameWindowRectangle = new Rect(
                (int) (this.getPositionX()),
                (int) (this.getPositionY()),
                (int) (this.getPositionX() + bitmapRectangle.width()),
                (int) (this.getPositionY() + bitmapRectangle.height())
        );

        canvas.drawBitmap(
                image,
                bitmapRectangle,
                inGameWindowRectangle,
                null
        );
    }
}
