package com.guptagagann.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static com.guptagagann.myapplication.Constants.IS_WELCOME_ACTIVITY_SHOWN;
import static com.guptagagann.myapplication.Constants.LAUNCH_COUNT;
import static com.guptagagann.myapplication.Constants.VERSION_NAME;

public class SplashScreen extends AppCompatActivity {
 TextView version;
    private static int SPLASH_SCREEN_TIME_OUT=1000;
    private SharedPreferences mSharedPreferences;
    //After completion of 2000 ms, the next activity will get started.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        version=findViewById(R.id.version);
        int count = mSharedPreferences.getInt(LAUNCH_COUNT, 0);
        if (count > 0 && count % 1 == 0)
            // mFeedbackUtils.rateUs();
            mSharedPreferences.edit().putInt(LAUNCH_COUNT, count + 1).apply();

        String versionName = mSharedPreferences.getString(VERSION_NAME, "");
        if (!versionName.equals(BuildConfig.VERSION_NAME)) {
            // WhatsNewUtils.displayDialog(this);
            mSharedPreferences.edit().putString(VERSION_NAME, BuildConfig.VERSION_NAME).apply();
        }

        version.setText("Copyright 2020-21 v"+versionName);

        //check for welcome activity
        openWelcomeActivity();

    }

    private void openWelcomeActivity() {
        if (!mSharedPreferences.getBoolean(IS_WELCOME_ACTIVITY_SHOWN, false)) {
            Intent intent = new Intent( SplashScreen.this, WelcomeActivity.class);
            mSharedPreferences.edit().putBoolean(IS_WELCOME_ACTIVITY_SHOWN, true).apply();
            startActivity(intent);
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i=new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, SPLASH_SCREEN_TIME_OUT);
        }
    }
}
