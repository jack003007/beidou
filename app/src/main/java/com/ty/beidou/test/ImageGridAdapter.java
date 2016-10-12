package com.ty.beidou.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ty.beidou.R;
import com.ty.beidou.model.ImageBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ImageGridAdapter extends BaseAdapter {

	private TextCallback textcallback = null;
	final String TAG = getClass().getSimpleName();
	Activity act;
	List<ImageBean> dataList;
	Map<String, String> map = new HashMap<String, String>();
	BitmapCache cache;
	private Handler mHandler;
	private int selectTotal = 0;
	BitmapCache.ImageCallback callback = new BitmapCache.ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals(imageView.getTag())) {
					imageView.setImageBitmap(bitmap);
				} else {
					Log.e(TAG, "callback, bmp not match");
				}
			} else {
				Log.e(TAG, "callback, bmp null");
			}
		}
	};

	/**
	 * 更新所选图片数量
	 */
	public interface TextCallback {
		void onListen(int count);
	}

	public void setTextCallback(TextCallback listener) {
		textcallback = listener;
	}

	public ImageGridAdapter(Activity act, List<ImageBean> list, Handler mHandler) {
		this.act = act;
		dataList = list;
		cache = new BitmapCache();
		this.mHandler = mHandler;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (dataList != null) {
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	class Holder {
		private ImageView iv;
		private ImageView ivCheck;
		private TextView tvFrame;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;

		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_image_grid, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.image);
			holder.ivCheck = (ImageView) convertView
					.findViewById(R.id.isselected);
			holder.tvFrame = (TextView) convertView
					.findViewById(R.id.item_image_grid_text);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final ImageBean item = dataList.get(position);

		holder.iv.setTag(item.imagePath);
		cache.displayBmp(holder.iv, item.thumbnailPath, item.imagePath,
				callback);
		//不能保存状态，所以没必要
//		if (item.isSelected) {
//			//选中
//			holder.selected.setImageResource(R.drawable.icon_data_select);
//			holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
//		} else {
//			//未选中
//			holder.selected.setImageResource(-1);
//			holder.text.setBackgroundColor(0x00000000);
//		}
		holder.iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String path = dataList.get(position).imagePath;

				if ((Bimp.strChosenList.size() + selectTotal) < 9) {
					item.isSelected = !item.isSelected;
					if (item.isSelected) {
						holder.ivCheck
								.setImageResource(R.drawable.icon_data_select);
						holder.tvFrame.setBackgroundResource(R.drawable.bgd_relatly_line);
						selectTotal++;
						if (textcallback != null)
							textcallback.onListen(selectTotal);
						map.put(path, path);

					} else if (!item.isSelected) {
						holder.ivCheck.setImageResource(0);
						holder.tvFrame.setBackgroundColor(0x00000000);
						selectTotal--;
						if (textcallback != null)
							textcallback.onListen(selectTotal);
						map.remove(path);
					}
				} else if ((Bimp.strChosenList.size() + selectTotal) >= 9) {
					if (item.isSelected == true) {
						item.isSelected = !item.isSelected;
						holder.ivCheck.setImageResource(0);
						selectTotal--;
						map.remove(path);

					} else {
						Message message = Message.obtain(mHandler, 0);
						message.sendToTarget();
					}
				}
			}

		});

		return convertView;
	}
}
