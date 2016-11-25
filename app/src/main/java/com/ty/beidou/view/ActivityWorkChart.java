package com.ty.beidou.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.libs.view.utils.EmptyUtils;
import com.ty.beidou.R;
import com.ty.beidou.common.BaseMvpActivity;
import com.ty.beidou.common.GeneralToolbar;
import com.ty.beidou.model.ChartBean;
import com.ty.beidou.presenter.WorkChartPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;
import lecho.lib.hellocharts.view.PreviewColumnChartView;

public class ActivityWorkChart extends BaseMvpActivity<IWorkChartView, WorkChartPresenter> implements IWorkChartView {


    GeneralToolbar mToolbar;

    ColumnChartView mColumnChartView;
    PreviewColumnChartView mPreColumnChartView;
    @BindView(R.id.ll_chart)
    LinearLayout llChart;


    /*========== 数据相关 ==========*/
    private ColumnChartData mChartData;
    private ColumnChartData mPreChartData;

    private final int TAG_PERSON = 0x001;
    private int TAG = TAG_PERSON;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_chart);
        ButterKnife.bind(this);
        setToolbar();
//        initData();
//        showLoading();
//        presenter.getLeaderFromServer("");
    }

    /**
     * 初始化Presenter的方法
     *
     * @return
     */
    @Override
    public WorkChartPresenter initPresenter() {
        return new WorkChartPresenter();
    }

    /**
     * 设置选区和预览区的所有数据
     */
    private void setAllDatas() {
        int numSubcolumns = 1;        //单子列
        int numColumns = 12;          //总列数

        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;

        //循环给每个列设置不同的随机值
        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<>();
            for (int j = 0; j < numSubcolumns; ++j) {
                values.add(new SubcolumnValue((float) Math.random() * 50f + 5, ChartUtils.pickColor())
                        .setLabel("沪宁"));
            }
            Column column = new Column(values);
            column.setHasLabels(true);
            columns.add(column);
        }

        //设置一些其他属性
        mChartData = new ColumnChartData(columns);
        mChartData.setAxisXBottom(new Axis());
        mChartData.setAxisYLeft(new Axis().setHasLines(true));

        //预览区数据相同
        mPreChartData = new ColumnChartData(mChartData);

        //所有的预览区列都变成灰色 好看一点
        for (Column column : mPreChartData.getColumns()) {
            for (SubcolumnValue value : column.getValues()) {
                value.setColor(ChartUtils.DEFAULT_DARKEN_COLOR);
            }
        }
    }

    public void initData() {
        setAllDatas();                                              //设置所有的数据

        mColumnChartView.setColumnChartData(mChartData);            //设置选中区内容
        mPreColumnChartView.setColumnChartData(mPreChartData);      //设置预览区内容

        mColumnChartView.setZoomEnabled(false);                     //禁用缩放
        mColumnChartView.setScrollEnabled(false);                   //禁用滚动


        previewX();                                                 //初识只能X方向滑动
    }

    /**
     * 只在X方向预览
     */
    private void previewX() {
        /*========== 类似于 PreviewLine Chart ==========*/
        Viewport tempViewport = new Viewport(mColumnChartView.getMaximumViewport());
        float dx = tempViewport.width() / 4;
        tempViewport.inset(dx, 0);
        mPreColumnChartView.setCurrentViewportWithAnimation(tempViewport);
        mPreColumnChartView.setZoomType(ZoomType.HORIZONTAL);
        mPreColumnChartView.setViewportChangeListener(new ViewportListener());
    }


    /**
     * 设置标题栏
     */
    private void setToolbar() {
        mToolbar = new GeneralToolbar(me);
        mToolbar.setCenterText("统计");
        mToolbar.inflateMenu(R.menu.menu_work_chart);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.person:
                        showLoading();
                        TAG = TAG_PERSON;
                        presenter.getPersonFromServer("");

                }
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

    //去除Html.fromHtml的警告
    @SuppressWarnings("deprecation")
    private void lookPerson(List<ChartBean> beans) {
        PieChartView pie = new PieChartView(me);
        PieChartData pieData;

        /*===== 随机设置每块的颜色和数据 =====*/
        List<SliceValue> values = new ArrayList<>();
        float total = 0f;
        for (int i = 0; i < beans.size(); ++i) {
            SliceValue sliceValue = new SliceValue(
                    beans.get(i).getPerson(), ChartUtils.pickColor());
            total += beans.get(i).getPerson();
            sliceValue.setLabel(beans.get(i).getPname()+":"+(int)beans.get(i).getPerson()+" 人");
            values.add(sliceValue);
        }
                        /*===== 设置相关属性 类似Line Chart =====*/
        pieData = new PieChartData(values);
        pieData.setHasLabels(true);
        pieData.setHasLabelsOutside(false);
        pieData.setSlicesSpacing(5);
        pie.setPieChartData(pieData);

        String info = "作业时间:" +
                "<em><font color='#ff0000'>%1$tF 至 %2$tF</font></em><br>" +
                "施工线路:" +
                "<big><font color='#ff0000'>%3$d</font></big> 条<br>" +
                "人员合计:" +
                "<big><font color='#ff0000'>%4$d</font></big> 人<br>";
        info = String.format(info, System.currentTimeMillis(),System.currentTimeMillis(), beans.size(),(int)total);
        CardView card = (CardView) LayoutInflater.from(me).inflate(R.layout.item_card_notice_board, null);
        TextView tvContent = (TextView) card.findViewById(R.id.tv_content);
        tvContent.setText(Html.fromHtml(info));
        llChart.removeAllViews();
        llChart.addView(card);
        llChart.addView(pie);
    }


    /**
     * 成功
     *
     * @param beans
     */
    @Override
    public void netSuccess(List<ChartBean> beans) {
        hideLoading();
        switch (TAG) {
            case TAG_PERSON:
                if (EmptyUtils.isNotEmpty(beans)) {
                    lookPerson(beans);
                }
                break;
        }
    }

    /**
     * 网络链接异常
     *
     * @param ResourceId
     */
    @Override
    public void netError(int ResourceId) {
        hideLoading();
        Toast.makeText(me, getResources().getString(ResourceId), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void netMsg(String msg) {
        hideLoading();
        Toast.makeText(me, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 预览区滑动监听
     */
    private class ViewportListener implements ViewportChangeListener {
        @Override
        public void onViewportChanged(Viewport newViewport) {
            // 这里切记不要使用动画，因为预览图是不需要动画的
            mColumnChartView.setCurrentViewport(newViewport);         //直接设置当前窗口图表
        }
    }

}
