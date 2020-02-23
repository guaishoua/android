package com.android.tacu.EventBus.model;

import java.io.Serializable;

/**
 * 切换MainActivity下面的fragment
 * Created by jiazhen on 2018/8/25.
 */
public class MainBannerEvent implements Serializable {

    private boolean isNotifyBanner;

    public MainBannerEvent(boolean isNotifyBanner) {
        this.isNotifyBanner = isNotifyBanner;
    }

    public boolean isNotifyBanner() {
        return isNotifyBanner;
    }

    public void setNotifyBanner(boolean notifyBanner) {
        isNotifyBanner = notifyBanner;
    }
}
