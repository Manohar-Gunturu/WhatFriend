package com.wf.gu.udpchat;

/**
 * Created by SA on 12/08/2016.
 */
public class Ozo {

    public static int user_id = 0;
    public static boolean c = false;
    public static String user_name = "";
    public static String user_image = "";
    public static String user_place = "";
    public static String user_status = "";

    public static void Ozoset(String name, String place, String image, String status, int id, boolean is_c) {
        user_id = id;
        user_name = name;
        user_place = place;
        user_image = image;
        user_status = status;
        c = is_c;
    }


}
