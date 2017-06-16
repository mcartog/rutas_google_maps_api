package com.marcostoral.keepmoving.models;

import com.marcostoral.keepmoving.app.KeepMovinApp;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by marcostoral on 3/04/17.
 */

public class Waypoint extends RealmObject {

    @PrimaryKey
    private long id;
    private double ltd;
    private double lng;
    private double alt;
    private double time;
    private String path;
    private String filePath;
    private boolean isVideo;


    public Waypoint() {
        this.id = KeepMovinApp.WaypointID.incrementAndGet();
        this.isVideo = false;
    }

    public Waypoint(double ltd, double lng) {
        this.id = KeepMovinApp.WaypointID.incrementAndGet();
        this.ltd = ltd;
        this.lng = lng;
        this.isVideo = false;
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

    public double getAlt() {
        return alt;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    //    protected Waypoint(Parcel in) {
//        id = in.readLong();
//        ltd = in.readLong();
//        lng = in.readLong();
//        path = in.readString();
//    }
//
//    public static final Creator<Waypoint> CREATOR = new Creator<Waypoint>() {
//        @Override
//        public Waypoint createFromParcel(Parcel in) {
//            return new Waypoint(in);
//        }
//
//        @Override
//        public Waypoint[] newArray(int size) {
//            return new Waypoint[size];
//        }
//    };
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//
//        dest.writeLong(id);
//        dest.writeDouble(ltd);
//        dest.writeDouble(lng);
//        dest.writeString(path);
//
//    }
//
//    public void readFromParcel (Parcel in){
//
//        id = in.readLong();
//        ltd =  in.readDouble();
//        lng = in.readDouble();
//        path = in.readString();
//
//    }
}
