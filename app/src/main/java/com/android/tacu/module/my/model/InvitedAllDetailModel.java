package com.android.tacu.module.my.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InvitedAllDetailModel implements Serializable {

    @SerializedName("invited_uid")
    public String invited_uid;
    @SerializedName("invited_name")
    public String invited_name;

    @SerializedName("total")
    public Long total;

    @SerializedName("list")
    public List<InvitedRecordModel> list = new ArrayList<>();

    public static class InvitedRecordModel implements Serializable {
        @SerializedName("name")
        public String name;
        @SerializedName("uid")
        public String uid;
        @SerializedName("createTime")
        public String createTime;
    }
}
