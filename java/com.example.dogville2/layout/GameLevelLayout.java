package com.example.dogvillev2.layout;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.dogvillev2.R;
import com.example.dogvillev2.gameObjects.Background;
import com.example.dogvillev2.gameObjects.BitmapGameObject;
import com.example.dogvillev2.gameObjects.GameObject;
import com.example.dogvillev2.gameObjects.animated.BearTrap;
import com.example.dogvillev2.sounds.GameSounds;
import com.example.dogvillev2.utilities.BitmapLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * GameLevelLayout class stores locations of all static objects in game in a hashmap "map".
 * Another hashmap "mapObjects" stores static objects that fill up current game window,
 * when leaving the game window objects are removed from memory, their locations are saved back to "map" hashmap.
 * Repeating background images are also stored and controlled in an instance of GameLevelLayout.
 */
public class GameLevelLayout {

    public static final double FINISH_POINT = 1680;
    private static final int TREE1 = 0;
    private static final int TREE2 = 1;
    private static final int STONE = 2;
    private static final int PLATFORM = 3;
    private static final int HOUSE1 = 4;
    private static final int HOUSE2 = 5;
    private static final int CAR = 6;
    private static final int BEAR_TRAP = 7;
    // Objects that are drawn in front of the player, the order of the drawing corresponds to the order in array
    private static final int[] frontObjects = {TREE1, PLATFORM, BEAR_TRAP, STONE};
    // Objects that are drawn behind the player, the order of the drawing corresponds to the order in array
    private static final int[] backObjects = {HOUSE1, HOUSE2, CAR, TREE2};
    // Game level objects: only objects in game window are created to reduce memory used
    private static HashMap<Integer, List<Integer>> gameObjectsCoordinates; // Stores coordinates of not yet created objects
    private static HashMap<Integer, List<GameObject>> gameObjectsCreated; // Stores created game objects
    private final GameView gameView;
    private final GameSounds gameSounds;
    private final Bitmap[] bitmaps;
    Background sky;
    Background houses;
    Background fence;
    Background road;
    private int platformCount = 0; // Used to adjust platform height

    @RequiresApi(api = Build.VERSION_CODES.N)
    public GameLevelLayout(GameView gameView, BitmapLoader bitmapLoader, GameSounds gameSounds) {
        this.gameView = gameView;
        this.gameSounds = gameSounds;

        // Load image resources
        bitmaps = new Bitmap[8];
        bitmaps[TREE1] = bitmapLoader.loadBitmap(R.drawable.trees_front);
        bitmaps[TREE2] = bitmapLoader.loadBitmap(R.drawable.tree_2);
        bitmaps[STONE] = bitmapLoader.loadBitmap(R.drawable.stone);
        bitmaps[PLATFORM] = bitmapLoader.loadBitmap(R.drawable.platform);
        bitmaps[HOUSE1] = bitmapLoader.loadBitmap(R.drawable.house_type1_front);
        bitmaps[HOUSE2] = bitmapLoader.loadBitmap(R.drawable.house_type2_front);
        bitmaps[CAR] = bitmapLoader.loadBitmap(R.drawable.car_front);
        bitmaps[BEAR_TRAP] = bitmapLoader.loadBitmap(R.drawable.bear_trap);

        // Initialize Background objects (images for parallax movement)
        // Sky
        Bitmap skyBitmap = bitmapLoader.loadBitmap(R.drawable.sky);
        sky = new Background(skyBitmap, gameView, 0, 0);
        sky.setVelocityX(-0.5); // Velocity of sky movement
        sky.setSpeedMultiplier(0.7); // Velocity of sky relative to player movement
        // Background houses
        Bitmap housesBitmap = bitmapLoader.loadBitmap(R.drawable.houses);
        houses = new Background(housesBitmap, gameView, 0, 0);
        houses.setSpeedMultiplier(0.8); // Velocity of background houses relative to player movement
        // Road fence
        Bitmap fenceBitmap = bitmapLoader.loadBitmap(R.drawable.fence);
        fence = new Background(fenceBitmap, gameView, 0, 50);
        // Road
        Bitmap roadBitmap = bitmapLoader.loadBitmap(R.drawable.road);
        road = new Background(roadBitmap, gameView, 0, 50);

        // Initialize GameObjects that are in the initial game window view
        initializeLevelLayout();
    }

