package com.wf.gu.udpchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {


    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        System.out.println(intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.d("Screen", "OFFFFFFFFFFFFFFFFFFFFFF");
            Intent in = new Intent("custom-event-name");
            in.putExtra("message", "screen_off");
            LocalBroadcastManager.getInstance(context).sendBroadcast(in);
            //context.stopService(new Intent(context, MyService.class));
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            //context.startService(new Intent(context, MyService.class));
            Intent in = new Intent("custom-event-name");
            in.putExtra("message", "screen_on");
            LocalBroadcastManager.getInstance(context).sendBroadcast(in);
            //sendNs(context,"Device","Got Slept until know");
        } else {
            Log.d("Screen", "Elseeee");
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                Log.e("FROM RECIVER", "Connected ra");
                //context.startService(new Intent(context, MyService.class));
            } else {
                // context.stopService(new Intent(context, MyService.class));
            }
        }


    }


}
