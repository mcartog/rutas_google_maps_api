package com.marcostoral.keepmoving.fragments;


import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.dto.Route;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class HeaderForMapsFragment extends Fragment {

    //UI
    private ImageButton btnWaypoint;
    private Chronometer chronometer;
    private TextView tvCurrentDistance;
    private ImageView ivCurrentType;
    private Button btnStart;
    private Button btnStop;

    //dto instance
    private Route myRoute;


    public HeaderForMapsFragment() {
        // Required empty public constructor
    }

    ///////////////////////////////////////////////////////
    //////////////////   CALLBACK   ///////////////////////
    ///////////////////////////////////////////////////////

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_header_for_maps, container, false);

        init(view);


        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    ///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////

    public void init(View view){
        btnWaypoint = (ImageButton) view.findViewById(R.id.ibWaypoint);
        chronometer = (Chronometer) view.findViewById(R.id.chrono);
        tvCurrentDistance = (TextView) view.findViewById(R.id.tvCurrentDistance);
        ivCurrentType = (ImageView) view.findViewById(R.id.ivCurrentType);
        btnStart = (Button) view.findViewById(R.id.btnStart);
        btnStop = (Button) view.findViewById(R.id.btnStop);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStart.setVisibility(View.INVISIBLE);
                myRoute = new Route();


                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                Toast.makeText(getContext(),"lanzo servicio "+myRoute.toString(),Toast.LENGTH_SHORT).show();

                //Lanzo servicio


                btnStop.setVisibility(View.VISIBLE);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStop.setVisibility(View.INVISIBLE);
                chronometer.stop();
                myRoute.setTime(chronometer.getText().toString());

                Toast.makeText(getContext(),"detengo servicio "+myRoute.toString(),Toast.LENGTH_LONG).show();

                btnStart.setVisibility(View.VISIBLE);
            }
        });

        btnWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "captuar waypoint",Toast.LENGTH_LONG).show();
            }
        });
    }





}
