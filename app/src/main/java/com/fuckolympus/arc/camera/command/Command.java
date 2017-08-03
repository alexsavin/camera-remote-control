package com.fuckolympus.arc.camera.command;

import android.app.IntentService;
import android.content.Context;
import com.fuckolympus.arc.util.Callback;
import com.fuckolympus.arc.util.DefaultFailureCallback;
import com.fuckolympus.arc.util.LogFailureCallback;

/**
 * Created by alex on 2.8.17.
 */
public abstract class Command<T> {

    Command<?> nextCommand;

    public abstract void apply(Callback<T> nextCommandCallback, Callback<String> failureCallback);

    void execute(final Context context) {
        apply(new Callback<T>() {
            @Override
            public void apply(T arg) {
                nextCommand.execute(context);
            }
        }, (context instanceof IntentService ? new LogFailureCallback() : new DefaultFailureCallback(context)));
    }
}
