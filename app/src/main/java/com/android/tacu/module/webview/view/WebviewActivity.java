package com.android.tacu.module.webview.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.tacu.R;
import com.android.tacu.api.ApiHost;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.webview.model.EPayParam;
import com.android.tacu.utils.EPayUtils;
import com.android.tacu.utils.Md5Utils;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.apache.http.util.EncodingUtils;

import butterknife.BindView;

public class WebviewActivity extends BaseActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.ll_web)
    LinearLayout llWeb;

    private String url;
    //是否加载富文本
    private boolean isDetailSpan = false;
    //epay加载表单
    private EPayParam payParam = null;

    private WebView webView;
    private ValueCallback<Uri> uploadFile;
    private ValueCallback<Uri[]> uploadFiles;

    public static Intent createActivity(Context context, String url, boolean isDetailSpan, EPayParam payParam) {
        Intent intent = new Intent(context, WebviewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("isDetailSpan", isDetailSpan);
        Bundle bundle = new Bundle();
        bundle.putSerializable("payParam", payParam);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_webview);
    }

    @Override
    protected void initView() {
        url = getIntent().getStringExtra("url");
        isDetailSpan = getIntent().getBooleanExtra("isDetailSpan", false);
        payParam = (EPayParam) getIntent().getSerializableExtra("payParam");

        mTopBar.removeAllLeftViews();
        mTopBar.addLeftImageButton(R.drawable.icon_close_black, R.id.qmui_topbar_item_left_back, 22, 22).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initWebViewSettings();
        webView.setWebViewClient(new WebViewClient() {
            /**
             * 防止加载网页时调起系统浏览器
             */
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                mTopBar.setTitle(webView.getTitle());
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView webView, int newProgress) {
                super.onProgressChanged(webView, newProgress);
                if (progressBar != null) {
                    progressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView webView, String title) {
                super.onReceivedTitle(webView, title);
                mTopBar.setTitle(title);
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                WebviewActivity.this.uploadFile = uploadFile;
                openFileChooseProcess();
            }

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsgs) {
                WebviewActivity.this.uploadFile = uploadFile;
                openFileChooseProcess();
            }

            // For Android  > 4.1.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                WebviewActivity.this.uploadFile = uploadFile;
                openFileChooseProcess();
            }

            // For Android  >= 5.0
            public boolean onShowFileChooser(com.tencent.smtt.sdk.WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                WebviewActivity.this.uploadFiles = filePathCallback;
                openFileChooseProcess();
                return true;
            }
        });
        if (isDetailSpan) {//加载本地html
            webView.loadDataWithBaseURL(null, url, "text/html", "UTF-8", null);
        } else if (payParam != null) {
            String language = "";
            if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN)) {
                language = "ZH_CN";
            } else {
                language = "EN_US";
            }
            String postData = EPayUtils.ePayData(payParam, language);
            webView.postUrl(ApiHost.EPay_host, EncodingUtils.getBytes(postData, "UTF-8"));
        } else {
            //OTC需要拼接
            if (TextUtils.equals(url, Constant.OTC_URL)) {
                url += "?chaoex_uid=" + Md5Utils.AESEncrypt(String.valueOf(spUtil.getUserUid())) + "&chaoex_token=" + Md5Utils.AESEncrypt(spUtil.getToken());
            }
            webView.loadUrl(url);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressBar != null) {
            progressBar = null;
        }
        if (webView != null) {
            webView.removeAllViews();
            webView.setVisibility(View.GONE);
            webView.stopLoading();
            webView.clearHistory();
            webView.clearView();
            webView.destroy();
            webView = null;
        }
        if (llWeb != null) {
            llWeb.removeAllViews();
            llWeb = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    if (null != uploadFile) {
                        Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                        uploadFile.onReceiveValue(result);
                        uploadFile = null;
                    }
                    if (null != uploadFiles) {
                        Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                        uploadFiles.onReceiveValue(new Uri[]{result});
                        uploadFiles = null;
                    }
                    break;
                default:
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (null != uploadFile) {
                uploadFile.onReceiveValue(null);
                uploadFile = null;
            }
            if (null != uploadFiles) {
                uploadFiles.onReceiveValue(null);
                uploadFiles = null;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initWebViewSettings() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        webView = new WebView(this);
        webView.setLayoutParams(params);
        llWeb.addView(webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setUserAgentString("Android");

        webView.setHorizontalScrollBarEnabled(false);//水平不显示
        webView.setVerticalScrollBarEnabled(false); //垂直不显示
        webView.requestFocus();
    }

    /**
     * goback方法返回上一页没有的时候就销毁当前页面
     */
    private void goBack() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    private void openFileChooseProcess() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        startActivityForResult(Intent.createChooser(i, ""), 0);
    }

    /**
     * 添加cookie
     */
    /*private void updateCookies() {
        //解析请求链接地址
        Uri htmlUriWithApp = Uri.parse(url);
        //将要绑定的域名进行分离
        String urll = htmlUriWithApp.getHost();
        CookieSyncManager.createInstance(this);

        //存cookie
        CookieManager mCookieManager = CookieManager.getInstance();
        mCookieManager.setAcceptCookie(true);

        //判断清空cookie
        String cookie = mCookieManager.getCookie(urll);
        if (cookie != null) {
            mCookieManager.removeAllCookie();
        }
        //存键值对形式进入cookie
        mCookieManager.setCookie(urll, "uid=" + Md5Utils.AESEncrypt(String.valueOf(spUtil.getUserUid())));
        mCookieManager.setCookie(urll, "token=" + Md5Utils.AESEncrypt(spUtil.getToken()));
        //同步cookie
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.getInstance().sync();
        } else {
            mCookieManager.flush();
        }
    }*/
}
