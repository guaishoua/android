package com.android.tacu.EventBus.model;

import java.io.Serializable;

/**
 * 用于传递marketListFragment的isVisibleToUser的值
 * Created by jiazhen on 2018/9/13.
 */
public class OTCOrderVisibleHintEvent implements Serializable {

    private boolean isVisibleToUser;

    public OTCOrderVisibleHintEvent(boolean isVisibleToUser) {
        this.isVisibleToUser = isVisibleToUser;
    }

    public boolean isVisibleToUser() {
        return isVisibleToUser;
    }

    public void setVisibleToUser(boolean visibleToUser) {
        isVisibleToUser = visibleToUser;
    }
}
