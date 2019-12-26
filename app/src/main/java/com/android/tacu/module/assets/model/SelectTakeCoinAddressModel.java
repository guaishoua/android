package com.android.tacu.module.assets.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SelectTakeCoinAddressModel implements Serializable {

    /**
     * attachment : {"resp":{"addressList":[{"note":"12CT","address":"CXm9uCyfh8bxBFA8hTny5z4x9LJ6rKuqYy","id":"742"}],"fee":0.002,"cashAmount":158372.4166552,"point":4},"detail":{"start":0,"size":10,"linkId":4,"currencyId":2,"currencyName":"12ct代币","actionId":4,"actionName":"提币","amountLowLimit":0.01,"amountHighLimit":9.99999999E8,"feeType":2,"fee":0.002,"riskHighAmount":13000,"status":1,"actionStatus":1,"createTime":"2017-06-28 22:28:14","createBy":"1","lastEditTime":"2017-07-15 04:17:44","lastEditBy":"1"}}
     * status : 200
     * message : null
     */

    /**
     * resp : {"addressList":[{"note":"12CT","address":"CXm9uCyfh8bxBFA8hTny5z4x9LJ6rKuqYy","id":"742"}],"fee":0.002,"cashAmount":158372.4166552,"point":4}
     * detail : {"start":0,"size":10,"linkId":4,"currencyId":2,"currencyName":"12ct代币","actionId":4,"actionName":"提币","amountLowLimit":0.01,"amountHighLimit":9.99999999E8,"feeType":2,"fee":0.002,"riskHighAmount":13000,"status":1,"actionStatus":1,"createTime":"2017-06-28 22:28:14","createBy":"1","lastEditTime":"2017-07-15 04:17:44","lastEditBy":"1"}
     */

    @SerializedName("resp")
    public RespBean resp;
    @SerializedName("detail")
    public DetailBean detail;

    public static class RespBean implements Serializable {
        /**
         * addressList : [{"note":"12CT","address":"CXm9uCyfh8bxBFA8hTny5z4x9LJ6rKuqYy","id":"742"}]
         * fee : 0.002
         * cashAmount : 158372.4166552
         * point : 4
         */
        @SerializedName("fee")
        public double fee;
        @SerializedName("cashAmount")
        public double cashAmount;
        @SerializedName("point")
        public int point;
        @SerializedName("msgCode")
        public String msgCode;
        @SerializedName("msgName")
        public String msgName;
        @SerializedName("addressList")
        public List<AddressListBean> addressList;

        public static class AddressListBean implements Serializable {
            /**
             * note : 12CT
             * address : CXm9uCyfh8bxBFA8hTny5z4x9LJ6rKuqYy
             * id : 742
             */

            @SerializedName("note")
            public String note;
            @SerializedName("address")
            public String address;
            @SerializedName("id")
            public String id;
        }
    }

    public static class DetailBean implements Serializable {
        /**
         * start : 0
         * size : 10
         * linkId : 4
         * currencyId : 2
         * currencyName : 12ct代币
         * actionId : 4
         * actionName : 提币
         * amountLowLimit : 0.01
         * amountHighLimit : 9.99999999E8
         * feeType : 2
         * fee : 0.002
         * riskHighAmount : 13000.0
         * status : 1
         * actionStatus : 1
         * createTime : 2017-06-28 22:28:14
         * createBy : 1
         * lastEditTime : 2017-07-15 04:17:44
         * lastEditBy : 1
         */

        @SerializedName("start")
        public int start;
        @SerializedName("size")
        public int size;
        @SerializedName("linkId")
        public int linkId;
        @SerializedName("currencyId")
        public int currencyId;
        @SerializedName("currencyName")
        public String currencyName;
        @SerializedName("actionId")
        public int actionId;
        @SerializedName("actionName")
        public String actionName;
        @SerializedName("amountLowLimit")
        public double amountLowLimit;
        @SerializedName("amountHighLimit")
        public double amountHighLimit;
        @SerializedName("feeType")
        public int feeType;
        @SerializedName("fee")
        public double fee;
        @SerializedName("riskHighAmount")
        public double riskHighAmount;
        @SerializedName("status")
        public int status;
        @SerializedName("actionStatus")
        public int actionStatus;
        @SerializedName("createTime")
        public String createTime;
        @SerializedName("createBy")
        public String createBy;
        @SerializedName("lastEditTime")
        public String lastEditTime;
        @SerializedName("lastEditBy")
        public String lastEditBy;
    }

}