    /**
     * Check for collision with Bear Traps (all existing bear trap objects),
     * activates the trap if needed and returns true only if it is time to count damage.
     */
    public static boolean isOnTrap(double objectLeftBottomX, double objectLeftBottomY, int objectWidth, int objectHeight) {

        // Get platform coordinates X, Y and check for collision for each of the platforms:
        List<GameObject> traps = gameObjectsCreated.get(BEAR_TRAP);
        if (traps == null) {
            return false;
        }

        // Trigger zone of the object
        Rect objectRect = new Rect(
                (int) objectLeftBottomX,
                (int) (objectLeftBottomY - objectHeight / 3),
                (int) (objectLeftBottomX + objectWidth),
                (int) (objectLeftBottomY - objectHeight / 3)
        );

        for (GameObject trap : traps) {
            int x = (int) trap.getPositionOnLevelX() + ((BearTrap) trap).getFrameWidth() / 3;
            int y = (int) trap.getPositionOnLevelY() + ((BearTrap) trap).getFrameHeight() / 3;

            // Trigger zone of the trap
            Rect trapRect = new Rect(
                    x + 10,
                    y,
                    x + ((BearTrap) trap).getFrameWidth() / 3 - 10,
                    y + ((BearTrap) trap).getFrameHeight() / 3
            );

            if (trapRect.intersect(objectRect)) {
                // Trap and object trigger zones intersect (player stands on the trap) -> try to trigger trap
                ((BearTrap) trap).triggerTrap();
                return ((BearTrap) trap).isInAction();
            }
        }
        return false;
    }

    /**
     * Check for collision with platforms (all existing platform game objects).
     * Returns 0 on no collision, or y coordinate of the collision.
     */
    public static int isOnPlatform(double objectLeftBottomX, double objectLeftBottomY, int objectWidth, int objectHeight) {

        // Get platform coordinates X, Y and check for collision for each of the platforms:
        List<GameObject> platforms = gameObjectsCreated.get(PLATFORM);
        if (platforms == null) {
            return 0;
        }

        // Trigger zone of a game object
        Rect objectRect = new Rect(
                (int) objectLeftBottomX,
                (int) objectLeftBottomY - 3,
                (int) (objectLeftBottomX + objectWidth),
                (int) (objectLeftBottomY)
        );

        for (GameObject platform : platforms) {
            int x = (int) platform.getPositionOnLevelX() + objectWidth / 2;
            int y = (int) platform.getPositionOnLevelY() + 10;

            // Trigger zone of a platform
            Rect platformRect = new Rect(
                    x,
                    y,
                    x + ((BitmapGameObject) platform).bitmapRectangle.width() - objectWidth,
                    y + 3 // only top surface of the platform
            );

            if (platformRect.intersect(objectRect)) {
                return y - objectHeight + 3;
            }
        }
        return 0;
    }

