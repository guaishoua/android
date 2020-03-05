package com.android.tacu.module.transaction.view;

import android.app.Activity;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.transaction.model.OrderParam;
import com.android.tacu.module.transaction.model.TradeParam;
import com.android.tacu.utils.SPUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiazhen on 2018/10/15.
 */
public class TradeRecordDrawerLayoutHelper implements View.OnClickListener {

    private Activity mActivity;
    private View viewDrawer;

    //初始的
    private int currencyIdOrgin;
    private int baseCurrencyIdOrgin;
    private String currencyNameEnOrgin;
    private String baseCurrencyNameEnOrgin;

    private OrderParam orderParam;
    private TradeParam tradeParam;

    //当前的类型 1：当前委托 2：历史委托 3：成交记录
    private int orderType = 1;

    //交易对
    private ConstraintLayout conlayoutTransactionpair;
    private QMUIRadiusImageView imgTransactionpair;
    private QMUIRoundTextView tvTransactionpairAll;
    private QMUIRoundTextView tvTransactionpairCustom;
    private QMUIRoundEditText editTradecoin;
    private QMUIRoundEditText editBasecoin;
    //交易类型
    private ConstraintLayout conlayoutTradetype;
    private QMUIRadiusImageView imgTradetype;
    private QMUIRoundTextView tvTradetypeAll;
    private QMUIRoundTextView tvTradetypeBuy;
    private QMUIRoundTextView tvTradetypeSell;
    //交易状态
    private ConstraintLayout conlayoutTradestatus;
    private QMUIRadiusImageView imgTradestatus;
    private QMUIRoundTextView tvTradestatusAll;
    private QMUIRoundTextView tvTradestatusDealed;
    private QMUIRoundTextView tvTradestatusDealedpart;
    private QMUIRoundTextView tvTradestatusRevoked;

    private QMUIRoundButton btnReset;
    private QMUIRoundButton btnOk;

    private Gson gson = new Gson();
    private List<MarketNewModel> cacheList = new ArrayList<>();

    private ConstraintSet csTransactionpair;
    private ConstraintSet csTradetype;
    private ConstraintSet csTradestatus;

    public TradeRecordDrawerLayoutHelper(Activity activity, View viewDrawer) {
        this.mActivity = activity;
        this.viewDrawer = viewDrawer;
    }

    public void clearActivity() {
        mActivity = null;
    }

