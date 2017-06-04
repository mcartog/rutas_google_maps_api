package com.marcostoral.keepmoving.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.dto.Waypoint;

import java.util.ArrayList;

/**
 * Created by marcostoral on 3/06/17.
 */

public class PhotoAdapter extends BaseAdapter {

    Context context;
    ArrayList<Waypoint> waypointList;
    int layout;

    public PhotoAdapter(Context context, ArrayList<Waypoint> waypointList, int layout) {
        this.context = context;
        this.waypointList = waypointList;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return waypointList.size();
    }

    @Override
    public Waypoint getItem(int position) {
        return waypointList.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        WaypointPhotoHolder waypointPhotoHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layout, null);

            waypointPhotoHolder = new WaypointPhotoHolder();

            waypointPhotoHolder.latitude = (TextView) convertView.findViewById(R.id.tv_gv_ltd);
            waypointPhotoHolder.longitude = (TextView) convertView.findViewById(R.id.tv_gv_lng);
            waypointPhotoHolder.photo = (ImageView) convertView.findViewById(R.id.iv_gv_photo);

            convertView.setTag(waypointPhotoHolder);

        } else {
            waypointPhotoHolder = (WaypointPhotoHolder) convertView.getTag();
        }

            waypointPhotoHolder.latitude.setText("Latitud: "+ String.valueOf(waypointList.get(position).getLtd()));
            waypointPhotoHolder.longitude.setText("Longitud: " + String.valueOf(waypointList.get(position).getLng()));

            Bitmap myPhoto = BitmapFactory.decodeFile(waypointList.get(position).getPath());
            waypointPhotoHolder.photo.setImageBitmap(myPhoto);

        return convertView;
    }

    public class WaypointPhotoHolder {

        public ImageView photo;
        public TextView latitude;
        public TextView longitude;
    }
}
