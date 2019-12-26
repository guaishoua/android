package com.android.tacu.utils;

import com.android.tacu.module.webview.model.EPayParam;

/**
 * epay资金划转表单生成
 * Created by jiazhen on 2018/11/16.
 */
public class EPayUtils {

    public static String ePayData(EPayParam param, String language) {
        return "PAYEE_ACCOUNT=" + param.PAYEE_ACCOUNT + "&PAYEE_NAME=" + param.PAYEE_NAME + "&PAYMENT_AMOUNT=" + param.PAYMENT_AMOUNT + "&PAYMENT_UNITS=" + param.PAYMENT_UNITS
                + "&PAYMENT_ID=" + param.PAYMENT_ID + "&STATUS_URL=" + param.STATUS_URL + "&PAYMENT_URL=" + param.PAYMENT_URL + "&NOPAYMENT_URL=" + param.NOPAYMENT_URL
                + "&INTERFACE_LANGUAGE=" + language + "&CHARACTER_ENCODING=UTF-8" + "&V2_HASH=" + param.V2_HASH;
    }

    /**
     * v2_hash加密
     *
     * @param PAYEE_ACCOUNT
     * @param PAYMENT_AMOUNT
     * @param PAYMENT_UNITS
     * @param API_KEY
     * @return
     */
    private static String ePaySign(String PAYEE_ACCOUNT, double PAYMENT_AMOUNT, String PAYMENT_UNITS, String API_KEY) {
        return Md5Utils.md5Encode(PAYEE_ACCOUNT + ":" + PAYMENT_AMOUNT + ":" + PAYMENT_UNITS + ":" + API_KEY);
    }
}
