package com.androidnetworking.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidnetworking.error.AndroidNetworkingError;
import com.androidnetworking.internal.AndroidNetworkingImageLoader;

/**
 * Created by amitshekhar on 23/03/16.
 */
public class GreatImageView extends ImageView {

    private String mUrl;

    private int mDefaultImageId;

    private int mErrorImageId;

    private AndroidNetworkingImageLoader.ImageContainer mImageContainer;

    public GreatImageView(Context context) {
        this(context, null);
    }

    public GreatImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GreatImageView(Context context, AttributeSet attrs, int defStyle) {
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
        ScaleType scaleType = getScaleType();

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

        AndroidNetworkingImageLoader.ImageContainer newContainer = AndroidNetworkingImageLoader.getInstance().get(mUrl,
                new AndroidNetworkingImageLoader.ImageListener() {
                    @Override
                    public void onResponse(final AndroidNetworkingImageLoader.ImageContainer response, boolean isImmediate) {
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
                    public void onResponse(Object response) {

                    }

                    @Override
                    public void onError(AndroidNetworkingError error) {
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
