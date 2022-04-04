package com.example.dogvillev2.layout;

/**
 * Responsible for following the current position of the player in the game window and
 * for placing static game objects (static relative to road)
 * Coordinates are relative to game size 480x320
 */
public class GameView {

    private double windowPositionX;
    private double levelVelocityX;
    private boolean isMoving = false; // Is game view moving or not (controlled by the actions of the player in game class)

    // Not used, the level only moves horizontally
    private double windowPositionY;
    private double levelVelocityY;

    /**
     * Update position of the game view (game window) relative to static level layer coordinates
     */
    public void update() {
        if (isMoving) {
            windowPositionX += levelVelocityX;
            //windowPositionY += levelVelocityY;
        }
    }

    /**
     * Get velocity between game window view and game level layer
     */
    public double getLevelVelocityX() {
        return levelVelocityX;
    }

    /**
     * Set velocity between game window view and game level layer
     */
    public void setLevelVelocityX(double levelVelocityX) {
        this.levelVelocityX = levelVelocityX;
    }

    /**
     * Get current position of game window view
     */
    public double getWindowPositionX() {
        return windowPositionX;
    }

    /**
     * Check if game window view is moving
     */
    public boolean isMoving() {
        return isMoving;
    }

    /**
     * Set game window movement true / false
     */
    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    /**
     * Convert level layer coordinate into game window view coordinate X
     * (to draw game objects correctly in a moving game window view)
     */
    public double getInWindowPositionX(double positionOnLevelX) {
        return positionOnLevelX - windowPositionX;
    }

    /**
     * Convert level layer coordinate into game window view coordinate Y
     * (to draw game objects correctly in a moving game window view)
     */
    public double getInWindowPositionY(double positionOnLevelY) {
        return positionOnLevelY - windowPositionY;
    }

    /**
     * Check if coordinate is within game window view - to load into memory and draw
     * only objects that can be viewed, not the whole game level
     */
    public boolean isWithinGameWindow(double location) {
        return windowPositionX - GameDisplay.GAME_WIDTH_BUFF_CANVAS < location && location < windowPositionX + GameDisplay.GAME_WIDTH_BUFF_CANVAS;
    }
}
