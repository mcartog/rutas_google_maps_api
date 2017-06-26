package com.marcostoral.keepmoving.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        gvPhotosFragment = (GridPhotoViewFragment) getSupportFragmentManager().findFragmentById(R.id.gvPicturesFragment);
        gvPhotos = gvPhotosFragment.getView();

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

    }

    /**
     * Recupera el path de una imagen y lanza una actividad parar reproducirla.
     * @param position en el GridView
     */
    private void showImage(int position) {

        String path = waypointArrayList.get(position).getPath();
        Intent intentPhoto = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
        intentPhoto.setDataAndType(Uri.parse(path), "image/*");
        startActivity(intentPhoto);

    }

    /**
     * Recupera el path de un video y lanza una actividad para reproducirlo.
     * @param position en el GridView
     */
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
