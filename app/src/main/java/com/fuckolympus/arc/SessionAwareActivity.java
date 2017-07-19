package com.fuckolympus.arc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by alex on 6.6.17.
 */
public abstract class SessionAwareActivity extends AppCompatActivity {

    protected Session session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = Session.getInstance(this.getApplicationContext());
    }
}
