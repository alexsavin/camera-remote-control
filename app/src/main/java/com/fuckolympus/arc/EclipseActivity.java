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
import com.fuckolympus.arc.util.StubCallback;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

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
                        .setMessage(R.string.confirm_start_process)
                        .setPositiveButton(R.string.yes_btn, clickListener)
                        .setNegativeButton(R.string.no_btn, clickListener)
                        .create();
                alertDialog.show();
            }
        });
    }

    private void startTotality() {
        String minShutSpeedValue = session.getSettings().getByKey(R.string.totality_min_shut_speed);
        String maxShutSpeedValue = session.getSettings().getByKey(R.string.totality_max_shut_speed);
        String[] shutSpeedValues = session.getCameraState().getShutterSpeedValueEnum();
        List<String> shutSpeedSet = new ArrayList<>();
        boolean foundMin = false;
        for (String shutSpeedValue : shutSpeedValues) {
            if (shutSpeedValue.equals(minShutSpeedValue)) {
                foundMin = true;
            }
            if (shutSpeedValue.equals(maxShutSpeedValue)) {
                shutSpeedSet.add(shutSpeedValue);
                foundMin = false;
            }
            if (foundMin) {
                shutSpeedSet.add(shutSpeedValue);
            }
        }

        TextView framesNumberText = (TextView) findViewById(R.id.framesNumberText);
        framesNumberText.setText(String.valueOf(shutSpeedSet.size()));

        final String focalValue = session.getSettings().getByKey(R.string.totality_focal_value);

        CommandChain commandChain = new CommandChain.CommandChainBuilder()
                .addCommand(new Command<String>() {
                    @Override
                    public void apply(Callback<String> nextCommandCallback, Callback<String> failureCallback) {
                        session.getCameraApi().switchToRecMode(EclipseActivity.this, nextCommandCallback, failureCallback);
                    }
                }).addCommand(new Command<String>() {
                    @Override
                    public void apply(final Callback<String> nextCommandCallback, final Callback<String> failureCallback) {
                        session.getCameraApi().setCameraProp(EclipseActivity.this, CameraApi.FOCALVALUE_PROP, focalValue,
                                new StubCallback<String>(), failureCallback);
                    }
                }).build();

        commandChain.run(this);

        ShootingIntentService.startActionTotalityPhase(EclipseActivity.this, shutSpeedSet.toArray(new String[shutSpeedSet.size()]));
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
        eCameraModeText.setText(session.getCameraState().takeMode);

        TextView eFocalValueText = (TextView) findViewById(R.id.eFocalValueText);
        eFocalValueText.setText(totalityFlag
                ? String.format("F%s", settings.getByKey(R.string.totality_focal_value))
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
