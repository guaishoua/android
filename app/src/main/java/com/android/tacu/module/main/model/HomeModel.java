package com.android.tacu.module.main.model;

import com.google.gson.annotations.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiazhen on 2018/10/18.
 */
public class HomeModel implements Serializable {


    /**
     * announce : [{"announceId":1,"title":"test","content":"test","desc":null,"authorName":null,"authorId":null,"publishTime":"2017-10-16 14:07:23","createTime":null,"outofTime":"2019-10-16 14:07:44","status":1,"local":"en_US","linkId":-1,"type":null,"language":1,"startTime":null,"endTime":null,"label":null,"rank":null}]
     * amount : 0
     * amountNumber : 8
     * cashAmountNumber : 2
     * banner : [{"bannerid":48,"type":null,"image":"https://chex.oss-cn-beijing.aliyuncs.com/banner-1-CN.png","title":"zhenshiceshi","comment":"zhenbuzhidao","status":1,"rank":1000,"url":"https://chaoex-zh-tw.udesk.cn/hc/articles/67680","createTime":"2018-09-12 18:30:43","updateTime":"2018-10-16 13:22:32","location":2,"startTime":"2018-09-20 17:45:33","endTime":"2019-09-20 17:45:44","platForm":48,"language":1,"label":1,"start":1,"size":20,"total":0,"orderBy":"desc"},{"bannerid":47,"type":null,"image":"https://chex.oss-cn-beijing.aliyuncs.com/a-image/e79ea025.png","title":"ceshi","comment":"buzhidao","status":1,"rank":33,"url":"https://chaoex-zh-tw.udesk.cn/hc/articles/67991","createTime":"2018-09-12 16:30:08","updateTime":"2018-10-16 13:22:21","location":2,"startTime":"2018-09-19 15:02:58","endTime":"2018-12-28 10:33:11","platForm":47,"language":1,"label":1,"start":1,"size":20,"total":0,"orderBy":"desc"}]
     * ownChoose : ["2,1","20,1","10,1","14,1","38,1","15,1"]
     * mainCoin : ["10,1","14,1","15,1"]
     */

    @SerializedName("amount")
    public double amount;
    @SerializedName("amountNumber")
    public int amountNumber;
    @SerializedName("cashAmountNumber")
    public int cashAmountNumber;
    @SerializedName("banner")
    public List<BannerBean> banner = new ArrayList<>();
    @SerializedName("ownChoose")
    public List<String> ownChoose = new ArrayList<>();
    @SerializedName("mainCoin")
    public List<String> mainCoin = new ArrayList<>();
    @SerializedName("activity")
    public Integer activity;

    public static class BannerBean implements Serializable {
        /**
         * bannerid : 48
         * type : null
         * image : https://chex.oss-cn-beijing.aliyuncs.com/banner-1-CN.png
         * title : zhenshiceshi
         * comment : zhenbuzhidao
         * status : 1
         * rank : 1000
         * url : https://chaoex-zh-tw.udesk.cn/hc/articles/67680
         * createTime : 2018-09-12 18:30:43
         * updateTime : 2018-10-16 13:22:32
         * location : 2
         * startTime : 2018-09-20 17:45:33
         * endTime : 2019-09-20 17:45:44
         * platForm : 48
         * language : 1
         * label : 1
         * start : 1
         * size : 20
         * total : 0
         * orderBy : desc
         */

        @SerializedName("bannerid")
        public int bannerid;
        @SerializedName("type")
        public Object type;
        @SerializedName("image")
        public String image;
        @SerializedName("title")
        public String title;
        @SerializedName("comment")
        public String comment;
        @SerializedName("status")
        public int status;
        @SerializedName("rank")
        public int rank;
        @SerializedName("url")
        public String url;
        @SerializedName("createTime")
        public String createTime;
        @SerializedName("updateTime")
        public String updateTime;
        @SerializedName("location")
        public int location;
        @SerializedName("startTime")
        public String startTime;
        @SerializedName("endTime")
        public String endTime;
        @SerializedName("platForm")
        public int platForm;
        @SerializedName("language")
        public int language;
        @SerializedName("label")
        public int label;
        @SerializedName("start")
        public int start;
        @SerializedName("size")
        public int size;
        @SerializedName("total")
        public int total;
        @SerializedName("orderBy")
        public String orderBy;
    }
}
