# Dogville game

  

#### Video Demo: <https://youtu.be/YSN2u2Iy6o0>

#### Description: 2D pixel demo platformer with Java



Hi! This is my final project for the CS50 course, where I explored what can be done with pure Java in the Android studio. The game is about a dog who found itself in a half-destroyed town full of Filthy Slimes of alien nature. He is brave enough to fight with enemies and go search for his lost friend Meowt!

![Screenshot_2022-04-02-14-29-47-659_com example dogvillev2](https://user-images.githubusercontent.com/91190871/161464176-ee6c5259-f1a5-4ca2-9ca4-06e4bc053928.jpg)
![1648884647535](https://user-images.githubusercontent.com/91190871/161464218-8dd0bc5e-86c2-46a8-a6a8-ba437a131cae.jpg)


### How to launch

First, copy all files from the project dogvillev2 folder into the new project in android studio.

No additional installations or third-party products.

Launch a new virtual device through the AVD manager / or connect the physical device to the PC and run the app.

  

### How it works

The entry point to the app is MainActivity.java, which creates the background music service and the game loop.

The game is run in a game loop thread that is set to repeat iterations in the run() method every 60ms, it subsequently calls the update() method to update in-game states, and the draw() method to render the results on the canvas. In order to provide consistent UPS, in some iterations draw() method can be skipped. Additionally, for efficiency, the game is rendered on the Buffered Image Canvas first, and the resulting image is scaled and drawn on the device screen. The resolution of the game is adaptive and should take the full screen on any device and look proportional. The performance panel shows the current UPS and FPS in the right top corner of the screen.

The Game class is responsible for the main logic of the game - it creates and fills with objects game level, manages interactions between objects, and handles touch events.

Game info panels are shown above the game view and are represented by separate classes: HealthBar, Performance, ExitMenu, WinPanel, LosePanel, and IntroPanel.

Game sounds are managed by two separate classes: background music (long, heavy files) is played with MediaPlayer through an instance of BackgroundSoundService class. Shorter audio effects (barking, clangs, meow, etc.) are played with the SoundPool through methods of the GameSounds class.

### Game classes
**Game Object** - abstract class that is a common parent for game objects. It determines coordinate fields and methods, and abstract draw() and update() methods.

**BitmapGameObject** - extends GameObject, represents objects that are drawn from a single bitmap frame (not animated), for example, platforms, or houses and trees. Positions of each BitmapGameObject are stored in the **GameLevelLayout** class, so it is easy to add/remove objects and change their locations, even add randomness.
**GameLevelLayout** also stores instantiated (loaded into memory) game objects, as only objects that fall within the game view are actually loaded. Objects that fall behind the game view window are released from memory and added back into the coordinates array HashMap<Integer, List<Integer>> gameObjectsCoordinates.
For animated objects, particularly game characters, the game used two classes: **CharacterSpriteSheet** class for storing and arranging frames and **Sprite** class for managing common behavior for all game characters. AnimalCharacter, FilthySlime, and FriendlyCat classes extend the Sprite class to add specific behavior. For example, AnimalCharacter is controlled by the player through the GUI elements, and FilthySlime is an enemy that changes behavior depending on the distance to the player (casts spells or moves forward and attacks).
  
Additional classes represent level traps and flying spells, each having specific states and animation styles.
  
The hardest part is the interaction between player and enemies, player and traps, and player and game environment. This is managed in AnimalCharacter class in the update() method - checks for collisions with platform or traps, in the main Game update() method - checks for spell hits and for enemy attacks, and in the main Game class in event handlers - checks for enemy attacks on enemies. Each of the collisions checks is based on simple calculations - whether the underlying rectangles of the bitmaps are intersecting or not.
  
Finally, the **Background** class is the result of experimenting with the parallax effect - when the closer objects move faster than further ones. Each layer of the background image has a velocity set proportional to the game level velocity, the coefficients can be manually adjusted or changed during the game.

### Game controls

The game has a joystick controller for the left thumb (movement of the player) and two buttons for the right thumb (action and jump). All touch events are handled manually - the coordinates of the events are recalculated and compared with the positions of the GUI elements. If the touch is within the GUI element, the actions are triggered.

After the game is either lost or won, GUI controls are hidden and buttons for exit or rerun options are shown.

  

### Resources used (free)
*[Animal pixel art asset](https://craftpix.net/freebies/free-street-animal-pixel-art-asset-pack/)*
  
*[Post apocalyptic backgrounds](https://craftpix.net/freebies/free-post-apocalyptic-pixel-art-game-backgrounds/)*
  
*[Royalty Free Background Music](https://www.fesliyanstudios.com/)*
