package com.android.tacu.module.webview.model;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.android.tacu.module.assets.view.AssetsActivity;

public class WebInterface {

    private Context mContext;

    public WebInterface(Context context) {
        mContext = context;
    }

    /**
     * 跳转币币充值页面
     */
    @JavascriptInterface
    public void JumpWithdraw(String currencyNameEn, int currencyId) {
        mContext.startActivity(AssetsActivity.createActivity(mContext, currencyNameEn, currencyId, 0, true));
    }
}

