package com.marcostoral.keepmoving.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.fragments.MapsEnvironmentFragment;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, com.google.android.gms.location.LocationListener {

    //Map
    private GoogleMap mMap;

    private String type;
    private MapsEnvironmentFragment mapsEnvironmentFragment;

    private LocationManager locationManager;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Recibo el tipo de actividad y lo paso al fragment.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type = extras.getString("type");
            MapsEnvironmentFragment mapsFragment = (MapsEnvironmentFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_maps_environment);
            mapsFragment.routeTypeIconReceptor(type);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
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
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);  //Desactivo el botón de localización de google de la interfaz
       // mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);  //Coger esto de las prefes.

        //Provedor de la señal, ms de refresco (3 seg), distancia de refresco, listener de cambio de ubicacion.
//        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 1000, 0, (LocationListener) this);
 //       locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 3000, 30, (LocationListener) this);

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
