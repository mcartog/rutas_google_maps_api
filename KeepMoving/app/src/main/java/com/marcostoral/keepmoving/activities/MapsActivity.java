package com.marcostoral.keepmoving.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.dto.Route;
import com.marcostoral.keepmoving.dto.Waypoint;
import com.marcostoral.keepmoving.fragments.MapsEnvironmentFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    //Map
    private GoogleMap mMap;
    private PolylineOptions routeTrack;

    //Realm
    private Realm realm;
    //dto instance
    private Route myRoute;
    private Waypoint currentWaypoint;
    public Waypoint lastWaypoint;

    //Location
    public Location currentLocation;
    public Location lastLocation;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

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

    //Control
    public boolean isTracking;
    private int type;
    private long milliseconds;
    private float distance;
    private float totalDistance;
    private LatLng newPoint;
    private LatLng lastPoint;
    public List<LatLng> latLngs;

    //Photo & video
    static final int REQUEST_VIDEO_CAPTURE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    private String mCurrentPhotoPath;
    private String mCurrentVideoPath;

    //Dialogs
    private Dialog waypointDialog;
    private Dialog saveConfirmationDialog;

    //Permissions
    public static final int PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int PERMISSIONS_REQUEST_WRITE_DATA = 98;

    ///////////////////////////////////////////////////////
    /////////////////   CALLBACKS   ///////////////////////
    ///////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        init();

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
        outState.putFloat("distance", totalDistance);
        outState.putInt("start", startState);
        outState.putInt("stop", stopState);
        outState.putBoolean("stopEnable", stopEnabled);
//        outState.putParcelable("location",currentLocation);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {

            if (savedInstanceState.getInt("start") == View.INVISIBLE && savedInstanceState.getBoolean("stopEnable") == true) {

                //Caso: actividad en curso. Start == Invisible  &&  Stop == Enable(true)

                myRoute = savedInstanceState.getParcelable("myRoute");
                milliseconds = savedInstanceState.getLong("milliseconds");
                startChronometer();
                totalDistance = savedInstanceState.getFloat("distance");
                setKm(totalDistance);
                btnStart.setVisibility(View.INVISIBLE);
                btnStop.setVisibility(View.VISIBLE);
                btnStop.setEnabled(true);
                btnWaypoint.setEnabled(true);
                isTracking = true;

            } else if (savedInstanceState.getInt("start") == View.INVISIBLE && savedInstanceState.getBoolean("stopEnable") == false) {

                //Caso: actividad en finalizada. Start == Invisible  &&  Stop == Enable(false)

                btnStart.setVisibility(View.INVISIBLE);
                btnStop.setVisibility(View.VISIBLE);
                milliseconds = savedInstanceState.getLong("milliseconds");
                startChronometer();
                totalDistance = savedInstanceState.getFloat("distance");
                setKm(totalDistance);
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

    ///////////////////////////////////////////////////////
    /////////////////////  UI   ///////////////////////////
    ///////////////////////////////////////////////////////


    /**
     * Inicialización general de la activity.
     */
    public void init() {


        //Al iniciar la actividad el trackeo está desactivado hasta pulsar el botón Start.
        isTracking = false;
        totalDistance = 0;

        // Obtain a Realm instance
        realm = Realm.getDefaultInstance();

        // Instancia dialogos
        waypointDialog = generateDialogCaptureWaypoint();
        saveConfirmationDialog = saveRouteConfirmation();

        //Captura los fragments de entorno y mapa
        mapsEnvironmentFragment = (MapsEnvironmentFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_maps_environment);
        mapsEnvironment = mapsEnvironmentFragment.getView();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Así se consigue que guarde la posición de los marcadores en el mapa.
        mapFragment.setRetainInstance(true);


        // Instancia la interfaz gráfica vinculada al fragment entorno.
        btnWaypoint = (ImageButton) mapsEnvironment.findViewById(R.id.ibWaypoint);
        chronometer = (Chronometer) mapsEnvironment.findViewById(R.id.chrono);
        tvCurrentDistance = (TextView) mapsEnvironment.findViewById(R.id.tvCurrentDistance);
        ivCurrentType = (ImageView) mapsEnvironment.findViewById(R.id.ivCurrentType);
        btnStart = (Button) mapsEnvironment.findViewById(R.id.btnStart);
        btnStop = (Button) mapsEnvironment.findViewById(R.id.btnStop);

        //Deshabilita el botón captura waypoint.
        btnWaypoint.setEnabled(false);

        //Recibe el tipo de actividad.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            //Recoge el tipo de ruta y lo convierte a int.
            String routeType = extras.getString("type");
            type = Integer.parseInt(routeType);

        }

        //Establece el tipo de ruta.
        routeTypeIconReceptor(type);

        //Establezce el cuentakilómetros
        setKm(totalDistance);


        //START
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Comprobar disponibilidad de GPS
                if (isGPSEnabled() == false) {

                    //Mostrar diálogo de aviso.
                    showInfoAlert();

                } else {

                    // Si está habilitado, se inicia la actividad.
                    isTracking = true;
                    latLngs = new ArrayList<>();

                    //Inicia la localización.
                    initLocation();

                    distance = 0;

                    // Se oculta botón start. Se muestra el botón stop. Se activa botón Waypoints.
                    btnStart.setVisibility(View.INVISIBLE);
                    btnStop.setVisibility(View.VISIBLE);
                    btnWaypoint.setEnabled(true);

                    //Crea objeto Route
                    myRoute = new Route();

                    //Inicia cronómetro
                    startChronometer();

                    }

            }
        });


        // STOP
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Trackeo parado
                isTracking = false;

                //Desconecta ApiGoogleClient
