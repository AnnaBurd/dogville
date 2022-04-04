package com.example.dogvillev2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.view.SurfaceHolder;

import androidx.annotation.RequiresApi;

import com.example.dogvillev2.layout.GameDisplay;

/**
 * GameLoop is a Thread that repeats game cycle and calls game update() and draw() methods.
 * Each iteration the game state is updated, then drawn on the buffered Bitmap,
 * and the resulting bitmap is rescaled onto the device screen.
 */
public class GameLoop extends Thread {
    public static final double MAX_UPS = 60.0; // Number of updates per second
    private static final double UPS_PERIOD = 1E+3 / MAX_UPS;
    private final SurfaceHolder surfaceHolder;
    private final Game game;
    private final Bitmap buffCanvasBitmap;
    private final Canvas buffCanvas;
    private final Rect buffCanvasBitmapRect;
    private final Rect realCanvasRect;
    private boolean isRunning = false;
    private double averageUPS;
    private double averageFPS;

    public GameLoop(Game game, SurfaceHolder surfaceHolder) {
        this.game = game;
        this.surfaceHolder = surfaceHolder;

        // Create buffer canvas - fixes flickering of bitmaps and more efficient that direct drawing on canvas
        buffCanvasBitmap = Bitmap.createBitmap(GameDisplay.GAME_WIDTH_BUFF_CANVAS, GameDisplay.GAME_HEIGHT_BUFF_CANVAS, Bitmap.Config.ARGB_8888);
        buffCanvas = new Canvas();
        buffCanvas.setBitmap(buffCanvasBitmap);
        buffCanvasBitmapRect = new Rect(0, 0, GameDisplay.GAME_WIDTH_BUFF_CANVAS, GameDisplay.GAME_HEIGHT_BUFF_CANVAS);
        realCanvasRect = new Rect(0, 0, GameDisplay.deviceWidthCapped, GameDisplay.deviceHeightCapped);

    }

    public double getAverageUPS() {
        return averageUPS;
    }

    public double getAverageFPS() {
        return averageFPS;
    }

    public void startLoop() {
        isRunning = true;
        start(); // implemented in Thread class
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {

        // Declare time and cycle count variables
        int updateCount = 0;
        int frameCount = 0;

        // Time counters
        long startTime;
        long elapsedTIme;
        long sleepTime;

        // Create new empty canvas
        Canvas canvas = null;

        // Game loop
        startTime = System.currentTimeMillis(); // System.nanoTime() is more error-prone but heavier
        while (isRunning) {

            // Try to update game (update()) and render(draw())
            try {

                // Get the canvas
                canvas = surfaceHolder.lockCanvas();
                // Prohibit multiple threads from calling update and draw methods at the same time as this thread
                synchronized (surfaceHolder) {
                    game.update();
                    updateCount++;
                    game.draw(buffCanvas);
                    canvas.drawBitmap(buffCanvasBitmap, buffCanvasBitmapRect, realCanvasRect, null);
                }

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    // Release the canvas
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas); // return canvas back
                        frameCount++;
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Pause game loop to not exceed target UPS (MAX_UPS)
            elapsedTIme = System.currentTimeMillis() - startTime;
            sleepTime = (long) (updateCount * UPS_PERIOD - elapsedTIme); // calculate time till the end of the iteration
            if (sleepTime > 0) {
                try {
                    sleep(sleepTime); // implemented in Thread class
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Skip frames to keep up with target UPS
            while (sleepTime < 0 && updateCount < MAX_UPS - 1) {
                // Update the game state without rendering
                game.update();
                updateCount++;
                // Recalculate the sleep time
                elapsedTIme = System.currentTimeMillis() - startTime;
                sleepTime = (long) (updateCount * UPS_PERIOD - elapsedTIme);
            }

            // Calculate average UPS and FPS (1000 ms = 1 second)
            elapsedTIme = System.currentTimeMillis() - startTime;
            if (elapsedTIme >= 1000) {
                averageUPS = updateCount / (elapsedTIme * 0.001);
                averageFPS = frameCount / (elapsedTIme * 0.001);
                updateCount = 0;
                frameCount = 0;
                startTime = System.currentTimeMillis();
            }
        }
    }

    public void stopLoop() {

        isRunning = false;

        // Don't stop the loop until run() method finishes
        try {
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
