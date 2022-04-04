package com.example.dogvillev2.gameObjects.characters;

import com.example.dogvillev2.layout.GameView;
import com.example.dogvillev2.sounds.GameSounds;

/**
 * The cat is supposed to sit on the car and wait until the player saves him.
 * It is a cat, so it does not say anything in the end.
 */
public class FriendlyCat extends Sprite {

    private final AnimalCharacter player;

    public FriendlyCat(GameView gameView, CharacterSpriteSheet characterSpriteSheet, AnimalCharacter player, double positionOnLevelX, double positionOnLevelY, GameSounds gameSounds) {
        super(
                gameView,
                positionOnLevelX,
                positionOnLevelY,
                characterSpriteSheet,
                gameSounds
        );

        this.player = player;
        this.setAnimation(CharacterSpriteSheet.ROW_IDLE);
    }

    @Override
    protected void playLastSound() {

    }

    @Override
    public void update() {

        // Calculate vector from center of enemy to center of player (in x and y coordinates)
        double distanceToPlayerX = player.getPositionOnLevelX() + player.getCharacterFrameWidth() / 2 - this.positionOnLevelX - this.getCharacterFrameWidth() / 2;
        double distanceToPlayerY = player.getPositionOnLevelY() + player.getCharacterFrameHeight() / 2 - this.positionOnLevelY - this.getCharacterFrameHeight() / 2;

        // Calculate absolute distance between enemy (this) and player
        double distanceToPlayer = Sprite.getDistanceBetweenSprites(this, player);

        // Calculate direction from enemy to player
        directionX = distanceToPlayerX / distanceToPlayer;
        directionY = distanceToPlayerY / distanceToPlayer;

        // Update animation state
        super.update();
    }
}
