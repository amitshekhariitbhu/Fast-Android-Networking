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

package com.androidnetworking.common;

import com.androidnetworking.core.Core;
import com.androidnetworking.interfaces.ConnectionQualityChangeListener;

/**
 * Created by amitshekhar on 29/05/16.
 */
public class ConnectionClassManager {

    private static final int BYTES_TO_BITS = 8;
    private static final int DEFAULT_SAMPLES_TO_QUALITY_CHANGE = 5;
    private static final int MINIMUM_SAMPLES_TO_DECIDE_QUALITY = 2;
    private static final int DEFAULT_POOR_BANDWIDTH = 150;
    private static final int DEFAULT_MODERATE_BANDWIDTH = 550;
    private static final int DEFAULT_GOOD_BANDWIDTH = 2000;
    private static final long BANDWIDTH_LOWER_BOUND = 10;

    private static ConnectionClassManager sInstance;
    private ConnectionQuality mCurrentConnectionQuality = ConnectionQuality.UNKNOWN;
    private int mCurrentBandwidthForSampling = 0;
    private int mCurrentNumberOfSample = 0;
    private int mCurrentBandwidth = 0;
    private ConnectionQualityChangeListener mConnectionQualityChangeListener;

    public static ConnectionClassManager getInstance() {
        if (sInstance == null) {
            synchronized (ConnectionClassManager.class) {
                if (sInstance == null) {
                    sInstance = new ConnectionClassManager();
                }
            }
        }
        return sInstance;
    }

    public synchronized void updateBandwidth(long bytes, long timeInMs) {
        if (timeInMs == 0 || bytes < 20000 || (bytes) * 1.0 / (timeInMs) *
                BYTES_TO_BITS < BANDWIDTH_LOWER_BOUND) {
            return;
        }
        double bandwidth = (bytes) * 1.0 / (timeInMs) * BYTES_TO_BITS;
        mCurrentBandwidthForSampling = (int) ((mCurrentBandwidthForSampling *
                mCurrentNumberOfSample + bandwidth) / (mCurrentNumberOfSample + 1));
        mCurrentNumberOfSample++;
        if (mCurrentNumberOfSample == DEFAULT_SAMPLES_TO_QUALITY_CHANGE ||
                (mCurrentConnectionQuality == ConnectionQuality.UNKNOWN &&
                        mCurrentNumberOfSample == MINIMUM_SAMPLES_TO_DECIDE_QUALITY)) {
            final ConnectionQuality lastConnectionQuality = mCurrentConnectionQuality;
            mCurrentBandwidth = mCurrentBandwidthForSampling;
            if (mCurrentBandwidthForSampling <= 0) {
                mCurrentConnectionQuality = ConnectionQuality.UNKNOWN;
            } else if (mCurrentBandwidthForSampling < DEFAULT_POOR_BANDWIDTH) {
                mCurrentConnectionQuality = ConnectionQuality.POOR;
            } else if (mCurrentBandwidthForSampling < DEFAULT_MODERATE_BANDWIDTH) {
                mCurrentConnectionQuality = ConnectionQuality.MODERATE;
            } else if (mCurrentBandwidthForSampling < DEFAULT_GOOD_BANDWIDTH) {
                mCurrentConnectionQuality = ConnectionQuality.GOOD;
            } else if (mCurrentBandwidthForSampling > DEFAULT_GOOD_BANDWIDTH) {
                mCurrentConnectionQuality = ConnectionQuality.EXCELLENT;
            }
            if (mCurrentNumberOfSample == DEFAULT_SAMPLES_TO_QUALITY_CHANGE) {
                mCurrentBandwidthForSampling = 0;
                mCurrentNumberOfSample = 0;
            }
            if (mCurrentConnectionQuality != lastConnectionQuality &&
                    mConnectionQualityChangeListener != null) {
                Core.getInstance().getExecutorSupplier().forMainThreadTasks()
                        .execute(new Runnable() {
                            @Override
                            public void run() {
                                mConnectionQualityChangeListener
                                        .onChange(mCurrentConnectionQuality, mCurrentBandwidth);
                            }
                        });
            }
        }

    }

    public int getCurrentBandwidth() {
        return mCurrentBandwidth;
    }

    public ConnectionQuality getCurrentConnectionQuality() {
        return mCurrentConnectionQuality;
    }

    public void setListener(ConnectionQualityChangeListener connectionQualityChangeListener) {
        mConnectionQualityChangeListener = connectionQualityChangeListener;
    }

    public void removeListener() {
        mConnectionQualityChangeListener = null;
    }

    public static void shutDown() {
        if (sInstance != null) {
            sInstance = null;
        }
    }

}
