package com.android.tacu.EventBus.model;

import java.io.Serializable;

public class VipBuyEvent implements Serializable {

    private boolean isNotify;

    public VipBuyEvent(boolean isNotify) {
        this.isNotify = isNotify;
    }

    public boolean isNotify() {
        return isNotify;
    }

    public void setNotify(boolean notify) {
        isNotify = notify;
    }
}
