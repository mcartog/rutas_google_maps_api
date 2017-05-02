package com.marcostoral.keepmoving.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.marcostoral.keepmoving.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by marcostoral on 3/04/17.
 */

public class Route implements Parcelable{

    private int id;
    //Cambiar a Date
    private Date date;
    private int type;
    private String distance;
    private String time;
    private List<Waypoint> waypointList;
    //private polilinea? arrayXY??? KML???

    public Route() {
        this.id = 001;
        this.date = new Date();
        this.waypointList = new ArrayList<Waypoint>();
    }

    public Route (Parcel p){

        readFromParcel(p);

    }

    //Este consturctor es aapra la prueba de diseño
    public Route(String distance, String time,int type, Date date) {
        this.date = date;
        this.type = type;
        this.distance = distance;
        this.time = time;
    }

    public static final Parcelable.Creator<Waypoint> CREATOR = new Parcelable.Creator<Waypoint>() {
        public Waypoint createFromParcel(Parcel in) {
            return new Waypoint();
        }

        public Waypoint[] newArray(int size) {
            return new Waypoint[size];
        }
    };


    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public List<Waypoint> getWaypointList() {
        return waypointList;
    }

    public void setWaypointList(List<Waypoint> waypointList) {
        this.waypointList = waypointList;
    }

    public void addWaypoint (Waypoint waypoint){
        waypointList.add(waypoint);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeLong(date.getTime());
        dest.writeInt(type);
        dest.writeString(distance);
        dest.writeString(time);
        dest.writeTypedList(waypointList);

    }

    public void readFromParcel (Parcel in){

        id = in.readInt();
        date =  new Date(in.readLong());
        type = in.readInt();
        distance = in.readString();
        time = in.readString();
        in.readTypedList(waypointList, CREATOR);
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
