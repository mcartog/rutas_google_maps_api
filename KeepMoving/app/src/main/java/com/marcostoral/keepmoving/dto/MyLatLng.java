package com.marcostoral.keepmoving.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.marcostoral.keepmoving.KeepMovinApp;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by marcostoral on 20/05/17.
 */

public class MyLatLng extends RealmObject implements Parcelable {

    @PrimaryKey
    private long id;
    private double latitude;
    private double longitude;

    public MyLatLng() {
        this.id = KeepMovinApp.RouteID.incrementAndGet();
    }

    public MyLatLng(double latitude, double longitude) {
        this.id = KeepMovinApp.RouteID.incrementAndGet();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected MyLatLng(Parcel in) {
        id = in.readLong();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<MyLatLng> CREATOR = new Creator<MyLatLng>() {
        @Override
        public MyLatLng createFromParcel(Parcel in) {
            return new MyLatLng(in);
        }

        @Override
        public MyLatLng[] newArray(int size) {
            return new MyLatLng[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
