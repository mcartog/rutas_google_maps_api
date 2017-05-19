package com.marcostoral.keepmoving.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.dto.Waypoint;
import com.marcostoral.keepmoving.fragments.MapsEnvironmentFragment;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    //Map
    private GoogleMap mMap;
    private CameraPosition camera;

    private String type;
    private MapsEnvironmentFragment mapsEnvironmentFragment;

    //Location
    private LocationManager locationManager;
    private Location currentLocation;

    //Control
    public boolean isTracking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Recibo el tipo de actividad y lo paso al fragment.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type = extras.getString("type");
            mapsEnvironmentFragment = (MapsEnvironmentFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_maps_environment);
            mapsEnvironmentFragment.routeTypeIconReceptor(type);
        }


        //Al iniciar la actividad el trackeo está desactivado hasta pulsar el botón Start.
        isTracking = false;

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
     * Anula el botón back si hay una ruta en proceso de captura.
     */
    @Override
    public void onBackPressed() {

        isTracking = MapsEnvironmentFragment.isTrackingNow();

        if (isTracking == true) {
            //Si el trackeo está en curso.
            Toast.makeText(this, "Finaliza la ruta antes de volver", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "puedes retornar", Toast.LENGTH_LONG).show();
            super.onBackPressed();
        }

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
//        camera = new CameraPosition(currentLocation);

        //Defino unos limites entre los que se manejará el zoom de la aplicación.
        mMap.setMaxZoomPreference(5);
        mMap.setMinZoomPreference(5);

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

        //Las siguientes opciones mejoran el rendimiento del posicionamiento.
        //Activar la localización permite conocer mi posición actual.
        mMap.setMyLocationEnabled(true);
        //Desactiva el boón de localización de la interfaz de usuario de GooogleMaps.
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        //Cuando implemente preferencias de usuario: tipo de mapa, color de polilinea, de marcadores...
        // mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        //Provedor de la señal, ms de refresco (se recomienda mínimo 60 seg), distancia de refresco, listener de cambio de ubicacion.
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 2000, 0, this);
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 2000, 0, this);

        Toast.makeText(this, "recibo ", Toast.LENGTH_LONG).show();

    }


    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "location change", Toast.LENGTH_LONG).show();
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
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        currentLocation = location;
        if (currentLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));

        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //Devuelve la posición actual.
    public Location getLocationWaypoint() {
        return currentLocation;
    }

    public LatLng getWaypointCoords() {
        LatLng singlePoint;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        currentLocation = location;
        if (currentLocation != null) {
            singlePoint = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            return singlePoint;

        }
        return null;
    }
}
