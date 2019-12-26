package com.stx.xhb.xbanner.entity;

/**
 * @author: xiaohaibin.
 * @time: 2018/12/3
 * @mail:xhb_199409@163.com
 * @github:https://github.com/xiaohaibin
 * @describe: CustomViewsInfo 继承 SimpleBannerInfo 根据个人情况重载两个方法
 */
public class CustomViewsInfo extends SimpleBannerInfo {

    private String info;
    private String url;

    public CustomViewsInfo(String info,String url) {
        this.info = info;
        this.url = url;
    }

    @Override
    public String getXBannerUrl() {
        return url;
    }

    public String getXBannerImage(){
        return info;
    }
}
