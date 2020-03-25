package com.android.tacu.module.otc.view;

import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.otc.contract.OtcTradeRulesContract;
import com.android.tacu.module.otc.presenter.OtcTradeRulesPresenter;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

import butterknife.BindView;
import butterknife.OnClick;

public class OtcTradeRulesActivity extends BaseActivity<OtcTradeRulesPresenter> implements OtcTradeRulesContract.IView {

    @BindView(R.id.cb_xieyi)
    CheckBox cb_xieyi;
    @BindView(R.id.tv_xieyi)
    TextView tv_xieyi;
    @BindView(R.id.btn_sure)
    QMUIRoundButton btn_sure;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_trade_rules);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.otc_trade_rules));

        tv_xieyi.setText(Html.fromHtml(getResources().getString(R.string.otc_xieyi) + "<font color=" + ContextCompat.getColor(this, R.color.color_otc_unhappy) + ">" + getResources().getString(R.string.otc_xieyi_name) + "</font>"));
        cb_xieyi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ((QMUIRoundButtonDrawable) btn_sure.getBackground()).setBgData(ContextCompat.getColorStateList(OtcTradeRulesActivity.this, R.color.color_default));
                    btn_sure.setEnabled(true);
                } else {
                    ((QMUIRoundButtonDrawable) btn_sure.getBackground()).setBgData(ContextCompat.getColorStateList(OtcTradeRulesActivity.this, R.color.color_grey));
                    btn_sure.setEnabled(false);
                }
            }
        });
    }

    @Override
    protected OtcTradeRulesPresenter createPresenter(OtcTradeRulesPresenter mPresenter) {
        return new OtcTradeRulesPresenter();
    }

    @OnClick(R.id.btn_sure)
    void sureClick() {
        mPresenter.disclaimer();
    }

    @Override
    public void disclaimerSuccess() {
        spUtil.setDisclaimer(1);
        finish();
    }
}
