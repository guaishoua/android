package com.android.tacu.module.webview.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.webview.model.WebInterface;
import com.android.tacu.utils.Md5Utils;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class WebviewActivity extends BaseActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.ll_web)
    LinearLayout llWeb;

    private String url;

    private WebView webView;
    private WebInterface webInterface;
    private ValueCallback<Uri> uploadFile;
    private ValueCallback<Uri[]> uploadFiles;

    public static Intent createActivity(Context context, String url) {
        Intent intent = new Intent(context, WebviewActivity.class);
        intent.putExtra("url", url);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_webview);
    }

    @Override
    protected void initView() {
        url = getIntent().getStringExtra("url");

        mTopBar.removeAllLeftViews();
        mTopBar.addLeftImageButton(R.drawable.icon_close_default, R.id.qmui_topbar_item_left_back, 22, 22).setOnClickListener(new View.OnClickListener() {
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

        List<String> urlList = new ArrayList<>();
        urlList.add(Constant.MEMBERSHIP);
        urlList.add(Constant.C2C_URL);
        urlList.add(Constant.C2C_ORDER_URL);

        if (urlList.contains(url)) {
            url += "?chaoex_uid=" + Md5Utils.AESEncrypt(String.valueOf(spUtil.getUserUid())) + "&chaoex_token=" + Md5Utils.AESEncrypt(spUtil.getToken()) + "&chaoex_htmlLang=" + spUtil.getLanguage();
        }
        webView.loadUrl(url);
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
        if (webInterface != null) {
            webInterface.clearData();
        }
        System.gc();
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
        webView.setBackgroundColor(ContextCompat.getColor(this, R.color.content_bg_color));
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
        webInterface = new WebInterface(this);
        webView.addJavascriptInterface(webInterface, "android");
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
}
