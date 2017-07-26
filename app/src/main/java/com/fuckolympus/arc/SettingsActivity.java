package com.fuckolympus.arc;

import android.content.Intent;
import android.os.Bundle;

public class SettingsActivity extends SessionAwareActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingsActivity.this, MenuActivity.class);
        SettingsActivity.this.startActivity(intent);
    }
}
