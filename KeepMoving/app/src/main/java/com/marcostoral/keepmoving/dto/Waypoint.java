package com.marcostoral.keepmoving.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.marcostoral.keepmoving.KeepMovinApp;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by marcostoral on 3/04/17.
 */

public class Waypoint extends RealmObject implements Parcelable {

    @PrimaryKey
    private long id;
    private double ltd;
    private double lng;
    private String path;
    //foto o video o audio?

    public Waypoint() {
        this.id = KeepMovinApp.RouteID.incrementAndGet();
    }

    public Waypoint(double ltd, double lng) {
        this.id = KeepMovinApp.RouteID.incrementAndGet();
        this.ltd = ltd;
        this.lng = lng;
    }

    protected Waypoint(Parcel in) {
        id = in.readLong();
        ltd = in.readLong();
        lng = in.readLong();
        path = in.readString();
    }

    public static final Creator<Waypoint> CREATOR = new Creator<Waypoint>() {
        @Override
        public Waypoint createFromParcel(Parcel in) {
            return new Waypoint(in);
        }

        @Override
        public Waypoint[] newArray(int size) {
            return new Waypoint[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(id);
        dest.writeDouble(ltd);
        dest.writeDouble(lng);
        dest.writeString(path);

    }

    public void readFromParcel (Parcel in){

        id = in.readLong();
        ltd =  in.readDouble();
        lng = in.readDouble();
        path = in.readString();

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLtd() {
        return ltd;
    }

    public void setLtd(double ltd) {
        this.ltd = ltd;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Waypoint{" +
                "id=" + id +
                ", ltd=" + ltd +
                ", lng=" + lng +
                ", path='" + path + '\'' +
                '}';
    }
}
