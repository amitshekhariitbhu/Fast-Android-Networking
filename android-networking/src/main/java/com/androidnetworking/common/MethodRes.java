package com.androidnetworking.common;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by amitshekhar on 26/03/16.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({Method.GET, Method.POST, Method.PUT, Method.DELETE, Method.HEAD, Method.PATCH})
public @interface MethodRes {
}
