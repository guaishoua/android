package com.android.tacu.module.assets.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.assets.contract.AssetsContract;
import com.android.tacu.module.assets.model.AssetDetailsModel;
import com.android.tacu.module.assets.model.CoinListModel;
import com.android.tacu.module.assets.model.MoneyFlowEvent;
import com.android.tacu.module.assets.model.RecordEvent;
import com.android.tacu.module.assets.presenter.AssetsPresenter;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.widget.ControlScrollViewPager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qmuiteam.qmui.alpha.QMUIAlphaImageButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.android.tacu.api.Constant.OTC_SELECT_COIN_CACHE;

/**
 * Created by xiaohong on 2018/9/27.
 */

public class AssetsActivity extends BaseActivity<AssetsPresenter> implements AssetsContract.ICurrencyView {
    @BindView(R.id.viewpager)
    ControlScrollViewPager viewpager;

    QMUIAlphaImageButton recordBtn;

    //标记标题
    private int flags;
    //标记当前功能(查看记录使用)
    private int current = 0;
    private String recharge;
    private String take;
    private String otcTransfer;
    private String c2cTransfer;
    private String otcC2cTransfer;
    private int currencyId = Constant.TAC_CURRENCY_ID;
    private String currencyNameEn = Constant.TAC_CURRENCY_NAME;
    private FragmentAdapter fragmentAdapter;

    private RechargeFragment rechargeFragment;
    private TakeCoinFragment takeCoinFragment;
    private OTCTransferFragment otcTransferFragment;
    private C2CTransferFragment c2cTransferFragment;
    private OTCC2CTransferFragment otcC2cTransferFragment;

    private Gson gson = new Gson();
    private List<Fragment> fragmentList;
    //划转的币种选择数据
    private List<CoinListModel.AttachmentBean> transferList = new ArrayList<>();

    private AssetDrawerLayoutHelper layoutHelper;

