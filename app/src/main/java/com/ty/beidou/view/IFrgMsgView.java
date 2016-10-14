package com.ty.beidou.view;

import com.ty.beidou.model.MsgBean;

import java.util.List;

/**
 * Created by ty on 2016/9/12.
 */
public interface IFrgMsgView {


    /**
     * @param list
     */
    void success(List<MsgBean> list);

    /**
     * @param msg
     */
    void error(String msg);
}
