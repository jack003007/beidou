package com.ty.beidou.test;

import android.content.Context;
import android.view.View;

import com.ty.beidou.model.FormStyleBean;

import java.util.HashMap;

/**
 * Created by ty on 2016/10/27.
 */

public class ChildStyle {
    Context context;

    public ChildStyle(Context context) {
        this.context = context;
    }

    /**
     * 设置每个表格的样式
     */
    protected View setAppearance(FormStyleBean bean) {
        return null;
    }

    /**
     * 获取该表格的值
     *
     * @return
     */
    protected HashMap<String,String> getValue() {
        return null;
    }

}
