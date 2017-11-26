package com.marcostoral.keepmoving.fragments;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.adapters.RouteAdapter;
import com.marcostoral.keepmoving.models.Route;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends Fragment implements RealmChangeListener<RealmResults<Route>> {

    private Realm realm;
    private ListView lvHistory;
    private RealmResults<Route> routes;
    private RouteAdapter adapter;

    private OnFragmentInteractionListener mListener;

    private String mTitle;

    //Permissions
    public static final int PERMISSIONS_REQUEST_WRITE_DATA = 98;

    public ListViewFragment() {
        // Required empty public constructor
    }

    ///////////////////////////////////////////////////////
    ///////////////////   CALLBACK    /////////////////////
    ///////////////////////////////////////////////////////

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);

        init(view);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onChange(RealmResults<Route> element) {
        adapter.notifyDataSetChanged();
    }


    /**
     * Interface propia para la que habrá que definir el método propio onListClicK que recibe como
     * parámetro un elemento de la lista.
     */
    public interface OnFragmentInteractionListener {
        void onListClick(Route route);
    }

    ///////////////////////////////////////////////////////
    /////////////////////   UI    /////////////////////////
    ///////////////////////////////////////////////////////

    /**
     * Instancia la interfaz gráfica.
     * @param view
     */
    public void init(View view){

        cargaRutas();

        lvHistory = (ListView) view.findViewById(R.id.lvRoutes);

        adapter = new RouteAdapter(getContext(),routes,R.layout.lv_item);
        lvHistory.setAdapter(adapter);

        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mListener.onListClick(routes.get(position));   //cambio el arrayRoutes por routes

            }
        });

        registerForContextMenu(lvHistory);
    }


    ///////////////////////////////////////////////////////
    /////////////////   CONTEXT MENU    ///////////////////
    ///////////////////////////////////////////////////////


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = new MenuInflater(getContext());
        inflater.inflate(R.menu.lv_ctx_menu,menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //Adaptador
        AdapterView.AdapterContextMenuInfo inf = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //Funcionamiento del switch, en función del id del elemento menú clicado podremos hacer direferentes cosas
        switch (item.getItemId()) {

            //Exportar kml
            case R.id.export_kml:

                exportKML(inf.position);
                return true;

            //Exportar gpx
            case R.id.export_gpx:

                exportGPX(inf.position);
                return true;

            //Exportar gpx
            case R.id.modify_title:

                showSetTitleAlert(inf.position);

                this.adapter.notifyDataSetChanged();
                return true;

            // ELIMINAR: elimina ruta seleccionada.
            case R.id.action_delete:

                deleteRoute(inf.position);
                this.adapter.notifyDataSetChanged();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    public void setTitle(int position){

        realm.beginTransaction();
        routes.get(position).setTitle(mTitle);
        realm.commitTransaction();

    }

    public void exportKML(int position){

        checkWritePermission();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String routeDate = sdf.format(routes.get(position).getDate());
        String filename = "KM_"+ routeDate +".kml";

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            try {

            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/KeepMovin");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, filename);

                OutputStreamWriter outputStream = new OutputStreamWriter(new FileOutputStream(file));

            //Abrir fichero y escribir en él

//                outputStream = getContext().getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
                String l1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                String l2 = "\n <kml xmlns=\"http://www.opengis.net/kml/2.2\"> <Document>";
                String l3 = "\n <name>" + routes.get(position).getTitle() + "</name>";
                String l5 = "\n <LineStyle> \n <color>7f00ffff</color> \n <width>5</width> \n  </LineStyle>";

                outputStream.write(l1);
                outputStream.write(l2);
                outputStream.write(l3);
                outputStream.write(l5);

                String l6 = "\n <Placemark> \n <LineString> \n  <extrude>1</extrude> \n <tessellate>1</tessellate> \n <altitudeMode>absoluto</altitudeMode>";
                outputStream.write(l6);

                String lc = "\n" +
                        " <coordinates>";
                outputStream.write(lc);
                for (int i = 0; i < routes.get(position).getWaypointList().size(); i++) {

                    String s1 = "\n"+routes.get(position).getWaypointList().get(i).getLng() + "," + routes.get(position).getWaypointList().get(i).getLtd() + "," + routes.get(position).getWaypointList().get(i).getAlt();
                    outputStream.write(s1);

                }
                String lc2 = "\n" +
                        " </coordinates> \n </LineString> \n </Placemark>\n </Document> \n </kml>";
                outputStream.write(lc2);
                outputStream.close();

                MediaScannerConnection.scanFile(getContext(),new String[] { file.toString() },null,null);

                Toast.makeText(getContext(), R.string.succesful_export, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), R.string.error_export, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void exportGPX(int position) {


        checkWritePermission();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        String routeDate = sdf.format(routes.get(position).getDate());
        String filename = "KM_" + routeDate + ".gpx";

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {


            try {

                File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/KeepMovin");
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                File file = new File(directory, filename);

                //Abrir fichero y escribir en él

//                outputStream = getContext().getApplicationContext().openFileOutput(filename, getContext().MODE_APPEND);
                OutputStreamWriter outputStream = new OutputStreamWriter(new FileOutputStream(file));
                String l1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
                String l2 = "<gpx version=\"1.1\" creator=\"GPSBabel - http://www.gpsbabel.org\" xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n";
                String l3 = "<metadata>\n";
                String l4 = "<time>" + sdf2.format(routes.get(position).getDate()) + "Z</time>\n"; //2017-05-23T19:14:56.069Z
                String l5 = "<bounds minlat=\"" + routes.get(position).getMinLtd() + "\" minlon=\"" + routes.get(position).getMinLng() + "\" maxlat=\"" + routes.get(position).getMaxLtd() + "\" maxlon=\"" + routes.get(position).getMaxLng() + "\"/>\n";
                String l6 = "</metadata>\n";
                String l7 = "<trk>\n";
                String l8 = "<name>" + routes.get(position).getTitle() + "</name>\n";
                String l9 = "<cmt/>\n";
                String l10 = "<trkseg>\n";
                String l11 = "</trkseg>\n";
                String l12 = "</trk>\n";
                String l13 = "</gpx>\n";

                outputStream.write(l1);
                outputStream.write  (l2);
                outputStream.write  (l3);
                outputStream.write   (l4);
                outputStream.write  (l5);
                outputStream.write  (l6);
                outputStream.write  (l7);
                outputStream.write   (l8);
                outputStream.write (l9);
                outputStream.write (l10);
                for (int i = 0; i < routes.get(position).getWaypointList().size(); i++) {

                    String s1 = "<trkpt lat=\"" + routes.get(position).getWaypointList().get(i).getLtd() + "\" lon=\"" + routes.get(position).getWaypointList().get(i).getLng() + "\">\n";
                    String s2 = "<ele>" + routes.get(position).getWaypointList().get(i).getAlt() + "</ele>\n";
                    String s3 = "<time>" + sdf2.format(routes.get(position).getWaypointList().get(i).getDate()) + "Z</time>\n";
                    String s4 = "</trkpt>\n";
                    outputStream.write(s1);
                    outputStream.write(s2);
                    outputStream.write(s3);
                    outputStream.write(s4);

                }

                outputStream.write(l11);
                outputStream.write(l12);
                outputStream.write(l13);

                outputStream.close();

                MediaScannerConnection.scanFile(getContext(),new String[] { file.toString() },null,null);

                Toast.makeText(getContext(), R.string.succesful_export, Toast.LENGTH_SHORT).show();


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), R.string.error_export, Toast.LENGTH_SHORT).show();
            }
        }
    }


    ///////////////////////////////////////////////////////
    ////////////////////   REALM    ///////////////////////
    ///////////////////////////////////////////////////////

     /**
     *  Instancia la base de datos y realiza una búsqueda de todas las rutas.
     */
    public void cargaRutas(){

        realm = Realm.getDefaultInstance();
        routes = realm.where(Route.class).findAll();
        routes.addChangeListener(this);

    }

    /**
     * Elimina ruta.
     * @param position
     */
    private void deleteRoute(int position) {

        realm.beginTransaction();
        routes.get(position).deleteFromRealm();
        realm.commitTransaction();
    }

    ///////////////////////////////////////////////////////
    ////////////////////   DIALOG    ///////////////////////
    ///////////////////////////////////////////////////////

    private void showSetTitleAlert(final int pos){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Introduzca nuevo nombre");

        // Set up the input
        final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTitle = input.getText().toString();
                setTitle(pos);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    /**
     * Comprueba los permisos de escritura. En caso de no disponer de ellos permite activación en AlertDialog.
     */
    public void checkWritePermission(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle("Write External Storage Permission Needed")
                        .setMessage("This app needs the Write External Storage permission, please accept to use this functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        PERMISSIONS_REQUEST_WRITE_DATA);
                            }
                        })
                        .create()
                        .show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_WRITE_DATA);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            //Permiso de escritura
            case PERMISSIONS_REQUEST_WRITE_DATA: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {

//                        dispatchTakePictureIntent();

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();

                }
                return;

                // other 'case' lines to check for other
                // permissions this app might request
            }
        }
    }


}
