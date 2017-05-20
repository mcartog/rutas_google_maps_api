package com.marcostoral.keepmoving.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.dto.Route;
import com.marcostoral.keepmoving.dto.Waypoint;
import com.marcostoral.keepmoving.fragments.MapsEnvironmentFragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    //Map
    private GoogleMap mMap;
    private CameraPosition camera;

    //Location
    private LocationManager locationManager;
    private Location currentLocation;
    private int gpsSignal;

    private int type;
    private long milliseconds;


    //Fragments
    private MapsEnvironmentFragment mapsEnvironmentFragment;
    private SupportMapFragment mapFragment;

    //UI & Views
    private View mapsEnvironment;
    private ImageButton btnWaypoint;
    private Chronometer chronometer;
    private TextView tvCurrentDistance;
    private ImageView ivCurrentType;
    private Button btnStart;
    private Button btnStop;

    //Realm
    private Realm realm;

    //dto instance
    private Route myRoute;

    //Photo & video
    static final int REQUEST_VIDEO_CAPTURE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    private String mCurrentPhotoPath;
    private String mCurrentVideoPath;

    //Control
    public boolean isTracking;

    //DIALOGS
    private Dialog waypointDialog;
    private Dialog saveConfirmationDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        init();

//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        long milliseconds = SystemClock.elapsedRealtime() - chronometer.getBase();
        int startState = btnStart.getVisibility();
        int stopState = btnStop.getVisibility();
        boolean stopEnabled = btnStop.isEnabled();


        outState.putParcelable("myRoute", myRoute);
        outState.putLong("milliseconds", milliseconds);
        outState.putInt("start", startState);
        outState.putInt("stop", stopState);
        outState.putBoolean("stopEnable", stopEnabled);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {

            if (savedInstanceState.getInt("start") == View.INVISIBLE && savedInstanceState.getBoolean("stopEnable") == true) {

                //Caso: actividad en curso. Start == Invisible  &&  Stop == Enable(true)

                milliseconds = savedInstanceState.getLong("milliseconds");
                startChronometer();
                myRoute = savedInstanceState.getParcelable("myRoute");
                btnStart.setVisibility(View.INVISIBLE);
                btnStop.setVisibility(View.VISIBLE);
                btnStop.setEnabled(true);
                btnWaypoint.setEnabled(true);
                isTracking = true;



            } else if (savedInstanceState.getInt("start") == View.INVISIBLE && savedInstanceState.getBoolean("stopEnable") == false) {

                //Caso: actividad en finalizada. Start == Invisible  &&  Stop == Enable(false)

                btnStart.setVisibility(View.INVISIBLE);
                btnStop.setVisibility(View.VISIBLE);
                btnStop.setEnabled(false);
                btnWaypoint.setEnabled(false);
                isTracking = false;

            } else {
                //Caso: actividad en pendiente de empezar Start == Visible

                btnWaypoint.setEnabled(false);

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Cierra la base de datos.
        realm.close();
    }

    /**
     * Anula el reorno a la pantalla anterior si hay una ruta en proceso de grabación.
     */
    @Override
    public void onBackPressed() {

        if (isTracking == true) {
            //Si el trackeo está en curso, lanza un aviso.
            Toast.makeText(this, R.string.pleaseStopTrackng, Toast.LENGTH_LONG).show();

        } else {
            //Permite el retorno a pantalla anterior.
            super.onBackPressed();
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
//        camera = new CameraPosition(currentLocation);

        //Defino unos limites entre los que se manejará el zoom de la aplicación.
//        mMap.setMaxZoomPreference(20);
        mMap.setMinZoomPreference(10);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //Las siguientes opciones mejoran el rendimiento del posicionamiento.
        //Activar la localización permite conocer mi posición actual.
        mMap.setMyLocationEnabled(true);
        //Desactiva el boón de localización de la interfaz de usuario de GooogleMaps.
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        //Cuando implemente preferencias de usuario: tipo de mapa, color de polilinea, de marcadores...
        // mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        //Provedor de la señal, ms de refresco (se recomienda mínimo 60 seg), distancia de refresco, listener de cambio de ubicacion.
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 1000, 0, this);
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 1000, 0, this);

        Toast.makeText(this, "recibiendo ", Toast.LENGTH_LONG).show();

    }

    ///////////////////////////////////////////////////////
    /////////////////////  UI   ///////////////////////////
    ///////////////////////////////////////////////////////


    public void init() {

        //Al iniciar la actividad el trackeo está desactivado hasta pulsar el botón Start.
        isTracking = false;
        // Obtain a Realm instance
        realm = Realm.getDefaultInstance();

        // Instancio los dialogos
        waypointDialog = generateDialogCaptureWaypoint();
        saveConfirmationDialog = saveRouteConfirmation();

        //Recibo el tipo de actividad.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            //Recojo el tipo de ruta y lo convierto a int.
            String routeType = extras.getString("type");
            type = Integer.parseInt(routeType);

        }

        //Capturo los fragments de entorno y mapa
        mapsEnvironmentFragment = (MapsEnvironmentFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_maps_environment);
        mapsEnvironment = mapsEnvironmentFragment.getView();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // Instancio la interfaz gráfica vinculada al fragment entorno.
        btnWaypoint = (ImageButton) mapsEnvironment.findViewById(R.id.ibWaypoint);
        chronometer = (Chronometer) mapsEnvironment.findViewById(R.id.chrono);
        tvCurrentDistance = (TextView) mapsEnvironment.findViewById(R.id.tvCurrentDistance);
        ivCurrentType = (ImageView) mapsEnvironment.findViewById(R.id.ivCurrentType);
        btnStart = (Button) mapsEnvironment.findViewById(R.id.btnStart);
        btnStop = (Button) mapsEnvironment.findViewById(R.id.btnStop);

        //Deshabilito el botón captura waypoint.
        btnWaypoint.setEnabled(false);

        //Establezco el tipo de ruta.
        routeTypeIconReceptor(type);


        //START
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Comprobar disponibilidad de GPS
                if(isGPSEnabled()==false){

                    //Mostrar diálogo de aviso.
                    showInfoAlert();

                } else {
                    // Si está habilitado, se inicia la actividad.
                    isTracking = true;
                    // Se oculta botón start. Se muestra el botón stop. Se activa botón Waypoints.
                    btnStart.setVisibility(View.INVISIBLE);
                    btnStop.setVisibility(View.VISIBLE);
                    btnWaypoint.setEnabled(true);

                    //Crea objeto Route
                    myRoute = new Route();

                    //Inicia cronómetro
                    startChronometer();

//               currentLocation = startTracking();


                    Toast.makeText(MapsActivity.this, myRoute.toString(), Toast.LENGTH_LONG).show();

                }
            }
        });


        // STOP
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Trackeo parado
                isTracking = false;

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

                //Recibe posición.
                LatLng waypointCoords = getWaypointCoords(currentLocation);
                //Añade un marcador en el mapa
                mMap.addMarker(new MarkerOptions().position(waypointCoords));

                //Crea Waypoint (pasando Lat y Lng de parámetros).
                Waypoint waypoint = new Waypoint(waypointCoords.latitude, waypointCoords.longitude);

                //Lanza dialog foto vs video.
