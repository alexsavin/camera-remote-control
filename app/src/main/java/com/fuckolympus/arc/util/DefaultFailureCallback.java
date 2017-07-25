package com.fuckolympus.arc.util;

import android.app.AlertDialog;
import android.content.Context;
import com.fuckolympus.arc.R;

/**
 * Created by alex on 25.7.17.
 */
public class DefaultFailureCallback implements Callback<String> {

    private Context context;

    public DefaultFailureCallback(Context context) {
        this.context = context;
    }

    @Override
    public void apply(String arg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = builder.setTitle(R.string.error_dialog_title)
                .setMessage(arg)
                .create();
        alertDialog.show();
    }
}
