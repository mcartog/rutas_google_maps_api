package com.marcostoral.keepmoving.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.marcostoral.keepmoving.KeepMovinApp;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by marcostoral on 3/04/17.
 */

public class Route extends RealmObject implements Parcelable{

    @PrimaryKey
    private long id;
    private String title;
    private Date date;
    private int type;
    private float distance;
    private String time;
    private RealmList<Waypoint> waypointList;
    private double maxLtd;
    private double minLtd;
    private double maxLng;
    private double minLng;
    private double maxAlt;
    private double minAlt;

    public Route() {
        this.id = KeepMovinApp.RouteID.incrementAndGet();
        this.title = "Ruta " + id;
        this.date = new Date();
        this.waypointList = new RealmList<Waypoint>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public RealmList<Waypoint> getWaypointList() {
        return waypointList;
    }

    public void setWaypointList(RealmList<Waypoint> waypointList) {
        this.waypointList = waypointList;
    }

    public double getMaxLtd() {
        return maxLtd;
    }

    public void setMaxLtd(double maxLtd) {
        this.maxLtd = maxLtd;
    }

    public double getMinLtd() {
        return minLtd;
    }

    public void setMinLtd(double minLtd) {
        this.minLtd = minLtd;
    }

    public double getMaxLng() {
        return maxLng;
    }

    public void setMaxLng(double maxLng) {
        this.maxLng = maxLng;
    }

    public double getMinLng() {
        return minLng;
    }

    public void setMinLng(double minLng) {
        this.minLng = minLng;
    }

    public double getMaxAlt() {
        return maxAlt;
    }

    public void setMaxAlt(double maxAlt) {
        this.maxAlt = maxAlt;
    }

    public double getMinAlt() {
        return minAlt;
    }

    public void setMinAlt(double minAlt) {
        this.minAlt = minAlt;
    }

    public void addWaypoint (Waypoint waypoint){
        waypointList.add(waypoint);
    }

    public Route(Parcel p){
        readFromParcel(p);
    }

    public static final Parcelable.Creator<Waypoint> CREATOR = new Parcelable.Creator<Waypoint>() {
        public Waypoint createFromParcel(Parcel in) {
            return new Waypoint();
        }

        public Waypoint[] newArray(int size) {
            return new Waypoint[size];
        }
    };

    public static Creator<Waypoint> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(id);
        dest.writeLong(date.getTime());
        dest.writeInt(type);
        dest.writeFloat(distance);
        dest.writeString(time);
        dest.writeTypedList(waypointList);

    }

    public void readFromParcel (Parcel in){

        id = in.readLong();
        date =  new Date(in.readLong());
        type = in.readInt();
        distance = in.readLong();
        time = in.readString();
        in.readTypedList(waypointList, CREATOR);

    }

}
