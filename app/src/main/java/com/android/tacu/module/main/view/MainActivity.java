package com.android.tacu.module.main.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.MainDrawerLayoutOpenEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.assets.view.AssetsFragment;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.main.contract.MainContract;
import com.android.tacu.module.main.model.ConvertModel;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.main.model.UploadModel;
import com.android.tacu.module.main.presenter.MainPresenter;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.model.SelfModel;
import com.android.tacu.module.otc.view.OtcHomeFragment;
import com.android.tacu.module.transaction.view.TradeFragment;
import com.android.tacu.EventBus.model.MainSwitchEvent;
import com.android.tacu.utils.ConvertMoneyUtils;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.utils.StatusBarUtils;
import com.android.tacu.utils.downloadfile.AppUpdateUtils;
import com.android.tacu.utils.PackageUtils;
import com.android.tacu.utils.user.UserManageUtils;
import com.android.tacu.widget.NoSlideViewPager;
import com.android.tacu.widget.dialog.DroidDialog;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.google.gson.Gson;

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

    /**
     * @param context
     * @param isClearTask 为true的情况下  清空所有的栈
     * @return
     */
    public static Intent createActivity(Context context, boolean isClearTask) {
        Intent intent = new Intent(context, MainActivity.class);
        if (isClearTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            lastShowFragment = savedInstanceState.getInt("lastfragment", 0);
            setTabSelection(lastShowFragment);
        }
        super.onCreate(savedInstanceState);
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

        initFragments();
        setTabSelection(Constant.MAIN_HOME);
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        //延时2秒加载侧边栏
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
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
            }
        }, 2000);

        //版本更新延时4秒加载 因为权限的弹窗影响控件的加载数据
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                //版本更新
                mPresenter.upload(PackageUtils.getVersion(), PackageUtils.getMetaValue(MainActivity.this, PackageUtils.META_NAME), false);
            }
        }, 4000);
    }

    @Override
    protected MainPresenter createPresenter(MainPresenter mPresenter) {
        return new MainPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (spUtil.getLogin()) {
            mPresenter.ownCenter();
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

        //应用退出 就清空所有粘性事件 因为这部分是存在内存中的
        EventManage.removeAllStickyEvent();
        AppUpdateUtils.cancel();
        AppUpdateUtils.clear();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("lastfragment", lastShowFragment);
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
            finish();
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
        UserManageUtils.setPersonInfo(model, null);
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
        UserManageUtils.setPersonInfo(null, list);
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

    private void logoutSuccess() {
        if (viewpager.getCurrentItem() != 1 && viewpager.getCurrentItem() != 2) {
            setTabSelection(0);
        }
        mPresenter.logout();
        showToastSuccess(getResources().getString(R.string.logout_success));
    }

    @Override
    public List<MarketNewModel.TradeCoinsBean> getTradeList() {
        if (homeFragment != null)
            return homeFragment.getTotalTradeList();
        return null;
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private Fragment[] list;

        public MyFragmentPagerAdapter(FragmentManager fm, Fragment[] list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list[position];
        }

        @Override
        public int getCount() {
            return list.length;
        }
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

    /*private void otcClick() {
        if (!spUtil.getLogin()) {
            jumpTo(LoginActivity.class);
        } else if (spUtil.getIsAuthSenior() == -1 || spUtil.getIsAuthSenior() == 0 || spUtil.getIsAuthSenior() == 1) {
            showToastError(getResources().getString(R.string.please_get_the_level_of_KYC));
        } else if (!spUtil.getPhoneStatus()) {
            showToastError(getResources().getString(R.string.please_bind_phone));
        } else {
            setTabSelection(Constant.MAIN_OTC);
        }
    }*/

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
