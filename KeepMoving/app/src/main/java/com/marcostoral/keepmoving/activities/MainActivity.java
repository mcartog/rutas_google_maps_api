package com.marcostoral.keepmoving.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiActivity;
import com.marcostoral.keepmoving.R;

public class MainActivity extends AppCompatActivity {
    //UI
    private Button btnMaps;
    private Button btnHistory;
    private Button btnStadistics;

    //Dialogs
    private Dialog dialogRouteType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    ///////////////////////////////////////////////////////
    ////////////////////   MENUS   ////////////////////////
    ///////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.opt_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_prefs:
                Toast.makeText(this, "Preferencias", Toast.LENGTH_SHORT).show();

                return true;
            case R.id.action_about:

                Toast.makeText(this, "Info api agoogle", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////

    private void init(){
        btnMaps = (Button) findViewById(R.id.btnMaps);
        btnStadistics = (Button) findViewById(R.id.btnStadistics);
        btnHistory = (Button) findViewById(R.id.btnHistory);

        dialogRouteType = generateDialogRouteType();


        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogRouteType.show();
                Toast.makeText(MainActivity.this, "al mapa",Toast.LENGTH_SHORT).show();

            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "historial",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        btnStadistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "estadisticas",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, StadisticsActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * Crea el diálogo de selección de tipo de actividad.
     * @return
     */
    private Dialog generateDialogRouteType(){

        final String[] items = getResources().getStringArray(R.array.route_type_values);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.title_dialog_route_type);
        builder.setItems(R.array.route_type, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Toast.makeText(MainActivity.this,"Opción elegida: " + items[item],Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("type",items[item]);
                startActivity(intent);
            }
        });

        return builder.create();
    }

}
