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
import com.marcostoral.keepmoving.models.Route;
import com.marcostoral.keepmoving.models.Waypoint;
import com.marcostoral.keepmoving.fragments.GridPhotoViewFragment;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class GelleryActivity extends AppCompatActivity {

    //Realm
    private Realm realm;

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
        setContentView(R.layout.activity_gallery);

        realm = Realm.getDefaultInstance();

        gvPhotosFragment = (GridPhotoViewFragment) getSupportFragmentManager().findFragmentById(R.id.gvPicturesFragment);
        gvPhotos = gvPhotosFragment.getView();

        //Consulta todas las rutas almacenadas en la db.
        final RealmResults<Route> routes = realm.where(Route.class).findAll();

        waypointArrayList = new ArrayList<Waypoint>();

        for (int i = 0; i < routes.size(); i++) {
            for (int j = 0; j < routes.get(i).getWaypointList().size(); j++) {
                if (routes.get(i).getWaypointList().get(j).getPath() != null) {
                    waypointArrayList.add(routes.get(i).getWaypointList().get(j));
                    }
                }
            }

        gvPhoto = (GridView) gvPhotos.findViewById(R.id.gvPictures);
        adapter = new PhotoAdapter(this,waypointArrayList,R.layout.gv_photogrid_item);
        gvPhoto.setAdapter(adapter);

        gvPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //A la galería de imágenes
                Intent intent = new Intent(Intent.ACTION_VIEW,  Uri.parse("content://media/internal/images/media"));
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }

}
