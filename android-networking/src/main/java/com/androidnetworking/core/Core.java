/*
 *    Copyright (C) 2016 Amit Shekhar
 *    Copyright (C) 2011 Android Open Source Project
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.androidnetworking.core;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class Core {

    private static Core sInstance = null;
    private final ExecutorSupplier mExecutorSupplier;

    private Core() {
        this.mExecutorSupplier = new DefaultExecutorSupplier();
    }

    public static Core getInstance() {
        if (sInstance == null) {
            synchronized (Core.class) {
                if (sInstance == null) {
                    sInstance = new Core();
                }
            }
        }
        return sInstance;
    }

    public ExecutorSupplier getExecutorSupplier() {
        return mExecutorSupplier;
    }

    public static void shutDown() {
        if (sInstance != null) {
            sInstance = null;
        }
    }
}
