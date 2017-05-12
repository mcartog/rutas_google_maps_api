package com.marcostoral.keepmoving.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;


import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
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
import com.marcostoral.keepmoving.dto.Route;
import com.marcostoral.keepmoving.dto.Waypoint;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;

import static android.support.v4.content.ContextCompat.getExternalFilesDirs;

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
    private Dialog saveRouteConfirmationDialog;


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 2;

    private long milliseconds;
    private int type;
    private int gpsSignal;

    private Realm realm;


    String mCurrentPhotoPath;

    public MapsEnvironmentFragment() {
        // Required empty public constructor
    }

    ///////////////////////////////////////////////////////
    //////////////////   CALLBACK   ///////////////////////
    ///////////////////////////////////////////////////////

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps_environment, container, false);
        this.init(view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isGPSEnabled();
    }

    /**
     * Salvo el estado de los botones start/stop y los milisegundos transcurridos con el cronómetro en marcha.
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        long milliseconds = SystemClock.elapsedRealtime() - chronometer.getBase();
        int startState = btnStart.getVisibility();
        int stopState = btnStop.getVisibility();
       //Meter un parcel de Route??

        outState.putParcelable("myRoute",myRoute);
        outState.putLong("milliseconds",milliseconds);
        outState.putInt("start",startState);
        outState.putInt("stop",stopState);

    }

    /**
     * Restaura los valores almacenados. Estado botones start/stop, milisegundos.
     * @param savedInstanceState
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState!=null){

            //Caso: actividad en curso.
            if(savedInstanceState.getInt("start")==4){
                milliseconds = savedInstanceState.getLong("milliseconds");
                startChronometer();
                myRoute = savedInstanceState.getParcelable("myRoute");
                btnStart.setVisibility(View.INVISIBLE);
                btnWaypoint.setEnabled(true);

            }else {
                btnStart.setVisibility(View.VISIBLE);
                btnWaypoint.setEnabled(false);
            }

            ///Reestructurar código. Esto se puede mejorar.
            //Caso del botón
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
    ///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////

    public void init(View view){

        Bundle bundle = getActivity().getIntent().getExtras();
        type = Integer.parseInt(bundle.getString("type"));

        // Obtain a Realm instance
        realm = Realm.getDefaultInstance();

        waypointDialog = generateDialogCaptureWaypoint();
        saveRouteConfirmationDialog = saveRouteConfirmation();

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

                try {
                gpsSignal = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
                //Comprueba por segunda vez que el GPS esté activado.
                if (gpsSignal == 0) {
                    //Si no lo está, nos muestra mensaje que conduce a configuración.
                    showInfoAlert();
                } else {
                    //Si está habilitado.
                    btnStart.setVisibility(View.INVISIBLE);
                    btnWaypoint.setEnabled(true);
                    myRoute = new Route();
                  //  myRoute = realm.createObject(Route.class);

                    startChronometer();
                    Toast.makeText(getContext(),myRoute.toString(),Toast.LENGTH_LONG).show();
                    //Lanzo servicio

                    btnStop.setVisibility(View.VISIBLE);
                }
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStop.setVisibility(View.INVISIBLE);
                btnWaypoint.setEnabled(false);

                chronometer.stop();

//                setRouteParameters();

                saveRouteConfirmation().show();



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

    /**
     * Al dar a Stop asignamos los valores a ROute.
     */
    private void setRouteParameters(){

        //myRoute.setDate(new Date());
        myRoute.setType(type);
        myRoute.setDistance(tvCurrentDistance.getText().toString());
        myRoute.setTime(chronometer.getText().toString());

    }
    private void startChronometer(){
        chronometer.setBase(SystemClock.elapsedRealtime() - milliseconds);
        chronometer.start();
    }

    /**
     * Recibe el tipo de ruta y le asigna una imagen en función de la elección.
     * @param type
     */
    public void routeTypeIconReceptor(String type){

            switch (Integer.parseInt(type)){
                case 0:
                    ivCurrentType.setImageResource(R.drawable.cycling);
                    break;
                case 1:
                    ivCurrentType.setImageResource(R.drawable.running);
                    break;
                case 2:
                    ivCurrentType.setImageResource(R.drawable.hiking);
                    break;
            }

        }




    /**
     * Dispara la petición de foto.
     * Se lanza el intent y crea el fichero, si tiene éxito su creación guarda la imagen en él.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    /**
     * Crea un fichero imagen con formato JPG_20170511_172510_.jpg
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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
     * Comprueba que le GPS esté activado. Si no lo está muestra un AlertDialog que nos conduce a
     * la configuración del GPS.
     */
    private void isGPSEnabled() {
        try {
            gpsSignal = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (gpsSignal == 0) {
                showInfoAlert();
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
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
                Toast.makeText(getContext(),"salvar",Toast.LENGTH_SHORT).show();
                /////////Proceso de salvado (meter en un método)

                setRouteParameters();
                realm.beginTransaction();
                //User user = realm.createObject(User.class);       Lo llamo al crear la ruta en startbutton
                //realm.createObject(myRoute);
                //realm.executeTransaction(s);

                realm.copyToRealm(myRoute);  //Crearla normal y luego grabarla así
                realm.commitTransaction();

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
    /**
     * AlertDialog
     */
    private void showInfoAlert() {
        new AlertDialog.Builder(getContext())
                .setTitle("GPS Signal")
                .setMessage("You don't have GPS signal enabled. Would you like to enable the GPS signal now?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }





}
