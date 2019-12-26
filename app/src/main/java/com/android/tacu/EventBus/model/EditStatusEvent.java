package com.android.tacu.EventBus.model;

import java.io.Serializable;

/**
 * Created by jiazhen on 2018/8/17.
 */
public class EditStatusEvent implements Serializable{

    private int status;

    public EditStatusEvent(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
