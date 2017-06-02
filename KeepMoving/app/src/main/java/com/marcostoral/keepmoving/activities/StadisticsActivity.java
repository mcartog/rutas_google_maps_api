package com.marcostoral.keepmoving.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.dto.Route;
import com.marcostoral.keepmoving.dto.Waypoint;

import io.realm.Realm;
import io.realm.RealmResults;

public class StadisticsActivity extends AppCompatActivity {

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stadistics);

        realm = Realm.getDefaultInstance();

        final RealmResults<Route> routes = realm.where(Route.class).findAll();



        realm.close();

    }
}
