package com.ty.beidou.view;

import com.ty.beidou.common.BaseView;

/**
 * Created by ty on 2016/9/12.
 */
public interface IPublishView extends BaseView {
    /**
     * 请求结果
     *
     * @param success
     * @param msg
     */
    void requestResult(boolean success, String msg);
}
