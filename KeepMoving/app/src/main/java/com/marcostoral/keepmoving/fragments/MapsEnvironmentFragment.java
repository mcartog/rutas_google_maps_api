package com.marcostoral.keepmoving.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.LauncherApps;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.activities.MapsActivity;
import com.marcostoral.keepmoving.dto.Route;
import com.marcostoral.keepmoving.dto.Waypoint;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsEnvironmentFragment extends Fragment {

    //UI
    private ImageButton btnWaypoint;
    private Chronometer chronometer;
    private TextView tvCurrentDistance;
    private ImageView ivCurrentType;
    private Button btnStart;
    private Button btnStop;

    //dto instance
    private Route myRoute;

    //DIALOGS
    private Dialog waypointDialog;

    MapsActivity activity = (MapsActivity) getActivity();
    String type;

    public MapsEnvironmentFragment() {
        // Required empty public constructor
    }

    ///////////////////////////////////////////////////////
    //////////////////   CALLBACK   ///////////////////////
    ///////////////////////////////////////////////////////


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle extras) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps_environment, container, false);
        init(view);


       // String type2 = activity.getType();

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

      //  long chronoState = chronometer.getBase();
        int startState = btnStart.getVisibility();
        int stopState = btnStop.getVisibility();


     //   outState.putLong("chrono",chronoState);
        outState.putInt("start",startState);
        outState.putInt("stop",stopState);

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState!=null){
            if(savedInstanceState.getInt("start")==4){
                btnStart.setVisibility(View.INVISIBLE);
            }else {
                btnStart.setVisibility(View.VISIBLE);
            }

            if(savedInstanceState.getInt("stop")==4)
            {
                btnStop.setVisibility(View.INVISIBLE);
            }else {
                btnStop.setVisibility(View.VISIBLE);
            }

        }
    }

  /*  @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null){
            if(savedInstanceState.getInt("start")==4){
                btnStart.setVisibility(View.INVISIBLE);
            }else {
                btnStart.setVisibility(View.VISIBLE);
            }

            if(savedInstanceState.getInt("stop")==4)
            {
                btnStop.setVisibility(View.INVISIBLE);
            }else {
                btnStop.setVisibility(View.VISIBLE);
            }

        }
    }*/
    ///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////

    public void init(View view){

        waypointDialog = generateDialogCaptureWaypoint();

        btnWaypoint = (ImageButton) view.findViewById(R.id.ibWaypoint);
        chronometer = (Chronometer) view.findViewById(R.id.chrono);
        tvCurrentDistance = (TextView) view.findViewById(R.id.tvCurrentDistance);
        ivCurrentType = (ImageView) view.findViewById(R.id.ivCurrentType);
        btnStart = (Button) view.findViewById(R.id.btnStart);
        btnStop = (Button) view.findViewById(R.id.btnStop);

        btnWaypoint.setEnabled(false);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStart.setVisibility(View.INVISIBLE);
                btnWaypoint.setEnabled(true);
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
                btnWaypoint.setEnabled(false);
                chronometer.stop();
//                myRoute.setTime(chronometer.getText().toString());

                Toast.makeText(getContext(),"detengo servicio "+myRoute.toString(),Toast.LENGTH_LONG).show();

                btnStart.setVisibility(View.VISIBLE);
            }
        });

        btnWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Waypoint waypoint = new Waypoint();
                waypointDialog.show();

             //   waypoint.setLng();
             //   waypoint.setLtd();
                myRoute.addWaypoint(waypoint);

                Toast.makeText(getContext(), "captuar waypoint",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void recibeIcono(String type){

            switch (Integer.parseInt(type)){
                case 0:
                    Toast.makeText(getContext(),type,Toast.LENGTH_SHORT).show();
                    //ivCurrentType.setImageResource(R.drawable.cycling);
                    break;
                case 1:
                    Toast.makeText(getContext(),type,Toast.LENGTH_SHORT).show();
                   // ivCurrentType.setImageResource(R.drawable.running);
                    break;
                case 2:
                    Toast.makeText(getContext(),type,Toast.LENGTH_SHORT).show();
                    //ivCurrentType.setImageResource(R.drawable.hiking);
                    break;
            }

        }

    ///////////////////////////////////////////////////////
    //////////////////  DIALOGS   /////////////////////////
    ///////////////////////////////////////////////////////
    /**
     * Crea el diálogo de selección de tipo de media caputrado.
     * @return
     */
    private Dialog generateDialogCaptureWaypoint(){

        final String[] items = getResources().getStringArray(R.array.route_media_values);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.title_dialog_media_type);
        builder.setItems(R.array.media_type, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if(Integer.parseInt(items[item])==0){
                    //start
                    Toast.makeText(getContext(),"Opción elegida: " + items[item],Toast.LENGTH_LONG).show();
                    /* Lanzo la actividad para caputrar foto.
                    startActivityForResult();
                     */
                } else {
                    /* Lanzo la actividad para caputrar video.
                    startActivityForResult();
                     */
                }
            }
        });

        return builder.create();
    }




}
