package com.ty.beidou.view;

import com.ty.beidou.common.BaseView;
import com.ty.beidou.model.UserBean;

import java.util.List;

/**
 * Created by ty on 2016/9/12.
 */
public interface IPutPlanView extends BaseView {

    /**
     * 网络链接异常
     *
     * @param ResourceId
     */
    void netError(int ResourceId);

    /**
     * 提示信息
     *
     * @param msg
     */
    void netMsg(String msg);

    /**
     * 成功获取人员
     *
     * @param users
     */
    void netSuccess(List<UserBean> users);
    /**
     * 提交成功
     *
     * @param msg
     */
    void netSuccess(String msg);
}
