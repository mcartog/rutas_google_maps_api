package com.marcostoral.keepmoving.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

import com.google.android.gms.common.api.GoogleApiActivity;
import com.marcostoral.keepmoving.dto.Route;

public class TrackMyRoute extends Service implements com.google.android.gms.location.LocationListener{

    private LocationManager locman;
    private LocationListener loclis;
    private Location location;


    private double lat;
    private double lon;

    public TrackMyRoute() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(android.location.Location location) {

    }


}
