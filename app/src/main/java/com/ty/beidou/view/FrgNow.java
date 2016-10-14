package com.ty.beidou.view;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

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
import com.orhanobut.logger.Logger;
import com.ty.beidou.R;
import com.ty.beidou.common.BaseMvpFragment;
import com.ty.beidou.model.LocationBean;
import com.ty.beidou.presenter.FrgNowPresenter;
import com.ty.beidou.uiutils.FlipShareView;
import com.ty.beidou.uiutils.ShareItem;
import com.ty.beidou.utils.EmptyUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ty.beidou.R.id.ib_multiple;


/**
 * Created by ty on 2016/9/21.
 */
public class FrgNow extends BaseMvpFragment<IFrgNowView, FrgNowPresenter> implements LocationSource, AMapLocationListener, View.OnClickListener, IFrgNowView {
    private static FrgNow f = null;
    /**
     * 一个显示地图的视图（View）。它负责从服务端获取地图数据。
     * 当屏幕焦点在这个视图上时，它将会捕捉键盘事件（如果手机配有实体键盘）及屏幕触控手势事件。
     */
    private MapView mMapView;
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
    private AMapLocationClientOption mAMapLocationClientOption;

    private View mRootView;
    private ImageButton ibMultiple;

    private List<LocationBean> nearbyBeans = new ArrayList<>();


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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public FrgNowPresenter initPresenter() {
        return new FrgNowPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_now, null);
        mMapView = (MapView) mRootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        mAMap.setLocationSource(this);
        mAMap.setMyLocationEnabled(true);
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(18f));
        mAMap.moveCamera(CameraUpdateFactory.changeTilt(20f));//倾斜角度0~45f
        //地图UI设置
        //UiSettings 一些选项设置响应事件
        UiSettings mUiSettings = mAMap.getUiSettings();
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
        mUiSettings.setMyLocationButtonEnabled(true);//设置定位可点击
        mUiSettings.setAllGesturesEnabled(true);//设置当前地图是否支持所有手势。

        mUiSettings.setZoomControlsEnabled(false);//设置了地图是否允许显示缩放按钮。
        mUiSettings.setScaleControlsEnabled(true);//设置比例尺功能是否可用
        mUiSettings.setRotateGesturesEnabled(true);
        //自定义小蓝点
        MyLocationStyle mLocationStyle = new MyLocationStyle();
        mLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_account_plus_black));//设置小蓝点图标
//        mLocationStyle.radiusFillColor(Color.argb(100,255,0,0));
        mAMap.setMyLocationStyle(mLocationStyle);


        /*交互操作*/
        ibMultiple = (ImageButton) mRootView.findViewById(ib_multiple);
        ibMultiple.setOnClickListener(this);
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        presenter.onResume("");

    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        deactivate();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (null != mAMapLocationClient) {
            mAMapLocationClient.onDestroy();
        }
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
            mAMapLocationClientOption = new AMapLocationClientOption();
            //设置定位监听
            mAMapLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mAMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mAMapLocationClient.setLocationOption(mAMapLocationClientOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mAMapLocationClient.startLocation();
        }
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
     * 定位成功后回调函数
     * 可获取详细地址、经纬度、省市区等信息
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mOnLocationChangedListener != null && aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                mOnLocationChangedListener.onLocationChanged(aMapLocation);
//                mAMap.moveCamera(CameraUpdateFactory.zoomTo(18));
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Logger.e("AmapErr", errText);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case ib_multiple:
                if (nearbyBeans != null && nearbyBeans.size() > 0) {
                    List<ShareItem> items = new ArrayList<>();
                    for (LocationBean m : nearbyBeans
                            ) {
                        ShareItem mm = new ShareItem(
                                m.getProvince() + m.getCity()
                                , Color.WHITE
                                , 0xff43549C
                                , BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                        items.add(mm);

                    }
                    FlipShareView share = new FlipShareView.Builder(getActivity(), ibMultiple)
                            .addItems(items)
//                            .addItem(new ShareItem("Facebook", Color.WHITE, 0xff43549C, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)))
//                            .addItem(new ShareItem("Twitter", Color.WHITE, 0xff4999F0, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)))
//                            .addItem(new ShareItem("Google+aaaaaaaaaaaaa", Color.WHITE, 0xffD9392D, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)))
//                            .addItem(new ShareItem("http://www.wangyuwei.me", Color.WHITE, 0xff57708A))
                            .setBackgroundColor(0x60000000)
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
                break;
        }
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

    }
}
