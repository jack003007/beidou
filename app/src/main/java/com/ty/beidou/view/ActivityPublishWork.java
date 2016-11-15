package com.ty.beidou.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.LocationSource;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.libs.view.utils.EmptyUtils;
import com.libs.view.utils.RegexUtils;
import com.ty.beidou.R;
import com.ty.beidou.common.BaseMvpActivity;
import com.ty.beidou.common.GeneralToolbar;
import com.ty.beidou.model.FormStyleBean;
import com.ty.beidou.presenter.PublishWorkPresenter;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityPublishWork extends BaseMvpActivity<IPublishWorkView, PublishWorkPresenter> implements IPublishWorkView, LocationSource, AMapLocationListener, WeatherSearch.OnWeatherSearchListener {


    @BindView(R.id.ll_main)
    LinearLayout llMain;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_weather)
    TextView tvWeather;
    @BindView(R.id.et_weather)
    EditText etWeather;
    @BindView(R.id.et_person)
    EditText etPerson;
    @BindView(R.id.et_channel)
    EditText etChannel;
    @BindView(R.id.et_mission)
    EditText etMission;
    @BindView(R.id.et_total)
    EditText etTotal;
    @BindView(R.id.et_level)
    EditText etLevel;
    @BindView(R.id.et_min)
    EditText etMin;
    @BindView(R.id.et_max)
    EditText etMax;

    GeneralToolbar mToolbar;
    @BindView(R.id.et_address)
    EditText etAddress;
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

    private WeatherSearchQuery mquery;
    private WeatherSearch mweathersearch;

    private AMapLocation mAMapLocation;
    private LocalWeatherLive mWeatherLive;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_work);
        ButterKnife.bind(this);
        setToolbar();
