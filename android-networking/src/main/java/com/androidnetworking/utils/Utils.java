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

package com.androidnetworking.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.androidnetworking.common.ANConstants;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.core.Core;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;

import okhttp3.Cache;
import okhttp3.Response;
import okio.Okio;

/**
 * Created by amitshekhar on 25/03/16.
 */
public class Utils {

    public static File getDiskCacheDir(Context context, String uniqueName) {
        return new File(context.getCacheDir(), uniqueName);
    }

    public static Cache getCache(Context context, int maxCacheSize, String uniqueName) {
        return new Cache(getDiskCacheDir(context, uniqueName), maxCacheSize);
    }

    public static String getMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    public static ANResponse<Bitmap> decodeBitmap(Response response, int maxWidth,
                                                  int maxHeight, Bitmap.Config decodeConfig,
                                                  ImageView.ScaleType scaleType) {
        return decodeBitmap(response, maxWidth, maxHeight, decodeConfig,
                new BitmapFactory.Options(), scaleType);
    }

    public static ANResponse<Bitmap> decodeBitmap(Response response, int maxWidth,
                                                  int maxHeight, Bitmap.Config decodeConfig,
                                                  BitmapFactory.Options decodeOptions,
                                                  ImageView.ScaleType scaleType) {
        byte[] data = new byte[0];
        try {
            data = Okio.buffer(response.body().source()).readByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = null;
        if (maxWidth == 0 && maxHeight == 0) {
            decodeOptions.inPreferredConfig = decodeConfig;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
        } else {
            decodeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
            int actualWidth = decodeOptions.outWidth;
            int actualHeight = decodeOptions.outHeight;

            int desiredWidth = getResizedDimension(maxWidth, maxHeight,
                    actualWidth, actualHeight, scaleType);
            int desiredHeight = getResizedDimension(maxHeight, maxWidth,
                    actualHeight, actualWidth, scaleType);

            decodeOptions.inJustDecodeBounds = false;
            decodeOptions.inSampleSize =
                    findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
            Bitmap tempBitmap =
                    BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);

            if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                    tempBitmap.getHeight() > desiredHeight)) {
                bitmap = Bitmap.createScaledBitmap(tempBitmap,
                        desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        }

        if (bitmap == null) {
            return ANResponse.failed(Utils.getErrorForParse(new ANError(response)));
        } else {
            return ANResponse.success(bitmap);
        }
    }

    private static int getResizedDimension(int maxPrimary, int maxSecondary,
                                           int actualPrimary, int actualSecondary,
                                           ImageView.ScaleType scaleType) {

        if ((maxPrimary == 0) && (maxSecondary == 0)) {
            return actualPrimary;
        }

        if (scaleType == ImageView.ScaleType.FIT_XY) {
            if (maxPrimary == 0) {
                return actualPrimary;
            }
            return maxPrimary;
        }

        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;

        if (scaleType == ImageView.ScaleType.CENTER_CROP) {
            if ((resized * ratio) < maxSecondary) {
                resized = (int) (maxSecondary / ratio);
            }
            return resized;
        }

        if ((resized * ratio) > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    public static int findBestSampleSize(int actualWidth, int actualHeight,
                                         int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }
        return (int) n;
    }

    public static void saveFile(Response response, String dirPath,
                                String fileName) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendAnalytics(final AnalyticsListener analyticsListener,
                                     final long timeTakenInMillis, final long bytesSent,
                                     final long bytesReceived, final boolean isFromCache) {
        Core.getInstance().getExecutorSupplier().forMainThreadTasks().execute(new Runnable() {
            @Override
            public void run() {
                if (analyticsListener != null) {
                    analyticsListener.onReceived(timeTakenInMillis, bytesSent, bytesReceived,
                            isFromCache);
                }
            }
        });
    }

    public static ANError getErrorForConnection(ANError error) {
        error.setErrorDetail(ANConstants.CONNECTION_ERROR);
        error.setErrorCode(0);
        return error;
    }


    public static ANError getErrorForServerResponse(ANError error, ANRequest request, int code) {
        error = request.parseNetworkError(error);
        error.setErrorCode(code);
        error.setErrorDetail(ANConstants.RESPONSE_FROM_SERVER_ERROR);
        return error;
    }

    public static ANError getErrorForParse(ANError error) {
        error.setErrorCode(0);
        error.setErrorDetail(ANConstants.PARSE_ERROR);
        return error;
    }

}
