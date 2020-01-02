package com.android.tacu.module.main.view;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.module.auth.view.AuthActivity;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.market.view.NoticeActivity;
import com.android.tacu.module.my.view.ContactUsActivity;
import com.android.tacu.module.my.view.ConvertActivity;
import com.android.tacu.module.my.view.InvitedinfoActivity;
import com.android.tacu.module.my.view.LanguageActivity;
import com.android.tacu.module.my.view.SecurityCenterActivity;
import com.android.tacu.module.webview.view.WebviewActivity;
import com.android.tacu.utils.PackageUtils;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.utils.user.UserInfoUtils;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.zendesk.sdk.model.access.AnonymousIdentity;
import com.zendesk.sdk.model.access.Identity;
import com.zendesk.sdk.network.impl.ZendeskConfig;
import com.zendesk.sdk.requests.RequestActivity;

/**
 * 帮助MainActivity处理DrawerLayout的问题
 * Created by jiazhen on 2018/9/16.
 */
public class MainDrawerLayoutHelper implements View.OnClickListener {

    private MainActivity mActivity;
    private Resources mResources;
    private UserInfoUtils spUtil;
    private View viewHome;
    private int itemHeight;

    private Handler mHandler = new Handler();

    //首页Fragment
    private RelativeLayout rl_unlogin;
    private RelativeLayout rl_login;
    private ImageView img_gologin;
    private TextView tv_gologin;
    private ImageView btnUnLogin;
    private TextView tvUser;
    private TextView tvUid;
    private QMUIGroupListView homeGroupListView;
    private QMUICommonListItemView itemRealName, itemSecuritySetting, itemInviting;

    private String realName;
    private String securitySetting;
    private String inviting;
    private String news;
    private String contactUs;
    private String question;
    private String myTicket;
    private String languageSetting;
    private String moneySetting;
    private String versionCode;

    public MainDrawerLayoutHelper(MainActivity activity, View viewDrawer) {
        this.mActivity = activity;
        this.mResources = activity.getResources();
        this.spUtil = UserInfoUtils.getInstance();
        viewHome = viewDrawer.findViewById(R.id.view_home);
        itemHeight = UIUtils.dp2px(50);
    }

