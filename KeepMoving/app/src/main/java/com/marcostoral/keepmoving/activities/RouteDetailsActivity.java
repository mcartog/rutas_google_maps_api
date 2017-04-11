package com.marcostoral.keepmoving.activities;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.dto.Route;
import com.marcostoral.keepmoving.fragments.RouteDetailsFragment;

public class RouteDetailsActivity extends AppCompatActivity {

    private String date;
    private String distance;
    private String time;
    private int type;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_details);

        if (getIntent().getExtras() != null) {

            distance = getIntent().getStringExtra("distance");
            time = getIntent().getStringExtra("time");
            date = getIntent().getStringExtra("date");
           // type = getIntent().getIntExtra("type");
        }

        Route route = new Route(distance,time,type,date);

        RouteDetailsFragment detailsFragment = (RouteDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentDetailsRoute);
        detailsFragment.renderRoute(route);

    }



}
