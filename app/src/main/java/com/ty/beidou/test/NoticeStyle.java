package com.ty.beidou.test;

import android.content.Context;

/**
 * Created by ty on 2016/10/27.
 */

public class NoticeStyle extends ChildStyle {
    public NoticeStyle(Context context) {
        super(context);
    }

//    private String field;
//    private String Value;
//    private EditText eDValue;
//
//    public NoticeStyle(Context context) {
//        super(context);
//    }
//
//    @Override
//    protected View setAppearance(FormStyleBean bean) {
//
//        View view = LayoutInflater
//                .from(context)
//                .inflate(R.layout.item_notice, null);
//        LinearLayout llItem = (LinearLayout) view.findViewById(R.id.ll_item);
//        TextView tv = (TextView) view.findViewById(R.id.tv);
//        this.field = bean.getField();
//        tv.setText("**" + bean.getValue());
//        return view;
//    }
//
//
//    @Override
//    public HashMap getValue() {
//        HashMap hashMap = new HashMap();
//        hashMap.put(field,"blank");
//        return hashMap;
//    }
}
