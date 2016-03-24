package com.androidnetworking.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by amitshekhar on 25/03/16.
 */
public class Utils {

    public static File getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !Environment.isExternalStorageRemovable() ? context.getExternalCacheDir().getPath() :
                        context.getCacheDir().getPath();
        return new File(cachePath + File.separator + uniqueName);
    }
}
