package com.ty.beidou.common;

/**
 * Created by ty on 2016/10/6.
 */

public class Flags {

    /**
     * 图片
     */
    public static class Photo {

        /**
         * 限定的图片数量
         */
        public static int MAX_NUM = 10;
        /**
         * 当前图片数量
         */
        public static int CURRENT_NUM = 0;
    }

    /**
     * Activity跳转
     */
    public static class Intent {
        /**
         * 查看图片
         * requestCode
         */
        public static int CODE_VIEWER = 0x5001;


        /**
         * Position
         */
        public static String POSITION = "Postion";
        /**
         * ImagePaths
         */
        public static String IMAGE_PATHS = "imagePaths";

    }

}
