package com.fuckolympus.arc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.fuckolympus.arc.camera.api.CameraState;
import com.fuckolympus.arc.camera.api.ShutterMode;
import com.fuckolympus.arc.util.Callback;
import com.fuckolympus.arc.util.DefaultFailureCallback;

public class ShutterActivity extends SessionAwareActivity {

    public static final String FOCAL_VALUE = "F%s";
    private volatile boolean buttonPressed = false;

    private DefaultFailureCallback failureCallback = new DefaultFailureCallback(this);
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
        updateUI(session.getCameraState());
        switchToShutterMode();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ShutterActivity.this, MenuActivity.class);
        ShutterActivity.this.startActivity(intent);
    }

    private void updateUI(CameraState cameraState) {
        TextView cameraModeText = (TextView) findViewById(R.id.cameraModeText);
        TextView shutterSpeedText = (TextView) findViewById(R.id.shutterSpeedText);
        TextView focalValueText = (TextView) findViewById(R.id.focalValueText);
        TextView expCompText = (TextView) findViewById(R.id.expCompText);

        cameraModeText.setText(cameraState.takeMode);
        shutterSpeedText.setText(cameraState.shutterSpeedValue);
        focalValueText.setText(String.format(FOCAL_VALUE, String.valueOf(cameraState.focalValue)));
        expCompText.setText(String.valueOf(cameraState.expComp));
    }

    private void switchToShutterMode() {
        session.getCameraApi().switchToShutterMode(new Callback<String>() {
            @Override
            public void apply(String arg) {
                ImageButton shutterButton = (ImageButton) findViewById(R.id.shutterButton);
                shutterButton.setClickable(true);
            }
        }, failureCallback);
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
            firstRelease();
        }
    }

    private class SuccessCallback implements Callback<String> {
        @Override
        public void apply(String arg) {
            // do nothing
        }
    }
}
