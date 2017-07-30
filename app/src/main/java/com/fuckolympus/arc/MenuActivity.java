package com.fuckolympus.arc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.fuckolympus.arc.util.Callback;

public class MenuActivity extends SessionAwareActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button shutterActivityButton = (Button) findViewById(R.id.shutterActivityButton);
        shutterActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ShutterActivity.class);
                MenuActivity.this.startActivity(intent);
            }
        });

        Button imagesActivityButton = (Button) findViewById(R.id.imagesActivityButton);
        imagesActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ImagesActivity.class);
                MenuActivity.this.startActivity(intent);
            }
        });

        Button eclipseActivityButton = (Button) findViewById(R.id.eclipseButton);
        eclipseActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, EclipseMenuActivity.class);
                MenuActivity.this.startActivity(intent);
            }
        });

        Button settingsActivityButton = (Button) findViewById(R.id.settingsButton);
        settingsActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
                MenuActivity.this.startActivity(intent);
            }
        });

        ImageButton powerOffButton = (ImageButton) findViewById(R.id.powerOffButton);
        powerOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                session.getCameraApi().powerOff(MenuActivity.this, new SuccessPowerOffCallback(), new FailurePowerOffCallback());
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                        dialog.dismiss();
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                AlertDialog alertDialog = builder.setTitle(R.string.confirm_dialog_title)
                        .setMessage(R.string.confirm_power_off)
                        .setPositiveButton(R.string.yes_btn, clickListener)
                        .setNegativeButton(R.string.no_btn, clickListener)
                        .create();
                alertDialog.show();
            }
        });
    }

    private class SuccessPowerOffCallback implements Callback<String> {
        @Override
        public void apply(String arg) {
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            MenuActivity.this.startActivity(intent);
        }
    }

    private class FailurePowerOffCallback implements Callback<String> {
        @Override
        public void apply(String arg) {
            // do nothing
        }
    }
}
