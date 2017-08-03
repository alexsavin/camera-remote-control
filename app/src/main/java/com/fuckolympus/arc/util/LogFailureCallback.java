package com.fuckolympus.arc.util;

import android.util.Log;

/**
 * Created by alex on 3.8.17.
 */
public class LogFailureCallback implements Callback<String> {

    @Override
    public void apply(String arg) {
        Log.w(this.getClass().getName(), arg);
    }
}
