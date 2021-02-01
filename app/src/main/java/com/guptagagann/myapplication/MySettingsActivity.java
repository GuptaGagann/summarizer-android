package com.guptagagann.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MySettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.action_about, new MySettingsFragment())
                .commit();
    }
}