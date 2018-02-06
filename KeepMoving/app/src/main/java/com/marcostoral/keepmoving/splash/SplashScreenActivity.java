package com.marcostoral.keepmoving.splash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.ads.MobileAds;
import com.marcostoral.keepmoving.activities.MainActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        MobileAds.initialize(this, "ca-app-pub-2224529439791003~4428454939");

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

        finish();
    }
}
