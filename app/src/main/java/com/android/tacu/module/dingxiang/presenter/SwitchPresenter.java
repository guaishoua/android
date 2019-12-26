package com.android.tacu.module.dingxiang.presenter;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.module.dingxiang.contract.ISwitchView;
import com.android.tacu.utils.user.UserInfoUtils;
import com.dingxiang.mobile.captcha.DXCaptchaEvent;
import com.dingxiang.mobile.captcha.DXCaptchaListener;
import com.dingxiang.mobile.captcha.DXCaptchaView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundRelativeLayout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaohong on 2018/6/8.
 */

public class SwitchPresenter extends BaseMvpPresenter {

    //滑动验证码
    private Activity activity;
    private ISwitchView iSwitchView;

    private Dialog dialog;
    private DXCaptchaView dxCaptcha;
    private UserInfoUtils mUserSpUtil;

    public SwitchPresenter(Activity activity, ISwitchView iSwitchView) {
        this.activity = activity;
        this.iSwitchView = iSwitchView;
    }

    /**
     * 滑动图形验证码
     */
    public void switchView() {
        mUserSpUtil = UserInfoUtils.getInstance();
        String language = null;
        if (TextUtils.equals(mUserSpUtil.getLanguage(), Constant.ZH_TW) || TextUtils.equals(mUserSpUtil.getLanguage(), Constant.ZH_CN)) {
            language = "ch";
        } else {
            language = "en";
        }

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = new Dialog(activity, R.style.UpdateAppDialog);
        dialog.setContentView(R.layout.view_dialog_switch);

        QMUIRoundRelativeLayout rlClose = dialog.findViewById(R.id.rl_switch_close);
        dxCaptcha = dialog.findViewById(R.id.dxCaptcha);
        dxCaptcha.init(Constant.DINGXINAG_APPID);
        HashMap<String, Object> config = new HashMap<>();
        Map<String, Object> customStyle = new HashMap<>();
        customStyle.put("bgColor", ContextCompat.getColor(activity, R.color.color_white));
        config.put("customStyle", customStyle);
        config.put("language", language);
        config.put("cacheStorage", true);//是否缓存上次校验结果，默认为true。界面中若只包含无感验证建议设置此项。
        //config.put("captchaJS", Constant.DINGXIANG_CDN);
        JSONObject customLanguage = new JSONObject();
        try {
            customLanguage.put("init_inform", activity.getResources().getString(R.string.switch_drag));
            customLanguage.put("slide_inform", activity.getResources().getString(R.string.slide_inform));
            customLanguage.put("smart_checking", activity.getResources().getString(R.string.smart_checking));
            customLanguage.put("verify_fail", activity.getResources().getString(R.string.switch_error));
            customLanguage.put("verifying", activity.getResources().getString(R.string.verifying));
        } catch (Exception e) {
            e.printStackTrace();
        }
        config.put("customLanguage", customLanguage);
        dxCaptcha.initConfig(config);
        //dxCaptcha.isShown();

        rlClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        dxCaptcha.startToLoad(new DXCaptchaListener() {
            @Override
            public void handleEvent(WebView webView, DXCaptchaEvent dxCaptchaEvent, Map<String, String> map) {
                switch (dxCaptchaEvent) {
                    case DXCAPTCHA_FAIL:
                        break;
                    case DXCAPTCHA_SUCCESS:
                        String token = map.get("token"); // 成功时会传递token参数
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        iSwitchView.switchSuccess(token);
                        break;
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Window window = dialog.getWindow();
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams layoutParams = window.getAttributes(); // 获取对话框当前的参数值
        layoutParams.width = display.getWidth();
        layoutParams.height = (display.getWidth() * 3) / 4;
        window.setAttributes(layoutParams);
    }

    @Override
    public void destroy() {
        super.destroy();
        if (dxCaptcha != null) {
            dxCaptcha.destroy();
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (activity != null) {
            activity = null;
        }
        if (iSwitchView != null) {
            iSwitchView = null;
        }
    }
}
