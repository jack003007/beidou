package com.ty.beidou.view;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;
import com.github.clans.fab.FloatingActionMenu;
import com.libs.view.LoadingDialog;
import com.libs.view.funnyitem.FlipShareView;
import com.libs.view.funnyitem.ShareItem;
import com.libs.view.utils.EmptyUtils;
import com.orhanobut.logger.Logger;
import com.ty.beidou.R;
import com.ty.beidou.common.BaseMvpFragment;
import com.ty.beidou.model.LocationBean;
import com.ty.beidou.presenter.FrgNowPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by ty on 2016/9/21.
 */
public class FrgNow extends BaseMvpFragment<IFrgNowView, FrgNowPresenter> implements LocationSource, AMapLocationListener, IFrgNowView {
    private static FrgNow f = null;
    /**
     * 一个显示地图的视图（View）。它负责从服务端获取地图数据。
     * 当屏幕焦点在这个视图上时，它将会捕捉键盘事件（如果手机配有实体键盘）及屏幕触控手势事件。
     */
    @BindView(R.id.mapView)
    MapView mMapView;
    //废弃
    @BindView(R.id.btn_fab)
    FloatingActionButton mBtnFab;
    //测试用
    @BindView(R.id.btn)
    Button btn;
    @BindView(R.id.fab_person)
    com.github.clans.fab.FloatingActionButton fabPerson;
    @BindView(R.id.fab_menu)
    FloatingActionMenu fabMenu;
    @BindView(R.id.fab_post)
    com.github.clans.fab.FloatingActionButton fabPost;

    private AMap mAMap;//定义AMap 地图对象的操作方法与接口

    private OnLocationChangedListener mOnLocationChangedListener;
    /**
     * 定位服务类。此类提供单次定位、持续定位、地理围栏、最后位置相关功能。
     */
    private AMapLocationClient mAMapLocationClient;
    /**
     * 定位参数设置，通过这个类可以对定位的相关参数进行设置
     * 在AMapLocationClient进行定位时需要这些参数
     */
    private AMapLocationClientOption mOptions;

    private View mRootView;

    private List<LocationBean> nearbyBeans = new ArrayList<>();

    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_now, null);

        unbinder = ButterKnife.bind(this, mRootView);
        mMapView.onCreate(savedInstanceState);
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        setAmap();
        //地图UI设置
        //UiSettings 一些选项设置响应事件
        UiSettings mUiSettings = mAMap.getUiSettings();
        setMapUiSettings(mUiSettings);
        //自定义小蓝点
        MyLocationStyle mLocationStyle = new MyLocationStyle();
        mLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_account_plus_black));//设置小蓝点图标
