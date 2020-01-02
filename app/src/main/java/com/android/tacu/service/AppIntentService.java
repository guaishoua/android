package com.android.tacu.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.android.tacu.BuildConfig;
import com.mob.MobSDK;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.zendesk.logger.Logger;
import com.zendesk.sdk.network.impl.ZendeskConfig;
import com.zopim.android.sdk.api.ZopimChat;
import com.zopim.android.sdk.widget.ChatWidgetService;


/**
 * 在Application里用的 将一些第三方的放到这里 优化app启动速度
 * Created by jiazhen on 2018/8/21.
 */
public class AppIntentService extends IntentService {

    private static final String ACTION_INIT = "com.android.tacu.action.INIT";

    public AppIntentService() {
        super("AppIntentService");
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, AppIntentService.class);
        intent.setAction(ACTION_INIT);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null && intent.getAction().equals(ACTION_INIT)) {
            //Mob初始化
            MobSDK.init(getApplication());
            //bugly错误统计
            buglySetting();
            //腾讯webview内核
            tencentWebViewSetting();
            //ZenDesk初始化
            zendeskSetting();

            //内存检测
            //leakcanarySetting();
            //App界面卡顿检测工具
            //blockSetting();
        }
    }

    /**
     * 腾讯bugly
     */
    private void buglySetting() {
        //17增加了BuildConfig特性，可以通过获取BuildConfig类的DEBUG变量来设置是否是开发设备 (测试阶段建议设置成true，发布时设置为false)
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        if (BuildConfig.DEBUG) {
            CrashReport.initCrashReport(getApplicationContext(), "6f53f71f62", true, strategy);
        } else {
            CrashReport.initCrashReport(getApplicationContext(), "6f53f71f62", false, strategy);
        }
    }

    /**
     * 腾讯webview内核加载
     */
    private void tencentWebViewSetting() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
            }

            @Override
            public void onCoreInitFinished() {
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    /**
     * ZenDesk初始化
     */
    private void zendeskSetting() {
        //工单
        ZendeskConfig.INSTANCE.init(this, "https://gexday.zendesk.com",
                "ee85de09056837386e6eec6910bb833c3a4804bc59848d83",
                "mobile_sdk_client_4fd535f4600198c3f5db");

        ZopimChat.init("2AOKoySErajKRe4IXwkf3easWZnJjZ9H");
        if (BuildConfig.DEBUG) {
            Logger.setLoggable(true);
        }
        ChatWidgetService.disable();
    }

    /**
     * 内存检测
     */
    /*private void leakcanarySetting() {
        if (BuildConfig.DEBUG) {
            if (LeakCanary.isInAnalyzerProcess(getApplicationContext())) {
                return;
            }
            LeakCanary.install(getApplication());
        }
    }*/

    /**
     * App界面卡顿检测工具
     */
    /*private void blockSetting() {
        if (BuildConfig.DEBUG) {
            BlockCanary.install(getApplicationContext(), new AppBlockCanaryContext()).start();
        }
    }

    private class AppBlockCanaryContext extends BlockCanaryContext {

        public String provideQualifier() {
            return "unknown";
        }

        public String provideUid() {
            return "uid";
        }

        public String provideNetworkType() {
            return "unknown";
        }

        public int provideMonitorDuration() {
            return -1;
        }

        public int provideBlockThreshold() {
            return 1000;
        }

        public int provideDumpInterval() {
            return provideBlockThreshold();
        }

        public String providePath() {
            return "/blockcanary/";
        }

        public boolean displayNotification() {
            return true;
        }

        public boolean zip(File[] src, File dest) {
            return false;
        }

        public void upload(File zippedFile) {
            throw new UnsupportedOperationException();
        }

        public List<String> concernPackages() {
            return null;
        }

        public boolean filterNonConcernStack() {
            return false;
        }

        public List<String> provideWhiteList() {
            LinkedList<String> whiteList = new LinkedList<>();
            whiteList.add("org.chromium");
            return whiteList;
        }

        public boolean deleteFilesInWhiteList() {
            return true;
        }

        public void onBlock(Context context, BlockInfo blockInfo) {
        }
    }*/
}