    public void clearActivity() {
        mActivity = null;
        mResources = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_gologin:
            case R.id.tv_gologin:
                if (!spUtil.getLogin()) {
                    jumpTo(LoginActivity.class);
                }
                break;
        }
    }

    /**
     * ===================================================
     * 首页
     * ===================================================
     */
    public void setHomeDrawerMenuView(View.OnClickListener clickListener, View.OnClickListener updateClick) {
        rl_unlogin = viewHome.findViewById(R.id.rl_unlogin);
        rl_login = viewHome.findViewById(R.id.rl_login);
        img_gologin = viewHome.findViewById(R.id.img_gologin);
        tv_gologin = viewHome.findViewById(R.id.tv_gologin);
        btnUnLogin = viewHome.findViewById(R.id.btnUnLogin);
        tvUser = viewHome.findViewById(R.id.tv_user);
        tvUid = viewHome.findViewById(R.id.tv_uid);
        homeGroupListView = viewHome.findViewById(R.id.groupListView);
        initHomeGroup(updateClick);

        img_gologin.setOnClickListener(this);
        tv_gologin.setOnClickListener(this);
        btnUnLogin.setOnClickListener(clickListener);
    }

    private void initHomeGroup(View.OnClickListener updateClick) {
        realName = mResources.getString(R.string.drawer_realName);
        itemRealName = homeGroupListView.createItemView(UIUtils.zoomDrawable(mActivity, R.mipmap.icon_realname, 16, 16), realName, itemHeight);
        itemRealName.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemRealName.setTextContainerPadding(0, 0, UIUtils.dp2px(10), 0);

        securitySetting = mResources.getString(R.string.drawer_safe);
        itemSecuritySetting = homeGroupListView.createItemView(UIUtils.zoomDrawable(mActivity, R.mipmap.icon_safecenter, 16, 16), securitySetting, itemHeight);
        itemSecuritySetting.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemSecuritySetting.setTextContainerPadding(0, 0, UIUtils.dp2px(20), 0);

        inviting = mResources.getString(R.string.drawer_invite);
        itemInviting = homeGroupListView.createItemView(UIUtils.zoomDrawable(mActivity, R.mipmap.icon_invite, 16, 16), inviting, itemHeight);
        itemInviting.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemInviting.setTextContainerPadding(0, 0, UIUtils.dp2px(20), 0);

        news = mResources.getString(R.string.drawer_news);
        QMUICommonListItemView itemNews = homeGroupListView.createItemView(UIUtils.zoomDrawable(mActivity, R.mipmap.icon_news, 16, 16), news, itemHeight);
        itemNews.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemNews.setTextContainerPadding(0, 0, UIUtils.dp2px(20), 0);

        contactUs = mResources.getString(R.string.drawer_contus);
        QMUICommonListItemView itemContactUs = homeGroupListView.createItemView(UIUtils.zoomDrawable(mActivity, R.mipmap.icon_contactus, 16, 16), contactUs, itemHeight);
        itemContactUs.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemContactUs.setTextContainerPadding(0, 0, UIUtils.dp2px(20), 0);

        /*question = mResources.getString(R.string.drawer_question);
        QMUICommonListItemView itemQuestion = homeGroupListView.createItemView(UIUtils.zoomDrawable(mActivity, R.mipmap.icon_question, 16, 16), question, itemHeight);
        itemQuestion.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemQuestion.setTextContainerPadding(0, 0, UIUtils.dp2px(20), 0);

        myTicket = mResources.getString(R.string.drawer_ticket);
        QMUICommonListItemView itemMyTicket = homeGroupListView.createItemView(UIUtils.zoomDrawable(mActivity, R.mipmap.icon_ticket, 16, 16), myTicket, itemHeight);
        itemMyTicket.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemMyTicket.setTextContainerPadding(0, 0, UIUtils.dp2px(20), 0);*/

        languageSetting = mResources.getString(R.string.drawer_language);
        QMUICommonListItemView itemLanguage = homeGroupListView.createItemView(UIUtils.zoomDrawable(mActivity, R.mipmap.icon_language, 16, 16), languageSetting, itemHeight);
        itemLanguage.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemLanguage.setTextContainerPadding(0, 0, UIUtils.dp2px(20), 0);

        moneySetting = mResources.getString(R.string.drawer_money);
        QMUICommonListItemView itemMoney = homeGroupListView.createItemView(UIUtils.zoomDrawable(mActivity, R.mipmap.icon_money, 16, 16), moneySetting, itemHeight);
        itemMoney.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemMoney.setTextContainerPadding(0, 0, UIUtils.dp2px(20), 0);

        versionCode = mResources.getString(R.string.drawer_nowversion);
        QMUICommonListItemView itemVersionCode = homeGroupListView.createItemView(UIUtils.zoomDrawable(mActivity, R.mipmap.icon_version, 16, 16), versionCode, itemHeight);
        itemVersionCode.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemVersionCode.setTextContainerPadding(0, 0, UIUtils.dp2px(20), 0);
        itemVersionCode.setDetailText(PackageUtils.getVersion());

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof QMUICommonListItemView) {
                    String text = (String) ((QMUICommonListItemView) v).getText();
                    mOnClickListenter(text);
                }
            }
        };

        QMUIGroupListView.newSection(mActivity)
                .setUseTitleViewForSectionSpace(false)
                .addItemView(itemRealName, onClickListener)
                .addItemView(itemSecuritySetting, onClickListener)
                .addItemView(itemInviting, onClickListener)
                .addItemView(itemNews, onClickListener)
                .addItemView(itemContactUs, onClickListener)
                //.addItemView(itemQuestion, onClickListener)
                //.addItemView(itemMyTicket, onClickListener)
                .addItemView(itemLanguage, onClickListener)
                .addItemView(itemMoney, onClickListener)
                .addItemView(itemVersionCode, updateClick)
                .addTo(homeGroupListView);
    }

    public void setLogin(boolean isLogin) {
        if (isLogin) {
            rl_unlogin.setVisibility(View.GONE);
            rl_login.setVisibility(View.VISIBLE);

            tvUser.setText("Hi," + spUtil.getAccount());
            tvUid.setText("UID " + spUtil.getUserUid());

            itemRealName.setVisibility(View.VISIBLE);
            itemSecuritySetting.setVisibility(View.VISIBLE);
            itemInviting.setVisibility(View.VISIBLE);
            btnUnLogin.setVisibility(View.VISIBLE);
        } else {
            rl_unlogin.setVisibility(View.VISIBLE);
            rl_login.setVisibility(View.GONE);

            itemRealName.setVisibility(View.GONE);
            itemSecuritySetting.setVisibility(View.GONE);
            itemInviting.setVisibility(View.GONE);
            btnUnLogin.setVisibility(View.GONE);
        }
    }

    /**
     * 控件跳转
     */
    private void mOnClickListenter(String text) {
        if (TextUtils.equals(text, realName)) {
            jumpTo(AuthActivity.class);
        } else if (TextUtils.equals(text, securitySetting)) {
            jumpTo(SecurityCenterActivity.class);
        } else if (TextUtils.equals(text, inviting)) {
            jumpTo(InvitedinfoActivity.class);
        } else if (TextUtils.equals(text, news)) {
            jumpTo(NoticeActivity.class);
        } else if (TextUtils.equals(text, contactUs)) {
            jumpTo(ContactUsActivity.class);
        } else if (TextUtils.equals(text, question)) {
            jumpTo(WebviewActivity.createActivity(mActivity, Constant.ZENDESK_WENTI));
        } else if (TextUtils.equals(text, myTicket)) {
            String name = "Android";
            String email = "Null";
            if (spUtil.getLogin()) {
                name = String.valueOf(spUtil.getUserUid());
                if (!TextUtils.isEmpty(spUtil.getEmail())) {
                    email = spUtil.getEmail();
                } else if (!TextUtils.isEmpty(spUtil.getPhone())) {
                    email = spUtil.getPhone();
                }
            }

            Identity identity = new AnonymousIdentity.Builder()
                    .withNameIdentifier(name)
                    .withEmailIdentifier(email)
                    .build();
            ZendeskConfig.INSTANCE.setIdentity(identity);
            jumpTo(RequestActivity.class);
        } else if (TextUtils.equals(text, languageSetting)) {
            jumpTo(LanguageActivity.class);
        } else if (TextUtils.equals(text, moneySetting)) {
            jumpTo(ConvertActivity.class);
        }
    }

    public void jumpTo(Class<?> clazz) {
        Intent intent = new Intent(mActivity, clazz);
        mActivity.startActivity(intent);
    }

    public void jumpTo(Intent intent) {
        mActivity.startActivity(intent);
    }
}
