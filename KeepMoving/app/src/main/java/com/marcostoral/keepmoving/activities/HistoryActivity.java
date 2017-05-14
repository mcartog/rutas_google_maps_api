package com.marcostoral.keepmoving.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.dto.Route;
import com.marcostoral.keepmoving.fragments.ListViewFragment;
import com.marcostoral.keepmoving.fragments.RouteDetailsFragment;

public class HistoryActivity extends AppCompatActivity implements ListViewFragment.OnFragmentInteractionListener{

    private boolean isMultiPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        setMultiPanel();

    }

    /**
     * Comprueba si el fragment de detalles está cargado en la activity (si es xlarge) y en caso de
     * que esté actualiza su visualización. Si no, abre nueva actividad.
     * @param route
     */
    @Override
    public void onListClick(Route route) {

        if (isMultiPanel) {

            RouteDetailsFragment detailsFragment = (RouteDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentDetailsRoute);
            detailsFragment.renderRoute(route);

        } else {

            Intent intent = new Intent(this, RouteDetailsActivity.class);
            //No podemos pasar el objetro ruta directamente, apsamos su id para hacer una consulta.
            intent.putExtra("routeID", route.getId());
            startActivity(intent);
        }
    }

    private void setMultiPanel() {
        isMultiPanel = (getSupportFragmentManager().findFragmentById(R.id.fragmentDetailsRoute) != null);
    }


}
