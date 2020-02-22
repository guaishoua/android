package com.android.tacu.module.auth.model;

import com.google.gson.annotations.*;

import java.io.Serializable;

public class SelectC2cSection implements Serializable {

    /**
     * private Integer buyOrSell;        //1购买 2出售
     * private BigDecimal startNumSection;    //开始 数量区间
     * private BigDecimal endNumSection;  //结束 数量区间
     * private BigDecimal price;        //区间价格
     * private String updateTime;       //修改时间
     */

    @SerializedName("id")
    public Integer id;
    @SerializedName("buyOrSell")
    public String buyOrSell;
    @SerializedName("startNumSection")
    public String startNumSection;
    @SerializedName("endNumSection")
    public String endNumSection;
    @SerializedName("price")
    public String price;
    @SerializedName("updateTime")
    public String updateTime;
}
