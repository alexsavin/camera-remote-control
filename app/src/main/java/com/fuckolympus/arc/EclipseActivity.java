package com.fuckolympus.arc;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.fuckolympus.arc.camera.api.CameraApi;
import com.fuckolympus.arc.camera.command.Command;
import com.fuckolympus.arc.camera.command.CommandChain;
import com.fuckolympus.arc.eclipse.EclipseUtils;
import com.fuckolympus.arc.service.ShootingIntentService;
import com.fuckolympus.arc.settings.Settings;
import com.fuckolympus.arc.util.Callback;
import com.fuckolympus.arc.util.StubCallback;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class EclipseActivity extends SessionAwareActivity {

    public static final String FOCAL_FORMAT = "F%s";

    private boolean totalityFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eclipse);

        IntentFilter intentFilter = new IntentFilter(ShootingIntentService.FRAME_TAKEN_ACTION);
        IntentFilter completeFilter = new IntentFilter(ShootingIntentService.SHOOTING_COMPLETE_ACTION);
        ShootingStateReceiver shootingStateReceiver = new ShootingStateReceiver();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(shootingStateReceiver, intentFilter);
        localBroadcastManager.registerReceiver(shootingStateReceiver, completeFilter);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            totalityFlag = bundle.getBoolean(getString(R.string.totality_flag));
        }

        final Button startButton = (Button) findViewById(R.id.startProcessBtn);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            if (totalityFlag) {
                                startTotality();
                            } else {
                                startPartialPhaseTimeLapse();
                            }
                            startButton.setEnabled(false);
                        }
                        dialog.dismiss();
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(EclipseActivity.this);
                AlertDialog alertDialog = builder.setTitle(totalityFlag ? R.string.total_phase_label : R.string.part_phase_label)
                        .setMessage(totalityFlag ? R.string.confirm_totality_shooting : R.string.confirm_partial_phase_shooting)
                        .setPositiveButton(R.string.yes_btn, clickListener)
                        .setNegativeButton(R.string.no_btn, clickListener)
                        .create();
                alertDialog.show();
            }
        });
    }

    private void startTotality() {
        String[] selectedShutSpeeds = StringUtils.split(session.getSettings().getByKey(R.string.totality_shut_speed_set), ',');
        if (selectedShutSpeeds == null || selectedShutSpeeds.length == 0) {
            return;
        }

        TextView framesNumberText = (TextView) findViewById(R.id.framesNumberText);
        framesNumberText.setText(String.valueOf(selectedShutSpeeds.length));

        final String focalValue = session.getSettings().getByKey(R.string.totality_focal_value);

        CommandChain commandChain = new CommandChain.CommandChainBuilder()
                .addCommand(new Command<String>() {
                    @Override
                    public void apply(Callback<String> nextCommandCallback, Callback<String> failureCallback) {
                        session.getCameraApi().switchToRecMode(nextCommandCallback, failureCallback);
                    }
                }).addCommand(new Command<String>() {
                    @Override
                    public void apply(final Callback<String> nextCommandCallback, final Callback<String> failureCallback) {
                        session.getCameraApi().setCameraProp(CameraApi.FOCALVALUE_PROP, focalValue.trim(),
                                new StubCallback<String>(), failureCallback);
                    }
                }).build();

        commandChain.run(this);

        long msInterval = NumberUtils.toLong(session.getSettings().getByKey(R.string.totality_interval), 5000);

        ShootingIntentService.startActionTotalityPhase(EclipseActivity.this, selectedShutSpeeds, msInterval);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateUI();
        if (totalityFlag) {
            calculateFramesNumberForTotality();
        } else {
            calculateFramesNumberForTimeLapse();
        }
    }

    private void calculateFramesNumberForTotality() {
        Settings settings = session.getSettings();

        String[] selectedShutSpeeds = StringUtils.split(settings.getByKey(R.string.totality_shut_speed_set), ',');
        TextView framesNumberText = (TextView) findViewById(R.id.framesNumberText);
        framesNumberText.setText(String.valueOf(selectedShutSpeeds.length));
    }

    private void calculateFramesNumberForTimeLapse() {
        Settings settings = session.getSettings();

        long framesNumber = EclipseUtils.calculateFramesForPartialPhase(settings.getByKey(R.string.totality_time),
                settings.getByKey(R.string.time_lapse_interval));

        TextView framesNumberText = (TextView) findViewById(R.id.framesNumberText);
        framesNumberText.setText(String.valueOf(framesNumber));
    }

    private void updateUI() {
        Settings settings = session.getSettings();

        TextView currentModeText = (TextView) findViewById(R.id.currentModeText);
        currentModeText.setText(totalityFlag ? R.string.total_phase_label : R.string.part_phase_label);

        TextView eCameraModeText = (TextView) findViewById(R.id.eCameraModeText);
        eCameraModeText.setText(session.getCameraState().takeMode);

        TextView eFocalValueText = (TextView) findViewById(R.id.eFocalValueText);
        eFocalValueText.setText(totalityFlag
                ? String.format(FOCAL_FORMAT, settings.getByKey(R.string.totality_focal_value))
                : String.format(FOCAL_FORMAT, settings.getByKey(R.string.partial_phase_focal_value)));

        TextView eShutSpeedText = (TextView) findViewById(R.id.eShutSpeedText);
        eShutSpeedText.setText(totalityFlag
                ? settings.getByKey(R.string.totality_shut_speed_set)
                : settings.getByKey(R.string.partial_phase_shut_speed));
    }

    private void startPartialPhaseTimeLapse() {
        final Settings settings = session.getSettings();

        String[] timeLapseIntArr = StringUtils.split(settings.getByKey(R.string.time_lapse_interval), ':');

        final long framesNumber = EclipseUtils.calculateFramesForPartialPhase(settings.getByKey(R.string.totality_time),
                settings.getByKey(R.string.time_lapse_interval));
        final long timeLapseIntervalInSeconds = (Integer.valueOf(timeLapseIntArr[1]) * 60) + Integer.valueOf(timeLapseIntArr[2]);

        final String focalValue = settings.getByKey(R.string.partial_phase_focal_value);
        final String shutSpeedValue = settings.getByKey(R.string.partial_phase_shut_speed);

        CommandChain commandChain = new CommandChain.CommandChainBuilder()
                .addCommand(new Command<String>() {
                    @Override
                    public void apply(Callback<String> nextCommandCallback, Callback<String> failureCallback) {
                        session.getCameraApi().switchToRecMode(nextCommandCallback, failureCallback);
                    }
                })
                .addCommand(new Command<String>() {
                    @Override
                    public void apply(final Callback<String> nextCommandCallback, final Callback<String> failureCallback) {
                        session.getCameraApi().setCameraProp(CameraApi.FOCALVALUE_PROP, focalValue.trim(),
                                nextCommandCallback, failureCallback);
                    }
                })
                .addCommand(new Command<String>() {
                    @Override
                    public void apply(final Callback<String> nextCommandCallback, final Callback<String> failureCallback) {
                        session.getCameraApi().getCameraProp(
                                CameraApi.FOCALVALUE_PROP, new Callback<String>() {
                                    @Override
                                    public void apply(String arg) {
                                        session.getCameraState().focalValue = arg;
                                        nextCommandCallback.apply(arg);
                                    }
                                }, failureCallback);
                    }
                })
                .addCommand(new Command<String>() {
                    @Override
                    public void apply(final Callback<String> nextCommandCallback, final Callback<String> failureCallback) {
                        session.getCameraApi().setCameraProp(CameraApi.SHUTSPEEDVALUE_PROP, shutSpeedValue.trim(),
                                nextCommandCallback, failureCallback);
                    }
                })
                .addCommand(new Command<String>() {
                    @Override
                    public void apply(final Callback<String> nextCommandCallback, final Callback<String> failureCallback) {
                        session.getCameraApi().getCameraProp(
                                CameraApi.SHUTSPEEDVALUE_PROP, new Callback<String>() {
                                    @Override
                                    public void apply(String arg) {
                                        session.getCameraState().shutterSpeedValue = arg;
                                        nextCommandCallback.apply(arg);
                                    }
                                }, failureCallback);
                    }
                })
                .addCommand(new Command<String>() {
                    @Override
                    public void apply(Callback<String> nextCommandCallback, Callback<String> failureCallback) {
                        session.getCameraApi().switchToShutterMode(
                                new Callback<String>() {
                                    @Override
                                    public void apply(String arg) {
                                        ShootingIntentService.startActionPartialPhase(EclipseActivity.this,
                                                framesNumber, timeLapseIntervalInSeconds * 1000,
                                                totalityTimeInMillis(settings.getByKey(R.string.totality_time)));
                                    }
                                }, failureCallback);
                    }
                })
                .build();

        commandChain.run(this);
    }

    private long totalityTimeInMillis(String totalityTime) {
        String[] totalityTimeArr = StringUtils.split(totalityTime, ':');
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                Integer.valueOf(totalityTimeArr[0]), Integer.valueOf(totalityTimeArr[1]), Integer.valueOf(totalityTimeArr[2]));
        Date totalityDate = calendar.getTime();
        return totalityDate.getTime();
    }

    private class ShootingStateReceiver extends BroadcastReceiver {

        private ShootingStateReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (ShootingIntentService.FRAME_TAKEN_ACTION.equals(intent.getAction())) {
                String currNumber = bundle.getString(ShootingIntentService.EXTENDED_DATA_STATUS);
                TextView currentFrameNumberText = (TextView) findViewById(R.id.currentFrameNumberText);
                currentFrameNumberText.setText(currNumber);
            } else if (ShootingIntentService.SHOOTING_COMPLETE_ACTION.equals(intent.getAction())) {
                TextView currentFrameNumberText = (TextView) findViewById(R.id.currentFrameNumberText);
                currentFrameNumberText.setText(R.string.shooting_complete_msg);
                currentFrameNumberText.setTextColor(Color.parseColor(getString(R.color.colorAccent)));

                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (r.isPlaying()) {
                            r.stop();
                        }
                    }
                }, 10000);
            }
        }
    }
}
