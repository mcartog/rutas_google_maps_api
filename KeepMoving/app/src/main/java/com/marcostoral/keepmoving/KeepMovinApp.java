package com.marcostoral.keepmoving;

import android.support.multidex.MultiDexApplication;

import com.marcostoral.keepmoving.models.Route;
import com.marcostoral.keepmoving.models.Waypoint;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by marcostoral on 6/05/17.
 */

public class KeepMovinApp extends MultiDexApplication {

    public static AtomicInteger RouteID = new AtomicInteger();
    public static AtomicInteger WaypointID = new AtomicInteger();


    @Override
    public void onCreate() {
        super.onCreate();

//      Configura la base de datos Realm
        setUpRealmConfig();

        Realm realm = Realm.getDefaultInstance();
        RouteID = getIdByTable(realm, Route.class);
        WaypointID = getIdByTable(realm, Waypoint.class);

        realm.close();
    }


    /**
     * Configuración de la base de datos.
     */
    private void setUpRealmConfig() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
    }

    private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm, Class<T> anyClass) {
        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0) ? new AtomicInteger(results.max("id").intValue()) : new AtomicInteger();
    }

}
