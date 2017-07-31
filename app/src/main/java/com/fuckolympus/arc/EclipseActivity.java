package com.fuckolympus.arc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.fuckolympus.arc.service.ShootingIntentService;
import com.fuckolympus.arc.settings.Settings;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class EclipseActivity extends SessionAwareActivity {

    public static final char SEPARATOR_CHAR = ':';
    private boolean totalityFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eclipse);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            totalityFlag = bundle.getBoolean(getString(R.string.totality_flag));
        }

        Button startButton = (Button) findViewById(R.id.startProcessBtn);
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
                                ShootingIntentService.startActionPartialPhase(EclipseActivity.this);
                            }
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

        String[] totalityTimeArr = StringUtils.split(settings.getByKey(R.string.totality_time), SEPARATOR_CHAR);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Date currentDate = calendar.getTime();

        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                Integer.valueOf(totalityTimeArr[0]), Integer.valueOf(totalityTimeArr[1]), Integer.valueOf(totalityTimeArr[2]));
        Date totalityDate = calendar.getTime();

        long secondsInterval = (totalityDate.getTime() - currentDate.getTime()) / 1000;

        String[] timeLapseIntArr = StringUtils.split(settings.getByKey(R.string.time_lapse_interval), SEPARATOR_CHAR);

        long timeLapseIntervalInSeconds = (Integer.valueOf(timeLapseIntArr[1]) * 60) + Integer.valueOf(timeLapseIntArr[2]);

        long framesNumber = secondsInterval / timeLapseIntervalInSeconds;

        Log.w(this.getClass().getName(), "Seconds interval: " + secondsInterval);
        Log.w(this.getClass().getName(), "Frames number: " + framesNumber);

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
}
