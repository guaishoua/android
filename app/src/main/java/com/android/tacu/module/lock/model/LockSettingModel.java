package com.android.tacu.module.lock.model;

import java.io.Serializable;

/**
 * 保存手势和指纹密码的相关设置
 */
public class LockSettingModel implements Serializable {

    private String gesture; //手势密码的信息
    private boolean isFinger = false;//指纹密码是否开启 false:没开启

    public LockSettingModel() {
    }

    public LockSettingModel(String gesture, boolean isFinger) {
        this.gesture = gesture;
        this.isFinger = isFinger;
    }

    public String getGesture() {
        return gesture;
    }

    public void setGesture(String gesture) {
        this.gesture = gesture;
    }

    public boolean isFinger() {
        return isFinger;
    }

    public void setFinger(boolean finger) {
        isFinger = finger;
    }
}
