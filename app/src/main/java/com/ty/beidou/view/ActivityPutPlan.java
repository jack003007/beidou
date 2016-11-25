package com.ty.beidou.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.libs.view.utils.EmptyUtils;
import com.libs.view.utils.RegexUtils;
import com.ty.beidou.R;
import com.ty.beidou.common.BaseMvpActivity;
import com.ty.beidou.common.GeneralToolbar;
import com.ty.beidou.model.UserBean;
import com.ty.beidou.presenter.PutPlanPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityPutPlan extends BaseMvpActivity<IPutPlanView, PutPlanPresenter> implements IPutPlanView {


    GeneralToolbar mToolbar;
    @BindView(R.id.tv_group)
    TextView tvGroup;
    @BindView(R.id.et_person)
    EditText etPerson;
    @BindView(R.id.et_min)
    EditText etMin;
    @BindView(R.id.et_max)
    EditText etMax;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.ll_channel)
    LinearLayout llChannel;

    List<LinearLayout> listItems = new ArrayList<>();

    HashMap<String, String> leaders = new HashMap<>();//所有人员列表

    HashMap<String, String> leaderSelected = new HashMap<>();//已选人员列表

    Integer[] dialogSelected;//dialog标记


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_plan);
        ButterKnife.bind(this);
        setToolbar();
        showLoading();
        presenter.getLeaderFromServer("");
    }

    /**
     * 初始化Presenter的方法
     *
     * @return
     */
    @Override
    public PutPlanPresenter initPresenter() {
        return new PutPlanPresenter();
    }

    /**
     * 设置标题栏
     */
    private void setToolbar() {
        mToolbar = new GeneralToolbar(me);
        mToolbar.setCenterText(getString(R.string.str_work_plan));
        mToolbar.setRightSingleIcon(R.drawable.icon_plane_send, getString(R.string.str_submit), new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                HashMap<String, String> hash = checkB4Post();
                if (EmptyUtils.isEmpty(hash)) {
                    Toast.makeText(me, "请完善资料", Toast.LENGTH_SHORT).show();
                    return false;
                }
                presenter.putJsonToServer(JSON.toJSONString(hash));
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
     * 添加道口
     * 绑定清除事件
     */
    @OnClick(R.id.iv_add)
    void add() {
        LinearLayout item = (LinearLayout) getLayoutInflater().inflate(R.layout.item_channel, null);
        listItems.add(item);
        llChannel.addView(item);
        item.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItems.remove(v.getParent());
                llChannel.removeView((View) v.getParent());
            }
        });
    }

    /**
     * 选择组长
     */
    @OnClick(R.id.tv_group)
    void group() {
        if (EmptyUtils.isEmpty(leaders)) {
            Toast.makeText(me, "没有可供选择的人员" + leaders.size(), Toast.LENGTH_SHORT).show();
            return;
        }
        new MaterialDialog.Builder(me)
                .title("请选择组长")
                .items(leaders.keySet())
                .itemsCallbackMultiChoice(dialogSelected, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        leaderSelected.clear();
                        dialogSelected = which;
                        for (int i = 0; i < text.length; i++) {
                            leaderSelected.put((String) text[i], leaders.get(text[i]));
                        }
                        tvGroup.setText("已选择" + which.length + "组");
                        return false;
                    }
                })
                .positiveText("选择")
                .show();
    }


    /**
     * 表单处理
     * 如果填写错误 返回null
     *
     * @return
     */
    private HashMap<String, String> checkB4Post() {
        HashMap<String, String> hash = new HashMap<>();
        String personNum = etPerson.getText().toString();
        String min = etMin.getText().toString();
        String max = etMax.getText().toString();
        //组长和人员
        if (EmptyUtils.isEmpty(leaderSelected)
                || TextUtils.isEmpty(personNum)
                ) {
            Toast.makeText(me, "请完善所有表格项", Toast.LENGTH_SHORT).show();
            return null;
        }
        //道口号
        if (!isMatch(min) || !isMatch(max)) {
            Toast.makeText(me, "工作量填写不符合规范\n请按照默认提示文本格式填写", Toast.LENGTH_SHORT).show();
            return null;
        } else {
            Toast.makeText(me, "匹配", Toast.LENGTH_SHORT).show();
        }
        //处理作业里程
        String channels = "";
//        min = getChannelNo(min);
//        max = getChannelNo(max);
        channels = min + "~" + max;
        //子项
        for (LinearLayout ll : listItems) {
            EditText etMin = (EditText) ll.findViewById(R.id.et_min);
            EditText etMax = (EditText) ll.findViewById(R.id.et_max);
            String mmin = etMin.getText().toString();
            String mmax = etMax.getText().toString();
            if (!isMatch(mmin)) {
                Toast.makeText(this, mmin + "  不符合规范", Toast.LENGTH_SHORT).show();
                return null;
            }
            if (!isMatch(mmax)) {
                Toast.makeText(this, mmax + "  不符合规范", Toast.LENGTH_SHORT).show();
                return null;
            }
            channels += "," + mmin + "~" + mmax;
        }
        //人员
        String leader = "";
        boolean flag = false;//是否加前缀 ，
        for (String s : leaderSelected.values()) {
            if (!flag) {
                leader += s;
                flag = true;
            } else {
                leader += "," + s;
            }
        }


        hash.put("leader", leader);
        hash.put("person", personNum);
        hash.put("channel", channels);
        return hash;
    }

    /**
     * 道口号是否符合标准
     *
     * @param str
     * @return
     */
    private boolean isMatch(String str) {
        if (RegexUtils.isMatch("^\\d{1,4}[+-]\\d{1,3}$", str)) {
            return true;
        }
        return false;
    }

    /**
     * 处理道口号
     *
     * @param str
     * @return
     */

    private String getChannelNo(String str) {
        String[] r1 = str.split("\\-");
        String[] r2 = str.split("\\+");
        if (r1.length == 2) {
            return Long.parseLong(r1[0]) * 1000 - Long.parseLong(r1[1]) + "";
        } else if (r2.length == 2) {
            return Long.parseLong(r2[0]) * 1000 + Long.parseLong(r2[1]) + "";
        }
        return "";
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
     * 成功获取人员
     *
     * @param users
     */
    @Override
    public void netSuccess(List<UserBean> users) {
        hideLoading();
        //拼接组长名数组
        for (int i = 0; i < users.size(); i++) {
            leaders.put(users.get(i).getRealname(), users.get(i).getId());
        }
    }

    /**
     * 提交成功
     *
     * @param msg
     */
    @Override
    public void netSuccess(String msg) {
        hideLoading();
        Toast.makeText(me, msg, Toast.LENGTH_SHORT).show();
    }


}
