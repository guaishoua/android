package com.android.tacu.module.assets.model;

import com.google.gson.annotations.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiazhen on 2018/12/26.
 */
public class RewardTotalAssetModel implements Serializable {

    @SerializedName("total")
    public long total;
    @SerializedName("totalPage")
    public int totalPage;
    @SerializedName("list")
    public List<TotalModel> list = new ArrayList<>();

    public static class TotalModel implements Serializable {
        /**
         * start : 0
         * size : 10
         * rechargeCtbBillId : 36686
         * ctbRechargeId : 201812261149229039464332403
         * ctbRechargePayUuid : 201812261149229039464332403
         * customerUuid : 62d3d43f95544ffab07ac85abefbc8e7
         * currencyId : 1
         * walletWaterSn : reward201812261149229039464332403
         * rechargeType : 6
         * ctbWalletSn : reward201812261149229039464332403
         * ctbWalletName : null
         * telephoneSn : null
         * note : 发放奖励
         * amount : 2
         * initAmount : 2
         * paidAmount : 2
         * fee : 0
         * bankFee : 0
         * bankResultNote : null
         * parentAbs : null
         * riskFlag : 1
         * status : 2
         * createTime : 2018-12-26 11:49:22.0
         * createBy : NULS节点奖励
         * lastEditTime : 2018-12-26 11:49:22.0
         * lastEditBy : null
         * type : null
         * mode : null
         * detailed : 6
         * createTimeBegin : null
         * createTimeEnd : null
         * notDetailed : null
         * currencyName : BTC
         * activityName : NULS节点奖励
         */

        @SerializedName("amount")
        public String amount;
        @SerializedName("createTime")
        public String createTime;
        @SerializedName("currencyName")
        public String currencyName;
        @SerializedName("activityName")
        public String activityName;
    }
}
