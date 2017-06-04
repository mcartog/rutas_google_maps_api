package com.marcostoral.keepmoving.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.adapters.PhotoAdapter;
import com.marcostoral.keepmoving.adapters.RouteAdapter;
import com.marcostoral.keepmoving.dto.Route;
import com.marcostoral.keepmoving.dto.Waypoint;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class GridPhotoViewFragment extends Fragment implements RealmChangeListener<RealmResults<Route>> {


    public GridPhotoViewFragment() {
        // Required empty public constructor
    }


//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
////
////        outState.putParcelableArrayList("waypointList", waypointArrayList);
//
//    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_grid_photo_view, container, false);

        return view;
    }

    @Override
    public void onChange(RealmResults<Route> element) {

    }

}
