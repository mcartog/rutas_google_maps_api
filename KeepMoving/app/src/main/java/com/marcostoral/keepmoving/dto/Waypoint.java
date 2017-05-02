package com.marcostoral.keepmoving.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by marcostoral on 3/04/17.
 */

public class Waypoint implements Parcelable {

    private int id;
    private long ltd;
    private long lng;
    private String path;
    //foto o video o audio?

    public Waypoint() {
    }

    public Waypoint(long ltd, long lng, String path) {
        this.ltd = ltd;
        this.lng = lng;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getLtd() {
        return ltd;
    }

    public void setLtd(long ltd) {
        this.ltd = ltd;
    }

    public long getLng() {
        return lng;
    }

    public void setLng(long lng) {
        this.lng = lng;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
