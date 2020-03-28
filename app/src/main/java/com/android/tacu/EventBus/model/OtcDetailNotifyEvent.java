package com.android.tacu.EventBus.model;

import java.io.Serializable;

/**
 * 用于传递marketListFragment的isVisibleToUser的值
 * Created by jiazhen on 2018/9/13.
 */
public class OtcDetailNotifyEvent implements Serializable {

    public OtcDetailNotifyEvent(boolean isNotify) {
        this.isNotify = isNotify;
    }

    public boolean isNotify() {
        return isNotify;
    }

    public void setNotify(boolean notify) {
        isNotify = notify;
    }

    private boolean isNotify;


}
