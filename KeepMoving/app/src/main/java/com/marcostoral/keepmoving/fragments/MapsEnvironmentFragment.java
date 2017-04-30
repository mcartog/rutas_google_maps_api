package com.marcostoral.keepmoving.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
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

import java.util.ArrayList;

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
    private Dialog saveConfirmationDialog;


    static final int REQUEST_VIDEO_CAPTURE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps_environment, container, false);
        init(view);

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
                btnWaypoint.setEnabled(true);

            }else {
                btnStart.setVisibility(View.VISIBLE);
                btnWaypoint.setEnabled(false);
            }

            if(savedInstanceState.getInt("stop")==4)
            {
                btnStop.setVisibility(View.INVISIBLE);
                btnWaypoint.setEnabled(false);
            }else {
                btnStop.setVisibility(View.VISIBLE);
                btnWaypoint.setEnabled(true);
            }

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
                    // Lanzo la actividad para caputrar foto.
                    dispatchTakePictureIntent();

                } else {

                    //Lanzo la actividad para caputrar video.
                    dispatchTakeVideoIntent();
                }
            }
        });

        return builder.create();
    }

    /**
     * Crea el diálogo de selección de tipo de media caputrado.
     * @return
     */
    private Dialog saveRouteConfirmation(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.title_dialog_persist_route);
        builder.setMessage(R.string.msn_dialog_persist_route);

        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(),"lanzo el procedimiento de salvar",Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(),"cancelo",Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        return builder.create();
    }


    ///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////

    public void init(View view){

        waypointDialog = generateDialogCaptureWaypoint();
        saveConfirmationDialog = saveRouteConfirmation();

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
                myRoute.setTime(chronometer.getText().toString());
                saveConfirmationDialog.show();

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

    public void routeTypeIconReceptor(String type){

            switch (Integer.parseInt(type)){
                case 0:
                   // Toast.makeText(getContext(),type,Toast.LENGTH_SHORT).show();
                    ivCurrentType.setImageResource(R.drawable.cycling);
                    break;
                case 1:
                   // Toast.makeText(getContext(),type,Toast.LENGTH_SHORT).show();
                    ivCurrentType.setImageResource(R.drawable.running);
                    break;
                case 2:
                   // Toast.makeText(getContext(),type,Toast.LENGTH_SHORT).show();
                    ivCurrentType.setImageResource(R.drawable.hiking);
                    break;
            }

        }


    /**
     * Método por el que se dispara la petición de video
     */
    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager())!= null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    /**
     * Dispara la petición de foto.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }





}
