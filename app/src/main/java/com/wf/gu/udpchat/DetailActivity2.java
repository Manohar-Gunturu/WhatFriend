package com.wf.gu.udpchat;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by SA on 11/08/2016.
 */
public class DetailActivity2 extends AppCompatActivity {

    public static final String USER_NAME = "position";
    public static final String STATUS = "position";
    public static final String PLACE = "position";
    public static final String USER_ID = "0";
    public static final String IMAGE = "0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Collapsing Toolbar layout to the screen
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        // Set title of Detail page
        // collapsingToolbar.setTitle(getString(R.string.item_title));
        Bundle extras = getIntent().getExtras();

        collapsingToolbar.setTitle(extras.getString("USER_NAME"));

        TextView placeDetail = (TextView) findViewById(R.id.place_detail);
        placeDetail.setText(extras.getString("STATUS"));
        TextView placeLocation = (TextView) findViewById(R.id.place_location);
        placeLocation.setText(extras.getString("PLACE"));

        ImageView placePicutre = (ImageView) findViewById(R.id.image);
        Picasso.with(getApplicationContext()).load("http://" + Static.IP + ":8080/" + extras.getString("IMAGE").replace("..", "")).into(placePicutre);

    }


}
