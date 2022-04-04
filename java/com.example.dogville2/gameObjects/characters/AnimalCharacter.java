package com.example.dogvillev2.gameObjects.characters;

import com.example.dogvillev2.GameLoop;
import com.example.dogvillev2.controls.Joystick;
import com.example.dogvillev2.layout.GameLevelLayout;
import com.example.dogvillev2.layout.GameView;
import com.example.dogvillev2.sounds.GameSounds;
import com.example.dogvillev2.utilities.Utils;

/**
 * AnimalCharacter object represents player in game,
 * its movement is controlled with virtual joystick and actions controlled with taps on screen
 */
public class AnimalCharacter extends Sprite {

    // Maximum speed of the character
    public static final double SPEED_PIXELS_PER_SECOND = 50.0;
    public static final double GRAVITY = 0.15;
    private static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS; // pixels per update
    // Controls movement
    private final Joystick joystick;
    private boolean inJump = false;

    public AnimalCharacter(GameView gameView, CharacterSpriteSheet characterSpriteSheet, Joystick joystick, double positionOnLevelX, double positionOnLevelY, GameSounds gameSounds) {
        super(gameView, positionOnLevelX, positionOnLevelY, characterSpriteSheet, gameSounds);
        this.joystick = joystick;

    }

    @Override
    protected void playLastSound() {
        gameSounds.playSoundPlayerDeath();
    }

    @Override
    public void update() {

        // Don't update dead player
        if (isSpriteDead) {
            return;
        }

        // Update velocity based on the actuator of a joystick
        velocityX = joystick.getActuatorX() * MAX_SPEED;

        //velocityY = joystick.getActuatorY() * MAX_SPEED;

        if (inJump) {
            accelerate(0, GRAVITY);
            // Collision with level floor
            if (positionOnLevelY > 243) {
                velocityY = 0;
                positionOnLevelY = 243;
                inJump = false;
            }

            // Check for collision with platforms from the game level layout
            int onPlatformY = GameLevelLayout.isOnPlatform(getPositionOnLevelX(), getPositionOnLevelY() + getCharacterFrameHeight(), getCharacterFrameWidth(), getCharacterFrameHeight());

            if (velocityY > 0 && onPlatformY != 0) {
                velocityY = 0;


                positionOnLevelY = onPlatformY;

                inJump = false;
            }
            ;
        }

        // Fall down from platform
        if (!inJump && GameLevelLayout.isOnPlatform(getPositionOnLevelX(), getPositionOnLevelY() + getCharacterFrameHeight(), getCharacterFrameWidth(), getCharacterFrameHeight()) == 0 && positionOnLevelY < 243) {
            inJump = true;
        }

        // Check for collision with traps
        if (GameLevelLayout.isOnTrap(getPositionOnLevelX(), getPositionOnLevelY() + getCharacterFrameHeight(), getCharacterFrameWidth(), getCharacterFrameHeight())) {
            changeHealthPoint(-1);
        }

        // Update position
        positionOnLevelX += velocityX;
        positionOnLevelY += velocityY;

        // Update direction (for casting spells etc.)
        if (velocityX != 0 || velocityY != 0) {
            double distance = Utils.getDistanceBetweenPoints(0, 0, velocityX, velocityY);
            directionX = velocityX / distance;
            directionY = velocityY / distance;
        }

        // Update current animation frame
        super.update();

        // Update current type of animation
        if (velocityX != 0 || velocityY != 0) {
            setAnimation(CharacterSpriteSheet.ROW_WALK);
        } else {
            setAnimation(CharacterSpriteSheet.ROW_IDLE);
        }
    }


    public void attack() {
        this.setAnimation(CharacterSpriteSheet.ROW_ATTACK, true);
    }


    public void jump() {

        if (!inJump) {
            inJump = true;
            accelerate(0, -4);
        }

    }

    private void accelerate(double accelerationX, double accelerationY) {
        velocityX += accelerationX;
        velocityY += accelerationY;
    }
}
