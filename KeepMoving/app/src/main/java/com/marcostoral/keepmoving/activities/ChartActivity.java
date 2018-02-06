package com.marcostoral.keepmoving.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.models.Route;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class ChartActivity extends AppCompatActivity {

    private LineChart lineChart;
    private LineDataSet lineDataSet;
    private LineData data;
    private ArrayList<Entry> entries;
//    private float t;
    private float z;
    private float d;

    private Realm realm;

    private Route route;
    private RealmResults<Route> routeById;
    private long id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        realm = Realm.getDefaultInstance();
        lineChart = (LineChart) findViewById(R.id.line_chart_zt);

        id = getIntent().getExtras().getLong("id");
        route = getRoute(id);

        entries = getData(route);

        lineDataSet = new LineDataSet(entries, getString(R.string.altitude));//parameterName);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setCubicIntensity(1);

        data = new LineData(lineDataSet);
        lineChart.setData(data);
        Description d = new Description();
        d.setText(getString(R.string.perfil));
        d.setTextSize(12);
        lineChart.setDescription(d);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
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

    public ArrayList<Entry> getData(Route route){

        entries = new ArrayList<Entry>();
        Entry entry;
        z = 0;

        for (int j = 0; j < route.getWaypointList().size(); j++) {

            if( (z - route.getWaypointList().get(j).getAlt()) >= 2.5 || (z - route.getWaypointList().get(j).getAlt()) < -2.5){  //+-5

                if(route.getWaypointList().get(j).getAlt() == 0 ){

                    //El primer posicionamiento puede no coger bien la altura
                    if (route.getWaypointList().get(j+1) != null){
                        z = (float) route.getWaypointList().get(j+1).getAlt();
                        d = (float) route.getWaypointList().get(j+1).getDistance();
                    }

                } else {

                    z = (float) route.getWaypointList().get(j).getAlt();
                    d = (float) route.getWaypointList().get(j).getDistance();

                }

                entry = new Entry(d,z);
                entries.add(entry);
            }


        }

        return entries;
    }
}
