package umt;

import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by SA on 21/12/2017.
 */
public class SocketWrapper {


    static SocketServer socketServer = null;
    public static ArrayList<Callback> callbacks = new ArrayList<Callback>();


    public static void send(String msg) {
        if (socketServer == null) {
            try {
                socketServer = new SocketServer("192.168.2.11");

            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
            }
        }
        try {
            socketServer.send(msg,false);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

        public static String send(String msg, boolean waitforReply) {
        if (socketServer == null) {
            try {
                socketServer = new SocketServer("192.168.2.11");
            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
            }
        }


        try {
           return socketServer.send(msg,waitforReply);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void start(SQLiteDatabase DB) {
        if (socketServer == null) {
            try {
                socketServer = new SocketServer("192.168.2.11");

            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
            }
        }
        socketServer.setDb(DB);
        socketServer.listen();
    }

    public static void attachListener(Callback c) {
        callbacks.add(c);
    }

    public static void removelistner(Callback callback) {
        callbacks.remove(callback);
    }
}
