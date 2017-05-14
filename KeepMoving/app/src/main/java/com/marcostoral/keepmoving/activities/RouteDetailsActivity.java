package com.marcostoral.keepmoving.activities;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.dto.Route;
import com.marcostoral.keepmoving.fragments.RouteDetailsFragment;

import io.realm.Realm;
import io.realm.RealmResults;

public class RouteDetailsActivity extends AppCompatActivity {

    private Realm realm;
    private RealmResults<Route> routeById;
    private Route route;

    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_details);

        //Recibo el id de la ruta y se lo paso al fragment para renderizar la ruta.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getLong("routeID");
            route = getRoute(id);
            RouteDetailsFragment detailsFragment = (RouteDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentDetailsRoute);
            detailsFragment.renderRoute(route);

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }


    public Route getRoute (long id){

        realm = Realm.getDefaultInstance();
        routeById = realm.where(Route.class).equalTo("id", id).findAll();
        return routeById.get(0);

    }

}
