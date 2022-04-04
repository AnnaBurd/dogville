package com.example.dogvillev2.sounds;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.example.dogvillev2.R;

/**
 * Plays short in game sounds, called as response to different game events in the Game class.
 */
public class GameSounds {

    private static final int MAX_STREAMS = 100;
    Context context;
    private int soundIdDogBite;
    private int soundIdHitSpell;
    private int soundIdHitEnemy;
    private int soundIdPlayerDeath;
    private int soundIdEnemyDeath;
    private int soundIdMeow;
    private int soundIdClang;

    private boolean soundPoolLoaded;
    private SoundPool soundPool;

    public GameSounds(Context context) {
        this.context = context;
        this.initSoundPool();
    }

    private void initSoundPool() {
        // Support different versions for android SDK >=21 or lower

        AudioAttributes audioAttrib = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setAudioAttributes(audioAttrib).setMaxStreams(MAX_STREAMS);
        this.soundPool = builder.build();


        // Load sound resources from raw files (wav, mp3)
        this.soundIdDogBite = this.soundPool.load(context, R.raw.bark, 1);
        this.soundIdHitSpell = this.soundPool.load(context, R.raw.water_spell_hit, 2);
        this.soundIdHitEnemy = this.soundPool.load(context, R.raw.enemy_bite, 3);
        this.soundIdPlayerDeath = this.soundPool.load(context, R.raw.dog_death, 1);
        this.soundIdEnemyDeath = this.soundPool.load(context, R.raw.enemy_death, 1);
        this.soundIdMeow = this.soundPool.load(context, R.raw.meow, 3);
        this.soundIdClang = this.soundPool.load(context, R.raw.bear_trap, 3);

        this.soundPoolLoaded = true;
    }

    // ---------------------------------------- Methods to play in Game sound ----------------------------------//
    public void playSoundBite() {
        if (this.soundPoolLoaded) {
            float leftVolume = 0.9f;
            float rightVolume = 0.9f;
            this.soundPool.play(this.soundIdDogBite, leftVolume, rightVolume, 1, 0, 1f);
        }
    }

    public void playSoundHitSpell() {
        if (this.soundPoolLoaded) {
            float leftVolume = 1f;
            float rightVolume = 1f;
            this.soundPool.play(this.soundIdHitSpell, leftVolume, rightVolume, 1, 0, 1f);
        }
    }

    public void playSoundHitEnemy() {
        if (this.soundPoolLoaded) {
            float leftVolume = 1f;
            float rightVolume = 1f;
            this.soundPool.play(this.soundIdHitEnemy, leftVolume, rightVolume, 1, 0, 1f);
        }
    }

    public void playSoundPlayerDeath() {
        if (this.soundPoolLoaded) {
            float leftVolume = 1f;
            float rightVolume = 1f;
            this.soundPool.play(this.soundIdPlayerDeath, leftVolume, rightVolume, 1, 0, 1f);
        }
    }

    public void playSoundEnemyDeath() {
        if (this.soundPoolLoaded) {
            float leftVolume = 1f;
            float rightVolume = 1f;
            this.soundPool.play(this.soundIdEnemyDeath, leftVolume, rightVolume, 1, 0, 1f);
        }
    }

    public void playSoundMeow() {
        if (this.soundPoolLoaded) {
            float leftVolume = 1f;
            float rightVolume = 1f;
            this.soundPool.play(this.soundIdMeow, leftVolume, rightVolume, 1, 0, 1f);
        }
    }

    public void playSoundClang() {
        if (this.soundPoolLoaded) {
            float leftVolume = 1f;
            float rightVolume = 1f;
            this.soundPool.play(this.soundIdClang, leftVolume, rightVolume, 1, 0, 1f);
        }
    }
}
