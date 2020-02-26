package com.android.tacu.module.otc.model;

import com.google.gson.annotations.*;

import java.io.Serializable;

public class OtcTradeModel implements Serializable {

    /**
     * private Long buyuid;
     * private Long selluid;
     * private Long merchantId; //商家uid
     * <p>
     * private Long orderId;     // 关联广告id
     * private BigDecimal num;     //数量
     * private BigDecimal price;   //价格
     * private BigDecimal amount;  //金额
     * private Integer currencyId;
     * private String currencyName;
     * private String orderNo;    //订单号
     * private Integer payType;   //支付类型 1 银行 2微信3支付宝
     * private Integer payId;
     * private Integer status; //1待确认  2 已确认待付款 3已付款待放币 4 仲裁 5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付 9 放币超时 10放币完成  12仲裁成功 13仲裁失败
     * private Integer timeOut; //
     * private String payInfo;  //付款凭证
     * <p>
     * private BigDecimal buyFee;          //手续费
     * private Integer buyType;            //手续费类型 1固定值 2比例
     * private BigDecimal sellFee;             //手续费
     * private Integer sellType;           //手续费类型 1固定值 2比例
     * <p>
     * private Date confirmEndTime;   确认倒计时
     * private Date payEndTime;        支付倒计时
     * private Date transCoinEndTime;   放币倒计时
     * <p>
     * private Date confirmTime;        //确认时间
     * private Date payTime;        //支付时间
     * private Date transCoinTime;          //放币时间
     * <p>
     * private Date createTime;         //创建时间
     * private Date updateTime;         //修改时间
     *
     * private Long arbitrateUid;  //仲裁uid
     *     private String arbitrateExp;  //仲裁说明
     *     private String arbitrateImg;  //仲裁图片
     *     private Long beArbitrateUid;  //被仲裁人uid
     *     private String beArbitrateExp;  //申诉说明
     *     private String beArbitrateImg;  //申诉图片
     *     private String arbitrateResults; //仲裁结果说明
     */

    @SerializedName("id")
    public String id;
    @SerializedName("buyuid")
    public Integer buyuid;
    @SerializedName("selluid")
    public Integer selluid;
    @SerializedName("merchantId")
    public Integer merchantId;
    @SerializedName("orderId")
    public String orderId;
    @SerializedName("num")
    public String num;
    @SerializedName("price")
    public String price;
    @SerializedName("amount")
    public String amount;
    @SerializedName("currencyId")
    public Integer currencyId;
    @SerializedName("currencyName")
    public String currencyName;
    @SerializedName("orderNo")
    public String orderNo;
    @SerializedName("payType")
    public Integer payType;
    @SerializedName("payId")
    public String payId;
    @SerializedName("status")
    public Integer status;
    @SerializedName("timeOut")
    public String timeOut;
    @SerializedName("payInfo")
    public String payInfo;
    @SerializedName("buyFee")
    public String buyFee;
    @SerializedName("buyType")
    public Integer buyType;
    @SerializedName("sellFee")
    public String sellFee;
    @SerializedName("sellType")
    public Integer sellType;
    @SerializedName("confirmEndTime")
    public String confirmEndTime;
    @SerializedName("payEndTime")
    public String payEndTime;
    @SerializedName("transCoinEndTime")
    public String transCoinEndTime;
    @SerializedName("confirmTime")
    public String confirmTime;
    @SerializedName("payTime")
    public String payTime;
    @SerializedName("transCoinTime")
    public String transCoinTime;
    @SerializedName("createTime")
    public String createTime;
    @SerializedName("updateTime")
    public String updateTime;
    @SerializedName("relationPay")
    public String relationPay;

    @SerializedName("arbitrateExp")
    public String arbitrateExp;
    @SerializedName("arbitrateImg")
    public String arbitrateImg;
    @SerializedName("arbitrateResults")
    public String arbitrateResults;



}
