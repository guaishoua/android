package com.android.tacu.module.webview.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * epay表单提交参数
 * Created by jiazhen on 2018/11/16.
 */
public class EPayParam implements Serializable {
    @SerializedName("PAYEE_NAME")
    public String PAYEE_NAME;//用户希望展示在易派支付系统交易页面上的名称
    @SerializedName("PAYMENT_AMOUNT")
    public String PAYMENT_AMOUNT;//交易金额  法币保留两位小数  加密货币保留四位小数
    @SerializedName("PAYMENT_UNITS")
    public String PAYMENT_UNITS;//交易币种
    @SerializedName("PAYMENT_ID")
    public String PAYMENT_ID;//用户交易编号
    @SerializedName("PAYEE_ACCOUNT")
    public String PAYEE_ACCOUNT;//用户用于收款的易派账户
    @SerializedName("STATUS_URL")
    public String STATUS_URL;//支付结果后台推送URL
    @SerializedName("PAYMENT_URL")
    public String PAYMENT_URL;//支付成功跳转URL
    @SerializedName("NOPAYMENT_URL")
    public String NOPAYMENT_URL;//支付失败跳转URL
    @SerializedName("V2_HASH")
    public String V2_HASH;//支付过程中，为了防止传输的数据被恶意篡改（如改变币种或交易金额）
}
