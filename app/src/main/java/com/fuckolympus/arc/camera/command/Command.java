package com.fuckolympus.arc.camera.command;

import android.content.Context;
import com.fuckolympus.arc.util.Callback;
import com.fuckolympus.arc.util.DefaultFailureCallback;

/**
 * Created by alex on 2.8.17.
 */
public abstract class Command<T> {

    protected Command<?> nextCommand;

    public abstract void apply(Callback<T> nextCommandCallback, Callback<String> failureCallback);

    void execute(final Context context) {
        apply(new Callback<T>() {
            @Override
            public void apply(T arg) {
                nextCommand.execute(context);
            }
        }, new DefaultFailureCallback(context));
    }
}
