package com.fuckolympus.arc.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by alex on 6.8.17.
 */
public class ImageLoaderRequestQueue {

    private static ImageLoaderRequestQueue instance;

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    private ImageLoaderRequestQueue(Context context) {
        createRequestQueue(context);

        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<>(50);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    private void createRequestQueue(Context ctx) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx);
        }
    }

    public static synchronized ImageLoaderRequestQueue getInstance(Context context) {
        if (instance == null) {
            instance = new ImageLoaderRequestQueue(context);
        }
        return instance;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
