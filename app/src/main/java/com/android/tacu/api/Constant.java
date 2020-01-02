package com.android.tacu.api;

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
    //币的详情页 时间段缓存
    public static final String MARKET_DETAIL_TIME = "MARKET_DETAIL_TIME_1";

    /****************************************************
     * 配置
     ****************************************************/

    //首页切换
    public static final int MAIN_HOME = 1001;
    public static final int MAIN_TRADE = 1002;
    public static final int MAIN_ASSETS = 1003;

    //Auction切换
    public static final int AUCTION_PLUS = 1001;
    public static final int AUCTION = 1002;

    //自选页面SelfSelectionFragment编辑按钮状态
    public static final int BTN_STATUS_NO = 1001;//按钮不显示
    public static final int BTN_STATUS_EDIT = 1002;//按钮显示编辑
    public static final int BTN_STATUS_NOTICE = 1003;//非自选页面显示公告 不显示编辑按钮

    //语言中文
    public static final String ZH_TW = "zh_TW";//繁体
    public static final String ZH_CN = "zh_CN";//简体
    public static final String EN_US = "en_US";//英文
    public static final String KO_KR = "ko_KR";//韩国

    //法币相对于汇率基础币(默认CODE)的比值
    public static final String CNY = "CNY";//人民币
    public static final String USD = "USD";//美元
    public static final String EUR = "EUR";//欧元

    //阿里云图片
    public static final String API_QINIU_URL = "https://globalex.oss-cn-beijing.aliyuncs.com/";

    //阿里云OSS图片上传
    public static final String OSS_ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";

    //顶象
    public static final String DINGXINAG_APPID = "bc6773614c088f0618f41616dab302ed";//图形滑动验证id

    //app的H5下载链接
    public static final String ANDROID_APP_DOWNLOAD = "https://app.gexday.com/app.html";

    //指纹和手势密码的setResult的回调
    public static final int PWD_RESULT = 1001;

    //显示资产的基准币 默认以BTC为基准
    public static final String ASSETS_COIN = "BTC";

    //UUEX的OTC
    public static final String UUEX_OTC_URL = "https://uuex.io";

    //zendesk
    public static final String ZENDESK_WENTI = "https://gexday.zendesk.com/hc/zh-cn/categories/360002072732";

    //注册协议
    public static final String REGISTER_XIEYI = "";
}
