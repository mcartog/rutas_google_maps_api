package com.marcostoral.keepmoving.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

public class RouteGalleryActivity extends AppCompatActivity {

    //Realm
    private Realm realm;
    private RealmResults<Route> routeById;

    //Model
    private Route route;
    private long id;

    //Fragment
    private GridPhotoViewFragment gvPhotosFragment;
    private View gvPhotos;

    //GridView
    private GridView gvPhoto;
    private ArrayList<Waypoint> waypointArrayList;
    private PhotoAdapter adapter;

    private ImageView showPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        gvPhotosFragment = (GridPhotoViewFragment) getSupportFragmentManager().findFragmentById(R.id.gvPicturesFragment);
        gvPhotos = gvPhotosFragment.getView();

        showPhoto = (ImageView) findViewById(R.id.iv_show_photo_det);

        id = getIntent().getExtras().getLong("id");

        route = getRoute(id);

        waypointArrayList = new ArrayList<>();

        for (int i = 0; i < route.getWaypointList().size(); i++) {
            if (route.getWaypointList().get(i).getPath() != null) {
                waypointArrayList.add(route.getWaypointList().get(i));
            }
        }


        gvPhoto = (GridView) gvPhotos.findViewById(R.id.gvPictures);
        adapter = new PhotoAdapter(this,waypointArrayList,R.layout.gv_photogrid_item);
        gvPhoto.setAdapter(adapter);

        gvPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(waypointArrayList.get(position).isVideo() == true){

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

    private void showImage(int position) {

        String path =  waypointArrayList.get(position).getPath();
        showPhoto.setImageURI(Uri.parse(path));
        showPhoto.setVisibility(View.VISIBLE);

    }

    private void showVideo(int position) {

        String path =   waypointArrayList.get(position).getPath();
        Intent intentVideo = new Intent(Intent.ACTION_VIEW, Uri.parse(path) );
        intentVideo.setDataAndType(Uri.parse(path), "video/mp4");
        startActivity(intentVideo);

    }

    /**
     * Devuelve la ruta seleccionada en la base de datos a apartir de su id.
     *
     * @param id Id de la ruta seleccionada
     * @return Ruta seleccionada en base de datos.
     */
    public Route getRoute(long id) {

        realm = Realm.getDefaultInstance();
        routeById = realm.where(Route.class).equalTo("id", id).findAll();
        return routeById.get(0);

    }

}
