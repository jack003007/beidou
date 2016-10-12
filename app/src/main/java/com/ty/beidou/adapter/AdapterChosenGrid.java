package com.ty.beidou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.ty.beidou.R;
import com.ty.beidou.common.Flags;

import java.io.File;
import java.util.List;

/**
 * Created by ty on 2016/9/28.
 */
//@SuppressLint("HandlerLeak")
public class AdapterChosenGrid extends BaseAdapter {

    private Context mContext;
    private List<String> imagePaths;

    public AdapterChosenGrid(Context context, List<String> paths) {
        this.mContext = context;
        this.imagePaths = paths;

    }

    @Override
    public int getCount() {
        return imagePaths.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return imagePaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_published_grida,
                    parent, false);
            holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.item_grida_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == Flags.Photo.CURRENT_NUM) {
            Picasso.with(mContext).load(R.drawable.ic_cross_light).into(holder.iv);
            if (position == Flags.Photo.MAX_NUM) {
                holder.iv.setVisibility(View.GONE);
            }
        } else {
            Picasso.with(mContext).load(new File(imagePaths.get(position))).fit().centerCrop().into(holder.iv);
        }

        return convertView;
    }

    public class ViewHolder {
        ImageView iv;
    }


}