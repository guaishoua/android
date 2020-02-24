package com.android.tacu.module.otc.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OtcMarketInfoModel implements Serializable {

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
    @SerializedName("uid")
    public Integer uid;
    @SerializedName("headImg")
    public String headImg;
    @SerializedName("nickname")
    public String nickname;
    @SerializedName("secondName")
    public String secondName;
    @SerializedName("isValidateEmail")
    public Integer isValidateEmail;//0. 未设置 1. 已设置
    @SerializedName("isAuthSenior")
    public Integer isAuthSenior;
    @SerializedName("isValidatePhone")
    public Integer isValidatePhone;//0. 未设置 1. 已设置
    @SerializedName("applyMerchantStatus")
    public Integer applyMerchantStatus;

    @SerializedName("applyAuthMerchantStatus")
    public Integer applyAuthMerchantStatus;//申请认证商户状态 0未申请(默认) 1待审核 2审核成功 3审核失败
    @SerializedName("vip")
    public Integer vip;// 0不是vip 1月度会员(30天) 2年度会员(12月) 3连续包年

    @SerializedName("bondMoney")
    public String bondMoney;
    @SerializedName("total")
    public String total;
    @SerializedName("buy")
    public String buy;
    @SerializedName("sell")
    public String sell;
    @SerializedName("success")
    public String success;
    @SerializedName("fail")
    public String fail;
    @SerializedName("disputeTotal")
    public String disputeTotal;
    @SerializedName("disputeBySelf")
    public String disputeBySelf;
    @SerializedName("disputeByOther")
    public String disputeByOther;
    @SerializedName("otherDispute")
    public String otherDispute;
    @SerializedName("winDispute")
    public String winDispute;
    @SerializedName("allTime")
    public String allTime;
}
