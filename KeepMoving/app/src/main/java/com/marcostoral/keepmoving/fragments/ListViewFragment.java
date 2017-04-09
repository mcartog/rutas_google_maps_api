package com.marcostoral.keepmoving.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
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

        cargaRutas();

        lvHistory = (ListView) view.findViewById(R.id.lvRoutes);

        adapter = new ListViewAdapter(getContext(),arrayRoutes);
        lvHistory.setAdapter(adapter);

        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Haces clic en la lista",Toast.LENGTH_LONG).show();
            }
        });


        return view;
    }

    /**
     * Interface propia para la que habrá que definir el método propio onListClic que recibe como
     * parámetro un elemento de la lista.
     */
    public interface OnFragmentInteractionListener {
        void onListClick(Route route);
    }

    /**
     * Metodo que carga datos, BORRAR cuando ya no sea necesario
     */

    public void cargaRutas(){

        arrayRoutes.add(new Route("13km","1h3m",R.drawable.running_48));
        arrayRoutes.add(new Route("63km","1h3m",R.drawable.cycling_48));
        arrayRoutes.add(new Route("10km","1h3m",R.drawable.trekking_48));
        arrayRoutes.add(new Route("13km","1h3m",R.drawable.cycling_48));


    }

}
