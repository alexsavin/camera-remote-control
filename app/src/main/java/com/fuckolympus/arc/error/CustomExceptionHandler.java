package com.fuckolympus.arc.error;

import android.util.Log;
import com.fuckolympus.arc.MainActivity;

/**
 * Created by alex on 6.6.17.
 */
public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

    private MainActivity mainActivity;

    public CustomExceptionHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Log.e(mainActivity.getClass().getName(), throwable.getMessage(), throwable);

        // todo - implement application restart

        mainActivity.finish();
        System.exit(-1);
    }
}
