package com.fuckolympus.arc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import org.apache.commons.lang3.StringUtils;

public class EclipseActivity extends SessionAwareActivity {

    private boolean totalityFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eclipse);

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
                                ShootingIntentService.startActionTotalityPhase(EclipseActivity.this);
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
                        .setMessage(R.string.confirm_start_process)
                        .setPositiveButton(R.string.yes_btn, clickListener)
                        .setNegativeButton(R.string.no_btn, clickListener)
                        .create();
                alertDialog.show();
            }
        });
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
        eCameraModeText.setText(settings.getByKey(totalityFlag ? R.string.totality_take_mode : R.string.partial_phase_take_mode));

        TextView eFocalValueText = (TextView) findViewById(R.id.eFocalValueText);
        eFocalValueText.setText(totalityFlag
                ? String.format("F%s - F%s", settings.getByKey(R.string.totality_min_focal_value), settings.getByKey(R.string.totality_max_focal_value))
                : String.format("F%s", settings.getByKey(R.string.partial_phase_focal_value)));

        TextView eShutSpeedText = (TextView) findViewById(R.id.eShutSpeedText);
        eShutSpeedText.setText(totalityFlag
                ? String.format("%s - %s", settings.getByKey(R.string.totality_min_shut_speed), settings.getByKey(R.string.totality_max_shut_speed))
                : settings.getByKey(R.string.partial_phase_shut_speed));
    }

    private void startPartialPhaseTimeLapse() {
        final Settings settings = session.getSettings();

        String[] timeLapseIntArr = StringUtils.split(settings.getByKey(R.string.time_lapse_interval), ':');

        final long framesNumber = EclipseUtils.calculateFramesForPartialPhase(settings.getByKey(R.string.totality_time),
                settings.getByKey(R.string.time_lapse_interval));
        final long timeLapseIntervalInSeconds = (Integer.valueOf(timeLapseIntArr[1]) * 60) + Integer.valueOf(timeLapseIntArr[2]);

        final String cameraMode = settings.getByKey(R.string.partial_phase_take_mode);
        final String focalValue = settings.getByKey(R.string.partial_phase_focal_value);
        final String shutSpeedValue = settings.getByKey(R.string.partial_phase_shut_speed);

        CommandChain commandChain = new CommandChain.CommandChainBuilder()
                .addCommand(new Command<String>() {
                    @Override
                    public void apply(Callback<String> nextCommandCallback, Callback<String> failureCallback) {
                        session.getCameraApi().switchToRecMode(EclipseActivity.this, nextCommandCallback, failureCallback);
                    }
                })
                .addCommand(new Command<String>() {
                    @Override
                    public void apply(final Callback<String> nextCommandCallback, final Callback<String> failureCallback) {
                        session.getCameraApi().setCameraProp(EclipseActivity.this, CameraApi.TAKEMODE_PROP, cameraMode,
                                nextCommandCallback, failureCallback);
                    }
                })
                .addCommand(new Command<String>() {
                    @Override
                    public void apply(final Callback<String> nextCommandCallback, final Callback<String> failureCallback) {
                        session.getCameraApi().getCameraProp(EclipseActivity.this,
                                CameraApi.TAKEMODE_PROP, new Callback<String>() {
                                    @Override
                                    public void apply(String arg) {
                                        session.getCameraState().takeMode = arg;
                                        nextCommandCallback.apply(arg);
                                    }
                                }, failureCallback);
                    }
                })
                .addCommand(new Command<String>() {
                    @Override
                    public void apply(final Callback<String> nextCommandCallback, final Callback<String> failureCallback) {
                        session.getCameraApi().setCameraProp(EclipseActivity.this, CameraApi.FOCALVALUE_PROP, focalValue,
                                nextCommandCallback, failureCallback);
                    }
                })
                .addCommand(new Command<String>() {
                    @Override
                    public void apply(final Callback<String> nextCommandCallback, final Callback<String> failureCallback) {
                        session.getCameraApi().getCameraProp(EclipseActivity.this,
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
                        session.getCameraApi().setCameraProp(EclipseActivity.this, CameraApi.SHUTSPEEDVALUE_PROP, shutSpeedValue,
                                nextCommandCallback, failureCallback);
                    }
                })
                .addCommand(new Command<String>() {
                    @Override
                    public void apply(final Callback<String> nextCommandCallback, final Callback<String> failureCallback) {
                        session.getCameraApi().getCameraProp(EclipseActivity.this,
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
                        session.getCameraApi().switchToShutterMode(EclipseActivity.this,
                                new Callback<String>() {
                                    @Override
                                    public void apply(String arg) {
                                        ShootingIntentService.startActionPartialPhase(EclipseActivity.this,
                                                framesNumber, timeLapseIntervalInSeconds * 1000);
                                    }
                                }, failureCallback);
                    }
                })
                .build();

        commandChain.run(this);
    }
}
