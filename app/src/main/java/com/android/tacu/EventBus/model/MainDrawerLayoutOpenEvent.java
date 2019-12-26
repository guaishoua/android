package com.android.tacu.EventBus.model;

import java.io.Serializable;

/**
 * 设置MainActivity的drawerlayout是否打开
 * Created by jiazhen on 2018/9/16.
 */
public class MainDrawerLayoutOpenEvent implements Serializable{

    private int mainSwitch;

    public MainDrawerLayoutOpenEvent(int mainSwitch) {
        this.mainSwitch = mainSwitch;
    }

    public int getMainSwitch() {
        return mainSwitch;
    }

    public void setMainSwitch(int mainSwitch) {
        this.mainSwitch = mainSwitch;
    }
}
