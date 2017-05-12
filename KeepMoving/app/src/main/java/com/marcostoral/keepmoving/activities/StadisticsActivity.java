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

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stadistics);


        realm = Realm.getDefaultInstance();
        // Query Realm for all dogs younger than 2 years old

        final RealmResults<Waypoint> routes = realm.where(Waypoint.class).findAll();

        Toast.makeText(this, "El tamaño es "+routes.size(),Toast.LENGTH_LONG).show();

        for (int i = 0; i < routes.size(); i++) {

            Toast.makeText(this, routes.get(i).toString(),Toast.LENGTH_LONG).show();


        }

        realm.close();

    }
}
