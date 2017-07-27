package com.fuckolympus.arc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.fuckolympus.arc.component.CustomSpinner;

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
                showTimePickerDialog();
            }
        });
    }

    private void showTimePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        LayoutInflater inflater = SettingsActivity.this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.custom_time_picker, null);

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        CustomSpinner hoursSpinner = (CustomSpinner) view.findViewById(R.id.hoursSpinner);
                        CustomSpinner minsSpinner = (CustomSpinner) view.findViewById(R.id.minutesSpinner);
                        CustomSpinner secsSpinner = (CustomSpinner) view.findViewById(R.id.secondsSpinner);
                        String timeValue = String.format(Locale.US, TIME_FORMAT,
                                hoursSpinner.getSelectedValue(), minsSpinner.getSelectedValue(), secsSpinner.getSelectedValue());
                        TextView totalityTimeText = (TextView) findViewById(R.id.totalityTimeText);
                        totalityTimeText.setText(timeValue);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingsActivity.this, MenuActivity.class);
        SettingsActivity.this.startActivity(intent);
    }
}
