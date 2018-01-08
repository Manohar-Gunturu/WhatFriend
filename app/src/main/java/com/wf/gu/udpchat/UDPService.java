package com.wf.gu.udpchat;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import umt.SocketWrapper;

/**
 * Created by SA on 25/12/2017.
 */
public class UDPService extends Service {

    DBHelper dbHelper = null;
    SQLiteDatabase db = null;


    @Override
    public void onCreate() {
        dbHelper = new DBHelper(this.getApplication());
        db = dbHelper.getReadableDatabase();

        new Thread(()->{
            SocketWrapper.start(db);
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                SocketWrapper.send("type=update_address;id="+Static.user_id+";");
            }
        },0,30000);

        new Thread(()->{
           SocketWrapper.send("type=update_address;id="+Static.user_id+";");
        }).start();


        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
