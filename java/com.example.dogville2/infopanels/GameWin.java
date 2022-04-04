package com.example.dogvillev2.infopanels;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.dogvillev2.R;

/**
 * GameWin is a panel that draws text You Win! above main game view.
 */
public class GameWin extends GameOver {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public GameWin(Context context) {
        super(context);
        setText("YOU WIN!");
        setColor(ContextCompat.getColor(context, R.color.healthBarHealth));
    }

}
