package com.marcostoral.keepmoving.activities;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.dto.Route;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //Map
    private GoogleMap mMap;

    //UI
    private Button btnStart;
    private Button btnStop;

    //Location
    private LocationManager locman;
    private LocationListener loclis;
    private Location location;

    private double lat;
    private double lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        init();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        LatLng myLocation = myLocationInit();

        // Add a marker in Sydney and move the camera
       // LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("You are here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
    }

    ///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////

    public void init(){

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStart.setVisibility(View.INVISIBLE);
                Route myRoute = new Route();
                Toast.makeText(MapsActivity.this,"lanzo servicio",Toast.LENGTH_SHORT).show();

                //Lanzo servicio


                btnStop.setVisibility(View.VISIBLE);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStop.setVisibility(View.INVISIBLE);
                Toast.makeText(MapsActivity.this,"detengo servicio",Toast.LENGTH_SHORT).show();
                btnStart.setVisibility(View.VISIBLE);
            }
        });



    }

    public LatLng myLocationInit(){
        LatLng myLocation;
        locman = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (location != null) {
            lat = location.getLatitude();
            lon = location.getLongitude();

            myLocation = new LatLng(lat, lon);

            return myLocation;

        } else {
            LatLng sydney = new LatLng(-34, 151);
            return sydney;
        }

    }


}
