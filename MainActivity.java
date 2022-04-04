package com.example.dogvillev2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.dogvillev2.sounds.BackgroundSoundService;


/**
 * Main activity is the entry point to the application.
 * Handles starting and pausing app.
 * Already set to full screen / landscape / no headers inside the AndroidManifest files
 * Caps the screen resolution to 1280x720.
 * Launches background music on separate thread.
 */
public class MainActivity extends Activity {

    int deviceWidthPixels;
    int deviceHeightPixels;
    // Main activity launches background music on a separate thread
    Intent backgroundMusic;
    // BroadcastManager to communicate between game thread, background music thread and main activity.
    LocalBroadcastManager mLocalBroadcastManager;
    BroadcastReceiver mBroadcastReceiver;
    // Main activity launches game
    private Game game;

    /**
     * Restart main activity, as if the app was closed and opened again.
     */
    public static void triggerRebirth(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cap screen resolution of the device (maximum allowed is 1280x720)
        deviceWidthPixels = Resources.getSystem().getDisplayMetrics().widthPixels;
        deviceHeightPixels = Resources.getSystem().getDisplayMetrics().heightPixels;
        if (deviceWidthPixels > 1280 || deviceHeightPixels > 720) {
            deviceWidthPixels = 1280;
            deviceHeightPixels = 720;
        }

        // Launch game
        game = new Game(this, deviceWidthPixels, deviceHeightPixels);
        game.getHolder().setFixedSize(deviceWidthPixels, deviceHeightPixels);
        setContentView(game);

        // Listen for the messages from the game thread
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // Close app if the exit button was pressed in Game
                if (intent.getAction().equals(getPackageName() + ".closeapp")) {
                    if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 21) {
                        stopService(backgroundMusic);
                        finishAffinity();
                    } else if (Build.VERSION.SDK_INT >= 21) {
                        stopService(backgroundMusic);
                        finishAndRemoveTask();
                    }
                }

                // Restart app if the restart button was pressed in Game
                if (intent.getAction().equals(getPackageName() + ".restartGame")) {
                    triggerRebirth(context);
                }

                // Start background music after the game intro
                if (intent.getAction().equals(getPackageName() + ".playBackgroundMusic")) {
                    backgroundMusic = new Intent(MainActivity.this, BackgroundSoundService.class);
                    startService(backgroundMusic);
                }
            }
        };

        // Filter messages that go to mBroadcastReceiver
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(getPackageName() + ".closeapp");
        mIntentFilter.addAction(getPackageName() + ".restartGame");
        mIntentFilter.addAction(getPackageName() + ".playBackgroundMusic");
        //mIntentFilter.addAction(getPackageName()+".changeBackgroundMusic");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    /**
     * Lock the back button (triangle) of the device
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed(); // Commented out the default code
    }

    /**
     * Stops the game loop when user hides the activity window
     * (prevents crashing app)
     */
    @Override
    protected void onPause() {
        game.pause();
        super.onPause();
    }

    /**
     * Remove broadcast listeners before exit
     */
    @Override
    protected void onDestroy() {
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}