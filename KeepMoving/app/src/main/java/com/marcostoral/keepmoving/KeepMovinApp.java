package com.marcostoral.keepmoving;

import android.app.Application;

import com.marcostoral.keepmoving.dto.Route;
import com.marcostoral.keepmoving.dto.Waypoint;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by marcostoral on 6/05/17.
 */

public class KeepMovinApp extends Application {

    public static AtomicInteger RouteID = new AtomicInteger();
    public static AtomicInteger WaypointID = new AtomicInteger();

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        Realm realm = Realm.getDefaultInstance();
        RouteID = getIdByTable(realm, Route.class);
        WaypointID = getIdByTable(realm, Waypoint.class);
        realm.close();
    }


    private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm, Class<T> anyClass) {
        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0) ? new AtomicInteger(results.max("id").intValue()) : new AtomicInteger();
    }
}
