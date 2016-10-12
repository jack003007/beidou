package com.ty.beidou.test;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.ty.beidou.R;
import com.ty.beidou.common.BaseActivity;
import com.ty.beidou.common.MyTitleBar;
import com.ty.beidou.model.ImageBean;

import java.util.List;

public class ActivityImageGrid extends BaseActivity {
    public static final String EXTRA_IMAGE_LIST = "imagelist";

    // ArrayList<Entity> dataList;
    List<ImageBean> dataList;
    GridView gridView;
    ImageGridAdapter adapter;
    AlbumHelper helper;

    private MyTitleBar mTitleBar;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(ActivityImageGrid.this, "最多选择9张图片", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_grid);

        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());

//        dataList = (List<ImageBean>) getIntent().getSerializableExtra(
//                EXTRA_IMAGE_LIST);

//        mTitleBar = new MyTitleBar(me);
//        mTitleBar.setCenterText("图片");
//        mTitleBar.setRightSingleIcon(R.drawable.icon_check, "确定", new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                ArrayList<String> list = new ArrayList<>();
//                Collection<String> c = adapter.map.values();
//                Iterator<String> it = c.iterator();
//                while (it.hasNext()) {
//                    list.add(it.next());
//                }
//                if (Bimp.act_bool) {
//                    Intent intent = new Intent(ActivityImageGrid.this,
//                            ActivityPublish.class);
//                    startActivity(intent);
//                    Bimp.act_bool = false;
//                }
//                for (int i = 0; i < list.size(); i++) {
//                    if (Bimp.strChosenList.size() < 9) {
//                        Bimp.strChosenList.add(list.get(i));
//                    }
//                }
//                finish();
//                return false;
//            }
//        });
        initView();


    }

    /**
     *
     */
    private void initView() {
        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new ImageGridAdapter(ActivityImageGrid.this, dataList,
                mHandler);
        gridView.setAdapter(adapter);
        adapter.setTextCallback(new ImageGridAdapter.TextCallback() {
            public void onListen(int count) {
                mTitleBar.setCenterText("完成" + "(" + count + ")");
            }
        });

        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                /**
                 *
                 */
                // if(dataList.get(position).isSelected()){
                // dataList.get(position).setSelected(false);
                // }else{
                // dataList.get(position).setSelected(true);
                // }

                adapter.notifyDataSetChanged();
            }

        });

    }
}
