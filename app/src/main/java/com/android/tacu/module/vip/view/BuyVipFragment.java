package com.android.tacu.module.vip.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.vip.contract.BuyVipContract;
import com.android.tacu.module.vip.presenter.BuyVipPresenter;
import com.android.tacu.widget.popupwindow.OtcPopWindow;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import butterknife.BindView;

public class BuyVipFragment extends BaseFragment<BuyVipPresenter> implements BuyVipContract.IChildView {

    @BindView(R.id.tv_vip_name)
    TextView tv_vip_name;
    @BindView(R.id.lin_vip_good)
    LinearLayout lin_vip_good;
    @BindView(R.id.lin_need_pay)
    LinearLayout lin_need_pay;
    @BindView(R.id.lin_trade_pwd)
    LinearLayout lin_trade_pwd;
    @BindView(R.id.tv_error)
    TextView tv_error;
    @BindView(R.id.btn)
    QMUIRoundButton btn;

    private TextView tv_tip;
    private TextView tv_tip1;
    private TextView tv_tip2;
    private ImageView img_success;

    //1=月度会员(30天)  2=年度会员(12月) 3=连续包年
    private int vipType = 0;
    //true=当前页面是支付页面
    private boolean isPay = false;
    //0=购买会员 1=去认证 2=确认支付
    private int btnStatus = 0;

    private OtcPopWindow popWindow;

    public static BuyVipFragment newInstance(int vipType, boolean isPay) {
        Bundle bundle = new Bundle();
        bundle.putInt("vipType", vipType);
        bundle.putBoolean("isPay", isPay);
        BuyVipFragment fragment = new BuyVipFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            vipType = bundle.getInt("vipType");
            isPay = bundle.getBoolean("isPay");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_buy_vip;
    }

    @Override
    protected void initData(View view) {
        switch (vipType) {
            case 1:
                tv_vip_name.setText(getResources().getString(R.string.vip_month));
                break;
            case 2:
                tv_vip_name.setText(getResources().getString(R.string.vip_year));
                break;
            case 3:
                tv_vip_name.setText(getResources().getString(R.string.vip_year_continue));
                break;
        }
        if (!isPay) {
            lin_vip_good.setVisibility(View.VISIBLE);
            lin_need_pay.setVisibility(View.GONE);
            lin_trade_pwd.setVisibility(View.GONE);
            btnStatus = 0;
        } else {
            lin_vip_good.setVisibility(View.GONE);
            lin_need_pay.setVisibility(View.VISIBLE);
            lin_trade_pwd.setVisibility(spUtil.getPwdVisibility() ? View.VISIBLE : View.GONE);
            btnStatus = 2;
        }
        if (spUtil.getIsAuthSenior() == 2) {
            tv_error.setVisibility(View.GONE);
            btnStatus = 0;
        } else if (spUtil.getIsAuthSenior() < 2) {
            tv_error.setVisibility(View.VISIBLE);
            btnStatus = 1;
        }
        showBtn();
    }

    @Override
    protected BuyVipPresenter createPresenter(BuyVipPresenter mPresenter) {
        return new BuyVipPresenter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
        }
    }

    private void showBtn() {
        switch (btnStatus) {
            case 0:
                btn.setText(getResources().getString(R.string.buy_vip));
                break;
            case 1:
                btn.setText(getResources().getString(R.string.go_auth));
                break;
            case 2:
                btn.setText(getResources().getString(R.string.confirm_pay));
                break;
        }
    }

    private void buyVipSuccessPop() {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
            return;
        }

        if (popWindow == null) {
            View view = View.inflate(getContext(), R.layout.pop_vip, null);
            tv_tip = view.findViewById(R.id.tv_tip);
            tv_tip1 = view.findViewById(R.id.tv_tip1);
            tv_tip2 = view.findViewById(R.id.tv_tip2);
            img_success = view.findViewById(R.id.img_success);

            switch (vipType) {
                case 1:
                    tv_tip.setText(String.format(getResources().getString(R.string.buy_success_tip), getResources().getString(R.string.vip_month)));
                    tv_tip1.setVisibility(View.GONE);
                    tv_tip2.setVisibility(View.GONE);
                    img_success.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    tv_tip.setText(String.format(getResources().getString(R.string.buy_success_tip), getResources().getString(R.string.vip_year)));
                    tv_tip1.setVisibility(View.GONE);
                    tv_tip2.setVisibility(View.GONE);
                    img_success.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    tv_tip.setText(String.format(getResources().getString(R.string.buy_success_tip), getResources().getString(R.string.vip_year_continue)));
                    tv_tip1.setVisibility(View.VISIBLE);
                    tv_tip2.setVisibility(View.VISIBLE);
                    img_success.setVisibility(View.GONE);
                    break;
            }

            popWindow = new OtcPopWindow(getContext());
            popWindow.create(view);
            popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    popWindow.setBg(1F);
                }
            });
        }
        popWindow.setBg(0.3F);
        popWindow.showAtLocation(getHostActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }
}
