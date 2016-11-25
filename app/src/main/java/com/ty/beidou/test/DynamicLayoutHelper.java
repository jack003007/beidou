package com.ty.beidou.test;

/**
 * Created by ty on 2016/10/27.
 */
@Deprecated
public class DynamicLayoutHelper {
//    Context context;
//    /**
//     * 表单样式表 用于生成界面
//     */
//    List<View> views = new ArrayList<>();
//    /**
//     * 表单子项表  用于解析表格样式、获取数据
//     */
//    List<ChildStyle> children = new ArrayList<>();
//    /**
//     * 数据键值
//     */
//    HashMap<String,String> forms = new HashMap();
//
//
//    public DynamicLayoutHelper(Context context) {
//        this.context = context;
//    }
//
//    /**
//     * 生成表单样式
//     *
//     * @param beans
//     */
//    public void createLayout(List<FormStyleBean> beans) {
//        for (FormStyleBean b : beans) {
//            ChildStyle c = null;
//            if (b.getCategory().equals("单行文本框")) {
//                c = new SimpleStyle(context);
//            } else if (b.getCategory().equals("顶部通知栏")) {
//                c = new NoticeStyle(context);
//                children.add(c);
//                views.add(0, c.setAppearance(b));
//                continue;
//            } else if (b.getCategory().equals("底部通知栏")) {
//                c = new NoticeStyle(context);
//                children.add(c);
//                views.add(c.setAppearance(b));
//                continue;
//            }
//
//            children.add(c);
//            View v = c.setAppearance(b);
//            int index = views.size() == 0 ? views.size() : views.size() - 1;
//            views.add(index, v);
//        }
//    }
//
//    /**
//     * 获取表格样式表
//     *
//     * @return
//     */
//    public List<View> getViews() {
//        if (EmptyUtils.isEmpty(views)) {
//            throw new RuntimeException("Views is null");
//        }
//        return views;
//    }
//
//    /**
//     * 获取表单信息  字段+Value
//     *
//     * @return
//     */
//    public HashMap<String,String> getForms() {
//        for (ChildStyle c : children) {
//            forms.putAll(c.getValue());
//        }
//        return forms;
//    }
//
}
