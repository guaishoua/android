package com.android.tacu.module.auction.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

public class AuctionModel implements Serializable {

    /**
     * private Integer id;
     * private Integer allowUserType; //1.全部用户 2会员用户 3非会员数量
     * private Integer currencyId;
     * private BigDecimal num;          //总数量
     * private Integer total;       //总次数
     * private Integer already;         //竞拍次数
     * private BigDecimal price;     //标的价格/每个价格
     * private Integer userTotal;    //每人参与该标的的次数
     * private String auctionName;       //标的名称 中文
     * private String auctionNameEn;  //标的名称 英文
     *
     * private String imgOne;       //图片 中文1
     * private String imgTwo;       //图片 中文2
     * private String imgThree;      //图片 中文3
     * private String imgOneEn;      //图片 英文1
     * private String imgTwoEn;      //图片 英文2
     * private String imgThreeEn;    //图片 英文3
     *
     * private String introduce;     //标的介绍 中文
     * private String introduceEn;       //标的介绍 英文
     * private Integer status;          //标的状态   1上架 2下架  3已结束
     * private String endTime;          //结束时间
     * private String createTime;    //创建时间/开始时间
     */
    @SerializedName("id")
    public Integer id;
    @SerializedName("num")
    public Integer num;
    @SerializedName("total")
    public Integer total;
    @SerializedName("already")
    public Integer already;
    @SerializedName("price")
    public BigDecimal price;

    @SerializedName("auctionName")
    public String auctionName;
    @SerializedName("auctionNameEn")
    public String auctionNameEn;

    @SerializedName("imgOne")
    public String imgOne;
    @SerializedName("imgTwo")
    public String imgTwo;
    @SerializedName("imgThree")
    public String imgThree;

    @SerializedName("imgOneEn")
    public String imgOneEn;
    @SerializedName("imgTwoEn")
    public String imgTwoEn;
    @SerializedName("imgThreeEn")
    public String imgThreeEn;

    @SerializedName("introduce")
    public String introduce;
    @SerializedName("introduceEn")
    public String introduceEn;

    @SerializedName("endTime")
    public String endTime;
}
