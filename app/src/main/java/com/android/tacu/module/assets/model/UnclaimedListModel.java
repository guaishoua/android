package com.android.tacu.module.assets.model;

import com.google.gson.annotations.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiazhen on 2018/12/26.
 */
public class UnclaimedListModel implements Serializable {

    @SerializedName("total")
    public long total;
    @SerializedName("rewardAll")
    public String rewardAll;
    @SerializedName("list")
    public List<UnclaimedModel> list = new ArrayList<>();
}
