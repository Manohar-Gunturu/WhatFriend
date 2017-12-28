package com.wf.gu.udpchat;

import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by SA on 04/08/2016.
 */
public class Static {


    public static int user_id = 0;
    public static int curr_view_users = 0;
    public static boolean is_visible = false;
    public static String user_name = "";
    public static String user_image = "";
    public static String user_place = "";
    public static String curent_view = "";
    public static TextView mTitle;
    public static String IP = "70.52.96.152";


    public static String postData(String s) {

        String text = "";
        BufferedReader reader = null;
        try {
            URL url = new URL(s);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(s);
            wr.flush();
            // Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line + "\n");
                Log.d("sssssssssssss", line);
            }
            text = sb.toString();
            Log.e("TAAAAAAAAAAAAAAAAAAAAA", text);

        } catch (Exception ex) {
            Log.e("TA", ex.toString());

        } finally {
            try {
                if (reader != null) reader.close();
            } catch (Exception ex) {
                Log.e("TA", ex.toString());

            }
        }
        return text;
    }


    public static void createBasef() {
        Log.e("FILE cccccccccccccc", "dddddddddddddddddddddddd");
        try {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "WhatFriend");
            if (!file.exists()) {
                Log.e("FILE cccccccccccccc", "noooooooooooooo");

                file.mkdir();
            }
        } catch (Exception e) {
            Log.e("FILE ERROR", e.toString());
        }


    }

}
