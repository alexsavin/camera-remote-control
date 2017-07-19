package com.fuckolympus.arc.util;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by alex on 4.6.17.
 */
public final class HttpUtil {

    public static final String TIMEOUT_ERROR = "Timeout error";
    public static final String NETWORK_ERROR = "Network error";

    private HttpUtil() {
    }

    public static void makeRequest(Context context, int method, String url,
                                   final SuccessResponseHandler successHandler,
                                   final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(method, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        successHandler.handle(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int statusCode = null != error.networkResponse ? error.networkResponse.statusCode : 0;
                        String message = null != error.getMessage() ? error.getMessage()
                                : (error instanceof TimeoutError ? TIMEOUT_ERROR : NETWORK_ERROR);
                        errorHandler.handle(statusCode, message);
                    }
                });

        queue.add(request);
    }

    public interface SuccessResponseHandler {
        void handle(String response);
    }

    public interface ErrorResponseHandler {
        void handle(int statusCode, String message);
    }
}