    /**
     * Load initial information about game objects locations.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initializeLevelLayout() {

        gameObjectsCoordinates = new HashMap<>();
        gameObjectsCreated = new HashMap<>();

        // Trees on the level
        List<Integer> locations = new ArrayList<>();
        locations.add(0);
        locations.add(745);
        locations.add(830);
        locations.add(900);
        gameObjectsCoordinates.put(TREE1, locations);

        // Cars on the level
        locations = new ArrayList<>();
        locations.add(1520);
        gameObjectsCoordinates.put(CAR, locations);

        // Houses type 1 on the level
        locations = new ArrayList<>();
        locations.add(280);
        gameObjectsCoordinates.put(HOUSE1, locations);

        // Houses type 2 on the level
        locations = new ArrayList<>();
        locations.add(-40);
        locations.add(482);
        gameObjectsCoordinates.put(HOUSE2, locations);

        // Trees type 2 on the level
        locations = new ArrayList<>();
        locations.add(1364);
        gameObjectsCoordinates.put(TREE2, locations);

        // Stones on the level
        locations = new ArrayList<>();
        locations.add(1442);
        locations.add(1470);
        gameObjectsCoordinates.put(STONE, locations);

        // Platforms on the level
        locations = new ArrayList<>();
        locations.add(1070);
        locations.add(1166);
        locations.add(1264);
        gameObjectsCoordinates.put(PLATFORM, locations);

        // Bear traps on the level
        locations = new ArrayList<>();

        locations.add(400);
        locations.add(600);
        locations.add(630);
        locations.add(1245);
        locations.add(1275);
        locations.add(1305);
        locations.add(1335);
        locations.add(1365);
        gameObjectsCoordinates.put(BEAR_TRAP, locations);

        // Call update first time on creation
        this.update();
    }

    /**
     * Add and remove static game objects from memory depending on the position of game window
     * While iterating also update state of each object
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void update() {

        // Update backgrounds
        sky.update();
        houses.update();
        fence.update();
        road.update();

        // Create static objects that come within the game window
        for (Integer key : gameObjectsCoordinates.keySet()) {
            List<Integer> listOfLevelLocations = gameObjectsCoordinates.get(key);
            for (int i = 0; i < listOfLevelLocations.size(); i++) {
                Integer location = listOfLevelLocations.get(i);
                if (gameView.isWithinGameWindow(location)) {
                    gameObjectsCreated.putIfAbsent(key, new ArrayList<>());
                    gameObjectsCreated.get(key).add(createObject(key, location));
                    // Temporary remove location of object from the hashmap
                    listOfLevelLocations.remove(location);
                    i--;
                }
            }
        }

        // Update state of each object and remove unused objects from memory
        for (Integer key : gameObjectsCreated.keySet()) {
            for (int i = 0; i < gameObjectsCreated.get(key).size(); i++) {
                GameObject object = gameObjectsCreated.get(key).get(i);
                object.update();
                if (!gameView.isWithinGameWindow(object.getPositionOnLevelX())) {
                    // Save location of object back to the hashmap
                    gameObjectsCoordinates.putIfAbsent(key, new ArrayList<>());
                    gameObjectsCoordinates.get(key).add((int) object.getPositionOnLevelX());
                    gameObjectsCreated.get(key).remove(object);
                    i--;
                }
            }
        }
    }


    /**
     * Draw all back game objects (relative to player),
     * the order of drawing is determined in backObjects array.
     */
    public void drawBackObjects(Canvas canvas) {

        sky.draw(canvas);
        houses.draw(canvas);

        for (Integer key : backObjects) {
            if (gameObjectsCreated.containsKey(key)) {
                for (int i = 0; i < gameObjectsCreated.get(key).size(); i++) {
                    GameObject object = gameObjectsCreated.get(key).get(i);
                    object.draw(canvas);
                }
            }
        }

        fence.draw(canvas);
    }

    /**
     * Draw all front game objects (relative to player),
     * the order of drawing is determined in frontObjects array.
     */
    public void drawFrontObjects(Canvas canvas) {

        for (Integer key : frontObjects) {
            if (gameObjectsCreated.containsKey(key)) {
                for (int i = 0; i < gameObjectsCreated.get(key).size(); i++) {
                    GameObject object = gameObjectsCreated.get(key).get(i);
                    object.draw(canvas);
                }
            }
        }

        road.draw(canvas);
    }

    /**
     * Call different constructor for different game object types ("trees", "houses1", "houses2", "cars", ...).
     * Returns null for unrecognized type.
     *
     * @param type     - name of the object type, accept "trees", "cars", etc.
     * @param location - position of the game object X on the game level
     * @return object of type GameObject (parent of all game object classes)
     */
    private GameObject createObject(Integer type, Integer location) {
        switch (type) {
            case TREE1:
                return new BitmapGameObject(
                        bitmaps[TREE1],
                        gameView,
                        location,
                        130
                );
            case TREE2:
                return new BitmapGameObject(
                        bitmaps[TREE2],
                        gameView,
                        location,
                        142
                );
            case STONE:
                return new BitmapGameObject(
                        bitmaps[STONE],
                        gameView,
                        location,
                        202
                );
            case PLATFORM:
                platformCount++;
                return new BitmapGameObject(
                        bitmaps[PLATFORM],
                        gameView,
                        location,
                        280 - 40 * platformCount
                );
            case HOUSE1:
                return new BitmapGameObject(
                        bitmaps[HOUSE1],
                        gameView,
                        location,
                        30
                );
            case HOUSE2:
                return new BitmapGameObject(
                        bitmaps[HOUSE2],
                        gameView,
                        location,
                        30
                );
            case CAR:
                return new BitmapGameObject(
                        bitmaps[CAR],
                        gameView,
                        location,
                        20
                );
            case BEAR_TRAP:
                return new BearTrap(
                        gameView,
                        location,
                        260,
                        bitmaps[BEAR_TRAP],
                        gameSounds
                );
            default:
                return null;
        }
    }
}
