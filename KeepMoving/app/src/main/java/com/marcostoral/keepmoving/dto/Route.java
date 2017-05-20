package com.marcostoral.keepmoving.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.Polyline;
import com.marcostoral.keepmoving.KeepMovinApp;
import com.marcostoral.keepmoving.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by marcostoral on 3/04/17.
 */

public class Route extends RealmObject implements Parcelable {

    @PrimaryKey
    private long id;
    private Date date;
    private int type;
    private String distance;
    private String time;
    private RealmList<MyLatLng> pointList;
    private RealmList<Waypoint> waypointList;

    public Route() {
        this.id = KeepMovinApp.RouteID.incrementAndGet();
        this.date = new Date();
        this.waypointList = new RealmList<Waypoint>();
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

    public static final Parcelable.Creator<MyLatLng> CREATOR_POINT = new Parcelable.Creator<MyLatLng>() {
        public MyLatLng createFromParcel(Parcel in) {
            return new MyLatLng();
        }

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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public RealmList<MyLatLng> getPointList() {
        return pointList;
    }

    public void setPointList(RealmList<MyLatLng> pointList) {
        this.pointList = pointList;
    }

    public RealmList<Waypoint> getWaypointList() {
        return waypointList;
    }

    public void setWaypointList(RealmList<Waypoint> waypointList) {
        this.waypointList = waypointList;
    }

    public static Creator<Waypoint> getCREATOR() {
        return CREATOR;
    }

    public void addWaypoint (Waypoint waypoint){
        waypointList.add(waypoint);
    }


    public void addMyLatLng (MyLatLng point){
        pointList.add(point);
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
        dest.writeString(distance);
        dest.writeString(time);
        dest.writeTypedList(waypointList);
        dest.writeTypedList(pointList);

    }

    public void readFromParcel (Parcel in){

        id = in.readInt();
        date =  new Date(in.readLong());
        type = in.readInt();
        distance = in.readString();
        time = in.readString();
        in.readTypedList(waypointList, CREATOR);
        in.readTypedList(pointList, CREATOR_POINT);
    }

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", date=" + date +
                ", type=" + type +
                ", distance='" + distance + '\'' +
                ", time='" + time + '\'' +
                ", waypointList=" + waypointList +
                '}';
    }
}
