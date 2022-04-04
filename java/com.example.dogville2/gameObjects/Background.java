package com.example.dogvillev2.gameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.dogvillev2.layout.GameDisplay;
import com.example.dogvillev2.layout.GameView;

/**
 * Background class represents looping background images
 */
public class Background extends BitmapGameObject {

    public Background(Bitmap image, GameView gameView, double positionOnLevelX, double positionOnLevelY) {
        super(image, gameView, positionOnLevelX, positionOnLevelY);
    }

    /**
     * Updates coordinates of the background in the game window (window of standard game size GAME_WIDTH and GAME_HEIGHT).
     * If the background moved outside the game window (the player moved on the level),
     * changes the coordinates so the same background is shown again.
     */
    @Override
    public void update() {

        super.update();

        if (positionOnLevelX < gameView.getWindowPositionX() - GameDisplay.GAME_WIDTH_BUFF_CANVAS) {
            positionOnLevelX = gameView.getWindowPositionX();
        } else if (positionOnLevelX > gameView.getWindowPositionX()) {
            positionOnLevelX = gameView.getWindowPositionX() - GameDisplay.GAME_WIDTH_BUFF_CANVAS;
        }
    }


    /**
     * Draws two consequent background images to fill up the whole game window
     *
     * @param canvas the drawing surface
     */
    @Override
    public void draw(Canvas canvas) {

        // Draw left image
        canvas.drawBitmap(
                image,
                bitmapRectangle,
                new Rect((int) this.getPositionX(),
                        (int) this.getPositionY(),
                        (int) this.getPositionX() + bitmapRectangle.width(),
                        (int) this.getPositionY() + bitmapRectangle.height()),
                //gameDisplay.getRectangleForBuffCanvas(this.getPositionX(), this.getPositionY(), bitmapRectangle.width(), bitmapRectangle.height()),
                null
        );

        // Draw right image
        if (positionOnLevelX < gameView.getWindowPositionX()) {
            canvas.drawBitmap(
                    image,
                    bitmapRectangle,
                    new Rect((int) this.getPositionX() - 1 + GameDisplay.GAME_WIDTH_BUFF_CANVAS,
                            (int) this.getPositionY(),
                            (int) this.getPositionX() - 1 + GameDisplay.GAME_WIDTH_BUFF_CANVAS + bitmapRectangle.width(),
                            (int) this.getPositionY() + bitmapRectangle.height()),
                    //gameDisplay.getRectangleForBuffCanvas(this.getPositionX() - 1 + GameDisplay.GAME_WIDTH_BUFF_CANVAS, this.getPositionY(), bitmapRectangle.width(), bitmapRectangle.height()),
                    null);
        }
    }
}
