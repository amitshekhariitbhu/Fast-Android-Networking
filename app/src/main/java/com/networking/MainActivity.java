package com.networking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.AndroidNetworkingError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.internal.AndroidNetworkingImageLoader;
import com.androidnetworking.widget.GreatImageView;
import com.networking.provider.Images;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String URL_JSON_ARRAY = "http://api.androidhive.info/volley/person_array.json";
    private static final String URL_JSON_OBJECT = "http://api.androidhive.info/volley/person_object.json";
    private static final String URL_STRING = "http://api.androidhive.info/volley/string_response.html";
    private static final String URL_IMAGE = "http://i.imgur.com/2M7Hasn.png";
    private static final String URL_IMAGE_LOADER = "http://i.imgur.com/52md06W.jpg";

    private ImageView imageView;
    private GreatImageView greatImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        greatImageView = (GreatImageView) findViewById(R.id.greatImageView);
        greatImageView.setDefaultImageResId(R.drawable.ic_toys_black_24dp);
        greatImageView.setErrorImageResId(R.drawable.ic_error_outline_black_24dp);
        greatImageView.setImageUrl(Images.imageThumbUrls[0]);
    }

    public void makeRequests(View view) {
        for (int i = 0; i < 10; i++) {
            AndroidNetworking.get(URL_JSON_ARRAY)
                    .setTag(this)
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d(TAG, "onResponse array : " + response.toString());
                        }

                        @Override
                        public void onError(AndroidNetworkingError error) {
                            if (error.getErrorCode() != 0) {
                                Log.d(TAG, "onError hasErrorFromServer : " + error.getContent());
                            } else {
                                Log.d(TAG, "onError : " + error.getError());
                            }
                        }
                    });


            AndroidNetworking.get(URL_JSON_OBJECT)
                    .setTag(this)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "onResponse object : " + response.toString());
                        }

                        @Override
                        public void onError(AndroidNetworkingError error) {
                            if (error.getErrorCode() != 0) {
                                Log.d(TAG, "onError hasErrorFromServer : " + error.getContent());
                            } else {
                                Log.d(TAG, "onError : " + error.getError());
                            }
                        }
                    });

        }
    }

    public void cancelAllRequests(View view) {
        AndroidNetworking.cancel(this);
    }

    public void loadImageDirect(View view) {
        AndroidNetworking.get(URL_IMAGE)
                .setTag("imageRequestTag")
                .setPriority(Priority.MEDIUM)
                .setImageScaleType(null)
                .setBitmapMaxHeight(0)
                .setBitmapMaxWidth(0)
                .setBitmapConfig(Bitmap.Config.ARGB_8888)
                .build()
                .getAsBitmap(new BitmapRequestListener() {
                    @Override
                    public void onResponse(Bitmap response) {
                        Log.d(TAG, "onResponse Bitmap");
                        imageView.setImageBitmap(response);
                    }

                    @Override
                    public void onError(AndroidNetworkingError error) {
                        if (error.getErrorCode() != 0) {
                            Log.d(TAG, "onError hasErrorFromServer : " + error.getContent());
                        } else {
                            Log.d(TAG, "onError : " + error.getError());
                        }
                    }
                });
    }

    public void loadImageFromImageLoader(View view) {
        AndroidNetworkingImageLoader.getInstance().get(URL_IMAGE_LOADER, AndroidNetworkingImageLoader.getImageListener(imageView,
                R.drawable.ic_toys_black_24dp, R.drawable.ic_error_outline_black_24dp));
    }

    public void startGridActivity(View view) {
        startActivity(new Intent(MainActivity.this, ImageGridActivity.class));
    }

    public void startApiTestActivity(View view) {
        startActivity(new Intent(MainActivity.this, ApiTestActivity.class));
    }

}