    /**
     * @param context
     * @param currencyNameEn
     * @param currencyId
     * @param flags          第几个 0=充币 1=提币 2=otc划转 3=c2c划转 4=otc划转c2c
     * @param isDetails      true：不用默认的btc
     * @return
     */
    public static Intent createActivity(Context context, String currencyNameEn, int currencyId, int flags, boolean isDetails) {
        Intent intent = new Intent(context, AssetsActivity.class);
        intent.putExtra("currencyNameEn", currencyNameEn);
        intent.putExtra("currencyId", currencyId);
        intent.putExtra("flags", flags);
        intent.putExtra("isDetails", isDetails);
        return intent;
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                initDatas();
                initFragments();
            }
        });
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_asset);
    }

    @Override
    protected void initView() {
        getIntentData();
        mTopBar.setTitle(currencyNameEn);
        recordBtn = mTopBar.addRightImageButton(R.mipmap.icon_assets_history, R.id.qmui_topbar_item_right, 18, 18);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current == 2 || current == 3 || current == 4) {
                    MoneyFlowEvent event = new MoneyFlowEvent(
                            getString(R.string.all),
                            "0",
                            currencyId,
                            currencyNameEn,
                            "",
                            ""
                    );

                    event.setType("0");
                    jumpTo(MoneyFlowActivity.createActivity(AssetsActivity.this, event));
                } else {
                    RecordEvent event = new RecordEvent(currencyId, currencyNameEn, "", "");
                    jumpTo(RecordActivity.createActivity(AssetsActivity.this, current, event));
                }
            }
        });
    }

    @Override
    protected AssetsPresenter createPresenter(AssetsPresenter mPresenter) {
        return new AssetsPresenter();
    }

    @Override
    protected void onPresenterCreated(AssetsPresenter presenter) {
        super.onPresenterCreated(presenter);
        mPresenter.coins(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (layoutHelper != null) {
            layoutHelper.clearActivity();
            layoutHelper = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (flags == 1 && takeCoinFragment.hackBackPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected boolean onTopBarBackPressed() {
        if (flags == 1 && takeCoinFragment.hackBackPress()) {
            return true;
        }
        return super.onTopBarBackPressed();
    }

    @Override
    protected void receiveEvent(BaseEvent event) {
        super.receiveEvent(event);
        if (event != null) {
            switch (event.getCode()) {
                case EventConstant.TakeCode:
                    boolean isShow = (boolean) event.getData();
                    recordBtn.setVisibility(isShow == true ? View.VISIBLE : View.GONE);
                    break;
            }
        }
    }

    private void getIntentData() {
        flags = getIntent().getIntExtra("flags", 0);
        if (getIntent().getBooleanExtra("isDetails", true)) {
            currencyNameEn = getIntent().getStringExtra("currencyNameEn");
            currencyId = getIntent().getIntExtra("currencyId", 0);
        }

        current = flags;
    }

    private void initDatas() {
        String transfer = SPUtils.getInstance().getString(OTC_SELECT_COIN_CACHE);
        List<AssetDetailsModel.CoinListBean> list = gson.fromJson(transfer, new TypeToken<List<AssetDetailsModel.CoinListBean>>() {
        }.getType());

        CoinListModel.AttachmentBean bean;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                bean = new CoinListModel.AttachmentBean();
                bean.currencyId = list.get(i).currencyId;
                bean.currencyNameEn = list.get(i).currencyNameEn;
                transferList.add(bean);
            }
        }
    }

    private void initTabTitle() {
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewpager.setOffscreenPageLimit(fragmentList.size() - 1);
        viewpager.setAdapter(fragmentAdapter);
        viewpager.setCurrentItem(current);
    }

    private void initFragments() {
        recharge = getResources().getString(R.string.recharge);
        take = getResources().getString(R.string.withdrawals);
        otcTransfer = getResources().getString(R.string.transfer);
        c2cTransfer = getResources().getString(R.string.transfer);
        otcC2cTransfer = getResources().getString(R.string.transfer);

        switch (flags) {
            case 0:
                mTopBar.setTitle(currencyNameEn + recharge);
                break;
            case 1:
                mTopBar.setTitle(currencyNameEn + take);
                break;
            case 2:
                mTopBar.setTitle(currencyNameEn + otcTransfer);
                break;
            case 3:
                mTopBar.setTitle(currencyNameEn + c2cTransfer);
                break;
            case 4:
                mTopBar.setTitle(currencyNameEn + otcC2cTransfer);
                break;
        }
        fragmentList = new ArrayList<>();

        boolean rechargeFlag = false, takeCoinFlag = false, otcTransferFlag = false, c2cTransferFlag = false, otcC2cTransferFlag = false;
        switch (current) {
            case 0:
                rechargeFlag = true;
                break;
            case 1:
                takeCoinFlag = true;
                break;
            case 2:
                otcTransferFlag = true;
                break;
            case 3:
                c2cTransferFlag = true;
                break;
            case 4:
                otcC2cTransferFlag = true;
                break;
        }
        rechargeFragment = RechargeFragment.newInstance(currencyId, currencyNameEn, rechargeFlag);
        takeCoinFragment = TakeCoinFragment.newInstance(currencyId, currencyNameEn, takeCoinFlag);
        otcTransferFragment = OTCTransferFragment.newInstance(currencyId, currencyNameEn, otcTransferFlag);
        c2cTransferFragment = C2CTransferFragment.newInstance(currencyId, currencyNameEn, c2cTransferFlag);
        otcC2cTransferFragment = OTCC2CTransferFragment.newInstance(currencyId, currencyNameEn, otcC2cTransferFlag);
        fragmentList.add(rechargeFragment);
        fragmentList.add(takeCoinFragment);
        fragmentList.add(otcTransferFragment);
        fragmentList.add(c2cTransferFragment);
        fragmentList.add(otcC2cTransferFragment);

        initTabTitle();
    }

    @Override
    public void currencyView(List<CoinListModel.AttachmentBean> attachment) {
        if (attachment != null && attachment.size() > 0) {
            SPUtils.getInstance().put(Constant.SELECT_COIN_NOGROUP_CACHE, gson.toJson(attachment));
        }
    }

    private class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList == null ? 0 : fragmentList.size();
        }
    }
}
