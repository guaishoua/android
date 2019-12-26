package com.android.tacu.module.assets.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CoinListModel implements Serializable {

    /**
     * attachment : [{"currencyId":2,"currencyName":"蝶链币","currencyNameEn":"12CT","icoUrl":"12ct_logo_small.png"}]
     * message : null
     */

    @SerializedName("message")
    public Object messageX;
    @SerializedName("attachment")
    public List<AttachmentBean> attachment;

    public static class AttachmentBean implements Serializable {
        /**
         * currencyId : 2
         * currencyName : 蝶链币
         * currencyNameEn : 12CT
         * icoUrl : 12ct_logo_small.png
         */
        @SerializedName("currencyId")
        public Integer currencyId;
        @SerializedName("currencyName")
        public String currencyName;
        @SerializedName("currencyNameEn")
        public String currencyNameEn;
        @SerializedName("icoUrl")
        public String icoUrl;
        @SerializedName("pointNum")
        public int pointNum;
        @SerializedName("pointPrice")
        public int pointPrice;
    }
}
