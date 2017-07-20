package com.fuckolympus.arc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.fuckolympus.arc.camera.api.ShutterMode;
import com.fuckolympus.arc.util.Callback;

public class ShutterActivity extends SessionAwareActivity {

    private volatile boolean buttonPressed = false;

    private FailureCallback failureCallback = new FailureCallback();
    private ShutterCallback shutterCallback = new ShutterCallback();
    private SuccessCallback successCallback = new SuccessCallback();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shutter);

        final ImageButton shutterButton = (ImageButton) findViewById(R.id.shutterButton);
        shutterButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        firstPush();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (buttonPressed) {
                                    execShutter();
                                }
                            }
                        }, 2000);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (buttonPressed) {
                            firstRelease();
                        }
                        break;
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        switchToShutterMode();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ShutterActivity.this, MenuActivity.class);
        ShutterActivity.this.startActivity(intent);
    }

    private void switchToShutterMode() {
        session.getCameraApi().switchToShutterMode(
                new Callback<String>() {
                    @Override
                    public void apply(String arg) {
                        ImageButton shutterButton = (ImageButton) findViewById(R.id.shutterButton);
                        shutterButton.setClickable(true);
                    }
                }, failureCallback
        );
    }

    private void execShutter() {
        Log.w(this.getClass().getName(), "shutter");
        session.getCameraApi().executeShutter(ShutterMode.SND_PUSH, shutterCallback, failureCallback);
    }

    private void firstRelease() {
        Log.w(this.getClass().getName(), "release");
        session.getCameraApi().executeShutter(ShutterMode.FST_RELEASE, successCallback, failureCallback);
        buttonPressed = false;
    }

    private void firstPush() {
        Log.w(this.getClass().getName(), "push");
        buttonPressed = true;
        session.getCameraApi().executeShutter(ShutterMode.FST_PUSH, successCallback, failureCallback);
    }

    private class ShutterCallback implements Callback<String> {
        @Override
        public void apply(String arg) {
            TextView errorText = (TextView) findViewById(R.id.errorText);
            errorText.setText("");
            firstRelease();
        }
    }

    private class SuccessCallback implements Callback<String> {
        @Override
        public void apply(String arg) {
            TextView errorText = (TextView) findViewById(R.id.errorText);
            errorText.setText("");
        }
    }

    private class FailureCallback implements Callback<String> {

        @Override
        public void apply(String arg) {
            TextView errorText = (TextView) findViewById(R.id.errorText);
            errorText.setText(arg);
        }
    }
}
