package com.android.tacu.module.login.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jiazhen on 2017/5/22.
 */

public class LoginModel implements Serializable {

    @SerializedName("uid")
    public int uid;
    @SerializedName("token")
    public String token;
    @SerializedName("point")
    public String point;
    @SerializedName("uname")
    public String uname;
    @SerializedName("isShow")
    public int isShow;
}
