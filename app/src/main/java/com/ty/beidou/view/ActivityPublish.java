package com.ty.beidou.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.orhanobut.logger.Logger;
import com.ty.beidou.R;
import com.ty.beidou.adapter.AdapterChosenGrid;
import com.ty.beidou.common.BaseMvpActivity;
import com.ty.beidou.common.Flags;
import com.ty.beidou.common.GeneralToolbar;
import com.ty.beidou.presenter.PublishPresenter;
import com.ty.beidou.test.ActivityImageViewer;
import com.libs.view.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityPublish extends BaseMvpActivity<IPublishView, PublishPresenter> implements IPublishView, AdapterView.OnItemClickListener, TakePhoto.TakeResultListener, InvokeListener {
    @BindView(R.id.gv_images)
    GridView gvImages;//图片列表
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.et_title)
    EditText etTitle;

    private AdapterChosenGrid adapter;

    private GeneralToolbar mTitleBar;

    private List<String> imagePaths = new ArrayList<>();
    /**
     * 获取照片相关类
     */
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);//获取照片相关类初始化
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectimg);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }


    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    /**
     * 初始化Presenter的方法
     *
     * @return
     */
    @Override
    public PublishPresenter initPresenter() {
        return new PublishPresenter();
    }

    public void initView() {
        gvImages = (GridView) findViewById(R.id.gv_images);
        adapter = new AdapterChosenGrid(this, imagePaths);
        gvImages.setAdapter(adapter);
        gvImages.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gvImages.setOnItemClickListener(this);

        mTitleBar = new GeneralToolbar(me);
        mTitleBar.setLeftIconAsBack();
        mTitleBar.setLeftOnclickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                me.finish();
            }
        });
        mTitleBar.setCenterText("发布动态");
        mTitleBar.setRightSingleIcon(R.drawable.icon_plane_send, "发送", new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String title = etTitle.getText().toString();
                String content = etContent.getText().toString();
//                ||EmptyUtils.isEmpty(imagePaths)
                if (StringUtils.isSpace(title) || StringUtils.isSpace(content) ) {
                    Toast.makeText(me, "请完善信息", Toast.LENGTH_SHORT).show();
                    return false;
                }
                presenter.onResume(title, content, imagePaths);
                return false;
            }
        });
    }


    /**
     * 弹出PopupWindow
     */
    public void showPopup() {
        View view = LayoutInflater.from(me).inflate(R.layout.item_popupwindows, null);
        final PopupWindow pop = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        //产生背景变暗效果
        setBackgroundTransparency(me, 0.4f);

        pop.setTouchable(true);
        pop.setFocusable(true);

        pop.setBackgroundDrawable(new BitmapDrawable(me.getResources(),(Bitmap) null));
        pop.setOutsideTouchable(true);
//        pop.update();
        pop.setAnimationStyle(R.style.anim_popupwindow_bottom);
        pop.showAtLocation(gvImages, Gravity.BOTTOM, 0, 0);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //恢复透明度
                setBackgroundTransparency(me, 1f);
            }
        });

        Button btnCamera = (Button) view.findViewById(R.id.btn_item_pop_camera);
        Button btnPhoto = (Button) view.findViewById(R.id.btn_item_pop_photo);
        Button btnCancel = (Button) view.findViewById(R.id.btn_item_pop_cancel);
        btnCamera.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
            }
        });
        btnPhoto.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                openPhotoAlbum();
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
            }
        });

    }

    /**
     * 设置Activity背景透明度
     *
     * @param ac     Activity
     * @param factor 0.0f完全透明
     */
    private void setBackgroundTransparency(Activity ac, float factor) {
        WindowManager.LayoutParams lp = ac.getWindow().getAttributes();
        lp.alpha = factor;
        ac.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ac.getWindow().setAttributes(lp);
    }

    /**
     * 配置
     */
    private void openPhotoAlbum() {
        TakePhoto mTakePhoto = getTakePhoto();
        //压缩
        int maxSize = 100 * 1024;
        int maxPixel = 800;
        CompressConfig config = new CompressConfig.Builder().setMaxSize(maxSize).setMaxPixel(maxPixel).create();
//        mTakePhoto.onEnableCompress(null, false);

        //张数限制
        mTakePhoto.onPickMultiple(Flags.Photo.MAX_NUM
                - Flags.Photo.CURRENT_NUM);

        //裁剪
//        CropOptions.Builder builder = new CropOptions.Builder();
//        builder.setOutputX(1000).setOutputY(1000);
//        builder.setWithOwnCrop(true);
//        mTakePhoto.onPickMultipleWithCrop(3,builder.create());
//        mTakePhoto.onPickFromDocuments();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Flags.Intent.CODE_VIEWER) {
            switch (resultCode) {
                case RESULT_OK:
                    if (data.hasExtra(Flags.Intent.IMAGE_PATHS)) {

                        ArrayList<String> returnPaths = data.getExtras().getStringArrayList(Flags.Intent.IMAGE_PATHS);
                        imagePaths.clear();
                        imagePaths.addAll(returnPaths);

                        Logger.d("返回时接收到的图片数量: " + returnPaths.size());
                        Logger.d("imagePaths.size: " + imagePaths.size());
                        adapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void takeSuccess(TResult result) {
        Flags.Photo.CURRENT_NUM += result.getImages().size();
        if (Flags.Photo.MAX_NUM > 1) {
            for (int i = 0; i < result.getImages().size(); i++) {
                if (!imagePaths.contains(result.getImages().get(i))) {
                    imagePaths.add(result.getImages().get(i).getPath());
                }
            }
        }
        Logger.d("当前图片数量：" + imagePaths.size());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        Logger.d(msg);
    }

    @Override
    public void takeCancel() {
        Logger.d("Cancel");

    }

    /**
     * 相册相关类
     *
     * @param invokeParam
     * @return
     */
    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    /**
     * 请求结果
     *
     * @param success
     * @param msg
     */
    @Override
    public void requestResult(boolean success, String msg) {
        if (success) {
            me.finish();
        } else {
            Toast.makeText(me, msg, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 显示Loading
     */
    @Override
    public void showLoading() {

    }

    /**
     * 隐藏Loading
     */
    @Override
    public void hideLoading() {

    }

    /**
     * 图片点击
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == Flags.Photo.CURRENT_NUM) {
            showPopup();
        } else {
            Intent i = new Intent(me, ActivityImageViewer.class);
            Bundle bundle = new Bundle();
            bundle.putInt(Flags.Intent.POSITION, position);
            bundle.putStringArrayList(Flags.Intent.IMAGE_PATHS, (ArrayList<String>) imagePaths);
            i.putExtras(bundle);
            startActivityForResult(i, Flags.Intent.CODE_VIEWER);
        }
    }

}
