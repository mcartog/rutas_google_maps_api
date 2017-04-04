package com.marcostoral.keepmoving.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.dto.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcostoral on 4/04/17.
 */

public class ListViewAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<Route> routeList;


    public ListViewAdapter(Context context, ArrayList<Route> routeList) {
        super(context, R.layout.lv_item, routeList);
        this.context = context;
        this.routeList = routeList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RouteHolder holder = null;

        View item = convertView;

        if(item == null){

            LayoutInflater inflater = LayoutInflater.from(context);
            item = inflater.inflate(R.layout.lv_item,null);

            holder = new RouteHolder();
            holder.type = (ImageView) item.findViewById(R.id.ivType);
            holder.distance = (TextView) item.findViewById(R.id.tvDistance);
            holder.time = (TextView) item.findViewById(R.id.tvTime);
            holder.date = (TextView) item.findViewById(R.id.tvDate);

            item.setTag(holder);

        }else {

            holder = (RouteHolder) item.getTag();

        }

   //     holder.type.setImageResource(routeList.get(position).getType());
        holder.distance.setText(routeList.get(position).getDistance());
        holder.time.setText(routeList.get(position).getTime());
        holder.date.setText((CharSequence) routeList.get(position).getDate());

        return item;
    }

    public static class RouteHolder{

        ImageView type;
        TextView distance;
        TextView time;
        TextView date;

    }
}
