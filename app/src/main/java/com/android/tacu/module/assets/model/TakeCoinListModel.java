package com.android.tacu.module.assets.model;

import com.android.tacu.base.BaseModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TakeCoinListModel extends BaseModel implements Serializable{

    /**
     * attachment : {"total":7,"sumMoney":4.1092455003E7,"list":[{"currencyNameEn":"DLC","amount":2999999.998,"address":"CMXPuGm5bVtnuaqoNxgeUtP5fzLraqBmKR","walletWaterSn":null,"currencyName":"蝶恋币","createTime":"2017-09-19 16:57:52","fee":0.002,"confirms":"Reject","initAmount":3000000,"currencyId":4,"paidAmount":2999999.998},{"currencyNameEn":"DLC","amount":122.998,"address":"CMXPuGm5bVtnuaqoNxgeUtP5fzLraqBmKR","walletWaterSn":null,"currencyName":"蝶恋币","createTime":"2017-09-19 16:52:04","fee":0.002,"confirms":"0","initAmount":123,"currencyId":4,"paidAmount":122.998},{"currencyNameEn":"DLC","amount":1.2312311998E7,"address":"CMXPuGm5bVtnuaqoNxgeUtP5fzLraqBmKR","walletWaterSn":null,"currencyName":"蝶恋币","createTime":"2017-09-19 16:51:51","fee":0.002,"confirms":"0","initAmount":1.2312312E7,"currencyId":4,"paidAmount":1.2312311998E7},{"currencyNameEn":"DLC","amount":2.5779999998E7,"address":"CMXPuGm5bVtnuaqoNxgeUtP5fzLraqBmKR","walletWaterSn":null,"currencyName":"蝶恋币","createTime":"2017-09-19 16:50:51","fee":0.002,"confirms":"0","initAmount":2.578E7,"currencyId":4,"paidAmount":2.5779999998E7},{"currencyNameEn":"DLC","amount":0.001,"address":"CMXPuGm5bVtnuaqoNxgeUtP5fzLraqBmKR","walletWaterSn":null,"currencyName":"蝶恋币","createTime":"2017-09-19 16:48:37","fee":0.002,"confirms":"0","initAmount":0.003,"currencyId":4,"paidAmount":0.001},{"currencyNameEn":"DLC","amount":9.998,"address":"CMXPuGm5bVtnuaqoNxgeUtP5fzLraqBmKR","walletWaterSn":null,"currencyName":"蝶恋币","createTime":"2017-09-19 15:30:09","fee":0.002,"confirms":"0","initAmount":10,"currencyId":4,"paidAmount":9.998},{"currencyNameEn":"DLC","amount":9.98,"address":"CMXPuGm5bVtnuaqoNxgeUtP5fzLraqBmKR","walletWaterSn":null,"currencyName":"蝶恋币","createTime":"2017-09-19 15:28:42","fee":0.02,"confirms":"0","initAmount":10,"currencyId":4,"paidAmount":9.98}]}
     * message : null
     */

    /**
     * total : 7
     * sumMoney : 4.1092455003E7
     * list : [{"currencyNameEn":"DLC","amount":2999999.998,"address":"CMXPuGm5bVtnuaqoNxgeUtP5fzLraqBmKR","walletWaterSn":null,"currencyName":"蝶恋币","createTime":"2017-09-19 16:57:52","fee":0.002,"confirms":"Reject","initAmount":3000000,"currencyId":4,"paidAmount":2999999.998},{"currencyNameEn":"DLC","amount":122.998,"address":"CMXPuGm5bVtnuaqoNxgeUtP5fzLraqBmKR","walletWaterSn":null,"currencyName":"蝶恋币","createTime":"2017-09-19 16:52:04","fee":0.002,"confirms":"0","initAmount":123,"currencyId":4,"paidAmount":122.998},{"currencyNameEn":"DLC","amount":1.2312311998E7,"address":"CMXPuGm5bVtnuaqoNxgeUtP5fzLraqBmKR","walletWaterSn":null,"currencyName":"蝶恋币","createTime":"2017-09-19 16:51:51","fee":0.002,"confirms":"0","initAmount":1.2312312E7,"currencyId":4,"paidAmount":1.2312311998E7},{"currencyNameEn":"DLC","amount":2.5779999998E7,"address":"CMXPuGm5bVtnuaqoNxgeUtP5fzLraqBmKR","walletWaterSn":null,"currencyName":"蝶恋币","createTime":"2017-09-19 16:50:51","fee":0.002,"confirms":"0","initAmount":2.578E7,"currencyId":4,"paidAmount":2.5779999998E7},{"currencyNameEn":"DLC","amount":0.001,"address":"CMXPuGm5bVtnuaqoNxgeUtP5fzLraqBmKR","walletWaterSn":null,"currencyName":"蝶恋币","createTime":"2017-09-19 16:48:37","fee":0.002,"confirms":"0","initAmount":0.003,"currencyId":4,"paidAmount":0.001},{"currencyNameEn":"DLC","amount":9.998,"address":"CMXPuGm5bVtnuaqoNxgeUtP5fzLraqBmKR","walletWaterSn":null,"currencyName":"蝶恋币","createTime":"2017-09-19 15:30:09","fee":0.002,"confirms":"0","initAmount":10,"currencyId":4,"paidAmount":9.998},{"currencyNameEn":"DLC","amount":9.98,"address":"CMXPuGm5bVtnuaqoNxgeUtP5fzLraqBmKR","walletWaterSn":null,"currencyName":"蝶恋币","createTime":"2017-09-19 15:28:42","fee":0.02,"confirms":"0","initAmount":10,"currencyId":4,"paidAmount":9.98}]
     */

    @SerializedName("total")
    public int total;
    @SerializedName("sumMoney")
    public double sumMoney;
    @SerializedName("list")
    public List<ListBean> list = new ArrayList<>();

    public static class ListBean{
        /**
         * currencyNameEn : DLC
         * amount : 2999999.998
         * address : CMXPuGm5bVtnuaqoNxgeUtP5fzLraqBmKR
         * walletWaterSn : null
         * currencyName : 蝶恋币
         * createTime : 2017-09-19 16:57:52
         * fee : 0.002
         * confirms : Reject
         * initAmount : 3000000.0
         * currencyId : 4
         * paidAmount : 2999999.998
         */

        @SerializedName("currencyNameEn")
        public String currencyNameEn;
        @SerializedName("amount")
        public double amount;
        @SerializedName("address")
        public String address;
        @SerializedName("walletWaterSn")
        public String walletWaterSn;
        @SerializedName("currencyName")
        public String currencyName;
        @SerializedName("createTime")
        public String createTime;
        @SerializedName("fee")
        public double fee;
        @SerializedName("confirms")
        public String confirms;
        @SerializedName("initAmount")
        public double initAmount;
        @SerializedName("currencyId")
        public int currencyId;
        @SerializedName("paidAmount")
        public double paidAmount;
    }
}
