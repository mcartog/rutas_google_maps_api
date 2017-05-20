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

        Toast.makeText(this, "El tamaño es "+routes.size(),Toast.LENGTH_LONG).show();

        if(routes.size()>0){
            for (int i = routes.size() -2; i < routes.size(); i++) {

                Toast.makeText(this, routes.get(i).toString(),Toast.LENGTH_LONG).show();

                if(routes.get(i).getWaypointList().size() > 0){
                    for (int j = 0; j < routes.get(i).getWaypointList().size(); j++){
                        Toast.makeText(this, routes.get(i).getWaypointList().get(j).toString(), Toast.LENGTH_LONG).show();
                    }
                }

            }
        }


        realm.close();

    }
}