    public void setHomeDrawerMenuView(int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn, final OKClickListener okClickListener) {
        this.currencyIdOrgin = currencyId;
        this.baseCurrencyIdOrgin = baseCurrencyId;
        this.currencyNameEnOrgin = currencyNameEn;
        this.baseCurrencyNameEnOrgin = baseCurrencyNameEn;

        //交易对
        conlayoutTransactionpair = viewDrawer.findViewById(R.id.conlayout_transactionpair);
        imgTransactionpair = viewDrawer.findViewById(R.id.img_transactionpair);
        tvTransactionpairAll = viewDrawer.findViewById(R.id.tv_transactionpair_all);
        tvTransactionpairCustom = viewDrawer.findViewById(R.id.tv_transactionpair_custom);
        editTradecoin = viewDrawer.findViewById(R.id.edit_tradecoin);
        editBasecoin = viewDrawer.findViewById(R.id.edit_basecoin);
        //交易类型
        conlayoutTradetype = viewDrawer.findViewById(R.id.conlayout_tradetype);
        imgTradetype = viewDrawer.findViewById(R.id.img_tradetype);
        tvTradetypeAll = viewDrawer.findViewById(R.id.tv_tradetype_all);
        tvTradetypeBuy = viewDrawer.findViewById(R.id.tv_tradetype_buy);
        tvTradetypeSell = viewDrawer.findViewById(R.id.tv_tradetype_sell);
        //交易状态
        conlayoutTradestatus = viewDrawer.findViewById(R.id.conlayout_tradestatus);
        imgTradestatus = viewDrawer.findViewById(R.id.img_tradestatus);
        tvTradestatusAll = viewDrawer.findViewById(R.id.tv_tradestatus_all);
        tvTradestatusDealed = viewDrawer.findViewById(R.id.tv_tradestatus_dealed);
        tvTradestatusDealedpart = viewDrawer.findViewById(R.id.tv_tradestatus_dealedpart);
        tvTradestatusRevoked = viewDrawer.findViewById(R.id.tv_tradestatus_revoked);

        btnReset = viewDrawer.findViewById(R.id.btn_reset);
        btnOk = viewDrawer.findViewById(R.id.btn_ok);

        csTransactionpair = new ConstraintSet();
        csTradetype = new ConstraintSet();
        csTradestatus = new ConstraintSet();

        csTransactionpair.clone(conlayoutTransactionpair);
        csTradetype.clone(conlayoutTradetype);
        csTradestatus.clone(conlayoutTradestatus);

        tvTransactionpairAll.setOnClickListener(this);
        tvTransactionpairCustom.setOnClickListener(this);
        tvTradetypeAll.setOnClickListener(this);
        tvTradetypeBuy.setOnClickListener(this);
        tvTradetypeSell.setOnClickListener(this);
        tvTradestatusAll.setOnClickListener(this);
        tvTradestatusDealed.setOnClickListener(this);
        tvTradestatusDealedpart.setOnClickListener(this);
        tvTradestatusRevoked.setOnClickListener(this);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInitStatus();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (okClickListener != null) {
                    Integer tradeCoinId = null;
                    Integer baseCoinId = null;
                    if (!TextUtils.isEmpty(editTradecoin.getText().toString().trim())) {
                        if (cacheList != null && cacheList.size() > 0) {
                            FLAG:
                            for (int i = 0; i < cacheList.size(); i++) {
                                for (int j = 0; j < cacheList.get(i).tradeCoinsList.size(); j++) {
                                    if (TextUtils.equals(cacheList.get(i).tradeCoinsList.get(j).currencyNameEn, editTradecoin.getText().toString().trim().toUpperCase())) {
                                        tradeCoinId = cacheList.get(i).tradeCoinsList.get(j).currencyId;
                                        break FLAG;
                                    }
                                }
                            }
                        }
                    }
                    if (orderType == 3) {
                        tradeParam.currencyId = tradeCoinId;
                    } else if (orderType == 1 || orderType == 2) {
                        orderParam.currencyId = tradeCoinId;
                    }

                    if (!TextUtils.isEmpty(editBasecoin.getText().toString().trim())) {
                        if (cacheList != null && cacheList.size() > 0) {
                            FLAG:
                            for (int i = 0; i < cacheList.size(); i++) {
                                for (int j = 0; j < cacheList.get(i).baseCoinList.size(); j++) {
                                    if (TextUtils.equals(cacheList.get(i).baseCoinList.get(j).currencyNameEn, editBasecoin.getText().toString().trim().toUpperCase())) {
                                        baseCoinId = cacheList.get(i).baseCoinList.get(j).currencyId;
                                        break FLAG;
                                    }
                                }
                            }
                        }
                    }
                    if (orderType == 3) {
                        tradeParam.baseCurrencyId = baseCoinId;
                    } else if (orderType == 1 || orderType == 2) {
                        orderParam.baseCurrencyId = baseCoinId;
                    }
                    okClickListener.coinClick(orderParam, tradeParam);
                }
            }
        });

        setInitStatus();

        String cacheString = SPUtils.getInstance().getString(Constant.SELECT_COIN_GROUP_CACHE);
        cacheList = gson.fromJson(cacheString, new TypeToken<List<MarketNewModel>>() {
        }.getType());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_transactionpair_all:
                setTradePair(1);
                break;
            case R.id.tv_transactionpair_custom:
                setTradePair(2);
                break;
            case R.id.tv_tradetype_all:
                setTradeType(1);
                if (orderType == 3) {
                    tradeParam.buyOrSell = 0;
                } else if (orderType == 1 || orderType == 2) {
                    orderParam.buyOrSell = 0;
                }
                break;
            case R.id.tv_tradetype_buy:
                setTradeType(2);
                if (orderType == 3) {
                    tradeParam.buyOrSell = 1;
                } else if (orderType == 1 || orderType == 2) {
                    orderParam.buyOrSell = 1;
                }
                break;
            case R.id.tv_tradetype_sell:
                setTradeType(3);
                if (orderType == 3) {
                    tradeParam.buyOrSell = 2;
                } else if (orderType == 1 || orderType == 2) {
                    orderParam.buyOrSell = 2;
                }
                break;
            case R.id.tv_tradestatus_all:
                setTradeStatus(1);
                orderParam.status = "2,4,5";
                break;
            case R.id.tv_tradestatus_dealed:
                setTradeStatus(2);
                orderParam.status = "2";
                break;
            case R.id.tv_tradestatus_dealedpart:
                setTradeStatus(3);
                orderParam.status = "5";
                break;
            case R.id.tv_tradestatus_revoked:
                setTradeStatus(4);
                orderParam.status = "4";
                break;
        }
    }

    /**
     * 设置侧边栏根据当前的类型显示
     *
     * @param childType 1：当前委托 2：历史委托 3：成交记录
     */
    public void setChildViewVis(int childType) {
        orderType = childType;
        if (orderType == 3) {
            tradeParam = new TradeParam(currencyIdOrgin, baseCurrencyIdOrgin);
        } else if (orderType == 1 || orderType == 2) {
            orderParam = new OrderParam(currencyIdOrgin, baseCurrencyIdOrgin);
        }
        setInitStatus();
        if (childType == 1) {
            conlayoutTradestatus.setVisibility(View.GONE);
        } else if (childType == 2) {
            conlayoutTradestatus.setVisibility(View.VISIBLE);
        } else if (childType == 3) {
            conlayoutTradestatus.setVisibility(View.GONE);
        }
    }

    /**
     * 重置
     */
    private void setInitStatus() {
        setTradePair(2);
        setTradeType(1);
        setTradeStatus(1);
        editTradecoin.setText(currencyNameEnOrgin);
        editBasecoin.setText(baseCurrencyNameEnOrgin);
        if (orderType == 3) {
            tradeParam = new TradeParam(currencyIdOrgin, baseCurrencyIdOrgin);
        } else if (orderType == 1 || orderType == 2) {
            orderParam = new OrderParam(currencyIdOrgin, baseCurrencyIdOrgin);
        }
    }

    /**
     * 交易对
     * flag: 1=全部交易对 2=自定义
     */
    private void setTradePair(int flag) {
        editTradecoin.setText("");
        editBasecoin.setText("");
        switch (flag) {
            case 1:
                editTradecoin.setEnabled(false);
                editBasecoin.setEnabled(false);
                selectedView(csTransactionpair, conlayoutTransactionpair, imgTransactionpair, tvTransactionpairAll);
                unSelectedView(tvTransactionpairCustom);
                break;
            case 2:
                editTradecoin.setEnabled(true);
                editBasecoin.setEnabled(true);
                selectedView(csTransactionpair, conlayoutTransactionpair, imgTransactionpair, tvTransactionpairCustom);
                unSelectedView(tvTransactionpairAll);
                break;
        }
    }

    /**
     * 交易类型
     * flag: 1=全部 2=买入 3=卖出
     */
    private void setTradeType(int flag) {
        switch (flag) {
            case 1:
                selectedView(csTradetype, conlayoutTradetype, imgTradetype, tvTradetypeAll);
                unSelectedView(tvTradetypeBuy);
                unSelectedView(tvTradetypeSell);
                break;
            case 2:
                selectedView(csTradetype, conlayoutTradetype, imgTradetype, tvTradetypeBuy);
                unSelectedView(tvTradetypeAll);
                unSelectedView(tvTradetypeSell);
                break;
            case 3:
                selectedView(csTradetype, conlayoutTradetype, imgTradetype, tvTradetypeSell);
                unSelectedView(tvTradetypeAll);
                unSelectedView(tvTradetypeBuy);
                break;
        }
    }

    /**
     * 交易状态
     * flag: 1=全部 2=已成交 3=部分成交后撤销 4=已撤销
     */
    private void setTradeStatus(int flag) {
        switch (flag) {
            case 1:
                selectedView(csTradestatus, conlayoutTradestatus, imgTradestatus, tvTradestatusAll);
                unSelectedView(tvTradestatusDealed);
                unSelectedView(tvTradestatusDealedpart);
                unSelectedView(tvTradestatusRevoked);
                break;
            case 2:
                selectedView(csTradestatus, conlayoutTradestatus, imgTradestatus, tvTradestatusDealed);
                unSelectedView(tvTradestatusAll);
                unSelectedView(tvTradestatusDealedpart);
                unSelectedView(tvTradestatusRevoked);
                break;
            case 3:
                selectedView(csTradestatus, conlayoutTradestatus, imgTradestatus, tvTradestatusDealedpart);
                unSelectedView(tvTradestatusAll);
                unSelectedView(tvTradestatusDealed);
                unSelectedView(tvTradestatusRevoked);
                break;
            case 4:
                selectedView(csTradestatus, conlayoutTradestatus, imgTradestatus, tvTradestatusRevoked);
                unSelectedView(tvTradestatusAll);
                unSelectedView(tvTradestatusDealed);
                unSelectedView(tvTradestatusDealedpart);
                break;
        }
    }

    /**
     * 选中
     *
     * @param tv
     */
    private void selectedView(ConstraintSet cs, ConstraintLayout conLayout, QMUIRadiusImageView img, QMUIRoundTextView tv) {
        cs.connect(img.getId(), ConstraintSet.BOTTOM, tv.getId(), ConstraintSet.BOTTOM);
        cs.connect(img.getId(), ConstraintSet.RIGHT, tv.getId(), ConstraintSet.RIGHT);
        ((QMUIRoundButtonDrawable) tv.getBackground()).setBgData(ContextCompat.getColorStateList(mActivity, R.color.color_default));
        tv.setTextColor(ContextCompat.getColor(mActivity, R.color.text_white));
        cs.applyTo(conLayout);
    }

    /**
     * 没选中
     *
     * @param tv
     */
    private void unSelectedView(QMUIRoundTextView tv) {
        ((QMUIRoundButtonDrawable) tv.getBackground()).setBgData(ContextCompat.getColorStateList(mActivity, R.color.color_grey));
        tv.setTextColor(ContextCompat.getColor(mActivity, R.color.text_color));
    }

    public interface OKClickListener {
        void coinClick(OrderParam orderParam, TradeParam tradeParam);
    }
}
