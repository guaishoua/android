package com.android.tacu.module.auth.model;

import com.google.gson.annotations.*;

import java.io.Serializable;

public class OtcSectionModel implements Serializable {

    /**
     * private Integer otcUserType;            //1 普通商户 2认证商户 3用户会员
     * <p>
     * private BigDecimal buyStartPriceSection;   //开始 购买价格区间
     * private BigDecimal buyEndPriceSection;    //结束 购买价格区间
     * <p>
     * private BigDecimal sellStartPriceSection;  //开始 出售价格区间
     * private BigDecimal sellEndPriceSection;       //结束 出售价格区间
     * <p>
     * private BigDecimal startTradeSection;     //开始 交易额度区间
     * private BigDecimal endTradeSection;          //结束 交易额度区间
     * <p>
     * private Integer otcUserTypeEndTimeDay;    //身份过期时间   该字段只针对otc_user_type=1
     * }
     */

    @SerializedName("id")
    public Integer id;
    @SerializedName("otcUserType")
    public Integer otcUserType;
    @SerializedName("buyStartPriceSection")
    public String buyStartPriceSection;
    @SerializedName("buyEndPriceSection")
    public String buyEndPriceSection;
    @SerializedName("sellStartPriceSection")
    public String sellStartPriceSection;
    @SerializedName("sellEndPriceSection")
    public String sellEndPriceSection;
    @SerializedName("startTradeSection")
    public String startTradeSection;
    @SerializedName("endTradeSection")
    public String endTradeSection;
    @SerializedName("otcUserTypeEndTimeDay")
    public String otcUserTypeEndTimeDay;
}
