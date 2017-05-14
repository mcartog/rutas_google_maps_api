package com.marcostoral.keepmoving.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.adapters.RouteAdapter;
import com.marcostoral.keepmoving.dto.Route;

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

    public ListViewFragment() {
        // Required empty public constructor
    }


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
     * Interface propia para la que habrá que definir el método propio onListClic que recibe como
     * parámetro un elemento de la lista.
     */
    public interface OnFragmentInteractionListener {
        void onListClick(Route route);
    }


    ///////////////////////////////////////////////////////
    ////////////////////   MENUS   ////////////////////////
    ///////////////////////////////////////////////////////

    ////////////////////   CONTEXT MENU   /////////////////

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = new MenuInflater(getContext());

        //Añade un titulo (estas 2 líneas son opcionales, pero muy elegantes.
       // AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
       // menu.setHeaderTitle((CharSequence) this.arrayRoutes.get(info.position));

        inflater.inflate(R.menu.lv_ctx_menu,menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //Adaptador
        AdapterView.AdapterContextMenuInfo inf = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //Funcionamiento del switch, en función del id del elemento menú clicado podremos hacer direferentes cosas
        switch (item.getItemId()) {

            // COMPARTIR: esta opción NO está disponible. Consultar:   https://developers.facebook.com/docs/sharing/android
            case R.id.action_share:

                Toast.makeText(getContext(), "Compartir en red social",Toast.LENGTH_SHORT).show();
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

}