//                stopLocation();

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

                if(myRoute.getWaypointList().size()>0){

                    //Lanza dialog foto vs video.
                    waypointDialog.show();

//                    //Añade un marcador en el mapa, añadiendo un LatLng
//                    LatLng point = new LatLng(myRoute.getWaypointList().last().getLtd(),myRoute.getWaypointList().last().getLng());
//                    mMap.addMarker(new MarkerOptions().position(point));
//                    myRoute.getWaypointList().last().setPath(mCurrentPhotoPath);

                } else {
                    Toast.makeText(MapsActivity.this, "wooo", Toast.LENGTH_LONG).show();
                }
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

    public void setKm (float totalDistance){
        float km = totalDistance / 1000;
        tvCurrentDistance.setText( km + " Km");

    }


    /**
     * Al dar a Stop asignamos los valores a ROute.
     */
    public void setRouteParameters(){

        myRoute.setType(type);
        myRoute.setDistance(totalDistance/1000);
        myRoute.setTime(chronometer.getText().toString());

    }

    ///////////////////////////////////////////////////////
    /////////////////////  LOCATION  //////////////////////
    ///////////////////////////////////////////////////////

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

        //Define limites entre los que se manejará el zoom de la aplicación.
        mMap.setMaxZoomPreference(20);
        mMap.setMinZoomPreference(10);

    }

    public void initLocation(){
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    public void stopLocation(){
//        mGoogleApiClient.disconnect();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}


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

        currentLocation = location;
        if (currentLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),15));
        }

        //Obtiene las coordenadas de un punto a partir de una localización.
        newPoint = getWaypointCoords(currentLocation);

        //Si la recuperación de coordenadas es exitosa..
        if(newPoint != null){

            //Crea point (pasando Lat y Lng de parámetros).
            currentWaypoint = new Waypoint(newPoint.latitude, newPoint.longitude);

            //...y la ruta se ha iniciado...
            if(myRoute != null){

                //Añadir punto a ruta.
                myRoute.addWaypoint(currentWaypoint);

                //...y si hay más de un punto...
                if(myRoute.getWaypointList().size() > 0) {

                    LatLng pointLatLng = new LatLng(currentWaypoint.getLtd(), currentWaypoint.getLng());
                    latLngs.add(pointLatLng);

                    routeTrack = new PolylineOptions()
                            .width(5)
                            .color(Color.RED)
                            .geodesic(true);

                    routeTrack.addAll(latLngs);

                    //Dibuja polilinea
                    mMap.addPolyline(routeTrack);

                }
            }

            if(lastLocation != null){

                distance = (long) calculateDistance(lastLocation, currentLocation);
                totalDistance = totalDistance + distance;
                setKm(totalDistance);

            }

            lastLocation = currentLocation;
        }
    }

    public float calculateDistance (Location lastLocation, Location currentLocation){

        return currentLocation.distanceTo(lastLocation);

    }


    ///////////////////////////////////////////////////////
    //////////////////// PERMISSIONS  /////////////////////
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
     * Comprobar permisos
     */
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    public void checkWritePermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        PERMISSIONS_REQUEST_WRITE_DATA);
                            }
                        })
                        .create()
                        .show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_WRITE_DATA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            //Permisos de localización
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            case PERMISSIONS_REQUEST_WRITE_DATA: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {

                        dispatchTakePictureIntent();

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();

                }
                return;

                // other 'case' lines to check for other
                // permissions this app might request
            }
        }
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
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

//
//
//
//
//
//            Nombre imagen y directorio
//            String pictureName = getPictureName();
//            File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//
//            String path = pictureDirectory +"/"+ pictureName;
//            Toast.makeText(MapsActivity.this, path, Toast.LENGTH_LONG).show();
//
//            //Fichero imagen
//            File imageFile = new File(pictureDirectory,pictureName);
//
//            Uri pictureUri = Uri.fromFile(imageFile);
//
//            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
//
//
//
//            ANDROID
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.example.android.fileprovider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }
    }


//    private File createImageFile() throws IOException {
//
//        // Crear fichero imagen
//        String pictureName = getPictureName();
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                pictureName,    /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
//        return image;
//    }

//    public Uri getVideoUri(Context inContext, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        MediaStore.Video.Media.
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
//        return Uri.parse(path);
//    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:

                if (resultCode == this.RESULT_OK  && data != null) {

                    Bitmap photo = (Bitmap) data.getExtras().get("data");

                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    Uri tempUri = getImageUri(getApplicationContext(), photo);

                    // CALL THIS METHOD TO GET THE ACTUAL PATH
                    File finalFile = new File(getRealPathFromURI(tempUri));

                    mCurrentPhotoPath = finalFile.toString();

                    //Añade un marcador en el mapa, añadiendo un LatLng
                    LatLng point = new LatLng(myRoute.getWaypointList().last().getLtd(),myRoute.getWaypointList().last().getLng());
                    mMap.addMarker(new MarkerOptions().position(point));
                    myRoute.getWaypointList().last().setPath(mCurrentPhotoPath);


                } else {
                    Toast.makeText(this, "There was an error with the picture, try again.", Toast.LENGTH_LONG).show();
                }

                break;

            case REQUEST_VIDEO_CAPTURE:

                if (resultCode == this.RESULT_OK && data != null) {
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
        return "KM"+timestamp;
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
        realm.copyToRealm(r);
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

                if(Integer.parseInt(items[item])==0) {


                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED) {

                            // Lanzo la actividad para caputrar foto.
                            dispatchTakePictureIntent();

                        } else {
                            //Request Location Permission
                            checkWritePermission();
                        }
                    }
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
