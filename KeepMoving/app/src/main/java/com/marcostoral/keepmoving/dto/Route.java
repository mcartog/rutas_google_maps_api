package com.marcostoral.keepmoving.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.marcostoral.keepmoving.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by marcostoral on 3/04/17.
 */

public class Route implements Parcelable{

    private int id;
    private String distance;
    private String time;
    //Cambiar a Date
    private String date;
    private String comment;
    private int type;
    private ArrayList<Waypoint> waypointList;
    //private polilinea? arrayXY??? KML???

    public static final Parcelable.Creator<Waypoint> CREATOR
            = new Parcelable.Creator<Waypoint>() {
        public Waypoint createFromParcel(Parcel in) {
            return new Waypoint();
        }

        public Waypoint[] newArray(int size) {
            return new Waypoint[size];
        }
    };


    public Route() {
       // this.date = new Date();
        this.waypointList = new ArrayList<Waypoint>();
    }

    public Route (Parcel p){
        // this.date = new Date();
        this.waypointList = new ArrayList<Waypoint>();
        readFromParcel(p);

    }

    //Este consturctor es aapra la prueba de diseño
    public Route(String distance, String time,int type, String date) {

        this.type = type;
        this.time = time;
        this.distance = distance;
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<Waypoint> getWaypointList() {
        return waypointList;
    }

    public void setWaypointList(ArrayList<Waypoint> waypointList) {
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

        dest.writeString(distance);
        dest.writeString(time);
    //    dest.writeTypedList(waypointList);

    }

    public void readFromParcel (Parcel in){

        distance = in.readString();
        time = in.readString();


        in.readTypedList(waypointList, CREATOR);
    }
}
