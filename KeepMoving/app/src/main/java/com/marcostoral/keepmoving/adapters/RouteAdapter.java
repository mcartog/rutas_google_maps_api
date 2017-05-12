package com.marcostoral.keepmoving.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.dto.Route;

import java.util.List;

/**
 * Created by marcostoral on 11/05/17.
 */

public class RouteAdapter extends BaseAdapter {

    Context context;
    List<Route> routeList;
    int layout;

    public RouteAdapter(Context context, List<Route> routeList, int layout) {
        this.context = context;
        this.routeList = routeList;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return routeList.size();
    }

    @Override
    public Route getItem(int position) {
        return routeList.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RouteHolder routeHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layout, null);

            routeHolder = new RouteHolder();

            routeHolder.type = (ImageView) convertView.findViewById(R.id.ivType);
            routeHolder.distance = (TextView) convertView.findViewById(R.id.tvDistance);
            routeHolder.time = (TextView) convertView.findViewById(R.id.tvTime);
            routeHolder.date = (TextView) convertView.findViewById(R.id.tvDate);

            convertView.setTag(routeHolder);

        } else {
            routeHolder = (RouteHolder) convertView.getTag();
        }

        routeHolder.type.setImageResource(routeList.get(position).getType());
        routeHolder.distance.setText(routeList.get(position).getDistance());
        routeHolder.time.setText(routeList.get(position).getTime().toString());
        routeHolder.date.setText(routeList.get(position).getDate().toString());

        return convertView;
    }

    public class RouteHolder {

        public ImageView type;
        public TextView distance;
        public TextView time;
        public TextView date;
    }
}
