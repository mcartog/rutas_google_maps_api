package com.marcostoral.keepmoving.dto;

import com.marcostoral.keepmoving.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by marcostoral on 3/04/17.
 */

public class Route {

    private String title;
    private String distance;
    private String time;
    private String date;
    private int type;
    private ArrayList<Waypoint> waypointList;


    public Route() {
        this.date = Utils.getDateTime();
        this.waypointList = new ArrayList<Waypoint>();
    }

    //Este consturctor es aapra la prueba de diseño
    public Route(String distance, String time,int type) {
        this.type = type;
        this.time = time;
        this.distance = distance;
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




//para pruebas
    @Override
    public String toString() {
        return "Route{" +
                ", distance='" + distance + '\'' +
                ", time='" + time + '\'' +
                ", date='" + date + '\'' +
                ", type=" + type +
                ", waypointList=" + waypointList +
                '}';
    }
}
