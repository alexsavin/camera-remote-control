package com.fuckolympus.arc;

import android.content.Intent;
import android.os.Bundle;

public class ImagesActivity extends SessionAwareActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ImagesActivity.this, MenuActivity.class);
        ImagesActivity.this.startActivity(intent);
    }
}
