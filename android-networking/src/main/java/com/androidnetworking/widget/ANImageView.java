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

package com.androidnetworking.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidnetworking.error.ANError;
import com.androidnetworking.internal.ANImageLoader;

/**
 * Created by amitshekhar on 23/03/16.
 */
public class ANImageView extends AppCompatImageView {

    private String mUrl;

    private int mDefaultImageId;

    private int mErrorImageId;

    private ANImageLoader.ImageContainer mImageContainer;

    public ANImageView(Context context) {
        this(context, null);
    }

    public ANImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ANImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImageUrl(String url) {
        mUrl = url;
        loadImageIfNecessary(false);
    }

    public void setDefaultImageResId(int defaultImage) {
        mDefaultImageId = defaultImage;
    }

    public void setErrorImageResId(int errorImage) {
        mErrorImageId = errorImage;
    }

    void loadImageIfNecessary(final boolean isInLayoutPass) {
        int width = getWidth();
        int height = getHeight();
        ImageView.ScaleType scaleType = getScaleType();

        boolean wrapWidth = false, wrapHeight = false;
        if (getLayoutParams() != null) {
            wrapWidth = getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT;
            wrapHeight = getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        boolean isFullyWrapContent = wrapWidth && wrapHeight;
        if (width == 0 && height == 0 && !isFullyWrapContent) {
            return;
        }

        if (TextUtils.isEmpty(mUrl)) {
            if (mImageContainer != null) {
                mImageContainer.cancelRequest();
                mImageContainer = null;
            }
            setDefaultImageOrNull();
            return;
        }

        if (mImageContainer != null && mImageContainer.getRequestUrl() != null) {
            if (mImageContainer.getRequestUrl().equals(mUrl)) {
                return;
            } else {
                mImageContainer.cancelRequest();
                setDefaultImageOrNull();
            }
        }

        int maxWidth = wrapWidth ? 0 : width;
        int maxHeight = wrapHeight ? 0 : height;

        ANImageLoader.ImageContainer newContainer = ANImageLoader.getInstance().get(mUrl,
                new ANImageLoader.ImageListener() {
                    @Override
                    public void onResponse(final ANImageLoader.ImageContainer response,
                                           boolean isImmediate) {
                        if (isImmediate && isInLayoutPass) {
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    onResponse(response, false);
                                }
                            });
                            return;
                        }

                        if (response.getBitmap() != null) {
                            setImageBitmap(response.getBitmap());
                        } else if (mDefaultImageId != 0) {
                            setImageResource(mDefaultImageId);
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if (mErrorImageId != 0) {
                            setImageResource(mErrorImageId);
                        }
                    }
                }, maxWidth, maxHeight, scaleType);

        mImageContainer = newContainer;
    }

    private void setDefaultImageOrNull() {
        if (mDefaultImageId != 0) {
            setImageResource(mDefaultImageId);
        } else {
            setImageBitmap(null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        loadImageIfNecessary(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mImageContainer != null) {
            mImageContainer.cancelRequest();
            setImageBitmap(null);
            mImageContainer = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

}
