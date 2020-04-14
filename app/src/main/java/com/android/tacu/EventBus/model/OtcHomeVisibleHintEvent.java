package com.android.tacu.EventBus.model;

import java.io.Serializable;

/**
 * 用于传递OtcHomeFragment的isVisibleToUser的值
 * Created by jiazhen on 2018/9/13.
 */
public class OtcHomeVisibleHintEvent implements Serializable {

    private boolean isVisibleToUser;

    public OtcHomeVisibleHintEvent(boolean isVisibleToUser) {
        this.isVisibleToUser = isVisibleToUser;
    }

    public boolean isVisibleToUser() {
        return isVisibleToUser;
    }

    public void setVisibleToUser(boolean visibleToUser) {
        isVisibleToUser = visibleToUser;
    }
}
