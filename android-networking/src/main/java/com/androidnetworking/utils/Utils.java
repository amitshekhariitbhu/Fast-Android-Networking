package com.androidnetworking.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import okhttp3.Cache;

/**
 * Created by amitshekhar on 25/03/16.
 */
public class Utils {

    public static File getDiskCacheDir(Context context, String uniqueName) {
        return new File(context.getCacheDir(),uniqueName);
    }

    public static Cache getCache(Context context, int maxCacheSize, String uniqueName) {
        return new Cache(getDiskCacheDir(context, uniqueName), maxCacheSize);
    }

}
