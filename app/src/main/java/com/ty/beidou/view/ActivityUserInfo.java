package com.ty.beidou.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.libs.view.utils.SDCardUtils;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;
import com.ty.beidou.R;
import com.ty.beidou.common.BaseMvpActivity;
import com.ty.beidou.common.GeneralToolbar;
import com.ty.beidou.common.MApplication;
import com.ty.beidou.common.API;
import com.ty.beidou.model.UserBean;
import com.ty.beidou.presenter.UserInfoPresenter;
import com.ty.beidou.widget.PhotoDialog;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.ty.beidou.R.id.sp_company;
import static com.ty.beidou.R.id.sp_identity;


/**
 * Created by ty on 2016/9/12.
 */
public class ActivityUserInfo extends BaseMvpActivity<IUserInfoView, UserInfoPresenter> implements IUserInfoView, TakePhoto.TakeResultListener, InvokeListener {


    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.iv_head)
    CircleImageView ivHead;
    @BindView(sp_company)
    MaterialSpinner spCompany;
    @BindView(sp_identity)
    MaterialSpinner spIdentity;


    private GeneralToolbar mToolbar;
    private HashMap<String, String> hashCompany = new HashMap<>();
    private HashMap<String, String> hashIdentity = new HashMap<>();

    private String companyId = "";
    private String identityId = "";
    private UserBean user;
    /**
     * 获取照片相关类
     */
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);//获取照片相关类初始化
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        mToolbar = new GeneralToolbar(this);
        setToolbar();
        setView();
        showLoading();
        presenter.getCompanyFromServer();
        presenter.getIdentityFromServer();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    /**
     * 填充界面数据，设置点击事件
     */
    private void setView() {
        user = MApplication.getInstance().getUser();
        Picasso.with(me)
                .load(API.BASE + user.getHead_thumb())
                .fit()
                .into(ivHead);
        etName.setText(user.getRealname());

        spCompany.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if (hashCompany.size() > 0) {
                    companyId = hashCompany.get(item);
                }
//                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_SHORT).show();
            }
        });
        spIdentity.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (hashIdentity.size() > 0) {
                    identityId = hashIdentity.get(item);
                }
            }
        });
    }

    @OnClick(R.id.iv_head)
    void click_head() {
        Bundle bundle = new Bundle();
        bundle.putString("url", API.BASE + user.getHead_thumb());
        final PhotoDialog dialog = PhotoDialog.newInstance(bundle);
        dialog.setPhotoClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePhoto mTakePhoto = getTakePhoto();
                //压缩
                int maxSize = 200 * 1024;
                int maxPixel = 1200;
                CompressConfig config = new CompressConfig.Builder().setMaxSize(maxSize).setMaxPixel(maxPixel).create();
                mTakePhoto.onEnableCompress(config, true);
                mTakePhoto.onPickFromGallery();
                dialog.dismiss();
            }
        });
        dialog.setCameraClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePhoto mTakePhoto = getTakePhoto();
                Logger.d(Uri.parse(SDCardUtils.getSDCardPath() + "/beidou/").toString());
                mTakePhoto.onPickFromCapture(Uri.parse(SDCardUtils.getSDCardPath() + "/beidou/"));
                dialog.dismiss();
            }
        });
        dialog.show(getFragmentManager(), "photodialog");
    }


    /**
     * 配置Toolbar
     */
    private void setToolbar() {
        mToolbar.setLeftIconAsBack();
        mToolbar.setLeftOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                me.finish();
            }
        });
        mToolbar.setCenterText("个人信息");
        mToolbar.setRightSingleIcon(R.drawable.icon_check, "提交", new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                putSettingToServer();
                return false;
            }
        });
    }

    /**
     * 提交配置给服务器
     */
    private void putSettingToServer() {
        HashMap<String, String> hash = new HashMap<>();
        String name = etName.getText().toString();
        if (!TextUtils.isEmpty(name) && !name.equals(user.getRealname())) {
            hash.put("realname", name);
        }
        if (!TextUtils.isEmpty(companyId)
                && !companyId.equals(hashCompany.get(user.getCompany_id()))) {
            hash.put("company_id", companyId);
        }
        if (!TextUtils.isEmpty(identityId)
                && !identityId.equals(hashCompany.get(user.getGid()))) {
            hash.put("gid", identityId);
        }
        if (hash.isEmpty()) {
            Toast.makeText(me, "数据好像没有变更", Toast.LENGTH_SHORT).show();
            return;
        }
        String json = JSON.toJSONString(hash);
        Logger.d("开始提交数据：" + json);
        showLoading();
        presenter.putSetting(json, "");
    }

    /**
     * 初始化Presenter的方法
     *
     * @return
     */
    @Override
    public UserInfoPresenter initPresenter() {
        return new UserInfoPresenter();
    }

    /**
     * 提交成功
     *
     * @param msg
     */
    @Override
    public void putSuccess(String msg) {
        hideLoading();
        user = MApplication.getInstance().getUser();
        Picasso.with(me).load(API.BASE + user.getHead_thumb()).fit().into(ivHead);
        Toast.makeText(me, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 提交失败
     *
     * @param msg
     */
    @Override
    public void putError(String msg) {
        hideLoading();
        Toast.makeText(me, msg, Toast.LENGTH_SHORT).show();
    }



    /**
     * 获取公司名称列表
     *
     * @param hash
     */
    @Override
    public void getCompanySuccess(HashMap<String, String> hash) {
        hideLoading();
        hashCompany.clear();
        hashCompany.putAll(hash);
        spCompany.setItems(hashCompany.keySet().toArray());
        //定位到现有值
        String company = MApplication.getInstance().getUser().getCompany_name();
        for (int i = 0; i < hashCompany.size(); i++) {
            if (company.equals(spCompany.getItems().get(i))) {
                spCompany.setSelectedIndex(i);
            }
        }
        //如果单位已定，不可更改
        if (!user.getCompany_id().equals("1")) {
            spCompany.setEnabled(false);
        }
    }

    /**
     * 获取身份列表
     *
     * @param hash
     */

    @Override
    public void getGroupSuccess(HashMap<String, String> hash) {
        hideLoading();
        hashIdentity.clear();
        hashIdentity.putAll(hash);
        //测量员和测量组长可以在单位内部更替
        String iid = user.getGid();
        if (!iid.equals("1")) {
            hashIdentity.remove("未认证");
        }
        if (!iid.equals("2")) {
            hashIdentity.remove("认证中");
        }
        if (iid.equals("2") || iid.equals("5") || iid.equals("6")) {
            spIdentity.setEnabled(false);
        }
        spIdentity.setItems(hashIdentity.keySet().toArray());
        //定位到现有值
        String identity = user.getGid();
        for (int i = 0; i < hashIdentity.size(); i++) {
            if (identity.equals(spIdentity.getItems().get(i))) {
                spIdentity.setSelectedIndex(i);
            }
        }


    }


    @Override
    public void getError(String msg) {
        hideLoading();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*------------拍照、相册相关方法--------------*/
    @Override
    public void takeSuccess(TResult result) {
        String path = result.getImage().getPath();
        Logger.d(path);
        if (!TextUtils.isEmpty(path)) {
            //上传头像
            presenter.putSetting("", path);
        }

    }

    @Override
    public void takeFail(TResult result, String msg) {
        Logger.d(msg);
    }

    @Override
    public void takeCancel() {
        Logger.d("取消");
    }


    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //以下代码为处理Android6.0、7.0动态权限所需
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

    /*------------拍照、相册相关方法--------------*/
    /**
     * 网络故障
     *
     * @param resourceId
     */
    @Override
    public void netError(int resourceId) {
        hideLoading();
        Toast.makeText(me, getResources().getString(resourceId), Toast.LENGTH_SHORT).show();
    }
}