//                waypointDialog.show();



                //Añade el Waypoint a la lista de waypoints del objeto ruta.
                myRoute.addWaypoint(waypoint);

            }
        });


    }

    /**
     * Recibe el tipo de ruta y le asigna una imagen en función de la elección.
     * @param type
     */
    public void routeTypeIconReceptor(int type) {

        switch (type) {
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
     * Inicia el cronómetro.
     */
    public void startChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime() - milliseconds);
        chronometer.start();
    }

    /**
     * Al dar a Stop asignamos los valores a ROute.
     */
    public void setRouteParameters(){

        myRoute.setType(type);
        myRoute.setDistance(tvCurrentDistance.getText().toString());
        myRoute.setTime(chronometer.getText().toString());

    }

    ///////////////////////////////////////////////////////
    /////////////////////  LOCATION  //////////////////////
    ///////////////////////////////////////////////////////
    /**
     * Comprueba que el GPS esté activado. Si no lo está muestra un AlertDialog que nos conduce a
     * la configuración del GPS.
     */
    public boolean isGPSEnabled() {
         try {
                int gpsSignal = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);

                if (gpsSignal == 0) {
                    return false;
                } else {
                    return true;
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

    }

    /**
     * última posición conocida.
     * @return Location, última localización conocida.
     */
    public Location startTracking() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        //Preferentemente la localización corre a cargo del GPS.
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        return location;

    }

    /**
     * Pasada una localización devuelve su posición en latitud y longitud.
     * @param currentLocation
     * @return LatLng Valores latitud y longitud de un punto.
     */
    public LatLng getWaypointCoords(Location currentLocation) {

        if (currentLocation != null) {
            LatLng singlePoint = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            return singlePoint;
        } else {
            return null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        currentLocation = location;
        if (currentLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));

        }


        Toast.makeText(this, "location CHANGE"+location.getProvider(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    ///////////////////////////////////////////////////////
    /////////////////  PHOTO & VIDEO  /////////////////////
    ///////////////////////////////////////////////////////

    /**
     * Método por el que se dispara la petición de video
     */
    public void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(this.getPackageManager())!= null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    /**
     * Dispara la petición de foto. Indicando el directorio público de imágetes, Crea un fichero imagen y un Uri apra pasar al intetn.
     */
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureName = getPictureName();
        File imageFile = new File(pictureDirectory,pictureName);
        Uri pictureUri = Uri.fromFile(imageFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:

                if (resultCode == this.RESULT_OK) {

//                    String result = data.toUri(0);
                    Bitmap cameraImage = (Bitmap) data.getExtras().get("data") ;
                    mCurrentPhotoPath = cameraImage.toString();
                    Toast.makeText(this, mCurrentPhotoPath , Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "There was an error with the picture, try again.", Toast.LENGTH_LONG).show();
                }
                break;

            case REQUEST_VIDEO_CAPTURE:

                if (resultCode == this.RESULT_OK) {
                    String result = data.toUri(0);
                    Toast.makeText(this, "Result: "+result, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "There was an error with the video, try again.", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
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


    ///////////////////////////////////////////////////////
    ////////////////////  REALM   /////////////////////////
    ///////////////////////////////////////////////////////


    /**
     * Persiste objeto Route.
     * @param r
     */
    public void saveRoute(Route r){
        realm.beginTransaction();
        realm.copyToRealm(myRoute);
        realm.commitTransaction();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
     * AlertDialog: Informa de que el GPS no está activado.
     */
    private void showInfoAlert() {
        new AlertDialog.Builder(this)
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
