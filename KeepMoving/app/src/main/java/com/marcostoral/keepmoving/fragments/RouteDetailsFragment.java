package com.marcostoral.keepmoving.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.dto.Route;

/**
 * A simple {@link Fragment} subclass.
 */
public class RouteDetailsFragment extends Fragment {

    //UI
    private TextView tvDate;
    private TextView tvDistance;
    private TextView tvTime;
    private ImageView ivType;

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
        tvDistance = (TextView) view.findViewById(R.id.tvDetailsDistance);
        tvTime = (TextView) view.findViewById(R.id.tvDetailsTime);
        ivType = (ImageView) view.findViewById(R.id.ivDetailsType);

    }


    public void renderRoute(Route route) {
        wrapper.setVisibility(View.VISIBLE);
       // tvDate.setText(route.getDate());
        tvDistance.setText(route.getDistance());
        tvTime.setText(route.getTime());
        switch (route.getType()){
            case R.drawable.cycling:
                ivType.setImageResource(R.drawable.cycling);
                break;
            case R.drawable.running:
                ivType.setImageResource(R.drawable.running);
                break;
            case R.drawable.hiking:
                ivType.setImageResource(R.drawable.hiking);
                break;
            default:
                ivType = null;
        }

    }


}
