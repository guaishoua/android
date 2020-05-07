package com.android.tacu.module.my.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaohong on 2018/10/17.
 */

public class InvitedAllModel implements Serializable {
    @SerializedName("total")
    public int total;
    @SerializedName("list")
    public List<InvitedRecordModel> list = new ArrayList<>();
    @SerializedName("psoter")
    public List<PosterModel> psoter = new ArrayList();
    @SerializedName("rule")
    public String rule;
    @SerializedName("invitedId")
    public String invitedId;

    @SerializedName("all")
    public String all;//累计获得佣金
    @SerializedName("pre")
    public String pre;//冻结中的佣金

    public static class InvitedRecordModel implements Serializable {
        @SerializedName("uid")
        public String uid;
        @SerializedName("createTime")
        public String createTime;
        @SerializedName("source")
        public String source;
    }

    public static class PosterModel implements Serializable {
        @SerializedName("imageUrl")
        public String imageUrl;
        @SerializedName("posterId")
        public String posterId;
    }
}
