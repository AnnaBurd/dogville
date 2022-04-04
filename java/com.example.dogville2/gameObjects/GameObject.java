package com.example.dogvillev2.gameObjects;

import android.graphics.Canvas;

import com.example.dogvillev2.layout.GameView;

/**
 * Game object is an abstract class that represents all objects in the game
 * Coordinates of game objects are related to the GameLevel.
 */
public abstract class GameObject {

    protected GameView gameView;

    // Position on game level
    protected double positionOnLevelX;
    protected double positionOnLevelY;

    // Initial movement
    protected double velocityX = 0;
    protected double velocityY = 0;

    // Initial direction to the right
    protected double directionX = 1;
    protected double directionY = 0;

    public GameObject(GameView gameView, double positionOnLevelX, double positionOnLevelY) {
        this.gameView = gameView;
        this.positionOnLevelX = positionOnLevelX;
        this.positionOnLevelY = positionOnLevelY;
    }

    /**
     * Draw method is used by the game loop to draw game object on the canvas.
     *
     * @param canvas - surface to draw game on
     */
    public abstract void draw(Canvas canvas);

    /**
     * Update method is called by the game loop to update state of the game object (coordinates, animation frame etc.)
     */
    public abstract void update();

    public double getPositionX() {
        return gameView.getInWindowPositionX(positionOnLevelX);
    }

    public double getPositionY() {
        return gameView.getInWindowPositionY(positionOnLevelY);
    }

    public double getPositionOnLevelX() {
        return positionOnLevelX;
    }

    public double getPositionOnLevelY() {
        return positionOnLevelY;
    }

    public double getDirectionX() {
        return directionX;
    }

    public double getDirectionY() {
        return directionY;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }
}
