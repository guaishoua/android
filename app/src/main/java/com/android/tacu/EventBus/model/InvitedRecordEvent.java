package com.android.tacu.EventBus.model;

import java.io.Serializable;

public class InvitedRecordEvent implements Serializable {

    private String invited_uid;
    private String invited_name;

    public InvitedRecordEvent(String invited_uid, String invited_name) {
        this.invited_uid = invited_uid;
        this.invited_name = invited_name;
    }

    public String getInvited_uid() {
        return invited_uid;
    }

    public void setInvited_uid(String invited_uid) {
        this.invited_uid = invited_uid;
    }

    public String getInvited_name() {
        return invited_name;
    }

    public void setInvited_name(String invited_name) {
        this.invited_name = invited_name;
    }
}
