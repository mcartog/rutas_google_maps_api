package com.marcostoral.keepmoving.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.dto.Route;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by marcostoral on 4/04/17.
 */

public class ListViewAdapter extends BaseAdapter implements ListAdapter {

    private Context context;
    private RealmResults<Route> routeList;
    private int layout;

    public ListViewAdapter(Context context, RealmResults<Route> routeList, int layout) {
        this.context = context;
        this.routeList = routeList;
        this.layout = layout;
    }


    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return routeList.size();
    }

    @Override
    public Object getItem(int position) {
        return routeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RouteHolder holder;

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

        holder.type.setImageResource(routeList.get(position).getType());
        holder.distance.setText(routeList.get(position).getDistance());
        holder.time.setText(routeList.get(position).getTime());
        //holder.date.setText((CharSequence) routeList.get(position).getDate());
//        hay que discurrir como pasar la fecha


        return item;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    public static class RouteHolder{

        public ImageView type;
        public TextView distance;
        public TextView time;
        public TextView date;

    }
}