//        presenter.getLayout();
        setLocation();
        searchliveweather("上海");
    }

    /**
     * 设置标题栏
     */
    private void setToolbar() {
        mToolbar = new GeneralToolbar(me);
        mToolbar.setCenterText(getString(R.string.str_work));
        mToolbar.setRightSingleIcon(R.drawable.icon_plane_send, getString(R.string.str_submit), new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                HashMap<String, String> hash = checkB4Post();
                if (EmptyUtils.isEmpty(hash)) {
                    return false;
                }
                presenter.putWorkToServer(JSON.toJSONString(hash));
                return false;
            }
        });
        mToolbar.setLeftIconAsBack();
        mToolbar.setLeftOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 表单处理
     * 如果填写错误 返回null
     *
     * @return
     */
    private HashMap<String, String> checkB4Post() {
        HashMap<String, String> hash = new HashMap<>();

        String address = "999";//etAddress.getText().toString();
        String weather = "999";//etWeather.getText().toString();
        String personNum = "999";//etPerson.getText().toString();
        String channel = "999";//etChannel.getText().toString();//可以带字母的编号
        String mission = "999";//etMission.getText().toString();
        String min = "999+000";//etMin.getText().toString();
        String max = "999+42";//etMax.getText().toString();
        String totalDev = "999";//etTotal.getText().toString();//全站仪
        String levelDev = "999";//etLevel.getText().toString();//水准仪

        if (TextUtils.isEmpty(personNum)
                || TextUtils.isEmpty(channel)
                || TextUtils.isEmpty(mission)
                || TextUtils.isEmpty(min)
                || TextUtils.isEmpty(max)
                || TextUtils.isEmpty(totalDev)
                || TextUtils.isEmpty(levelDev)
                || TextUtils.isEmpty(address)
                || TextUtils.isEmpty(weather)
                ) {
            Toast.makeText(me, "请完善所有表格项", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (!RegexUtils.isMatch("^\\d{1,4}[+-]\\d{1,3}$", min) ||
                !RegexUtils.isMatch("^\\d{1,4}[+-]\\d{1,3}$", max)) {
            Toast.makeText(me, "工作量填写不符合规范\n请按照默认提示文本格式填写", Toast.LENGTH_SHORT).show();
            return null;
        } else {
            Toast.makeText(me, "匹配", Toast.LENGTH_SHORT).show();
        }
        //处理作业里程
        String[] a = min.split("\\-");
        String[] b = min.split("\\+");
        String[] c = max.split("\\-");
        String[] d = max.split("\\+");
        if (a.length == 2) {
            min = Long.parseLong(a[0]) * 1000 - Long.parseLong(a[1]) + "";
        }
        if (b.length == 2) {
            min = Long.parseLong(b[0]) * 1000 + Long.parseLong(b[1]) + "";
        }
        if (c.length == 2) {
            max = Long.parseLong(c[0]) * 1000 - Long.parseLong(c[1]) + "";
        }
        if (d.length == 2) {
            max = Long.parseLong(d[0]) * 1000 + Long.parseLong(d[1]) + "";
        }

        hash.put("person", personNum);
        hash.put("channel", channel);
        hash.put("mission", mission);
        hash.put("min", min);
        hash.put("max", max);
        hash.put("totaldev", totalDev);
        hash.put("leveldev", levelDev);
        //天气
        if (!EmptyUtils.isEmpty(mWeatherLive)) {
            hash.put("weather", mWeatherLive.getWeather());
            hash.put("temperature", mWeatherLive.getTemperature());
            hash.put("wind", mWeatherLive.getWindDirection() + mWeatherLive.getWindPower() + "级");
        }
        //定位
        if (!EmptyUtils.isEmpty(mAMapLocation)) {
            hash.put("longitude", mAMapLocation.getLongitude() + "");
            hash.put("latitude", mAMapLocation.getLatitude() + "");
            hash.put("address", mAMapLocation.getAddress());
            hash.put("province", mAMapLocation.getProvince());
            hash.put("city", mAMapLocation.getCity());
            hash.put("district", mAMapLocation.getDistrict());
        }
        return hash;
    }

    /**
     * 根据地名查询天气
     *
     * @param cityname
     */
    private void searchliveweather(String cityname) {
        mquery = new WeatherSearchQuery(cityname, WeatherSearchQuery.WEATHER_TYPE_LIVE);//检索参数为城市和天气类型，实时天气为1、天气预报为2
        mweathersearch = new WeatherSearch(this);
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索
    }

    /**
     * 设置定位参数并开启定位
     */
    private void setLocation() {
        AMapLocationClient locationClient = new AMapLocationClient(me);
        AMapLocationClientOption locationOption = new AMapLocationClientOption();
        // 设置定位监听
        locationClient.setLocationListener(this);
        // 设置定位模式为低功耗模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置为单次定位
        locationOption.setOnceLocation(false);
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        // 设置定位间隔
        locationOption.setInterval(2000);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 设置定位监听回调
     */
    @Override
    public void onLocationChanged(AMapLocation location) {
        if (null != location && location.getErrorCode() == 0) {
            mAMapLocation = location;
            etAddress.setText(location.getAddress());
            etAddress.setEnabled(false);
            Log.v("location", "经度:" + location.getLongitude());
            Log.v("location", "纬度:" + location.getLatitude());
            Log.v("location", "国家:" + location.getCountry());
            Log.v("location", "省:" + location.getProvince());
            Log.v("location", "市:" + location.getCity());
            Log.v("location", "地址:" + location.getAddress());
        } else {
            etAddress.setHint("定位失败,请自行补充");
            //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
            Log.e("AmapError", "location Error, ErrCode:"
                    + location.getErrorCode() + ", errInfo:"
                    + location.getErrorInfo());
        }
    }


    @Override
    public PublishWorkPresenter initPresenter() {
        return new PublishWorkPresenter();
    }


    //TODO 暂时不用自动布局
    @Override
    public void requestSuccess(final List<FormStyleBean> beans) {

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                helper.createLayout(beans);
//                res = helper.getViews();
//                handler.obtainMessage(1).sendToTarget();
//            }
//        }).start();
    }

    /**
     * 网络链接异常
     *
     * @param ResourceId
     */
    @Override
    public void netError(int ResourceId) {
        Toast.makeText(me, getResources().getString(ResourceId), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void netMsg(String msg) {
        Toast.makeText(me, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mOnLocationChangedListener = onLocationChangedListener;
        if (mAMapLocationClient == null) {
            mAMapLocationClient = new AMapLocationClient(me);
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
     * 查询实时天气的回调函数
     *
     * @param localWeatherLiveResult
     * @param i
     */
    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
        if (i == 1000) {
            if (localWeatherLiveResult != null && localWeatherLiveResult.getLiveResult() != null) {
                LocalWeatherLive live = localWeatherLiveResult.getLiveResult();
                mWeatherLive = live;
                String res = live.getWeather()
                        + " " + live.getTemperature() + "°C"
                        + " " + live.getWindDirection() + "风" + live.getWindPower() + "级";
                etWeather.setText(res);
                return;
            }
        }
        etWeather.setHint("天气信息获取失败，请自行填写");
    }

    /**
     * 查询未来几天天气的回调函数
     *
     * @param localWeatherForecastResult
     * @param i
     */
    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult
                                                  localWeatherForecastResult, int i) {

    }

}
