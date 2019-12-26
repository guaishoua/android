package com.android.tacu.EventBus;

/**
 * Created by jiazhen on 2018/9/3.
 */
public class EventConstant {

    //切换MainActivity当前的fragment
    public static final int MainSwitchCode = 1001;
    //MarketListFragment左上角按钮
    public static final int EditStatusCode = 1002;
    //MarketListFragment的isVisibleToUser的值
    public static final int MarkListVisibleCode = 1003;
    //详情页跳转交易页面的买卖页面
    public static final int JumpTradeIsBuyCode = 1005;
    //设置MainActivity的drawerlayout
    public static final int MainDrawerLayoutOpenCode = 1006;
    //提币查看记录页面按钮展示
    public static final int TakeCode = 1007;
    //AuctionListFragment切换fragment
    public static final int AuctionListCode = 1008;
    //AuctionPlusListFragment切换fragment
    public static final int AuctionPlusListCode = 1009;
    //homgfragment通知marketfragment和selffragment刷新
    public  static final int HomeNotifyCode = 2001;
}
