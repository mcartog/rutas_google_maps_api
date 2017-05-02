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
import com.marcostoral.keepmoving.adapters.ListViewAdapter;
import com.marcostoral.keepmoving.dto.Route;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends Fragment {

    private ListView lvHistory;
    private ArrayList<Route> arrayRoutes = new ArrayList<>();
    private ListViewAdapter adapter;

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
            case R.id.action_share:
                //Aquí van las acciones que hará esto: borrar elemento de una lista, editarlo, añadir elemento a una lista, iniciar una actividad...
                Toast.makeText(getContext(), "Compartir en red social",Toast.LENGTH_SHORT).show();
              /* Consulta cómo hacer esto en:
              https://developers.facebook.com/docs/sharing/android

                ShareButton shareButton = (ShareButton)findViewById(R.id.fb_share_button);
                shareButton.setShareContent(content);
                */
                return true;
            case R.id.action_delete:
                //Ejemplo borrar elemento
                //Borar item seleccionado
                this.arrayRoutes.remove(inf.position);
                //Notificar el cambio
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

        adapter = new ListViewAdapter(getContext(),arrayRoutes);
        lvHistory.setAdapter(adapter);

        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mListener.onListClick(arrayRoutes.get(position));

            }
        });

        registerForContextMenu(lvHistory);
    }

    /**
     * Metodo que carga datos, BORRAR cuando ya no sea necesario
     */

    public void cargaRutas(){
/*
        arrayRoutes.add(new Route("10km","1h",R.drawable.running,"15/02/2004"));
        arrayRoutes.add(new Route("20km","1h10m",R.drawable.cycling,"15/02/2005"));
        arrayRoutes.add(new Route("30km","1h20m",R.drawable.hiking,"15/02/2006"));
        arrayRoutes.add(new Route("40km","1h30m",R.drawable.cycling,"15/02/2014"));
        arrayRoutes.add(new Route("10km","1h",R.drawable.running,"15/02/2004"));
        arrayRoutes.add(new Route("20km","1h10m",R.drawable.cycling,"15/02/2005"));
        arrayRoutes.add(new Route("30km","1h20m",R.drawable.hiking,"15/02/2006"));
        arrayRoutes.add(new Route("40km","1h30m",R.drawable.cycling,"15/02/2014"));
        arrayRoutes.add(new Route("10km","1h",R.drawable.running,"15/02/2004"));
        arrayRoutes.add(new Route("20km","1h10m",R.drawable.cycling,"15/02/2005"));
        arrayRoutes.add(new Route("30km","1h20m",R.drawable.hiking,"15/02/2006"));
        arrayRoutes.add(new Route("40km","1h30m",R.drawable.cycling,"15/02/2014"));
        arrayRoutes.add(new Route("10km","1h",R.drawable.running,"15/02/2004"));
        arrayRoutes.add(new Route("20km","1h10m",R.drawable.cycling,"15/02/2005"));
        arrayRoutes.add(new Route("30km","1h20m",R.drawable.hiking,"15/02/2006"));
        arrayRoutes.add(new Route("40km","1h30m",R.drawable.cycling,"15/02/2014"));
*/

    }

}
