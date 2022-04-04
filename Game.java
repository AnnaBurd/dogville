package com.example.dogvillev2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.dogvillev2.controls.Button;
import com.example.dogvillev2.controls.GameExitRepeatMenu;
import com.example.dogvillev2.controls.Joystick;
import com.example.dogvillev2.gameObjects.animated.SpellSpriteSheet;
import com.example.dogvillev2.gameObjects.animated.WaterSpell;
import com.example.dogvillev2.gameObjects.characters.AnimalCharacter;
import com.example.dogvillev2.gameObjects.characters.CharacterSpriteSheet;
import com.example.dogvillev2.gameObjects.characters.FilthySlime;
import com.example.dogvillev2.gameObjects.characters.FriendlyCat;
import com.example.dogvillev2.gameObjects.characters.Sprite;
import com.example.dogvillev2.infopanels.GameIntro;
import com.example.dogvillev2.infopanels.GameOver;
import com.example.dogvillev2.infopanels.GameWin;
import com.example.dogvillev2.infopanels.HealthBar;
import com.example.dogvillev2.infopanels.Performance;
import com.example.dogvillev2.layout.GameDisplay;
import com.example.dogvillev2.layout.GameLevelLayout;
import com.example.dogvillev2.layout.GameView;
import com.example.dogvillev2.sounds.GameSounds;
import com.example.dogvillev2.utilities.BitmapLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Game SurfaceView manages all objects in the game
 * and is responsible for updating all states (update() + eventHandler)
 * and render all objects to the screen (draw())
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback {

    // Game window scale to the device display
    private final GameDisplay gameDisplay;
    // Game window view position on the game level
    private final GameView gameView;
    // Helper class to load image resources
    private final BitmapLoader bitmapLoader;
    // Sprite sheets for animated objects
    private final SpellSpriteSheet waterSpellSpriteSheet;
    private final CharacterSpriteSheet slimeSpriteSheet;
    private final CharacterSpriteSheet dogSpriteSheet;
    //-----------------------Game objects and characters------------------------------//
    private final GameLevelLayout gameLevelLayout;
    private final AnimalCharacter player;
    private final FriendlyCat cat;
    private final ArrayList<FilthySlime> enemies = new ArrayList<>();
    private final List<WaterSpell> spellList = new ArrayList<>();
    //-------------------Game info panels---------------------------//
    private final Performance performance;
    private final HealthBar healthBar;
    //In game sound control
    private final GameSounds gameSounds;
    // Game thread
    private GameLoop gameLoop;
    //private boolean playerPlayedDeathSound = false;
    //private boolean enemyPlayedDeath = false;
    //-------------------Game controls---------------------------//
    private Joystick joystick;
    private int joystickPointerId = 0;
    private Button actionButton;
    private Button jumpButton;
    private GameExitRepeatMenu gameExitRepeatMenu;
    private GameIntro gameIntro;
    private boolean playIntro = true;
    private int updatesAfterIntro = 150;

    private GameOver gameOver;
    private boolean isGameOver = false;
    private int updatesAfterGameOver = 100;

    private GameWin gameWin;
    private boolean isGameWin = false;
    private int updatesAfterGameWin = 100;

    /**
     * Load resources, initialize game objects and user interface objects
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Game(Context context, int deviceWidthPixels, int deviceHeightPixels) {
        super(context);

        // Get surface holder and add callback (to respond to the user touch input)
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setFocusable(true);

        // --------------- Initialize game loop and helper classes --------------//
        gameDisplay = new GameDisplay(deviceWidthPixels, deviceHeightPixels);
        gameLoop = new GameLoop(this, surfaceHolder);
        bitmapLoader = new BitmapLoader(context);
        gameSounds = new GameSounds(context);

        // --------------Initialize game info panels -------------------------//
        gameIntro = new GameIntro(context);
        performance = new Performance(context, gameLoop);

        //---------------Initialize game controls-----------------------------//
        joystick = new Joystick(70, 280, bitmapLoader);

        actionButton = new Button(
                355,
                277,
                bitmapLoader.loadBitmap(R.drawable.button_up),
                bitmapLoader.loadBitmap(R.drawable.button_down)
        );

        jumpButton = new Button(
                415,
                277,
                bitmapLoader.loadBitmap(R.drawable.green_button_up),
                bitmapLoader.loadBitmap(R.drawable.green_button_down)
        );

        //--------------Initialize game level layout -------------------------//
        gameView = new GameView();
        gameLevelLayout = new GameLevelLayout(gameView, bitmapLoader, gameSounds);

        //-------------Initialize characters and spells  ---------------------------//

        // Player
        dogSpriteSheet = new CharacterSpriteSheet(
                bitmapLoader.loadBitmap(R.drawable.dog),
                5,
                6,
                new int[]{4, 3, 0, 1, 2},
                new int[]{6, 4, 4, 4, 2}
        );
        player = new AnimalCharacter(gameView, dogSpriteSheet, joystick, 100, 243, gameSounds);
        healthBar = new HealthBar(getContext(), player);

        // Friendly NPC Cat
        CharacterSpriteSheet catSpriteSheet = new CharacterSpriteSheet(
                bitmapLoader.loadBitmap(R.drawable.cat),
                5,
                6,
                new int[]{4, 3, 0, 1, 2},
                new int[]{6, 4, 4, 4, 2}
        );
        cat = new FriendlyCat(gameView, catSpriteSheet, player, 1730, 124, gameSounds);

        // Enemy Slimes
        slimeSpriteSheet = new CharacterSpriteSheet(
                bitmapLoader.loadBitmap(R.drawable.slime_enemy),
                5,
                5,
                new int[]{2, 0, 3, 4, 1},
                new int[]{4, 4, 5, 4, 4}
        );

        // Spawn enemies
        FilthySlime enemy;
        // Random slimes on the level
        for (int i = 0; i < 3; i++) {
            enemy = new FilthySlime(gameView, slimeSpriteSheet, player, 400 + Math.random() * 1000, 254, gameSounds);
            enemies.add(enemy);
        }
        // Slimes near the cat
        enemy = new FilthySlime(gameView, slimeSpriteSheet, player, 1700, 254, gameSounds);
        enemies.add(enemy);
        enemy = new FilthySlime(gameView, slimeSpriteSheet, player, 1750, 254, gameSounds);
        enemies.add(enemy);

        // Sprite sheet for future water spells
        waterSpellSpriteSheet = new SpellSpriteSheet(
                bitmapLoader.loadBitmap(R.drawable.water_ball),
                15,
                3
        );
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

        // Fixes the error of app crashes from the error of configuring surface
        if (gameLoop.getState().equals(Thread.State.TERMINATED)) {
            gameLoop = new GameLoop(this, surfaceHolder);
        }

        // Start the game loop (update -> draw)
        gameLoop.startLoop();

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
    }

    /**
     * Stops the game loop when user hides the activity window
     * (prevents crashing app)
     * Called by onPause() method in main activity
     */
    public void pause() {
        gameLoop.stopLoop();
    }


    /**
     * Update game state of all game objects - main logic of the game is here.
     */
    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void update() {

        //----------------Game controls-----------------------------------------------//
        if (joystick != null) {
            joystick.update();
        }

        if (actionButton != null) {
            actionButton.update();
        }

        if (jumpButton != null) {
            jumpButton.update();
        }

        //--------- Check win and lose conditions--------------------------//

        // Freeze game state of the finished game (on players win or death)
        if (isGameOver || isGameWin) {
            return;
        }

        // Player loosed all health points
        if (player.isDead()) {

            // Immediately play lose sound
            if (!player.playedLastSound) {
                gameSounds.playSoundPlayerDeath();
                player.playedLastSound = true;
            }
            // With delay show the game over panel
            updatesAfterGameOver--;
            if (updatesAfterGameOver <= 0) {
                isGameOver = true;
                gameOver = new GameOver(getContext());
            }
        }

        // Player win the game if he reached the cat and no slimes are left nearby
        boolean winCondition = false;
        if (player.getPositionOnLevelX() > GameLevelLayout.FINISH_POINT) {
            winCondition = true;
        }
        for (FilthySlime slime : enemies) {
            if (Sprite.getDistanceBetweenSprites(player, slime) < 200 && !slime.isDead()) {
                winCondition = false;
                break;
            }
        }

        if (winCondition) {
            if (!cat.playedLastSound) {
                gameSounds.playSoundMeow();
                cat.playedLastSound = true;
            }
            updatesAfterGameWin--;
            if (updatesAfterGameWin <= 0) {
                isGameWin = true;
                gameWin = new GameWin(getContext());

                // Send message to music player activity to change background music
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
                localBroadcastManager.sendBroadcast(new Intent(getContext().getPackageName() + ".changeBackgroundMusic"));
            }
        }


        //--------------- Enemy State-------------------------------------------------//
        Iterator<FilthySlime> slimeIterator = enemies.iterator();
        while (slimeIterator.hasNext()) {
            FilthySlime slime = slimeIterator.next();
            slime.update();

            // Cast spells
            if (slime.getSpellToCast() > 0) {
                spellList.add(new WaterSpell(
                        waterSpellSpriteSheet,
                        gameView,
                        slime
                ));
                slime.registerSpellCasted();
            }

            // Close attacks
            if (slime.isTimeToCountHitDamage() && Sprite.getDistanceBetweenSprites(slime, player) < 50) {
                player.changeHealthPoint(-10);
                gameSounds.playSoundHitEnemy();
            }
        }

        //--------------- Player character update----------------------------------------//
        player.update();

        //--------------- Friendly character update--------------------------------------//
        cat.update();

        //--------------- Spells -----------------------------------------------------//
        Iterator<WaterSpell> iteratorSpell = spellList.iterator();
        while (iteratorSpell.hasNext()) {
            WaterSpell spell = iteratorSpell.next();
            spell.update();
            // Start hit animation of the spell when collides with player
            if (spell.isHit(player)) {
                spell.setAnimationHit();

                // Take player health points
                if (!spell.isCountedHit()) {
                    gameSounds.playSoundHitSpell();
                    player.changeHealthPoint(-5);
                    spell.countHit();
                }
                continue;
            }

            // Remove spells that has finished animation
            if (spell.isFinishedAnimation()) {
                iteratorSpell.remove();
                continue;
            }

            // Remove spells that are behind the game window (missed ones)
            if (spell.getPositionX() < -100 || spell.getPositionX() > GameDisplay.GAME_WIDTH_BUFF_CANVAS + 100) {
                iteratorSpell.remove();
                continue;
            }
        }


        //--------------- Game view window movement--------------------------------------------//
        if (player.getVelocityX() == 0) {
            gameView.setMoving(false);
            gameView.setLevelVelocityX(0);
        } else if (player.getPositionX() > 170) {
            gameView.setMoving(true);
            gameView.setLevelVelocityX(player.getVelocityX());
        } else if (player.getPositionX() < 100) {
            gameView.setMoving(true);
            gameView.setLevelVelocityX(player.getVelocityX());
        }

        // Update level layout and game window position
        gameLevelLayout.update();
        gameView.update();
    }


    /**
     * Draw game objects on canvas (draws one frame for each object)
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // Draw game objects and characters
        gameLevelLayout.drawBackObjects(canvas);
        for (FilthySlime slime : enemies) {
            slime.draw(canvas);
        }
        cat.draw(canvas);
        player.draw(canvas);
        gameLevelLayout.drawFrontObjects(canvas);

        for (WaterSpell spell : spellList) {
            spell.draw(canvas);
        }

        // Draw game info panels and controls
        healthBar.draw(canvas);
        if (joystick != null) {
            joystick.draw(canvas);
        }
        if (actionButton != null) {
            actionButton.draw(canvas);
        }
        if (jumpButton != null) {
            jumpButton.draw(canvas);
        }
        performance.draw(canvas);

        //Intro game panel (gradually fades into game view)
        if (playIntro) {
            gameIntro.draw(canvas);
            updatesAfterIntro--;

            if (updatesAfterIntro == 10) {
                // Start background music after intro
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
                localBroadcastManager.sendBroadcast(new Intent(getContext().getPackageName() + ".playBackgroundMusic"));
            }
            if (updatesAfterIntro == 0) {
                gameIntro.setFading(true);
                updatesAfterIntro = -1;
            }
            if (gameIntro.opacity < 0) {
                playIntro = false;
                gameIntro = null;
            }
        }

        // Game over and game win game panels
        if (isGameOver) {
            gameOver.draw(canvas);
        }
        if (isGameWin) {
            gameWin.draw(canvas);
        }

        // Restart or exit menu
        if ((isGameOver || isGameWin)) {
            if (gameExitRepeatMenu == null) {
                gameExitRepeatMenu = new GameExitRepeatMenu(getContext());
                // Remove game controls
                joystick = null;
                jumpButton = null;
                actionButton = null;
            }
            gameExitRepeatMenu.draw(canvas);
        }
    }


    /**
     * Manage touch events: taps and dragging. Multiple taps allowed.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // PointerID and index are used to work with multiple touches (left and right thumbs)
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);

        int secondPointerId;
        int secondPointerIndex = pointerIndex;

        if (event.getPointerCount() > 1) {
            secondPointerId = event.getPointerId(1);
            secondPointerIndex = event.findPointerIndex(secondPointerId);
        }

        // Tap coordinates of the first pointer
        double x = gameDisplay.realToGameWindowPositionX(event.getX());
        double y = gameDisplay.realToGameWindowPositionY(event.getY());

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:

                // Joystick was already pressed, check if second touch is on buttons
                if (joystick != null && joystick.getIsPressed()) {

                    int secondPointerTouchX = (int) gameDisplay.realToGameWindowPositionX(event.getX(secondPointerIndex));
                    int secondPointerTouchY = (int) gameDisplay.realToGameWindowPositionY(event.getY(secondPointerIndex));
                    buttonEventActions(secondPointerTouchX, secondPointerTouchY);

                    // Joystick is pressed
                } else if (joystick != null && joystick.isPressed(x, y)) {
                    joystickPointerId = event.getPointerId(event.getActionIndex());
                    joystick.setIsPressed(true);

                    // Joystick is not pressed, check if touch is on buttons
                } else {
                    buttonEventActions((int) x, (int) y);
                }
                // end of case
                return true;

            case MotionEvent.ACTION_MOVE:
                // Joystick is moved
                if (joystick != null && joystick.getIsPressed()) {
                    joystick.setActuator(x, y);
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                // Joystick is released
                if (joystick != null && joystickPointerId == event.getPointerId(event.getActionIndex())) {
                    joystick.setIsPressed(false);
                    joystick.resetActuator();
                }
                return true;
        }

        return super.onTouchEvent(event);
    }

    private void buttonEventActions(int x, int y) {

        // Player pressed attack button
        if (actionButton != null && actionButton.isPressed(x, y)) {
            player.attack();
            gameSounds.playSoundBite();

            // Check if attack hit enemy slime and count damage
            for (FilthySlime slime : enemies) {
                if (!slime.isDead() && Sprite.getDistanceBetweenSprites(player, slime) < 50) {
                    slime.changeHealthPoint(-30);
                    break; // Count damage on only for one slime, comment this for area damage attacks
                }
            }
            return;
        }

        // Player jumps
        if (jumpButton != null && jumpButton.isPressed(x, y)) {
            player.jump();
            return;
        }


        // Player chooses exit or restart game
        if (gameExitRepeatMenu != null) {
            if (gameExitRepeatMenu.isExitPressed(x, y)) {
                // Send message to main activity to close app
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
                localBroadcastManager.sendBroadcast(new Intent(getContext().getPackageName() + ".closeapp"));
            }
            if (gameExitRepeatMenu.isRestartPressed(x, y)) {
                // Send message to main activity to restart app
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
                localBroadcastManager.sendBroadcast(new Intent(getContext().getPackageName() + ".restartGame"));
            }
        }
    }
}



