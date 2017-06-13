package com.marcostoral.keepmoving.adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.marcostoral.keepmoving.R;
import com.marcostoral.keepmoving.models.Waypoint;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by marcostoral on 3/06/17.
 */

public class PhotoAdapter extends BaseAdapter {

    Context context;
    ArrayList<Waypoint> waypointList;
    int layout;
    WaypointPhotoHolder waypointPhotoHolder;

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

            waypointPhotoHolder.latitude.setText("Ltd: "+ String.valueOf(waypointList.get(position).getLtd()));
            waypointPhotoHolder.longitude.setText("Lng: " + String.valueOf(waypointList.get(position).getLng()));

//            Bitmap myPhoto = BitmapFactory.decodeFile(waypointList.get(position).getPath());
//            waypointPhotoHolder.photo.setImageBitmap(myPhoto);

//        ImageLoader loadBitmap = new ImageLoader();
//        loadBitmap.execute(waypointPhotoHolder.photo , waypointList.get(position).getPath());
//        waypointPhotoHolder.photo.setImageBitmap(myPhoto);

        return convertView;
    }

    public class WaypointPhotoHolder {

        public ImageView photo;
        public TextView latitude;
        public TextView longitude;
    }


//    public class ImageLoader extends AsyncTask<Object, String, Bitmap> {
//
//        private View view;
//        private Bitmap myPhoto = null;
//
//        @Override
//        protected Bitmap doInBackground(Object... parameters) {
//
//            // Get the passed arguments here
//            view = (View) parameters[0];
//            String uri = (String)parameters[1];
//
//            myPhoto = BitmapFactory.decodeFile(uri);
//
//            return myPhoto;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            if (bitmap != null && view != null) {
//
//                ImageView img = (ImageView) view.getTag(R.id.iv_gv_photo);
//                img.setImageBitmap(bitmap);
//            }
//        }
//    }

}
