package com.guptagagann.myapplication;

import android.content.SharedPreferences;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import lombok.SneakyThrows;

import static com.guptagagann.myapplication.Constants.VERSION_NAME;

public class About extends AppCompatActivity {
    TextView version;
     SharedPreferences mSharedPreferences;

    @SneakyThrows
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.about);
        super.onCreate(savedInstanceState);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String versionName = mSharedPreferences.getString(VERSION_NAME, "");
        version = findViewById(R.id.version);
        version.setText(versionName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
