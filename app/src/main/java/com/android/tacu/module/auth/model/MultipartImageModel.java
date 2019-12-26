package com.android.tacu.module.auth.model;

import java.io.Serializable;

/**
 * Created by jiazhen on 2018/7/23.
 */

public class MultipartImageModel implements Serializable {

    //文件名
    private String imageName;
    //本地文件路径
    private String imageLocalFileName;

    public MultipartImageModel(String imageName, String imageLocalFileName) {
        this.imageName = imageName;
        this.imageLocalFileName = imageLocalFileName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageLocalFileName() {
        return imageLocalFileName;
    }

    public void setImageLocalFileName(String imageLocalFileName) {
        this.imageLocalFileName = imageLocalFileName;
    }
}
