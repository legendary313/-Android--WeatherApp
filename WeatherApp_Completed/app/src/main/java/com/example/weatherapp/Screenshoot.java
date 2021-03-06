package com.example.weatherapp;

import android.graphics.Bitmap;
import android.view.View;

public class Screenshoot {

    public static Bitmap takescreenshoot(View v){
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return b;
    }

    public static Bitmap takescreenshootOfRootView(View v) {
        return takescreenshoot(v.getRootView());
    }
}
