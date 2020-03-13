package com.android.tacu.module.otc.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.MainDrawerLayoutOpenEvent;
import com.android.tacu.EventBus.model.OTCListVisibleHintEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.common.TabAdapter;
import com.android.tacu.module.auth.view.AuthMerchantActivity;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.otc.dialog.OtcDialogUtils;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.widget.popupwindow.ListPopWindow;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundLinearLayout;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class OtcMarketFragment extends BaseFragment implements View.OnTouchListener {

    @BindView(R.id.title)
    QMUITopBar mTopBar;
    @BindView(R.id.tv_money)
    TextView tv_money;
    @BindView(R.id.tv_real_price)
    TextView tv_real_price;
    @BindView(R.id.magic_indicator)
    ScrollIndicatorView magic_indicator;
    @BindView(R.id.vp)
    ViewPager viewPager;

    @BindView(R.id.lin_guanggao)
    QMUIRoundLinearLayout lin_guanggao;
    @BindView(R.id.lin_guanggao2)
    LinearLayout lin_guanggao2;
    @BindView(R.id.tv_guanggao1)
    TextView tv_guanggao1;
    @BindView(R.id.tv_guanggao2)
    TextView tv_guanggao2;

    @BindView(R.id.lin_auth)
    QMUIRoundLinearLayout lin_auth;
    @BindView(R.id.lin_auth2)
    LinearLayout lin_auth2;
    @BindView(R.id.tv_auth1)
    TextView tv_auth1;
    @BindView(R.id.tv_auth2)
    TextView tv_auth2;

    private int currencyId = Constant.ACU_CURRENCY_ID;
    private String currencyNameEn = Constant.ACU_CURRENCY_NAME;

    private List<String> tabTitle = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();

    private ListPopWindow listPopup;

    private Handler mHandler = new Handler();

    public static OtcMarketFragment newInstance() {
        Bundle bundle = new Bundle();
        OtcMarketFragment fragment = new OtcMarketFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (spUtil != null) {
            EventManage.sendEvent(new BaseEvent<>(EventConstant.OTCListVisibleCode, new OTCListVisibleHintEvent(isVisibleToUser)));
        }
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_otc_market;
    }

    @Override
    protected void initData(View view) {
        initTitle();

        tabTitle.add(getResources().getString(R.string.goumai));
        tabTitle.add(getResources().getString(R.string.chushou));

        fragmentList.add(OtcMarketBuySellFragment.newInstance(currencyId, currencyNameEn, true));
        fragmentList.add(OtcMarketBuySellFragment.newInstance(currencyId, currencyNameEn, false));

        magic_indicator.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tab_bg_color));
        magic_indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(getContext(), R.color.text_default), ContextCompat.getColor(getContext(), R.color.tab_text_color)).setSize(14, 14));
        magic_indicator.setScrollBar(new TextWidthColorBar(getContext(), magic_indicator, ContextCompat.getColor(getContext(), R.color.text_default), 4));
        magic_indicator.setSplitAuto(true);

        IndicatorViewPager indicatorViewPager = new IndicatorViewPager(magic_indicator, viewPager);
        indicatorViewPager.setAdapter(new TabAdapter(getChildFragmentManager(), getContext(), tabTitle, fragmentList));
        viewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        viewPager.setCurrentItem(0, false);

        lin_guanggao2.setOnTouchListener(this);
        lin_auth2.setOnTouchListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listPopup != null) {
            listPopup.dismiss();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    @OnClick(R.id.tv_money)
    void moneyClick() {
        showMoneyType(tv_money);
    }

    @OnClick(R.id.lin_guanggao)
    void guanggaoClick() {
        if (!OtcDialogUtils.isDialogShow(getContext())) {
            jumpTo(OtcManageActivity.class);
        }
    }

    @OnClick(R.id.lin_auth)
    void authClick() {
        if (spUtil.getLogin()) {
            jumpTo(AuthMerchantActivity.class);
        } else {
            jumpTo(LoginActivity.class);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.lin_guanggao2:
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    lin_guanggao.setVisibility(View.VISIBLE);
                    lin_guanggao2.setVisibility(View.GONE);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lin_guanggao.setVisibility(View.GONE);
                            lin_guanggao2.setVisibility(View.VISIBLE);
                        }
                    }, 1500);
                }
                break;
            case R.id.lin_auth2:
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    lin_auth.setVisibility(View.VISIBLE);
                    lin_auth2.setVisibility(View.GONE);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lin_auth.setVisibility(View.GONE);
                            lin_auth2.setVisibility(View.VISIBLE);
                        }
                    }, 1500);
                }
                break;
        }
        return true;
    }

    private void initTitle() {
        mTopBar.setTitle(Constant.ACU_CURRENCY_NAME);
        mTopBar.setBackgroundDividerEnabled(true);

        ImageView circleImageView = new ImageView(getContext());
        circleImageView.setBackgroundColor(Color.TRANSPARENT);
        circleImageView.setScaleType(CENTER_CROP);
        circleImageView.setImageResource(R.mipmap.icon_mines);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventManage.sendEvent(new BaseEvent<>(EventConstant.MainDrawerLayoutOpenCode, new MainDrawerLayoutOpenEvent(Constant.MAIN_HOME)));
            }
        });
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(UIUtils.dp2px(20), UIUtils.dp2px(20));
        lps.topMargin = UIUtils.dp2px(15);
        lps.rightMargin = UIUtils.dp2px(8);
        mTopBar.addLeftView(circleImageView, R.id.qmui_topbar_item_left_back, lps);
        mTopBar.addRightImageButton(R.drawable.icon_ordercenter, R.id.qmui_topbar_item_right, 22, 22).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!OtcDialogUtils.isDialogShow(getContext())) {
                    jumpTo(OtcOrderListActivity.class);
                }
            }
        });
    }

    private void showMoneyType(final TextView tv) {
        if (listPopup != null && listPopup.isShowing()) {
            listPopup.dismiss();
            return;
        }
        if (listPopup == null) {
            final List<String> data = new ArrayList<>();
            data.add(getResources().getString(R.string.renminbi));
            ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.simple_list_item, data);
            listPopup = new ListPopWindow(getContext(), adapter);
            listPopup.create(UIUtils.dp2px(120), UIUtils.dp2px(40), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tv.setText(data.get(position));
                    listPopup.dismiss();
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            listPopup.setDropDownGravity(Gravity.START);
        }
        listPopup.setAnchorView(tv);
        listPopup.show();
    }
}
