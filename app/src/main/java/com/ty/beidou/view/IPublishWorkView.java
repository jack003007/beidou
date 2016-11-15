package com.ty.beidou.view;

import com.ty.beidou.common.BaseView;
import com.ty.beidou.model.FormStyleBean;

import java.util.List;

/**
 * Created by ty on 2016/9/12.
 */
public interface IPublishWorkView extends BaseView {
    /**
     * 成功
     *
     * @param beans
     */
    void requestSuccess(List<FormStyleBean> beans);

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
}
