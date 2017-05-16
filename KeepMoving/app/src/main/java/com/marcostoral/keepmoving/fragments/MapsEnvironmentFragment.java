package com.marcostoral.keepmoving.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
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

import com.google.android.gms.maps.model.Polyline;
import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.dto.Route;
import com.marcostoral.keepmoving.dto.Waypoint;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;

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

    //Directorios de salvado
    private String mCurrentPhotoPath;
    private String mCurrentVideoPath;

    //Persisteir detalles de ruta
    private long milliseconds;
    private int type;
    private int gpsSignal;

    private Polyline routeTrack;

    //Realm
    private Realm realm;

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:

                if (resultCode == getActivity().RESULT_OK) {

//                    String result = data.toUri(0);
                    Bitmap cameraImage = (Bitmap) data.getExtras().get("data") ;
                    mCurrentPhotoPath = cameraImage.toString();
                    Toast.makeText(getContext(), mCurrentPhotoPath , Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "There was an error with the picture, try again.", Toast.LENGTH_LONG).show();
                }
                break;

            case REQUEST_VIDEO_CAPTURE:

                if (resultCode == getActivity().RESULT_OK) {
                    String result = data.toUri(0);
                    Toast.makeText(getContext(), "Result: "+result, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "There was an error with the video, try again.", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
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
        boolean stopEnabled = btnStop.isEnabled();


        outState.putParcelable("myRoute",myRoute);
        outState.putLong("milliseconds",milliseconds);
        outState.putInt("start",startState);
        outState.putInt("stop",stopState);
        outState.putBoolean("stopEnable",stopEnabled);

    }

    /**
     * Restaura los valores almacenados. Estado botones start/stop, milisegundos.
     * @param savedInstanceState
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState!=null){


            if(savedInstanceState.getInt("start")==View.INVISIBLE && savedInstanceState.getBoolean("stopEnable")==true){
                //Caso: actividad en curso. Start == Invisible  &&  Stop == Enable(true)
                milliseconds = savedInstanceState.getLong("milliseconds");
                startChronometer();
                myRoute = savedInstanceState.getParcelable("myRoute");
                btnStart.setVisibility(View.INVISIBLE);
                btnStop.setVisibility(View.VISIBLE);
                btnWaypoint.setEnabled(true);


            }else if (savedInstanceState.getInt("start")==View.INVISIBLE && savedInstanceState.getBoolean("stopEnable")==false){
                //Caso: actividad en finalizada. Start == Invisible  &&  Stop == Enable(false)
                btnStart.setVisibility(View.INVISIBLE);
                btnStop.setVisibility(View.VISIBLE);
                btnStop.setEnabled(false);
                btnWaypoint.setEnabled(false);

            } else {
                //Caso: actividad en pendiente de empezar Start == Visible

                btnWaypoint.setEnabled(false);

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
        saveConfirmationDialog = saveRouteConfirmation();

        btnWaypoint = (ImageButton) view.findViewById(R.id.ibWaypoint);
        chronometer = (Chronometer) view.findViewById(R.id.chrono);
        tvCurrentDistance = (TextView) view.findViewById(R.id.tvCurrentDistance);
        ivCurrentType = (ImageView) view.findViewById(R.id.ivCurrentType);
        btnStart = (Button) view.findViewById(R.id.btnStart);
        btnStop = (Button) view.findViewById(R.id.btnStop);

        btnWaypoint.setEnabled(false);

        // START
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                gpsSignal = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
                //Comprueba por segunda vez que el GPS esté activado.
                if (gpsSignal == 0) {
                    //Si no lo está, nos muestra diálogo que conduce a configuración.
                    showInfoAlert();
                } else {
                    //Si está habilitado, se inicia la actividad.
                    //Se oculta botón start. Se muestra el botón stop. Se activa botón Waypoints.
                    btnStart.setVisibility(View.INVISIBLE);
                    btnStop.setVisibility(View.VISIBLE);
                    btnWaypoint.setEnabled(true);

                    //Crea objeto Route
                    myRoute = new Route();

                    //Inicia cronómetro
                    startChronometer();

                    Toast.makeText(getContext(),myRoute.toString(),Toast.LENGTH_LONG).show();
                    //Lanzo servicio

                }
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        // STOP
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Anula botones
                btnWaypoint.setEnabled(false);
                btnStop.setEnabled(false);

                //Finaliza cronómetro
                chronometer.stop();

                //Asigna datos a objeto Route
                setRouteParameters();

                //Inicia dialogo de persistencia.
                saveConfirmationDialog.show();

            }
        });

        // WAYPOINT
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

        myRoute.setType(type);
        myRoute.setDistance(tvCurrentDistance.getText().toString());
        myRoute.setTime(chronometer.getText().toString());

    }



    ///////////////////////////////////////////////////////
    //////////////////  EVENTS   /////////////////////////
    ///////////////////////////////////////////////////////

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

    public int btnStartState(){
        Toast.makeText(getContext(), "Elemento "+btnStart.getVisibility(), Toast.LENGTH_SHORT).show();
        return btnStart.getVisibility();
    }

    private void startChronometer(){
        chronometer.setBase(SystemClock.elapsedRealtime() - milliseconds);
        chronometer.start();
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
     * Dispara la petición de foto. Indicando el directorio público de imágetes, Crea un fichero imagen y un Uri apra pasar al intetn.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureName = getPictureName();
        File imageFile = new File(pictureDirectory,pictureName);
        Uri pictureUri = Uri.fromFile(imageFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

    }


    private String getPictureName(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        return "KM"+timestamp+".jpg";
    }
    ///////////////////////////////////////////////////////////////////////////////////////
//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
//        return image;
//    }
    ///////////////////////////////////////////////////////////////////////////////////////


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

                saveRoute(myRoute);

                dialog.cancel();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        return builder.create();
    }

    /**
     * Persiste objeto Route.
     * @param r
     */
    public void saveRoute(Route r){
        realm.beginTransaction();
        realm.copyToRealm(myRoute);
        realm.commitTransaction();
    }

    /**
     * AlertDialog: Informa de que el GPS no está activado.
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
