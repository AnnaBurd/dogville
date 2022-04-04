package com.example.dogvillev2.gameObjects.characters;

import com.example.dogvillev2.GameLoop;
import com.example.dogvillev2.layout.GameView;
import com.example.dogvillev2.sounds.GameSounds;


/**
 * FilthySlime is the first and only enemy in the game.
 * It's movement and actions depend on the player behaviour.
 */
public class FilthySlime extends Sprite {

    // Maximum speed of the character
    public static final double SPEED_PIXELS_PER_SECOND = AnimalCharacter.SPEED_PIXELS_PER_SECOND * 0.8; // Enemy is slightly slower than player
    private static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS; // pixels per update

    // Behaviour of the character
    private final AnimalCharacter player;

    private int spellToCast = 0;
    private int updatesTillNextSpell = 0;


    private int updatesTillNextAttack = 0;
    private int updatesTillAttackRegister = -1;

    private int updatesTillNextFastMovement = 0;

    private int updatesTillNextFastReach;
    private int fastReachLength;

    public FilthySlime(GameView gameView, CharacterSpriteSheet characterSpriteSheet, AnimalCharacter player, double positionOnLevelX, double positionOnLevelY, GameSounds gameSounds) {
        super(
                gameView,
                positionOnLevelX,
                positionOnLevelY,
                characterSpriteSheet,
                gameSounds
        );

        this.player = player;
    }


    @Override
    protected void playLastSound() {
        gameSounds.playSoundEnemyDeath();
    }

    @Override
    public void update() {

        // Don't update dead enemies
        if (isSpriteDead) {
            return;
        }

        // Calculate vector from center of enemy to center of player (in x and y coordinates)
        double distanceToPlayerX = player.getPositionOnLevelX() + player.getCharacterFrameWidth() / 2 - this.positionOnLevelX - this.getCharacterFrameWidth() / 2;
        double distanceToPlayerY = player.getPositionOnLevelY() + player.getCharacterFrameHeight() / 2 - this.positionOnLevelY - this.getCharacterFrameHeight() / 2;

        // Calculate absolute distance between enemy (this) and player
        double distanceToPlayer = Sprite.getDistanceBetweenSprites(this, player);

        // Calculate direction from enemy to player
        directionX = distanceToPlayerX / distanceToPlayer;
        directionY = distanceToPlayerY / distanceToPlayer;


        // Enemy behavior logic
        if (distanceToPlayer > 400) {
            // Idle
            velocityX = 0;
        } else if (distanceToPlayer > 250) {
            castSpell();
        } else if (distanceToPlayer > 150) {
            velocityX = directionX * MAX_SPEED * 1.0;
        } else if (distanceToPlayer > 70) {
            // Fast move towards the player
            int xSpeed;
            xSpeed = updatesTillNextFastMovement / 1000;
            velocityX = directionX * MAX_SPEED * Math.max(0.5, xSpeed);
            updatesTillNextFastMovement++;
        } else if (distanceToPlayer > 30) {

            // Fast reach and attack player
            updatesTillNextFastReach--;

            velocityX = directionX * MAX_SPEED * 1.0;

            if (updatesTillNextFastReach <= 0) {
                fastReachLength = 10;
                updatesTillNextFastReach = 500;
            }
            if (fastReachLength > 0) {
                attack();
                fastReachLength--;
                velocityX = directionX * MAX_SPEED * distanceToPlayer / 30;
            }
        } else {
            attack();
            velocityX = 0;
        }

        // Update current position of the enemy
        positionOnLevelX += velocityX;

        // Update current type of animation
        if (velocityX != 0 || velocityY != 0) {
            setAnimation(CharacterSpriteSheet.ROW_WALK);
        } else {
            setAnimation(CharacterSpriteSheet.ROW_IDLE);
        }

        // Update animation state
        super.update();

    }

    public int getSpellToCast() {
        return spellToCast;
    }

    public void registerSpellCasted() {
        if (spellToCast > 0) {
            spellToCast--;
        }
    }

    private void castSpell() {
        if (updatesTillNextSpell <= 0) {
            spellToCast++;

            setAnimation(CharacterSpriteSheet.ROW_WALK, true);

            updatesTillNextSpell = 250;

            velocityX = 0;
            velocityY = 0;
            return;
        }
        updatesTillNextSpell--;
    }

    private void attack() {
        if (updatesTillNextAttack == 0) {
            // Launch attack
            setAnimation(CharacterSpriteSheet.ROW_ATTACK, true);

            updatesTillNextAttack = characterSpriteSheet.ROW_MAX_FRAMES[CharacterSpriteSheet.ROW_ATTACK] * super.updatesTillNextFrame * 5;
            updatesTillAttackRegister = characterSpriteSheet.ROW_MAX_FRAMES[CharacterSpriteSheet.ROW_ATTACK] * super.updatesTillNextFrame / 2;
            return;
        }

        updatesTillNextAttack--;
        // Register attack for the player in the middle of the attack animation:
        updatesTillAttackRegister--;

    }

    public boolean isTimeToCountHitDamage() {
        return updatesTillAttackRegister == 0;
    }
}
