package com.wf.gu.udpchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class CheckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        SharedPreferences sharedPref = this.getSharedPreferences("com.wf.gu.udpchat", Context.MODE_PRIVATE);

        boolean condition = sharedPref.getBoolean("whatfriend_if_reg", false);
        if (condition) {
            intent = new Intent(this, Main2Activity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
