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
import com.marcostoral.keepmoving.dto.Route;
import com.marcostoral.keepmoving.dto.Waypoint;
import com.marcostoral.keepmoving.fragments.GridPhotoViewFragment;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class GelleryActivity extends AppCompatActivity {


    private Realm realm;

    private GridPhotoViewFragment gvPhotosFragment;
    private View gvPhotos;

    private GridView gvPhoto;
    private ArrayList<Waypoint> waypointArrayList;
    private PhotoAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        gvPhotosFragment = (GridPhotoViewFragment) getSupportFragmentManager().findFragmentById(R.id.gvPicturesFragment);
        gvPhotos = gvPhotosFragment.getView();


//        if(savedInstanceState == null) {

            realm = Realm.getDefaultInstance();

            final RealmResults<Route> routes = realm.where(Route.class).findAll();

            realm.close();

            waypointArrayList = new ArrayList<Waypoint>();
            for (int i = 0; i < routes.size(); i++) {
                for (int j = 0; j < routes.get(i).getWaypointList().size(); j++) {

                    if (routes.get(i).getWaypointList().get(j).getPath() != null) {
                        waypointArrayList.add(routes.get(i).getWaypointList().get(j));
                    }
                }

            }

//        } else {

//            waypointArrayList = savedInstanceState.getParcelableArrayList("waypointList");

//        }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        realm.close();
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        outState.putParcelableArrayList("waypointList", waypointArrayList);
//
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//
//        waypointArrayList = savedInstanceState.getParcelableArrayList("waypointList");
//
//    }
}
