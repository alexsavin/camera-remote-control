package com.fuckolympus.arc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.fuckolympus.arc.camera.api.CameraState;
import com.fuckolympus.arc.camera.vo.Caminfo;
import com.fuckolympus.arc.camera.vo.Desclist;
import com.fuckolympus.arc.error.CustomExceptionHandler;
import com.fuckolympus.arc.settings.Settings;
import com.fuckolympus.arc.util.Callback;

public class MainActivity extends SessionAwareActivity {

    public static final int WAITING_FLAG = 0;
    public static final int SUCCSESS_FLAG = 1;
    public static final int FAILURE_FLAG = -1;

    private FailureCallback failureCallback = new FailureCallback();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this));
        setContentView(R.layout.activity_main);

        final Button tryAgainBtn = (Button) findViewById(R.id.tryAgainBtn);
        tryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initConnection();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initConnection();
    }

    private void initConnection() {
        updateUI(WAITING_FLAG, "");

        session.getCameraApi().getCameraInfo(
                new Callback<Caminfo>() {
                    @Override
                    public void apply(Caminfo arg) {
                        updateUI(SUCCSESS_FLAG, arg.model);
                        switchToRecMode();
                    }
                }, failureCallback);
    }

    private void switchToRecMode() {
        session.getCameraApi().switchToRecMode(new Callback<String>() {
            @Override
            public void apply(String arg) {
                getCameraProps();
            }
        }, failureCallback);
    }

    private void getCameraProps() {
        session.getCameraApi().getCameraProps(new Callback<Desclist>() {
            @Override
            public void apply(Desclist arg) {
                CameraState cameraState = new CameraState.CameraStateBuilder().setDesclist(arg).build();
                session.setCameraState(cameraState);
                readSettings();
            }
        }, failureCallback);
    }

    private void readSettings() {
        CameraState cameraState = session.getCameraState();
        Settings settings = new Settings(this, cameraState);
        session.setSettings(settings);
    }

    private class FailureCallback implements Callback<String> {
        @Override
        public void apply(String arg) {
            updateUI(FAILURE_FLAG, arg);
        }
    }

    private void updateUI(int flag, String info) {
        final TextView initMsgText = (TextView) findViewById(R.id.initMsgText);
        final TextView cameraName = (TextView) findViewById(R.id.cameraNameText);
        final Button tryAgainBtn = (Button) findViewById(R.id.tryAgainBtn);

        switch (flag) {
            case WAITING_FLAG: {
                initMsgText.setText(R.string.conn_in_progress);
                tryAgainBtn.setVisibility(View.INVISIBLE);
                break;
            }
            case SUCCSESS_FLAG: {
                initMsgText.setText(R.string.conn_success);
                cameraName.setText(info);
                tryAgainBtn.setVisibility(View.INVISIBLE);
                showMenuActivity();
                break;
            }
            case FAILURE_FLAG: {
                initMsgText.setText(R.string.conn_failed);
                cameraName.setText(info);
                tryAgainBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showMenuActivity() {
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        MainActivity.this.startActivity(intent);
    }
}
