package com.guptagagann.myapplication;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import lombok.SneakyThrows;

public class About extends AppCompatActivity {
    TextView version;
    @SneakyThrows
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.about);
        super.onCreate(savedInstanceState);
        PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
        float versionCode = packageInfo.versionCode;
        version = findViewById(R.id.version);
        version.setText("2.0");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
