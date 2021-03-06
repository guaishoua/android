package com.android.tacu.module.main.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.security.rp.RPSDK;
import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.MainDrawerLayoutOpenEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.MyFragmentPagerAdapter;
import com.android.tacu.module.payinfo.model.PayInfoModel;
import com.android.tacu.module.assets.view.AssetsFragment;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.main.contract.MainContract;
import com.android.tacu.module.main.model.AliModel;
import com.android.tacu.module.main.model.ConvertModel;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.main.model.UploadModel;
import com.android.tacu.module.main.presenter.MainPresenter;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.model.SelfModel;
import com.android.tacu.module.otc.view.OtcHomeFragment;
import com.android.tacu.module.splash.SplashActivity;
import com.android.tacu.module.transaction.view.TradeFragment;
import com.android.tacu.EventBus.model.MainSwitchEvent;
import com.android.tacu.utils.ActivityStack;
import com.android.tacu.utils.ConvertMoneyUtils;
import com.android.tacu.utils.LogUtils;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.utils.StatusBarUtils;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.utils.downloadfile.AppUpdateUtils;
import com.android.tacu.utils.PackageUtils;
import com.android.tacu.utils.user.UserManageUtils;
import com.android.tacu.widget.NoSlideViewPager;
import com.android.tacu.widget.SonnyJackDragView;
import com.android.tacu.widget.dialog.DroidDialog;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.google.gson.Gson;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity<MainPresenter> implements View.OnClickListener, MainContract.IView, TradeDataBridge {

    @BindView(R.id.drawerLayout_main)
    DrawerLayout drawerMain;
    @BindView(R.id.ll_tab_home)
    LinearLayout ll_tab_home;
    @BindView(R.id.ll_tab_otc)
    LinearLayout ll_tab_otc;
    @BindView(R.id.ll_tab_trade)
    LinearLayout ll_tab_trade;
    @BindView(R.id.ll_tab_assets)
    LinearLayout ll_tab_assets;
    @BindView(R.id.img_home)
    ImageView img_home;
    @BindView(R.id.img_trade)
    ImageView img_trade;
    @BindView(R.id.img_assets)
    ImageView img_assets;
    @BindView(R.id.img_otc)
    ImageView img_otc;
    @BindView(R.id.tv_home)
    TextView tv_home;
    @BindView(R.id.tv_trade)
    TextView tv_trade;
    @BindView(R.id.tv_otc)
    TextView tv_otc;
    @BindView(R.id.tv_assets)
    TextView tv_assets;
    @BindView(R.id.viewpager)
    NoSlideViewPager viewpager;
    @BindView(R.id.view_drawer)
    View viewDrawer;

    private Fragment[] fragments;
    private HomeFragment homeFragment;
    private TradeFragment tradeFragment;
    private OtcHomeFragment otcHomeFragment;
    private AssetsFragment assetsFragment;

    private MainDrawerLayoutHelper mainDrawerLayoutHelper;

    private int lastShowFragment = 0;
    //记录用户首次点击返回键的时间
    private long firstTime = 0;
    private Dialog gaDialog;
    private Gson gson = new Gson();
    //实人认证的时候，认证成功返回需要发送接口通知后端，这时候不能调用ownceterapp接口，因为这时候后端还没有收到认证成功的消息
    private boolean isOwnCenterFlag = true;

    private DroidDialog goVideoAuthDialog;
    private DroidDialog videoAuthFailureDialog;

    /**
     * @param context
     * @param isClearTop A-B-C-D 如果D-B 需要清空C 设置FLAG_ACTIVITY_CLEAR_TOP 和 FLAG_ACTIVITY_SINGLE_TOP(设置这个则B不需要重新创建)
     * @return
     */
    public static Intent createActivity(Context context, boolean isClearTop) {
        Intent intent = new Intent(context, MainActivity.class);
        if (isClearTop) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initView() {
        StatusBarUtils.setColorForDrawerLayout(this, drawerMain, ContextCompat.getColor(this, R.color.content_bg_color), 0);

        spUtil.setFirst(false);

        drawerMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerMain.setFocusableInTouchMode(false);
        drawerMain.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {
            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                if (mainDrawerLayoutHelper != null) {
                    mainDrawerLayoutHelper.openDraw();
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
            }

            @Override
            public void onDrawerStateChanged(int i) {
            }
        });

        ll_tab_home.setOnClickListener(this);
        ll_tab_trade.setOnClickListener(this);
        ll_tab_otc.setOnClickListener(this);
        ll_tab_assets.setOnClickListener(this);

        reboundAnim(ll_tab_home);
        reboundAnim(ll_tab_trade);
        reboundAnim(ll_tab_otc);
        reboundAnim(ll_tab_assets);

        initDragview();
    }

    @Override
    protected MainPresenter createPresenter(MainPresenter mPresenter) {
        return new MainPresenter();
    }

    @Override
    public void onActivityFirstVisible() {
        initFragments();
        setTabSelection(Constant.MAIN_HOME);

        mainDrawerLayoutHelper = new MainDrawerLayoutHelper(MainActivity.this, viewDrawer);
        mainDrawerLayoutHelper.setHomeDrawerMenuView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spUtil.getLogin()) {
                    new DroidDialog.Builder(MainActivity.this)
                            .title(getResources().getString(R.string.logout))
                            .positiveButton(getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                                @Override
                                public void onPositive(Dialog droidDialog) {
                                    logoutSuccess();
                                    tokenInvalid();
                                    mainDrawerLayoutHelper.setLogin(false);
                                }
                            })
                            .negativeButton(getResources().getString(R.string.cancel), null)
                            .show();
                } else {
                    jumpTo(LoginActivity.class);
                }
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.upload(PackageUtils.getVersion(), PackageUtils.getMetaValue(MainActivity.this, PackageUtils.META_NAME), true);
            }
        });
        mainDrawerLayoutHelper.setLogin(spUtil.getLogin());

        //版本更新
        mPresenter.upload(PackageUtils.getVersion(), PackageUtils.getMetaValue(MainActivity.this, PackageUtils.META_NAME), false);
    }

    @Override
    public void onActivityResume() {
        super.onActivityResume();
        if (spUtil.getLogin()) {
            if (isOwnCenterFlag) {
                mPresenter.ownCenter();
            }
            mPresenter.getSelfList();
            mPresenter.selectBank();
        }
        mPresenter.getConvertModel();
        if (mainDrawerLayoutHelper != null) {
            mainDrawerLayoutHelper.setLogin(spUtil.getLogin());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gaDialog != null && gaDialog.isShowing()) {
            gaDialog.dismiss();
            gaDialog = null;
        }
        if (mainDrawerLayoutHelper != null) {
            mainDrawerLayoutHelper.clearActivity();
            mainDrawerLayoutHelper = null;
        }
        if (goVideoAuthDialog != null && goVideoAuthDialog.isShowing()) {
            goVideoAuthDialog.dismiss();
        }
        if (videoAuthFailureDialog != null && videoAuthFailureDialog.isShowing()) {
            videoAuthFailureDialog.dismiss();
        }

        //应用退出 就清空所有粘性事件 因为这部分是存在内存中的
        EventManage.removeAllStickyEvent();
        AppUpdateUtils.cancel();
        AppUpdateUtils.clear();
    }

    @Override
    public void onBackPressed() {
        if (drawerMain.isDrawerOpen(Gravity.LEFT)) {
            drawerMain.closeDrawer(Gravity.LEFT);
            return;
        }
        if (System.currentTimeMillis() - firstTime > 2000) {
            showToast(getResources().getString(R.string.exit));
            firstTime = System.currentTimeMillis();
        } else {
            ActivityStack.getInstance().finishActivity(SplashActivity.class);
            finish();
            MobclickAgent.onKillProcess(getApplicationContext());
            System.exit(0);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_tab_home:
                setTabSelection(Constant.MAIN_HOME);
                break;
            case R.id.ll_tab_trade:
                setTabSelection(Constant.MAIN_TRADE);
                break;
            case R.id.ll_tab_otc:
                setTabSelection(Constant.MAIN_OTC);
                break;
            case R.id.ll_tab_assets:
                setTabSelection(Constant.MAIN_ASSETS);
                break;
        }
    }

    @Override
    public void upload(final UploadModel model, final boolean isTip) {
        if (model != null) {
            if (PackageUtils.splitVersionNum(model.nowVersion, PackageUtils.getVersion())) {
                AppUpdateUtils.showSimpleUpdate(MainActivity.this, model);
            } else if (isTip) {
                showToast(getResources().getString(R.string.update_hint));
            }
        }
    }

    @Override
    public void ownCenter(OwnCenterModel model) {
        UserManageUtils.setPersonInfo(model);
        //必须登录并且KYC2通过
        if (spUtil.getIsAuthSenior() == 2 && spUtil.getIsAuthVideo() != 2) {
            if (model.isChina != null && model.isChina == 1) {
                showALAuth();
            }
        } else if (spUtil.getIsAuthSenior() == 2 && spUtil.getIsAuthVideo() == 2) {
            if (goVideoAuthDialog != null && goVideoAuthDialog.isShowing()) {
                goVideoAuthDialog.dismiss();
            }
            if (videoAuthFailureDialog != null && videoAuthFailureDialog.isShowing()) {
                videoAuthFailureDialog.dismiss();
            }
        }
    }

    @Override
    public void getSelfSelectionValue(SelfModel selfModel) {
        if (selfModel == null) {
            selfModel = new SelfModel();
        }
        SPUtils.getInstance().put(Constant.SELFCOIN_LIST, gson.toJson(selfModel));
    }

    @Override
    public void convertMoney(ConvertModel model) {
        SPUtils.getInstance().put(Constant.CONVERT_CACHE, gson.toJson(model));
        if (!ConvertMoneyUtils.hasValue()) {
            ConvertMoneyUtils.setHttpConvertBean(model);
            if (homeFragment != null) {
                homeFragment.setConvertModel();
            }
            if (assetsFragment != null) {
                assetsFragment.setConvertModel();
            }
        }
        ConvertMoneyUtils.setHttpConvertBean(model);
    }

    @Override
    public void selectBank(List<PayInfoModel> list) {
        UserManageUtils.setPeoplePayInfo(list);
    }

    @Override
    public void getVerifyToken(AliModel model) {
        if (model != null && !TextUtils.isEmpty(model.token)) {
            isOwnCenterFlag = false;
            RPSDK.start(model.token, this, new RPSDK.RPCompletedListener() {
                @Override
                public void onAuditResult(RPSDK.AUDIT audit, String code) {
                    LogUtils.i("jiazhen", "audit=" + audit + " code=" + code);
                    if (audit == RPSDK.AUDIT.AUDIT_PASS) {
                        //认证通过。建议接入方调用实人认证服务端接口DescribeVerifyResult来获取最终的认证状态，并以此为准进行业务上的判断和处理
                        mPresenter.vedioAuth();
                    } else if (audit == RPSDK.AUDIT.AUDIT_FAIL) {
                        showALAuthFailure(true);
                        isOwnCenterFlag = true;
                        //认证不通过。建议接入方调用实人认证服务端接口DescribeVerifyResult来获取最终的认证状态，并以此为准进行业务上的判断和处理
                    } else if (audit == RPSDK.AUDIT.AUDIT_NOT) {
                        showALAuthFailure(true);
                        isOwnCenterFlag = true;
                        //未认证，具体原因可通过code来区分（code取值参见下方表格），通常是用户主动退出或者姓名身份证号实名校验不匹配等原因，导致未完成认证流程
                        /**
                         * 1=认证通过
                         * 2-12 表示认证不通过，具体的不通过原因可以查看服务端的查询认证结果（DescribeVerifyResult）接口文档中认证状态的表格说明。
                         * -1=未完成认证，原因：用户在认证过程中，主动退出。
                         * 3001=未完成认证，原因：认证token无效或已过期。
                         * 3101=未完成认证，原因：用户姓名身份证实名校验不匹配。
                         * 3102=未完成认证，原因：实名校验身份证号不存在。
                         * 3103=未完成认证，原因：实名校验身份证号不合法。
                         * 3104=未完成认证，原因：认证已通过，重复提交。
                         * 3204=未完成认证，原因：非本人操作。
                         * 3206=未完成认证，原因：非本人操作。
                         * 3208=未完成认证，原因：公安网无底照。
                         */
                    }
                }
            });
        }
    }

    @Override
    public void getVerifyTokenError(int status) {
        LogUtils.i("jiazhen", "status=" + status);
        if (status == -1000) {//超过三次机会
            showALAuthFailure(false);
        } else if (status == -1002) {//已经认证成功的
            if (goVideoAuthDialog != null && goVideoAuthDialog.isShowing()) {
                goVideoAuthDialog.dismiss();
            }
            if (videoAuthFailureDialog != null && videoAuthFailureDialog.isShowing()) {
                videoAuthFailureDialog.dismiss();
            }
        } else {
            showALAuthFailure(true);
        }
    }

    @Override
    public void vedioAuth() {
        if (goVideoAuthDialog != null && goVideoAuthDialog.isShowing()) {
            goVideoAuthDialog.dismiss();
        }
        if (videoAuthFailureDialog != null && videoAuthFailureDialog.isShowing()) {
            videoAuthFailureDialog.dismiss();
        }
        spUtil.setIsAuthVideo(2);
        isOwnCenterFlag = true;
        mPresenter.ownCenter();
    }

    @Override
    protected void receiveEvent(BaseEvent event) {
        super.receiveEvent(event);
        if (event != null) {
            switch (event.getCode()) {
                case EventConstant.JumpTradeIsBuyCode:
                    setTabSelection(Constant.MAIN_TRADE);
                    break;
                case EventConstant.MainSwitchCode:
                    MainSwitchEvent mainSwitchEvent = (MainSwitchEvent) event.getData();
                    if (mainSwitchEvent != null) {
                        if (drawerMain.isDrawerOpen(Gravity.LEFT)) {
                            drawerMain.closeDrawer(Gravity.LEFT);
                        }
                        switch (mainSwitchEvent.getMainSwitch()) {
                            case Constant.MAIN_HOME:
                                setTabSelection(Constant.MAIN_HOME);
                                break;
                            case Constant.MAIN_TRADE:
                                setTabSelection(Constant.MAIN_TRADE);
                                break;
                            case Constant.MAIN_OTC:
                                setTabSelection(Constant.MAIN_OTC);
                                break;
                            case Constant.MAIN_ASSETS:
                                setTabSelection(Constant.MAIN_ASSETS);
                                break;
                        }
                    }
                    break;
                case EventConstant.MainDrawerLayoutOpenCode:
                    MainDrawerLayoutOpenEvent mainDrawerLayoutOpenEvent = (MainDrawerLayoutOpenEvent) event.getData();
                    if (mainDrawerLayoutOpenEvent != null) {
                        switch (mainDrawerLayoutOpenEvent.getMainSwitch()) {
                            case Constant.MAIN_TRADE:
                            case Constant.MAIN_HOME:
                                if (!drawerMain.isDrawerOpen(Gravity.LEFT)) {
                                    drawerMain.openDrawer(Gravity.LEFT);
                                } else {
                                    drawerMain.closeDrawer(Gravity.LEFT);
                                }
                                break;
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public List<MarketNewModel.TradeCoinsBean> getTradeList() {
        if (homeFragment != null)
            return homeFragment.getTotalTradeList();
        return null;
    }

    private void logoutSuccess() {
        if (viewpager.getCurrentItem() != 0 && viewpager.getCurrentItem() != 1) {
            setTabSelection(Constant.MAIN_HOME);
        }
        mPresenter.logout();
        showToastSuccess(getResources().getString(R.string.logout_success));
    }

    private void initFragments() {
        homeFragment = HomeFragment.newInstance();
        tradeFragment = TradeFragment.newInstance();
        otcHomeFragment = OtcHomeFragment.newInstance();
        assetsFragment = AssetsFragment.newInstance();

        fragments = new Fragment[]{homeFragment, tradeFragment, otcHomeFragment, assetsFragment};
        viewpager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments));
        viewpager.setOffscreenPageLimit(3);
    }

    private void setTabSelection(int pos) {
        clearSelection(pos);

        switch (pos) {
            case Constant.MAIN_HOME:
                lastShowFragment = Constant.MAIN_HOME;
                img_home.setImageResource(R.mipmap.img_main_home_selected);
                tv_home.setTextColor(ContextCompat.getColor(this, R.color.main_tab_black_color));
                break;
            case Constant.MAIN_TRADE:
                lastShowFragment = Constant.MAIN_TRADE;
                img_trade.setImageResource(R.mipmap.img_main_trade_selected);
                tv_trade.setTextColor(ContextCompat.getColor(this, R.color.main_tab_black_color));
                break;
            case Constant.MAIN_OTC:
                lastShowFragment = Constant.MAIN_OTC;
                img_otc.setImageResource(R.mipmap.img_main_otc_selected);
                tv_otc.setTextColor(ContextCompat.getColor(this, R.color.main_tab_black_color));
                break;
            case Constant.MAIN_ASSETS:
                if (spUtil.getLogin()) {
                    lastShowFragment = Constant.MAIN_ASSETS;
                    img_assets.setImageResource(R.mipmap.img_main_asset_selected);
                    tv_assets.setTextColor(ContextCompat.getColor(this, R.color.main_tab_black_color));
                } else {
                    jumpTo(LoginActivity.class);
                }
                break;
        }

        if (pos == Constant.MAIN_ASSETS && !spUtil.getLogin()) {
            return;
        }
        viewpager.setCurrentItem(lastShowFragment >= Constant.MAIN_HOME ? lastShowFragment - Constant.MAIN_HOME : 0, false);
    }

    private void clearSelection(int pos) {
        if (pos == Constant.MAIN_ASSETS && spUtil != null && !spUtil.getLogin()) {
            return;
        }
        img_home.setImageResource(R.mipmap.img_main_home_normal);
        img_trade.setImageResource(R.mipmap.img_main_trade_normal);
        img_otc.setImageResource(R.mipmap.img_main_otc_normal);
        img_assets.setImageResource(R.mipmap.img_main_asset_normal);

        tv_home.setTextColor(ContextCompat.getColor(this, R.color.main_tab_text_color));
        tv_trade.setTextColor(ContextCompat.getColor(this, R.color.main_tab_text_color));
        tv_assets.setTextColor(ContextCompat.getColor(this, R.color.main_tab_text_color));
        tv_otc.setTextColor(ContextCompat.getColor(this, R.color.main_tab_text_color));
    }

    private void initDragview() {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.img_customer);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerClick();
            }
        });
        new SonnyJackDragView.Builder()
                .setActivity(this)//当前Activity，不可为空
                .setDefaultRight(UIUtils.dp2px(30))//初始位置右边距
                .setDefaultBottom(UIUtils.dp2px(200))//初始位置底部边距
                .setSize(UIUtils.dp2px(50))//DragView大小
                .setView(imageView)//设置自定义的DragView，切记不可为空
                .build();
    }

    private void showALAuth() {
        if (videoAuthFailureDialog != null && videoAuthFailureDialog.isShowing()) {
            return;
        }
        if (goVideoAuthDialog != null && goVideoAuthDialog.isShowing()) {
            return;
        }
        goVideoAuthDialog = new DroidDialog.Builder(MainActivity.this)
                .title(getResources().getString(R.string.friendly_tip))
                .content(getResources().getString(R.string.please_go_shirenrenzheng))
                .contentGravity(Gravity.CENTER)
                .positiveButton(getResources().getString(R.string.go_auth), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        mPresenter.getVerifyToken();
                    }
                })
                .cancelable(false, false)
                .show();
    }

    private void showALAuthFailure(boolean isShowGoAuth) {
        if (goVideoAuthDialog != null && goVideoAuthDialog.isShowing()) {
            goVideoAuthDialog.dismiss();
        }
        if (videoAuthFailureDialog != null && videoAuthFailureDialog.isShowing()) {
            return;
        }
        View view = View.inflate(this, R.layout.view_video_failure, null);
        TextView tv_uid = view.findViewById(R.id.tv_uid);
        tv_uid.setText(String.valueOf(spUtil.getUserUid()));
        if (isShowGoAuth) {
            videoAuthFailureDialog = new DroidDialog.Builder(MainActivity.this)
                    .title(getResources().getString(R.string.authentication_failed))
                    .viewCustomLayout(view)
                    .positiveButton(getResources().getString(R.string.go_auth), new DroidDialog.onPositiveListener() {
                        @Override
                        public void onPositive(Dialog droidDialog) {
                            mPresenter.getVerifyToken();
                        }
                    })
                    .cancelable(false, false)
                    .show();
        } else {
            videoAuthFailureDialog = new DroidDialog.Builder(MainActivity.this)
                    .title(getResources().getString(R.string.authentication_failed))
                    .viewCustomLayout(view)
                    .cancelable(false, false)
                    .show();
        }
    }

    private void customerClick() {
        String title = "";
        /**
         * 设置访客来源，标识访客是从哪个页面发起咨询的，用于客服了解用户是从什么页面进入。
         * 三个参数分别为：来源页面的url，来源页面标题，来源页面额外信息（保留字段，暂时无用）。
         * 设置来源后，在客服会话界面的"用户资料"栏的页面项，可以看到这里设置的值。
         */
        String sourceUrl = null;
        String sourceTitle = null;
        switch (lastShowFragment) {
            case Constant.MAIN_HOME:
                sourceUrl = "HomeFragment";
                sourceTitle = "行情首页";
                break;
            case Constant.MAIN_TRADE:
                sourceUrl = "TradeFragment";
                sourceTitle = "交易页面";
                break;
            case Constant.MAIN_OTC:
                sourceUrl = "OtcMarketListFragment";
                sourceTitle = "OTC页面";
                break;
            case Constant.MAIN_ASSETS:
                sourceUrl = "AssetsFragment";
                sourceTitle = "资产页面";
                break;
        }
        ConsultSource source = new ConsultSource(sourceUrl, sourceTitle, "custom information string");
        /**
         * 请注意： 调用该接口前，应先检查Unicorn.isServiceAvailable()，
         * 如果返回为false，该接口不会有任何动作
         *
         * @param context 上下文
         * @param title   聊天窗口的标题
         * @param source  咨询的发起来源，包括发起咨询的url，title，描述信息等
         */
        Unicorn.openServiceActivity(this, title, source);
    }

    /**
     * FaceBook的弹簧效果
     *
     * @param linView
     */
    private void reboundAnim(final LinearLayout linView) {
        SpringSystem springSystem = SpringSystem.create();
        final Spring spring = springSystem.createSpring();
        spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(100, 10));
        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float value = (float) spring.getCurrentValue();
                float scale = value;
                linView.setScaleX(scale);
                linView.setScaleY(scale);
            }
        });
        linView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        spring.setEndValue(1.2f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        spring.setEndValue(1.0f);
                        break;
                }
                return false;
            }
        });
        spring.setEndValue(1.0f);
    }
}
