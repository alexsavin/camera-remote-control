package com.fuckolympus.arc;

import android.content.Intent;
import android.os.Bundle;

public class EclipseActivity extends SessionAwareActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eclipse);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EclipseActivity.this, MenuActivity.class);
        EclipseActivity.this.startActivity(intent);
    }
}
