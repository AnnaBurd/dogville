package com.example.dogvillev2.infopanels;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.example.dogvillev2.R;
import com.example.dogvillev2.gameObjects.characters.AnimalCharacter;
import com.example.dogvillev2.gameObjects.characters.Sprite;

/**
 * HealthBar displays the player's health bar (green rectangle) in top left corner.
 */
public class HealthBar {
    private final AnimalCharacter player;
    private final int height;
    private final int margin;
    private final Paint borderPaint;
    private final Paint healthPaint;
    private int width;

    public HealthBar(Context context, AnimalCharacter player) {
        this.player = player;
        this.width = (int) (Sprite.MAX_HEALTH_POINTS * 0.7f);
        this.height = 13;
        this.margin = 2;

        this.borderPaint = new Paint();
        int borderColor = ContextCompat.getColor(context, R.color.healthBarBorder);
        borderPaint.setColor(borderColor);

        this.healthPaint = new Paint();
        int healthColor = ContextCompat.getColor(context, R.color.healthBarHealth);
        healthPaint.setColor(healthColor);

    }

    /**
     * Update and draw health bar, called by the game loop.
     */
    public void draw(Canvas canvas) {

        float healthPointsPercentage = (float) player.getHealthPoints() / Sprite.MAX_HEALTH_POINTS;

        // Adjust health bar for increased health points
        if (player.getHealthPoints() > Sprite.MAX_HEALTH_POINTS) {
            width = player.getHealthPoints();
        }

        // Draw background boarder
        float borderLeft, borderTop, borderRight, borderBottom;

        borderLeft = 10;
        borderTop = 30;
        borderBottom = borderTop + height;
        borderRight = borderLeft + width + margin * 2;

        canvas.drawRect(
                borderLeft,
                borderTop,
                borderRight,
                borderBottom,
                borderPaint);

        // Draw current player's health
        float healthLeft, healthTop, healthRight, healthBottom, healthWidth;
        healthWidth = width;
        healthLeft = borderLeft + margin;
        healthBottom = borderBottom - margin;
        healthTop = borderTop + margin;
        healthRight = healthLeft + healthWidth * healthPointsPercentage;

        canvas.drawRect(
                healthLeft,
                healthTop,
                healthRight,
                healthBottom,
                healthPaint);
    }
}