//        mLocationStyle.radiusFillColor(Color.argb(100,255,0,0));
        mAMap.setMyLocationStyle(mLocationStyle);
        return mRootView;
    }

    private void setAmap() {
        mAMap.setLocationSource(this);
        mAMap.setMyLocationEnabled(true);
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(18f));
        mAMap.moveCamera(CameraUpdateFactory.changeTilt(20f));//倾斜角度0~45f
    }

    private void setMapUiSettings(UiSettings mUiSettings) {
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
        mUiSettings.setMyLocationButtonEnabled(true);//设置定位可点击
        mUiSettings.setAllGesturesEnabled(true);//设置当前地图是否支持所有手势。

        mUiSettings.setZoomControlsEnabled(false);//设置了地图是否允许显示缩放按钮。
        mUiSettings.setScaleControlsEnabled(true);//设置比例尺功能是否可用
        mUiSettings.setRotateGesturesEnabled(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fabMenu.setClosedOnTouchOutside(true);
        fabPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(me, ActivityPutWork.class));
            }
        });
        fabPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPerson(fabPerson);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        presenter.getLocationsFromServer();
    }

    @Override
    public void onPause() {
        super.onPause();
        deactivate();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        mMapView.onDestroy();
        if (null != mAMapLocationClient) {
            mAMapLocationClient.onDestroy();
        }
    }

    //单例
    public static FrgNow newInstance() {
        if (f == null) {
            synchronized (FrgNow.class) {
                if (f == null) {
                    f = new FrgNow();
                }
            }
        }
        return f;
    }

    @Override
    public FrgNowPresenter initPresenter() {
        return new FrgNowPresenter();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }


    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mOnLocationChangedListener = null;
        if (mAMapLocationClient != null) {
            mAMapLocationClient.stopLocation();
            mAMapLocationClient.onDestroy();
        }
        mAMapLocationClient = null;
    }

    /**
     * 激活定位
     *
     * @param onLocationChangedListener
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mOnLocationChangedListener = onLocationChangedListener;
        if (mAMapLocationClient == null) {
            mAMapLocationClient = new AMapLocationClient(getActivity());
            mOptions = new AMapLocationClientOption();
            //设置定位监听
            mAMapLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mOptions.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位间隔 十分钟
            mOptions.setInterval(10 * 60 * 1000);
            //设置定位参数
            mAMapLocationClient.setLocationOption(mOptions);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mAMapLocationClient.startLocation();
        }
    }

    /**
     * 定位成功后回调函数
     * 可获取详细地址、经纬度、省市区等信息
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mOnLocationChangedListener != null && aMapLocation != null) {

            //模拟定位
//            presenter.putLocationToServer(MApplication.getInstance().getToken()
//                    , getJsonFromLBean(aMapLocation));

            if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
                //定位成功
                mOnLocationChangedListener.onLocationChanged(aMapLocation);
                //TODO 上传位置信息
                presenter.putLocationToServer(getJsonFromLBean(aMapLocation));
//                aMapLocation.getProvince();//获取省的名称
//                aMapLocation.getCity();//获取城市名称
//                aMapLocation.getDistrict();//获取区的名称
//
//                aMapLocation.getLongitude();//获取经度
//                aMapLocation.getLatitude();//获取纬度

//                aMapLocation.getAccuracy();//获取定位精度 单位:米
//                aMapLocation.getAdCode();//获取区域编码
//                aMapLocation.getAddress();//获取地址
//                aMapLocation.getAltitude();//获取海拔高度 单位：米
//                aMapLocation.getAoiName();//获取aoiName
//                aMapLocation.getBearing();//获取方向角 单位：度
//                aMapLocation.getCityCode();//获取城市编码
//                aMapLocation.getCountry();//获取国家名称
//                aMapLocation.getErrorCode();//获取错误码
//                aMapLocation.getErrorInfo();//获取错误信息
//                aMapLocation.getLocationDetail();//获取定位信息描述
//                aMapLocation.getLocationType();//获取定位结果来源
//                aMapLocation.getPoiName();//获取兴趣点名称
//                aMapLocation.getProvider();//获取定位提供者
//                aMapLocation.getSatellites();// 获取卫星数量, 仅在GPS定位时有效
//                aMapLocation.getSpeed();// 获取当前速度 单位：米/秒
//                aMapLocation.getStreet();// 获取街道名称
//                aMapLocation.getStreetNum();// 获取门牌号
//                aMapLocation.toStr();// 将定位结果转换成字符串
//                mAMap.moveCamera(CameraUpdateFactory.zoomTo(18));
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Logger.d("AmapErr:" + errText);
            }
        }
    }

    private String getJsonFromLBean(AMapLocation a) {

        LocationBean m = new LocationBean();
//        m.setAddress(a.getAddress());
//        m.setProvince(a.getProvince());
//        m.setCity(a.getCity());
//        m.setDistrict(a.getDistrict());
//        m.setLongitude(a.getLongitude());
//        m.setLatitude(a.getLatitude());
        m.setAddress("上海市静安区新马路207号");
        m.setProvince("上海市");
        m.setCity("上海市");
        m.setDistrict("静安区");
        m.setLongitude(121.265);
        m.setLatitude(31.356);

        return JSON.toJSONString(m);
    }


    @OnClick(R.id.btn)
    void hh() {
        showPerson(btn);
    }

    @OnClick(R.id.btn_fab)
    void showInfo() {
//        showDialog();
        showPerson(mBtnFab);
    }

    private void showPerson(View target) {

        int itemBgColor = 0xff43549C;
        int BgColor = 0x60000000;

        if (nearbyBeans != null && nearbyBeans.size() > 0) {
            List<ShareItem> items = new ArrayList<>();
            for (LocationBean m : nearbyBeans
                    ) {
                ShareItem mm = new ShareItem(
                        m.getProvince() + m.getCity()
                        , Color.WHITE
                        , itemBgColor
                        , BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                items.add(mm);

            }

            FlipShareView share = new FlipShareView.Builder(getActivity(), target)
                    .addItems(items)
                    .setBackgroundColor(BgColor)
                    .create();

            share.setOnFlipClickListener(new FlipShareView.OnFlipClickListener() {
                @Override
                public void onItemClick(int position) {
                    Toast.makeText(getActivity(), "position " + position + " is clicked.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void dismiss() {

                }
            });
        }
    }

    private void showDialog() {
        LoadingDialog fragment = LoadingDialog.newInstance();
        fragment.show(_mActivity.getFragmentManager(), "");
    }

    @Override
    public void success(List<LocationBean> t) {
        if (EmptyUtils.isNotEmpty(t)) {
            nearbyBeans.clear();
            nearbyBeans.addAll(t);
        }
    }

    @Override
    public void error(String msg) {
        Toast.makeText(me, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 位置提交成功
     *
     * @param msg
     */
    @Override
    public void postSuccess(String msg) {

    }


}
