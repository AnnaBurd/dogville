package com.example.dogvillev2.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * BitmapLoader is a helper class for loading image resources into the game.
 * Ensures that bitmaps are not scaled on loading.
 */
public class BitmapLoader {

    BitmapFactory.Options bitmapFactoryOptions;
    Resources gameResources;

    public BitmapLoader(Context context) {
        bitmapFactoryOptions = new BitmapFactory.Options();
        bitmapFactoryOptions.inScaled = false;
        gameResources = context.getResources();
    }

    public Bitmap loadBitmap(int resource) {
        return BitmapFactory.decodeResource(gameResources, resource, bitmapFactoryOptions);
    }
}
