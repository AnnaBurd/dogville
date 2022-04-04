package com.example.dogvillev2.sounds;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.dogvillev2.R;

/**
 * Plays music on the background.
 * Runs on different thread from the main game loop, communication
 * between threads is through the LocalBroadcastManager in main activity.
 */
public class BackgroundSoundService extends Service {
    MediaPlayer mediaPlayer;
    BroadcastReceiver mBroadcastReceiver;
    Context context;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        // Load background music file from raw resources
        mediaPlayer = MediaPlayer.create(this, R.raw.background);
        mediaPlayer.setLooping(true); // Set looping
        mediaPlayer.setVolume(40, 40);

        // Listen for the messages from the game thread
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // Change background music after the game ended
                if (intent.getAction().equals(getPackageName() + ".changeBackgroundMusic")) {
                    mediaPlayer.stop();
                    mediaPlayer.release();

                    mediaPlayer = MediaPlayer.create(context, R.raw.win);
                    mediaPlayer.setLooping(true); // Set looping
                    mediaPlayer.setVolume(40, 40);
                    mediaPlayer.start();
                }
            }
        };
        LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(getPackageName() + ".changeBackgroundMusic");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.start();
        Toast.makeText(getApplicationContext(), "Help doge find the friend", Toast.LENGTH_SHORT).show();
        return startId;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public void onLowMemory() {
    }
}