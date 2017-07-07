package com.marcostoral.keepmoving.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.activities.ChartActivity;
import com.marcostoral.keepmoving.activities.RouteGalleryActivity;
import com.marcostoral.keepmoving.models.Route;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * A simple {@link Fragment} subclass.
 */
public class RouteDetailsFragment extends Fragment {

    //UI
    private TextView tvDate;
    private TextView tvHour;
    private TextView tvDistance;
    private TextView tvTime;
    private TextView tvTitle;
    private ImageView ivType;
    private ImageButton ibGallery;
    private ImageButton ibChart;

    private LinearLayout wrapper;

    public RouteDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details_route, container, false);

        init(view);

        return view;
    }

    public void init(View view){

        wrapper = (LinearLayout) view.findViewById(R.id.wrapper_fragment_details);
        tvDate = (TextView) view.findViewById(R.id.tvDetailsDate);
        tvHour = (TextView) view.findViewById(R.id.tvDetailsHour);
        tvDistance = (TextView) view.findViewById(R.id.tvDetailsDistance);
        tvTime = (TextView) view.findViewById(R.id.tvDetailsTime);
        tvTitle = (TextView) view.findViewById(R.id.tvDetailsTitle);
        ivType = (ImageView) view.findViewById(R.id.ivDetailsType);
        ibGallery = (ImageButton) view.findViewById(R.id.ib_gallery);
        ibChart = (ImageButton) view.findViewById(R.id.ib_chart);

    }

    /**
     * Renderiza la ruta a partir de los datos de la db.
     * @param route
     */
    public void renderRoute(Route route) {
        wrapper.setVisibility(View.VISIBLE);

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat dfh = new SimpleDateFormat("HH:mm");

        tvDate.setText(df.format(route.getDate()));
        tvHour.setText(dfh.format(route.getDate()));
        tvDistance.setText(String.format("%.3f", route.getDistance())+ " Km");
        tvTime.setText(route.getTime());
        tvTitle.setText(route.getTitle());
        switch (route.getType()){
            case 0:
                ivType.setImageResource(R.drawable.cycling);
                break;
            case 1:
                ivType.setImageResource(R.drawable.running);
                break;
            case 2:
                ivType.setImageResource(R.drawable.hiking);
                break;
            default:
                ivType = null;
        }

        final long ref = route.getId();

        ibGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RouteGalleryActivity.class);

                intent.putExtra("id",ref);
                startActivity(intent);

            }
        });

        ibChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChartActivity.class);

                intent.putExtra("id",ref);
                startActivity(intent);

            }
        });

    }

}
