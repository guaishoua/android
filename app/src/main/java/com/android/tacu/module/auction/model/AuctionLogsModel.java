package com.android.tacu.module.auction.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

public class AuctionLogsModel implements Serializable {

    /**
     * private Integer auctionId;           //标的id
     * private Long uid;              //投标uid
     * private BigDecimal targetCurrentPrice; //标的当前价格
     * private Integer targetCurrencyId;     //标的币种id
     * private Integer targetNum;          //标的数量
     * private Integer status;                //1.竞拍成功  2.已开奖 3.等待收货
     * private Integer sendGoodsStatus;      //发货信息  0未填写   1已填写
     * private String courierNo;           //快递单号
     * private String courierName;             //快递名称
     * private String uidObscured;             //脱敏uid
     * @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
     * private String createTime;          //创建时间
     * @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
     * private String updateTime;          //修改时间
     * private Integer rand;     //随机数 用户随机中奖用户
     * private Integer addressId;    //用户选择的地址id 对应base_address的id
     */
    @SerializedName("id")
    public Integer id;
    @SerializedName("auctionId")
    public Integer auctionId;
    @SerializedName("uid")
    public String uid;
    @SerializedName("targetNum")
    public String targetNum;
    @SerializedName("targetCurrentPrice")
    public BigDecimal targetCurrentPrice;
    @SerializedName("createTime")
    public String createTime;

    @SerializedName("status")
    public Integer status;//1.竞拍成功  2.已开奖 3.等待收货 4未中奖
}
