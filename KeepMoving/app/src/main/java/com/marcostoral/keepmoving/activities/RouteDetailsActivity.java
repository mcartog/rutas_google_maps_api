package com.marcostoral.keepmoving.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.fragments.RouteDetailsFragment;
import com.marcostoral.keepmoving.models.Route;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class RouteDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    //Realm
    private Realm realm;
    private RealmResults<Route> routeById;
    private Route route;

    //Map Fragment
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    //Fragment
    private RouteDetailsFragment detailsFragment;

    //Model id
    private long id;
    private LatLng cornerMax;
    private LatLng cornerMin;

//    //Chart
//    private LineChart lineChart;
//    private ArrayList<Entry> entries;
//    private float t;
//    private float z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_details);

        //Recibe el id de la ruta y lo pasa al fragment para renderizar la ruta seleccionada.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getLong("routeID");
            route = getRoute(id);
            detailsFragment = (RouteDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentDetailsRoute);
            detailsFragment.renderRoute(route);

            cornerMax = new LatLng(route.getMaxLtd(), route.getMaxLng());
            cornerMin = new LatLng(route.getMinLtd(), route.getMinLng());


        }

        //Map fragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapRouteDetails);
        mapFragment.getMapAsync(this);
        mapFragment.setRetainInstance(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        //Obtiene el primer punto de la ruta, lo convierte a LatLng.
//        LatLng initialPoint = new LatLng(route.getWaypointList().get(0).getLtd(), route.getWaypointList().get(0).getLng());


        //Establece el zoom mínimo y centra la cámara en el primer punto de la ruta.
        mMap.setMinZoomPreference(10);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(cornerMax);
        builder.include(cornerMin);
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.20);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,15));
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,width,height,padding));

        drawWaypoint(route);
        drawRoute(route);

    }

    /**
     * Retorna ruta seleccionada en la base de datos a apartir de su id.
     *
     * @param id Id de la ruta seleccionada
     * @return Ruta seleccionada en base de datos.
     */
    public Route getRoute(long id) {

        realm = Realm.getDefaultInstance();
        routeById = realm.where(Route.class).equalTo("id", id).findAll();
        return routeById.get(0);

    }

    /**
     * Inserta en el mapa los puntos (markers) en los que se han capturado fotografías.
     *
     * @param route Ruta (Route) seleccionada en db.
     */
    public void drawWaypoint(Route route) {
        //Redibuja marcadores.
        if (route.getWaypointList().size() > 0) {
            for (int j = 0; j < route.getWaypointList().size(); j++) {
                if (route.getWaypointList().get(j).getPath() != null) {
                    LatLng waypoint = new LatLng(route.getWaypointList().get(j).getLtd(), route.getWaypointList().get(j).getLng());
                    mMap.addMarker(new MarkerOptions().position(waypoint));
                }
            }
        }
    }

    /**
     * Dibuja path (polyline) de la ruta.
     *
     * @param route Ruta (Route) seleccionada en db.
     */
    public void drawRoute(Route route) {

        List<LatLng> latLngs = new ArrayList<>();

        if (route.getWaypointList().size() > 0) {
            for (int j = 0; j < route.getWaypointList().size(); j++) {
                LatLng point = new LatLng(route.getWaypointList().get(j).getLtd(), route.getWaypointList().get(j).getLng());
                latLngs.add(j, point);
            }
        }

        PolylineOptions routeTrack = new PolylineOptions()
                .width(5)
                .color(Color.RED)
                .geodesic(true);

        routeTrack.addAll(latLngs);

        mMap.addPolyline(routeTrack);

    }

}

