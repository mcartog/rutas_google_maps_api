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


}
