package com.fuckolympus.arc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EclipseMenuActivity extends SessionAwareActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eclipse_menu);

        Button partialPhaseButton = (Button) findViewById(R.id.partialPhaseButton);
        partialPhaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo - check time
                startEclipseActivity();
            }
        });

        Button totalityButton = (Button) findViewById(R.id.totalityButton);
        totalityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo - check time
                startEclipseActivity();
            }
        });
    }

    private void startEclipseActivity() {
        Intent intent = new Intent(EclipseMenuActivity.this, EclipseActivity.class);
        EclipseMenuActivity.this.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EclipseMenuActivity.this, MenuActivity.class);
        EclipseMenuActivity.this.startActivity(intent);
    }
}
