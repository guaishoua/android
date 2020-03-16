package com.android.tacu.module.otc.model;

import com.android.tacu.api.Constant;

import java.io.Serializable;

public class OtcPublishParam implements Serializable {

    public OtcPublishParam(boolean isBuy) {
        if (isBuy) {
            this.buyorsell = 1;
        } else {
            this.buyorsell = 2;
        }
    }

    public int buyorsell = 1;//1买2卖
    public int currencyId = Constant.ACU_CURRENCY_ID;
    public int money = 1;//1 人民币
    public int timeOut = 15;//超时时间
    public String price;//单价
    public String num;//数量
    public String amount;//金额
    public String lowLimit;//最细限额
    public String highLimit;//最高限额
    public Integer payByCard;//银行卡支付 1支持0 不支持
    public Integer payWechat;//微信支付 1支持0 不支持
    public Integer payAlipay;//支付 1支持0 不支持
    public String explain;//OTC订单手机号
}
