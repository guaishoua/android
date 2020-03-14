package com.android.tacu.EventBus.model;

import java.io.Serializable;

public class TradeVisibleHintEvent implements Serializable {

    private boolean isVisibleToUser;

    public TradeVisibleHintEvent(boolean isVisibleToUser) {
        this.isVisibleToUser = isVisibleToUser;
    }

    public boolean isVisibleToUser() {
        return isVisibleToUser;
    }

    public void setVisibleToUser(boolean visibleToUser) {
        isVisibleToUser = visibleToUser;
    }
}
