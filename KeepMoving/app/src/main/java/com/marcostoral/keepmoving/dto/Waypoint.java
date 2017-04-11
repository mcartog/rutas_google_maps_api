package com.marcostoral.keepmoving.dto;

/**
 * Created by marcostoral on 3/04/17.
 */

public class Waypoint {
    private long ltd;
    private long lng;
    //foto o video o audio?

    public Waypoint() {
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
}
