package com.marcostoral.keepmoving.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.marcostoral.keepmoving.dto.Route;

public class TrackMyRoute extends Service {

    Route myRoute = new Route();




    public TrackMyRoute() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
