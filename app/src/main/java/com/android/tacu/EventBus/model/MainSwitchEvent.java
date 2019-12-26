package com.android.tacu.EventBus.model;

import java.io.Serializable;

/**
 * 切换MainActivity下面的fragment
 * Created by jiazhen on 2018/8/25.
 */
public class MainSwitchEvent implements Serializable{

    private int mainSwitch;

    public MainSwitchEvent(int mainSwitch) {
        this.mainSwitch = mainSwitch;
    }

    public int getMainSwitch() {
        return mainSwitch;
    }

    public void setMainSwitch(int mainSwitch) {
        this.mainSwitch = mainSwitch;
    }
}
