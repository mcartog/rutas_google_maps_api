package com.marcostoral.keepmoving.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.adapters.PhotoAdapter;
import com.marcostoral.keepmoving.dto.Route;
import com.marcostoral.keepmoving.dto.Waypoint;
import com.marcostoral.keepmoving.fragments.GridPhotoViewFragment;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class RouteGalleryActivity extends AppCompatActivity {

    private Realm realm;
    private RealmResults<Route> routeById;

    private Route route;
    private long id;

    private GridPhotoViewFragment gvPhotosFragment;
    private View gvPhotos;

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
        Toast.makeText(this, "hola" + id,Toast.LENGTH_SHORT).show();

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
//                Uri path = Uri.parse("content:/"+waypointArrayList.get(position).getPath());
                Intent intent = new Intent(Intent.ACTION_VIEW,  Uri.parse("content://media/internal/images/media"));
                startActivity(intent);
            }
        });

    }

    /**
     * Método que devuelve la ruta seleccionada en la base de datos a apartir de su id.
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
