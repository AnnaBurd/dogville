package com.example.dogvillev2.gameObjects.characters;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;

/**
 * CharacterSpriteSheet class divides source image sprite sheet into arrays.
 * Character classes get current frame from CharacterSpriteSheet by number of row and number of column.
 */
public class CharacterSpriteSheet {

    public static final int ROW_DEATH = 3;
    public static final int ROW_HURT = 4;
    // Possible types of action
    protected static final int ROW_WALK = 0;
    protected static final int ROW_IDLE = 1;
    protected static final int ROW_ATTACK = 2;
    // Single frame size parameters
    protected final int frameWidth;
    protected final int frameHeight;
    protected final Rect frameRect;
    // Arrays of frames for different types of action
    private final Bitmap[] walkBitmaps;
    private final Bitmap[] idleBitmaps;
    private final Bitmap[] attackBitmaps;
    private final Bitmap[] deathBitmaps;
    private final Bitmap[] hurtBitmaps;

    // Number of frames for each action (inconsistent value in the source images)
    protected int[] ROW_MAX_FRAMES;

    // Matrices for rotation of frames before drawing on canvas
    protected Matrix matrixOfBitmap = new Matrix();
    protected Matrix matrixFlipped = new Matrix();

    public CharacterSpriteSheet(Bitmap sourceImage, int rowCountInSourceImage, int colCountInSourceImage, int[] movementRows, int[] actionMaxFrames) {

        // Calculate frame size
        frameWidth = sourceImage.getWidth() / colCountInSourceImage;
        frameHeight = sourceImage.getHeight() / rowCountInSourceImage;
        this.frameRect = new Rect(0, 0, frameWidth, frameHeight);

        // Set flipped matrix
        matrixFlipped.postScale(-1, 1);

        // Save maximum number of frames for each type of action
        ROW_MAX_FRAMES = actionMaxFrames;

        // Initialize arrays to store bitmaps
        walkBitmaps = new Bitmap[ROW_MAX_FRAMES[ROW_WALK]];
        idleBitmaps = new Bitmap[ROW_MAX_FRAMES[ROW_IDLE]];
        attackBitmaps = new Bitmap[ROW_MAX_FRAMES[ROW_ATTACK]];
        deathBitmaps = new Bitmap[ROW_MAX_FRAMES[ROW_DEATH]];
        hurtBitmaps = new Bitmap[ROW_MAX_FRAMES[ROW_HURT]];

        // Fill arrays with frames (sub-images of the source image)
        for (int col = 0; col < colCountInSourceImage; col++) {
            if (col < ROW_MAX_FRAMES[ROW_WALK]) {
                walkBitmaps[col] = createFrameImageAt(sourceImage, movementRows[ROW_WALK], col);
            }
            if (col < ROW_MAX_FRAMES[ROW_IDLE]) {
                idleBitmaps[col] = createFrameImageAt(sourceImage, movementRows[ROW_IDLE], col);
            }
            if (col < ROW_MAX_FRAMES[ROW_ATTACK]) {
                attackBitmaps[col] = createFrameImageAt(sourceImage, movementRows[ROW_ATTACK], col);
            }
            if (col < ROW_MAX_FRAMES[ROW_DEATH]) {
                deathBitmaps[col] = createFrameImageAt(sourceImage, movementRows[ROW_DEATH], col);
            }
            if (col < ROW_MAX_FRAMES[ROW_HURT]) {
                hurtBitmaps[col] = createFrameImageAt(sourceImage, movementRows[ROW_HURT], col);
            }
        }
    }

    /**
     * Helper method subdivides source image into frames
     *
     * @param image
     * @param row
     * @param col
     * @return
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
     * Helper method to get array of bitmap for current type of action
     *
     * @param rowUsingFrame
     * @return
     */

    private Bitmap[] getMoveFrames(int rowUsingFrame) {
        switch (rowUsingFrame) {
            case ROW_WALK:
                return this.walkBitmaps;
            case ROW_IDLE:
                return this.idleBitmaps;
            case ROW_ATTACK:
                return this.attackBitmaps;
            case ROW_DEATH:
                return this.deathBitmaps;
            case ROW_HURT:
                return this.hurtBitmaps;
            default:
                return null;
        }
    }

    /**
     * Get current bitmap frame from the sprite sheet.
     * Called by the draw() method of the character.
     *
     * @param rowUsingFrame
     * @param colUsingFrame
     * @return
     */
    public Bitmap getCurrentMoveFrame(int rowUsingFrame, int colUsingFrame) {
        return this.getMoveFrames(rowUsingFrame)[colUsingFrame];
    }

    /**
     * Get current bitmap frame from the sprite sheet.
     * Called by the draw() method of the character.
     *
     * @param rowUsingFrame
     * @param colUsingFrame
     * @param flipped       true for flipping the image around vertical axe.
     * @return
     */
    public Bitmap getCurrentMoveFrame(int rowUsingFrame, int colUsingFrame, boolean flipped) {
        Bitmap currentFrame = getCurrentMoveFrame(rowUsingFrame, colUsingFrame);
        if (flipped) {
            currentFrame = Bitmap.createBitmap(currentFrame, 0, 0, frameWidth, frameHeight, matrixFlipped, true);
        }
        return currentFrame;
    }

    /**
     * Get current bitmap frame from the sprite sheet rotated,
     * angle of rotation calculated from the directionX and directionY parameters.
     * Called by the draw() method of the character.
     *
     * @param rowUsingFrame
     * @param colUsingFrame
     * @param directionX
     * @param directionY
     * @return
     */
    public Bitmap getCurrentMoveFrame(int rowUsingFrame, int colUsingFrame, double directionX, double directionY) {
        Bitmap currentFrame = getCurrentMoveFrame(rowUsingFrame, colUsingFrame);
        double angle = Math.atan2(directionY, directionX) * 180 / Math.PI;
        matrixOfBitmap.setRotate((float) angle);
        return Bitmap.createBitmap(currentFrame, 0, 0, frameWidth, frameHeight, matrixOfBitmap, true);
    }
}
