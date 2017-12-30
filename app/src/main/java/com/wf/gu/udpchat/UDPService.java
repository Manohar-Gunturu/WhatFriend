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

import umt.SocketWrapper;

/**
 * Created by SA on 25/12/2017.
 */
public class UDPService extends Service {

    DBHelper dbHelper = null;
    BroadcastReceiver mReceiver;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onCreate() {
        dbHelper = new DBHelper(this.getApplication());
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new MyReceiver();
        registerReceiver(mReceiver, intentFilter);
        new Thread(()->{
            SocketWrapper.start();
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(()->{
           SocketWrapper.send("type=update_address;id="+Static.user_id+";");
        });

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


}
