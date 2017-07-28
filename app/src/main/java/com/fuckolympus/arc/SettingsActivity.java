package com.fuckolympus.arc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.fuckolympus.arc.camera.api.CameraState;
import com.fuckolympus.arc.component.CustomSpinner;
import com.fuckolympus.arc.settings.Settings;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Locale;

public class SettingsActivity extends SessionAwareActivity {

    public static final String TIME_FORMAT = "%02d:%02d:%02d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView totalityTimeText = (TextView) findViewById(R.id.totalityTimeText);
        totalityTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(R.id.totalityTimeText, R.string.totality_time, false);
            }
        });

        TextView totalityDurationText = (TextView) findViewById(R.id.totalityDurationText);
        totalityDurationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(R.id.totalityDurationText, R.string.totality_duration, true);
            }
        });

        CameraState cameraState = session.getCameraState();
        configureSpinner(R.id.takeModePPSpinner, cameraState.getTakeModeEnum(), R.string.partial_phase_take_mode);
        configureSpinner(R.id.shutSpeedPPSpinner, cameraState.getShutterSpeedValueEnum(), R.string.partial_phase_shut_speed);
        configureSpinner(R.id.focalValuePPSpinner, cameraState.getFocalValueEnum(), R.string.partial_phase_focal_value);
    }

    private void configureSpinner(int spinnerId, String[] values, final int preferenceKey) {
        Spinner spinner = (Spinner) findViewById(spinnerId);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item,
                values);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = ((ArrayAdapter<String>) parent.getAdapter()).getItem(position);
                Settings settings = session.getSettings();
                settings.updateByKey(preferenceKey, value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // leave current value
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateUI(session.getSettings());
    }

    private void updateUI(Settings settings) {
        TextView totalityTimeText = (TextView) findViewById(R.id.totalityTimeText);
        totalityTimeText.setText(settings.getByKey(R.string.totality_time));

        TextView totalityDurationText = (TextView) findViewById(R.id.totalityDurationText);
        totalityDurationText.setText(settings.getByKey(R.string.totality_duration));

        setSpinnerSelection(R.id.takeModePPSpinner, settings.getByKey(R.string.partial_phase_take_mode));
        setSpinnerSelection(R.id.shutSpeedPPSpinner, settings.getByKey(R.string.partial_phase_shut_speed));
        setSpinnerSelection(R.id.focalValuePPSpinner, settings.getByKey(R.string.partial_phase_focal_value));
    }

    private void setSpinnerSelection(int spinnerId, String selectedValue) {
        Spinner spinner = (Spinner) findViewById(spinnerId);
        spinner.setSelection(((ArrayAdapter<String>) spinner.getAdapter()).getPosition(selectedValue));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingsActivity.this, MenuActivity.class);
        SettingsActivity.this.startActivity(intent);
    }

    private void showTimePickerDialog(final int boundViewId, final int preferenceKey, boolean disableHours) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        LayoutInflater inflater = SettingsActivity.this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.custom_time_picker, null);

        final CustomSpinner hoursSpinner = (CustomSpinner) view.findViewById(R.id.hoursSpinner);
        final CustomSpinner minsSpinner = (CustomSpinner) view.findViewById(R.id.minutesSpinner);
        final CustomSpinner secsSpinner = (CustomSpinner) view.findViewById(R.id.secondsSpinner);
        setTimeValue(session.getSettings().getByKey(preferenceKey), hoursSpinner, minsSpinner, secsSpinner);

        hoursSpinner.setVisibility(disableHours ? View.INVISIBLE : View.VISIBLE);

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        String value = String.format(Locale.US, TIME_FORMAT,
                                hoursSpinner.getSelectedValue(), minsSpinner.getSelectedValue(), secsSpinner.getSelectedValue());
                        TextView boundTextView = (TextView) findViewById(boundViewId);
                        boundTextView.setText(value);
                        session.getSettings().updateByKey(preferenceKey, value);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
                dialog.dismiss();
            }
        };

        AlertDialog dialog = builder.setView(view)
                .setPositiveButton(R.string.ok_btn, onClickListener)
                .setNegativeButton(R.string.cancel_btn, onClickListener)
                .create();
        dialog.show();
    }

    private void setTimeValue(String value, CustomSpinner hoursSpinner, CustomSpinner minsSpinner, CustomSpinner secsSpinner) {
        String[] items = StringUtils.split(value, ':');
        if (items.length < 3) {
            return;
        }
        hoursSpinner.setSelectedValue(NumberUtils.toInt(items[0]));
        minsSpinner.setSelectedValue(NumberUtils.toInt(items[1]));
        secsSpinner.setSelectedValue(NumberUtils.toInt(items[2]));
    }
}
