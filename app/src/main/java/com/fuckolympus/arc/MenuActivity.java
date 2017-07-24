package com.fuckolympus.arc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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

        Button eclipseActivityButton = (Button) findViewById(R.id.eclipseButton);
        eclipseActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, EclipseActivity.class);
                MenuActivity.this.startActivity(intent);
            }
        });

        ImageButton powerOffButton = (ImageButton) findViewById(R.id.powerOffButton);
        powerOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo ask to switch camera off
            }
        });
    }
}
