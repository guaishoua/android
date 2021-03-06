package com.android.tacu.api;

import com.android.tacu.BuildConfig;

import static com.android.tacu.api.ApiHost.SOCKET_IP;

public class Constant {

    /****************************************************
     * 缓存设置
     ****************************************************/

    //自选列表服务器上拿到的数据
    public static final String SELFCOIN_LIST = "SELFCOIN_LIST_1";
    //选币界面(分组)的网络缓存 直接缓冲的Socket的市场数据
    public static final String SELECT_COIN_GROUP_CACHE = "SELECT_COIN_GROUP_CACHE_2";
    //OTC选币界面的网络缓存
    public static final String OTC_SELECT_COIN_CACHE = "OTC_SELECT_COIN_CACHE_1";
    //选币界面(不分组)的网络缓存
    public static final String SELECT_COIN_NOGROUP_CACHE = "SELECT_COIN_NOGROUP_CACHE_1";
    //缓存首页的接口
    public static final String HOME_CACHE = "HOME_CACHE_1";
    //缓存首页公告的接口
    public static final String HOME_NOTICE_CACHE = "HOME_NOTICE_CACHE_1";
    //公告的轮动条被关闭时的公告内容
    public static final String HOME_NOTICE_CLOSE_CACHE = "HOME_NOTICE_CLOSE_CACHE_1";
    //缓存汇率的
    public static final String CONVERT_CACHE = "CONVERT_CACHE_1";
    //币的详情页 用户的选择K线相关的缓存
    public static final String MARKET_DETAIL_TIME = "MARKET_DETAIL_TIME_2";

    /****************************************************
     * 配置
     ****************************************************/

    //首页切换
    public static final int MAIN_HOME = 1001;
    public static final int MAIN_TRADE = 1002;
    public static final int MAIN_OTC = 1003;
    public static final int MAIN_ASSETS = 1004;

    //语言中文
    public static final String ZH_TW = "zh_TW";//繁体
    public static final String ZH_CN = "zh_CN";//简体
    public static final String EN_US = "en_US";//英文

    //法币相对于汇率基础币(默认CODE)的比值
    public static final String CNY = "CNY";//人民币
    public static final String USD = "USD";//美元
    public static final String EUR = "EUR";//欧元

    public static final String CNY_SIGN = "¥";//人民币
    public static final String USD_SIGN = "$";//美元
    public static final String EUR_SIGN = "€";//欧元

    //阿里云图片
    //这里需要注意/a-Heads-image/修改了，对应的 CommonUtils.getHeadImg也要修改
    public static final String HEAD_IMG_URL = "https://ta-public.oss-accelerate.aliyuncs.com/a-Heads-image/";
    public static final String SMALL_ICON_URL = "https://ta-public.oss-cn-hongkong.aliyuncs.com/";

    //阿里云OSS图片上传
    public static final String OSS_ENDPOINT = "http://oss-cn-hongkong.aliyuncs.com";
    //头像上传专用桶，本地写死
    public static final String OSS_HEAD_BUCKET = "ta-public";
    public static final String OSS_HEAD_DIR = "a-Heads-image/";
    public static final String OSS_HEAD_ENDPOINT = "http://oss-accelerate.aliyuncs.com";

    //顶象
    public static final String DINGXINAG_APPID = "398d3298f34bd4d05673e5e2f7b1498a";//图形滑动验证id

    //app的H5下载链接
    public static final String ANDROID_APP_DOWNLOAD = SOCKET_IP + "/download/app.html";

    //zendesk
    public static final String ZENDESK_WENTI = "https://tacu.zendesk.com/hc/zh-tw/categories/360001893293";
    //zendesk帮助中心
    public static final String ZENDESK_HELP = "https://tacu.zendesk.com/hc/zh-tw";

    //会员权益
    public static final String MEMBERSHIP = SOCKET_IP + "/MemberShip";

    //注册协议
    public static final String REGISTER_XIEYI = "";
    //商家申请条件及保证金制度
    public static final String SHOP_APPLY_SYSTEM = "http://ta-public.oss-accelerate.aliyuncs.com/商家申请条件及保证金制度.docx";

    //邀请好友前缀
    public static final String INVITED_FRIEND_URL = SOCKET_IP + "/RegisterInvite/";

    //c2c购买页面
    public static final String C2C_URL = BuildConfig.ISFORMALHOST ? "https://c2c.tacu.com" : "https://c2c.cdcbk.com";
    public static final String C2C_URL_TRADE = C2C_URL + "/ctrade";
    public static final String C2C_ORDER_LIST_URL = C2C_URL + "/orderList";

    //几个币种的ID
    public static final int ACU_CURRENCY_ID = 237;
    public static final int TAC_CURRENCY_ID = 238;

    //ACU图片地址
    public static final String ACU_ICON = SMALL_ICON_URL + "coinimg/acu.png";

    //OTC开放的币种
    public static final String[] OTCList = new String[]{"ACU"};
    public static final String ACU_CURRENCY_NAME = "ACU";
    public static final String TAC_CURRENCY_NAME = "TAC";
}
