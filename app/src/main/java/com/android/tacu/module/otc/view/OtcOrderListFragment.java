package com.android.tacu.module.otc.view;

import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.common.MyFragmentPagerAdapter;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.widget.popupwindow.ListPopWindow;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class OtcOrderListFragment extends BaseFragment {

    @BindView(R.id.tv_buy)
    TextView tv_buy;
    @BindView(R.id.tv_sell)
    TextView tv_sell;
    @BindView(R.id.tv_status)
    TextView tv_status;
    @BindView(R.id.view_buy)
    View view_buy;
    @BindView(R.id.view_sell)
    View view_sell;
    @BindView(R.id.con_status)
    ConstraintLayout con_status;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private String[] tabTitle;
    private List<Fragment> fragmentList = new ArrayList<>();
    private OtcOrderFragment buyFragment;
    private OtcOrderFragment sellFragment;

    //买=1 ，卖=2
    private int buyOrSell = 1;
    private int buyOrderStatus = 0;
    private int sellOrderStatus = 0;
    private ListPopWindow listPopup;

    private List<String> data = new ArrayList<>();
    private List<Integer> dataInt = new ArrayList<>();

    public static OtcOrderListFragment newInstance() {
        Bundle bundle = new Bundle();
        OtcOrderListFragment fragment = new OtcOrderListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_otc_list;
    }

    @Override
    protected void initData(View view) {
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        tabTitle = new String[]{getResources().getString(R.string.buy_order), getResources().getString(R.string.sell_order)};
        buyFragment = OtcOrderFragment.newInstance(1, buyOrderStatus);
        sellFragment = OtcOrderFragment.newInstance(2, sellOrderStatus);
        fragmentList.add(buyFragment);
        fragmentList.add(sellFragment);
        viewpager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList));
        viewpager.setOffscreenPageLimit(tabTitle.length - 1);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                buyOrSell = i + 1;
                setBuySellClick();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        data.add(getResources().getString(R.string.otc_order_all));
        data.add(getResources().getString(R.string.otc_order_confirmed));
        data.add(getResources().getString(R.string.wait_deal));
        data.add(getResources().getString(R.string.finished));
        data.add(getResources().getString(R.string.canceled));

        dataInt.add(0);
        dataInt.add(1);
        dataInt.add(20);
        dataInt.add(21);
        dataInt.add(22);

        setBuySellClick();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listPopup != null && listPopup.isShowing()) {
            listPopup.dismiss();
        }
    }

    @OnClick(R.id.tv_buy)
    void buyClick() {
        buyOrSell = 1;
        setBuySellClick();
    }

    @OnClick(R.id.tv_sell)
    void sellClick() {
        buyOrSell = 2;
        setBuySellClick();
    }

    @OnClick(R.id.tv_status)
    void statusClick() {
        showStatusType();
    }

    private void setBuySellClick() {
        tv_buy.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
        tv_sell.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
        view_buy.setVisibility(View.GONE);
        view_sell.setVisibility(View.GONE);

        int status = 0;
        if (buyOrSell == 1) {
            tv_buy.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
            view_buy.setVisibility(View.VISIBLE);
            viewpager.setCurrentItem(0);
            status = buyOrderStatus;
        } else if (buyOrSell == 2) {
            tv_sell.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
            view_sell.setVisibility(View.VISIBLE);
            viewpager.setCurrentItem(1);
            status = sellOrderStatus;
        }

        tv_status.setText(data.get(dataInt.indexOf(status)));
    }

    private void showStatusType() {
        if (listPopup != null && listPopup.isShowing()) {
            listPopup.dismiss();
            return;
        }
        if (listPopup == null) {
            ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.simple_list_item, data);
            listPopup = new ListPopWindow(getContext(), adapter);
            listPopup.create(UIUtils.dp2px(100), UIUtils.dp2px(200), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (buyOrSell == 1) {
                        if (buyFragment != null) {
                            buyOrderStatus = dataInt.get(position);
                            buyFragment.setValue(buyOrderStatus);
                        }
                    } else if (buyOrSell == 2) {
                        if (sellFragment != null) {
                            sellOrderStatus = dataInt.get(position);
                            sellFragment.setValue(sellOrderStatus);
                        }
                    }
                    tv_status.setText(data.get(position));
                    listPopup.dismiss();
                }
            });
        }
        listPopup.showAsDropDown(con_status, 0, 0);
    }
}
