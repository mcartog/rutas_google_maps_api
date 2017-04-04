package com.marcostoral.keepmoving.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.adapters.ListViewAdapter;
import com.marcostoral.keepmoving.dto.Route;

import java.util.ArrayList;

public class HistoryActivity extends FragmentActivity {

    private Fragment lvFragment;
    private ArrayList<Route> listaRutas = new ArrayList<>();

    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        cargaRutas();
    }


    ///////////////////////////////////////////////////////
    ////////////////////   CRUD   /////////////////////////
    ///////////////////////////////////////////////////////

    public void cargaRutas(){

        listaRutas.add(new Route("13km","1h3m",R.drawable.running_48));
        listaRutas.add(new Route("63km","1h3m",R.drawable.cycling_48));
        listaRutas.add(new Route("10km","1h3m",R.drawable.trekking_48));
        listaRutas.add(new Route("13km","1h3m",R.drawable.cycling_48));


    }


}
