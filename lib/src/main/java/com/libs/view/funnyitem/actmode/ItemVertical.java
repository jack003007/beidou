package com.libs.view.funnyitem.actmode;

/**
 * Created by ty on 2016/10/25.
 */


import android.graphics.Camera;

import com.libs.view.funnyitem.interfaces.ItemBehaviorListener;

/**
 * 垂直
 */
public class ItemVertical implements ItemBehaviorListener {


    @Override
    public Camera rotate(Camera camera, float dgree) {
        camera.rotateX(dgree);
        return camera;
    }

}