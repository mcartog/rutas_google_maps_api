package com.marcostoral.keepmoving.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.dto.Route;
import com.marcostoral.keepmoving.fragments.RouteDetailsFragment;

import io.realm.Realm;
import io.realm.RealmResults;

public class RouteDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    //Realm
    private Realm realm;
    private RealmResults<Route> routeById;
    private Route route;

    //Map
    private GoogleMap mMap;
    private CameraPosition camera;



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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapRouteDetails);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }


    /**
     * Método que devuelve la ruta seleccionada en la base de datos a apartir de su id.
     * @param id  Id de la ruta seleccionada
     * @return Ruta seleccionada en base de datos.
     */
    public Route getRoute (long id){

        realm = Realm.getDefaultInstance();
        routeById = realm.where(Route.class).equalTo("id", id).findAll();
        return routeById.get(0);

    }

    /**
     * Inserta los waypoints en el mapa.
     * @param route
     */
    public void drawWaypoint(Route route){
        //Redibuja marcadores.
        if(route.getWaypointList().size() > 0){
            for (int j = 0; j < route.getWaypointList().size(); j++){
                LatLng waypoint = new LatLng(route.getWaypointList().get(j).getLtd(),route.getWaypointList().get(j).getLng());
                Toast.makeText(RouteDetailsActivity.this, waypoint.toString(), Toast.LENGTH_SHORT).show();
                mMap.addMarker(new MarkerOptions().position(waypoint));

                }
            }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
//        camera = CameraPosition
        drawWaypoint(route);
    }


}
