package com.marcostoral.keepmoving.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.models.Route;
import com.marcostoral.keepmoving.fragments.ListViewFragment;
import com.marcostoral.keepmoving.fragments.RouteDetailsFragment;

public class HistoryActivity extends AppCompatActivity implements ListViewFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
    }

    @Override
    public void onListClick(Route route) {

            Intent intent = new Intent(this, RouteDetailsActivity.class);
            //No podemos pasar el objetro ruta directamente, pasamos su id para hacer una consulta.
            intent.putExtra("routeID", route.getId());
            startActivity(intent);
        }

}
