package com.marcostoral.keepmoving.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.adapters.ListViewAdapter;
import com.marcostoral.keepmoving.dto.Route;
import com.marcostoral.keepmoving.fragments.ListViewFragment;
import com.marcostoral.keepmoving.fragments.RouteDetailsFragment;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity implements ListViewFragment.OnFragmentInteractionListener{

    private Fragment lvFragment;
    private ArrayList<Route> listaRutas = new ArrayList<>();

    private ListViewAdapter adapter;

    private boolean isMultiPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        setMultiPanel();

    }

    @Override
    public void onListClick(Route route) {
        if (isMultiPanel) {

            RouteDetailsFragment detailsFragment = (RouteDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.fRouteDetails);
            detailsFragment.renderRoute(route);


        } else {

            Intent intent = new Intent(this, RouteDetailsActivity.class);

            intent.putExtra("distance", route.getDistance());
            intent.putExtra("time",route.getTime());
            //     intent.putExtra("type", route.getType());
            intent.putExtra("date", route.getDate());

            startActivity(intent);



            //    Toast.makeText(this, "Haces clic en la lista",Toast.LENGTH_LONG).show();

        }

    }

    private void setMultiPanel() {
        isMultiPanel = (getSupportFragmentManager().findFragmentById(R.id.fRouteDetails) != null);
    }


    ///////////////////////////////////////////////////////
    ////////////////////   CRUD   /////////////////////////
    ///////////////////////////////////////////////////////




}
