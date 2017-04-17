package com.marcostoral.keepmoving.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by marcostoral on 6/04/17.
 */

public class Utils {


    /**
     * Devuelve la fecha del dispositivo en formato yyyy/MM/dd String.
     * @return
     */
    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return dateFormat.format(date); }


    /**
     * Calcula la distancia entre dos puntos A y B
     * @param xA
     * @param yA
     * @param xB
     * @param yB
     * @return
     */
    public static double distanceCalculator (double xA, double yA, double xB, double yB){

        return Math.sqrt((Math.pow((xB-xA),2)) + (Math.pow((yB-yA),2)));

    }

    /**
     * Suma la distancia acumulada con la nueva
     * @param prevDistance
     * @param xA
     * @param yA
     * @param xB
     * @param yB
     * @return
     */
    public static double currentDistance (double prevDistance, double xA, double yA, double xB, double yB){

        return prevDistance + distanceCalculator(xA,yA,xB,yB);

    }


}
