package com.ty.beidou.model;

import java.io.Serializable;

/**
 * Created by ty on 2016/9/27.
 */

public class ImageBean implements Serializable {
    public String imageId;
    public String thumbnailPath;
    public String imagePath;
    public boolean isSelected = false;
}
