package com.marcostoral.keepmoving.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.adapters.PhotoAdapter;
import com.marcostoral.keepmoving.fragments.GridPhotoViewFragment;
import com.marcostoral.keepmoving.models.Route;
import com.marcostoral.keepmoving.models.Waypoint;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class GalleryActivity extends AppCompatActivity {

    //Realm
    private Realm realm;

    //Fragment
    private GridPhotoViewFragment gvPhotosFragment;
    private View gvPhotos;

    //GridView
    private GridView gvPhoto;
    private ArrayList<Waypoint> waypointArrayList;
    private PhotoAdapter adapter;

    private ImageView showPhoto;

//    private final int PERMISSION_READ_EXTERNAL_MEMORY = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        realm = Realm.getDefaultInstance();

        gvPhotosFragment = (GridPhotoViewFragment) getSupportFragmentManager().findFragmentById(R.id.gvPicturesFragment);
        gvPhotos = gvPhotosFragment.getView();
        showPhoto = (ImageView) findViewById(R.id.iv_show_photo);

//        checkForPermission();

        waypointArrayList = getWaypoint();

        gvPhoto = (GridView) gvPhotos.findViewById(R.id.gvPictures);
        adapter = new PhotoAdapter(this, waypointArrayList, R.layout.gv_photogrid_item);
        gvPhoto.setAdapter(adapter);

        gvPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (waypointArrayList.get(position).isVideo() == true) {

                    showVideo(position);

                } else {

                    showImage(position);

                }
            }
        });

        showPhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showPhoto.setVisibility(View.INVISIBLE);
                return false;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_READ_EXTERNAL_MEMORY:
//                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    waypointArrayList.clear();
//                    getWaypoint();
//                }
//                break;
//            default:
//                break;
//        }
//    }
//
//    private void checkForPermission() {
//        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
//
//        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_MEMORY);
//        }
//    }

//    private boolean hasPermission(String permissionToCheck) {
//        int permissionCheck = ContextCompat.checkSelfPermission(this, permissionToCheck);
//        return (permissionCheck == PackageManager.PERMISSION_GRANTED);
//    }

    private void showImage(int position) {

        String path = waypointArrayList.get(position).getPath();

        showPhoto.setImageURI(Uri.parse(path));
        showPhoto.setVisibility(View.VISIBLE);

    }

    private void showVideo(int position) {

        String path = waypointArrayList.get(position).getPath();
        Intent intentVideo = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
        intentVideo.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentVideo.setDataAndType(Uri.parse(path), "video/mp4");
        startActivity(intentVideo);

    }

    private ArrayList<Waypoint> getWaypoint() {
        //Consulta todas las rutas almacenadas en la db.
        final RealmResults<Route> routes = realm.where(Route.class).findAll();

        //Crea una lista de waypoint con los que tienen info en el atributo path
        ArrayList<Waypoint> waypointList = new ArrayList<Waypoint>();

//        if (hasPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            for (int i = 0; i < routes.size(); i++) {
                for (int j = 0; j < routes.get(i).getWaypointList().size(); j++) {
                    if (routes.get(i).getWaypointList().get(j).getPath() != null) {
                        waypointList.add(routes.get(i).getWaypointList().get(j));
                    }
                }
            }
//        }
        return waypointList;
    }
}





