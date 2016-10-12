package com.ty.beidou.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ty.beidou.R;
import com.ty.beidou.model.MsgBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by ty on 2016/9/27.
 */

public class AdapterMsgs extends RecyclerView.Adapter<AdapterMsgs.ItemViewHolder> {

    private Context context;

    private List<MsgBean> mBeans;

    public AdapterMsgs(Context context, List<MsgBean> mBeans) {
        this.context = context;
        this.mBeans = mBeans;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemViewHolder holder = new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_msg, null, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.tvTitle.setText(mBeans.get(position).getTitle());
        holder.tvContent.setText(mBeans.get(position).getContent());
        holder.tvTimestamp.setText(mBeans.get(position).getTime());

    }

    @Override
    public int getItemCount() {
        return mBeans.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_icon)
        ImageView ivIcon;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_timestamp)
        TextView tvTimestamp;
        @BindView(R.id.ll_top)
        LinearLayout llTop;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.ll_images)
        LinearLayout llImages;
        @BindView(R.id.ll_center)
        LinearLayout llCenter;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

}
