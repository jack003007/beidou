package com.ty.beidou.test;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.libs.view.utils.StringUtils;
import com.ty.beidou.R;
import com.ty.beidou.model.FormStyleBean;

import java.util.HashMap;

/**
 * Created by ty on 2016/10/27.
 */

public class SimpleStyle extends ChildStyle {

    private String field;
    private String Value;
    private EditText eDValue;

    public SimpleStyle(Context context) {
        super(context);
    }

    @Override
    protected View setAppearance(FormStyleBean bean) {
        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.item_label_with_edittext, null);
        LinearLayout llItem = (LinearLayout) view.findViewById(R.id.ll_item);
        TextView tvLabel = (TextView) view.findViewById(R.id.tv_label);
        eDValue = (EditText) view.findViewById(R.id.et_value);
        this.field = bean.getField();
        tvLabel.setText(bean.getLabel());
        if (TextUtils.isEmpty(bean.getValue())) {
            eDValue.setHint(bean.getHint());
        } else {
            eDValue.setText(bean.getValue());
        }
        return view;
    }


    @Override
    public HashMap getValue() {
        String v = eDValue.getText().toString() ;
        if (StringUtils.isEmpty(v)) {
            throw new RuntimeException("值为空");
        }
        HashMap hashMap = new HashMap();
        hashMap.put(field, v);
        return hashMap;
    }
}
