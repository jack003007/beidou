package com.ty.beidou.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ty.beidou.R;
import com.ty.beidou.model.MsgBean;
import com.libs.view.utils.TimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by ty on 2016/9/27.
 */

public class AdapterMsgs extends BaseQuickAdapter<MsgBean> {

    private Context context;

    private List<MsgBean> mBeans;


    public AdapterMsgs(int layoutResId, List<MsgBean> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, MsgBean msgBean) {
        baseViewHolder.setText(R.id.tv_title, msgBean.getTitle())
                .setText(R.id.tv_content, msgBean.getContent())
                .setText(R.id.tv_timestamp, TimeUtils.milliseconds2String(1000L * Long.parseLong(msgBean.getCtime())));

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
