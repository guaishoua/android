package com.android.tacu.module.otc.model;

import com.google.gson.annotations.*;

import java.io.Serializable;

public class OtcMarketOrderModel implements Serializable {

    /**
     * private String headImg;  //头像
     * private String nickname;  //昵称
     * private String secondName;
     * private Integer isValidateEmail;
     * private Integer isAuthSenior;
     * private Integer isValidatePhone;
     * private Integer applyMerchantStatus;
     * <p>
     * <p>
     * private BigDecimal bondMoney;  //保证金总额
     * <p>
     * private Integer total;       //总成交数
     * private Integer buy;         //总成交数
     * private Integer sell;        //总成交数
     * private Integer success;
     * private Integer fail;
     * <p>
     * private Integer disputeTotal;   //纠纷量
     * private Integer disputeBySelf;   //仲裁量
     * private Integer disputeByOther;  //被仲裁量
     * private Integer otherDispute;     //申诉量
     * private Integer winDispute;     //胜诉量
     * <p>
     * <p>
     * private Integer buyorsell; //1.买 2.卖
     * private Integer currencyId; //
     * private String currencyName;
     * private Integer money; // 货币 1.人民币
     * private Integer timeOut; // 操作时限
     * private Integer payByCard; //1.银行卡 0 不支持
     * private Integer payWechat; //1.微信 0 不支持
     * private Integer payAlipay; //1.支付宝 0 不支持
     * <p>
     * private BigDecimal price; // 单价
     * private BigDecimal num;   //数量
     * private BigDecimal amount; //总额
     * private BigDecimal remainAmount; //剩余待成交数量
     * private BigDecimal lockAmount;   //已匹配待成交数量
     * private BigDecimal tradeAmount;   // 已成交数量
     * private BigDecimal lowLimit; //最低限额
     * private BigDecimal highLimit; //最高限额
     * private BigDecimal fee; // 手续费
     * private Integer feeType; // 手续费类型
     * private String explaininfo; // 说明信息
     * private BigDecimal bondFreezeAmount;   // 保证金冻结数量
     * <p>
     * <p>
     * private Integer finishNum; //已成交单数
     * private Integer waitNum;//待处理单数
     * private Integer failNum;//失败单数
     * private BigDecimal feeAll; //总手续费
     * private BigDecimal cardPay;// 银行卡收款额
     * private BigDecimal chatPay;//微信收款额
     * private BigDecimal aliPay;//支付宝收款额
     * <p>
     * <p>
     * <p>
     * private Integer status; // 0未成交 1部分成交 2 全部成交 3 撤销
     */

    @SerializedName("id")
    public Integer id;
    @SerializedName("uid")
    public String uid;
    @SerializedName("uuid")
    public String uuid;
    @SerializedName("buyorsell")
    public Integer buyorsell;
    @SerializedName("currencyId")
    public Integer currencyId;
    @SerializedName("currencyName")
    public String currencyName;
    @SerializedName("money")
    public Integer money;
    @SerializedName("timeOut")
    public Integer timeOut;
    @SerializedName("payByCard")
    public Integer payByCard;
    @SerializedName("payWechat")
    public Integer payWechat;
    @SerializedName("payAlipay")
    public Integer payAlipay;
    @SerializedName("price")
    public String price;
    @SerializedName("num")
    public String num;
    @SerializedName("amount")
    public String amount;
    @SerializedName("remainAmount")
    public String remainAmount;
    @SerializedName("lockAmount")
    public String lockAmount;
    @SerializedName("tradeAmount")
    public String tradeAmount;
    @SerializedName("lowLimit")
    public String lowLimit;
    @SerializedName("highLimit")
    public String highLimit;
    @SerializedName("fee")
    public String fee;
    @SerializedName("feeType")
    public String feeType;
    @SerializedName("explaininfo")
    public String explaininfo;
    @SerializedName("bondFreezeAmount")
    public String bondFreezeAmount;
    @SerializedName("finishNum")
    public String finishNum;
    @SerializedName("waitNum")
    public String waitNum;
    @SerializedName("failNum")
    public String failNum;
    @SerializedName("feeAll")
    public String feeAll;
    @SerializedName("cardPay")
    public String cardPay;
    @SerializedName("chatPay")
    public String chatPay;
    @SerializedName("aliPay")
    public String aliPay;
    @SerializedName("status")
    public String status;
    @SerializedName("createTime")
    public String createTime;
    @SerializedName("updateTime")
    public String updateTime;
}
