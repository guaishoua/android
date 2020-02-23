package com.android.tacu.module.main.view;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.module.assets.view.AssetsCenterActivity;
import com.android.tacu.module.auth.view.AuthActivity;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.market.view.NoticeActivity;
import com.android.tacu.module.my.view.ContactUsActivity;
import com.android.tacu.module.my.view.EditPersonalDataActivity;
import com.android.tacu.module.my.view.InvitedinfoActivity;
import com.android.tacu.module.my.view.LanguageActivity;
import com.android.tacu.module.my.view.SecurityCenterActivity;
import com.android.tacu.module.otc.dialog.OtcDialogUtils;
import com.android.tacu.module.otc.view.OtcManageActivity;
import com.android.tacu.module.otc.view.OtcOrderListActivity;
import com.android.tacu.module.webview.view.WebviewActivity;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.PackageUtils;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.utils.user.UserInfoUtils;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
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
    private QMUIRoundButton btnUnLogin;
    private TextView tvUser;
    private TextView tvUid;
    private QMUIRadiusImageView img_login;
    private QMUIGroupListView homeGroupListView;
    private QMUICommonListItemView itemRealName, itemMoney, itemOrderCenter, itemOtcManage, itemSecuritySetting, itemInviting;

    private String realName;
    private String orderCenter;
    private String otcManage;
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
            case R.id.img_login:
                jumpTo(EditPersonalDataActivity.class);
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
        img_login = viewHome.findViewById(R.id.img_login);
        homeGroupListView = viewHome.findViewById(R.id.groupListView);
        initHomeGroup(updateClick);

        img_gologin.setOnClickListener(this);
        tv_gologin.setOnClickListener(this);
        img_login.setOnClickListener(this);
        btnUnLogin.setOnClickListener(clickListener);
    }

    private void initHomeGroup(View.OnClickListener updateClick) {
        realName = mResources.getString(R.string.drawer_realName);
        itemRealName = homeGroupListView.createItemView(ContextCompat.getDrawable(mActivity, R.drawable.icon_realname), realName, itemHeight);
        itemRealName.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemRealName.setTextContainerPadding(0, 0, UIUtils.dp2px(10), 0);

        orderCenter = mActivity.getResources().getString(R.string.order_center);
        itemOrderCenter = homeGroupListView.createItemView(ContextCompat.getDrawable(mActivity, R.drawable.icon_ordercenter), orderCenter, itemHeight);
        itemOrderCenter.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemOrderCenter.setTextContainerPadding(0, 0, UIUtils.dp2px(20), 0);

        otcManage = mActivity.getResources().getString(R.string.otc_manage);
        itemOtcManage = homeGroupListView.createItemView(ContextCompat.getDrawable(mActivity, R.drawable.icon_otc_manage), otcManage, itemHeight);
        itemOtcManage.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemOtcManage.setTextContainerPadding(0, 0, UIUtils.dp2px(20), 0);

        securitySetting = mResources.getString(R.string.drawer_safe);
        itemSecuritySetting = homeGroupListView.createItemView(ContextCompat.getDrawable(mActivity, R.drawable.icon_safecenter), securitySetting, itemHeight);
        itemSecuritySetting.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemSecuritySetting.setTextContainerPadding(0, 0, UIUtils.dp2px(20), 0);

        inviting = mResources.getString(R.string.drawer_invite);
        itemInviting = homeGroupListView.createItemView(ContextCompat.getDrawable(mActivity, R.drawable.icon_invite), inviting, itemHeight);
        itemInviting.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemInviting.setTextContainerPadding(0, 0, UIUtils.dp2px(20), 0);

        news = mResources.getString(R.string.drawer_news);
        QMUICommonListItemView itemNews = homeGroupListView.createItemView(ContextCompat.getDrawable(mActivity, R.drawable.icon_news), news, itemHeight);
        itemNews.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemNews.setTextContainerPadding(0, 0, UIUtils.dp2px(20), 0);

        contactUs = mResources.getString(R.string.drawer_contus);
        QMUICommonListItemView itemContactUs = homeGroupListView.createItemView(ContextCompat.getDrawable(mActivity, R.drawable.icon_contactus), contactUs, itemHeight);
        itemContactUs.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemContactUs.setTextContainerPadding(0, 0, UIUtils.dp2px(20), 0);

        question = mResources.getString(R.string.drawer_question);
        QMUICommonListItemView itemQuestion = homeGroupListView.createItemView(ContextCompat.getDrawable(mActivity, R.drawable.icon_drawer_question), question, itemHeight);
        itemQuestion.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemQuestion.setTextContainerPadding(0, 0, UIUtils.dp2px(20), 0);

        myTicket = mResources.getString(R.string.drawer_ticket);
        QMUICommonListItemView itemMyTicket = homeGroupListView.createItemView(ContextCompat.getDrawable(mActivity, R.drawable.icon_ticket), myTicket, itemHeight);
        itemMyTicket.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemMyTicket.setTextContainerPadding(0, 0, UIUtils.dp2px(20), 0);

        languageSetting = mResources.getString(R.string.drawer_language);
        QMUICommonListItemView itemLanguage = homeGroupListView.createItemView(ContextCompat.getDrawable(mActivity, R.drawable.icon_language), languageSetting, itemHeight);
        itemLanguage.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemLanguage.setTextContainerPadding(0, 0, UIUtils.dp2px(20), 0);

        moneySetting = mResources.getString(R.string.drawer_asset_center);
        itemMoney = homeGroupListView.createItemView(ContextCompat.getDrawable(mActivity, R.drawable.icon_money), moneySetting, itemHeight);
        itemMoney.setPadding(UIUtils.dp2px(15), 0, 0, 0);
        itemMoney.setTextContainerPadding(0, 0, UIUtils.dp2px(20), 0);

        versionCode = mResources.getString(R.string.drawer_nowversion);
        QMUICommonListItemView itemVersionCode = homeGroupListView.createItemView(ContextCompat.getDrawable(mActivity, R.drawable.icon_version), versionCode, itemHeight);
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
                .addItemView(itemMoney, onClickListener)
                .addItemView(itemOrderCenter, onClickListener)
                .addItemView(itemOtcManage, onClickListener)
                .addItemView(itemSecuritySetting, onClickListener)
                .addItemView(itemInviting, onClickListener)
                .addItemView(itemNews, onClickListener)
                .addItemView(itemContactUs, onClickListener)
                .addItemView(itemQuestion, onClickListener)
                .addItemView(itemMyTicket, onClickListener)
                .addItemView(itemLanguage, onClickListener)
                .addItemView(itemVersionCode, updateClick)
                .addTo(homeGroupListView);
    }

    public void setLogin(boolean isLogin) {
        if (isLogin) {
            rl_unlogin.setVisibility(View.GONE);
            rl_login.setVisibility(View.VISIBLE);

            tvUser.setText("Hi," + spUtil.getAccount());
            tvUid.setText("UID " + spUtil.getUserUid());
            if (!TextUtils.isEmpty(spUtil.getHeadImg())) {
                GlideUtils.disPlay(mActivity, Constant.HEAD_IMG_URL + spUtil.getHeadImg(), img_login);
            } else {
                img_login.setImageResource(R.mipmap.img_maindrawer_unlogin);
            }

            itemRealName.setVisibility(View.VISIBLE);
            itemMoney.setVisibility(View.VISIBLE);
            itemOrderCenter.setVisibility(View.VISIBLE);
            itemOtcManage.setVisibility(View.VISIBLE);
            itemSecuritySetting.setVisibility(View.VISIBLE);
            itemInviting.setVisibility(View.VISIBLE);
            btnUnLogin.setVisibility(View.VISIBLE);
        } else {
            rl_unlogin.setVisibility(View.VISIBLE);
            rl_login.setVisibility(View.GONE);

            itemRealName.setVisibility(View.GONE);
            itemMoney.setVisibility(View.GONE);
            itemOrderCenter.setVisibility(View.GONE);
            itemOtcManage.setVisibility(View.GONE);
            itemSecuritySetting.setVisibility(View.GONE);
            itemInviting.setVisibility(View.GONE);
            btnUnLogin.setVisibility(View.GONE);
        }
    }

    /**
     * 解决DrawerLayout展开，头像请求不加载的问题
     */
    public void openDraw() {
        if (spUtil.getLogin() && !TextUtils.isEmpty(spUtil.getHeadImg())) {
            GlideUtils.disPlay(mActivity, Constant.HEAD_IMG_URL + spUtil.getHeadImg(), img_login);
        } else {
            img_login.setImageResource(R.mipmap.img_maindrawer_unlogin);
        }
    }

    /**
     * 控件跳转
     */
    private void mOnClickListenter(String text) {
        if (TextUtils.equals(text, realName)) {
            jumpTo(AuthActivity.class);
        } else if (TextUtils.equals(text, orderCenter)) {
            if (!OtcDialogUtils.isDialogShow(mActivity)) {
                jumpTo(OtcOrderListActivity.class);
            }
        } else if (TextUtils.equals(text, otcManage)) {
            if (!OtcDialogUtils.isDialogShow(mActivity)) {
                jumpTo(OtcManageActivity.class);
            }
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
            jumpTo(AssetsCenterActivity.class);
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
