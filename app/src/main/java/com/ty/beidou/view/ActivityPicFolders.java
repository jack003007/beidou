package com.ty.beidou.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ty.beidou.R;
import com.ty.beidou.adapter.AdapterImageFolder;
import com.ty.beidou.common.BaseActivity;
import com.ty.beidou.common.MyTitleBar;
import com.ty.beidou.model.ImageFolderBean;
import com.ty.beidou.test.AlbumHelper;

import java.util.List;

/**
 * Created by ty on 2016/9/27.
 */

public class ActivityPicFolders extends BaseActivity {

    // ArrayList<Entity> imageFolderList;//用来装载数据源的列表
    List<ImageFolderBean> imageFolderList;
    GridView gridView;
    AdapterImageFolder adapter;// 自定义的适配器
    AlbumHelper helper;
    public static final String EXTRA_IMAGE_LIST = "imagelist";
    public static Bitmap mBitmap;

    private MyTitleBar mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_folders);


        initData();
        initView();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mTitleBar = new MyTitleBar(me);
        mTitleBar.setCenterText("选择文件夹");
        mTitleBar.setRightSingleIcon(R.drawable.icon_close, "取消", new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                finish();
                return false;
            }
        });

        // /**
        // * 这里，我们假设已经从网络或者本地解析好了数据，所以直接在这里模拟了10个实体类，直接装进列表中
        // */

        // imageFolderList = new ArrayList<Entity>();
        // for(int i=-0;i<10;i++){
        // Entity entity = new Entity(R.drawable.picture, false);
        // imageFolderList.add(entity);
        // }

        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());
        imageFolderList = helper.getImagesBucketList(false);
        //待移除
        mBitmap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.ic_cross_light);
    }

    /**
     * 初始化view视图
     */
    private void initView() {
        gridView = (GridView) findViewById(R.id.gridview);
        adapter = new AdapterImageFolder(ActivityPicFolders.this, imageFolderList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                /**
                 * 根据position参数，可以获得跟GridView的子View相绑定的实体类，然后根据它的isSelected状态，
                 * 来判断是否显示选中效果。 至于选中效果的规则，下面适配器的代码中会有说明
                 */
                // if(imageFolderList.get(position).isSelected()){
                // imageFolderList.get(position).setSelected(false);
                // }else{
                // imageFolderList.get(position).setSelected(true);
                // }
                /**
                 * 通知适配器，绑定的数据发生了改变，应当刷新视图
                 */
                // adapter.notifyDataSetChanged();
//                Intent intent = new Intent(ActivityPicFolders.this,
//                        ActivityImageGrid.class);
//                intent.putExtra(ActivityPicFolders.EXTRA_IMAGE_LIST,
//                        (Serializable) imageFolderList.get(position).imageList);
//                startActivity(intent);
//                finish();
            }

        });
    }
}
