package com.example.srinivas.flickrapp;

import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by Srinivas on 07-06-2016.
 */
public class GlobalPhotoInfoCache  extends LruCache<String,PhotoInfo> implements ComponentCallbacks2 {

    public GlobalPhotoInfoCache(int maxSize){
        super(maxSize);
    }

    @Override
    public void onTrimMemory(int level) {
        if(level>=TRIM_MEMORY_MODERATE){
            evictAll();
        } else if(level >= TRIM_MEMORY_UI_HIDDEN){
            trimToSize(size()/2);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void onLowMemory() {

    }
}
