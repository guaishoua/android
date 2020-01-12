package com.android.tacu.module.auctionplus.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.JumpTradeCodeIsBuyEvent;
import com.android.tacu.EventBus.model.MainSwitchEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.assets.model.AmountModel;
import com.android.tacu.module.assets.view.AssetsActivity;
import com.android.tacu.module.auctionplus.adapter.AuctionPlusOfferPriceAdapter;
import com.android.tacu.module.auctionplus.contract.AuctionPlusContract;
import com.android.tacu.module.auctionplus.dialog.AuctionPlusPayDialogUtils;
import com.android.tacu.module.auctionplus.modal.AuctionPayStatusModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusListByIdModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusPayInfoModel;
import com.android.tacu.module.auctionplus.presenter.AuctionPlusPresent;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.utils.AnimUtils;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.view.CustomCountDownTimer;
import com.android.tacu.widget.dialog.DroidDialog;
import com.qmuiteam.qmui.alpha.QMUIAlphaImageButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundRelativeLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.zhouzhuo.zzhorizontalprogressbar.ZzHorizontalProgressBar;

/**
 * Created by jiazhen on 2019/6/5.
 */
public class AuctionPlusDetailActivity extends BaseActivity<AuctionPlusPresent> implements AuctionPlusContract.IDetailView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.chronometer)
    TextView chronometer;
    @BindView(R.id.img_coin)
    ImageView img_coin;
    @BindView(R.id.rl_status)
    QMUIRoundRelativeLayout rl_status;
    @BindView(R.id.tv_status)
    TextView tv_status;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.progress)
    ZzHorizontalProgressBar progress;
    @BindView(R.id.tv_count)
    TextView tv_count;
    @BindView(R.id.tv_allcount)
    TextView tv_allcount;
    @BindView(R.id.btn_now)
    QMUIRoundButton btn_now;
    @BindView(R.id.tv_collection_num)
    TextView tv_collection_num;
    @BindView(R.id.tv_goprice_num)
    TextView tv_goprice_num;
    @BindView(R.id.tv_watch_num)
    TextView tv_watch_num;
    @BindView(R.id.tv_every_num)
    TextView tv_every_num;
    @BindView(R.id.tv_count_time)
    TextView tv_count_time;
    @BindView(R.id.tv_every_price)
    TextView tv_every_price;
    @BindView(R.id.tv_fee)
    TextView tv_fee;
    @BindView(R.id.tv_all_num)
    TextView tv_all_num;
    @BindView(R.id.tv_add_price)
    TextView tv_add_price;
    @BindView(R.id.img_rules)
    ImageView img_rules;
    @BindView(R.id.tv_rules_details)
    TextView tv_rules_details;
    @BindView(R.id.tv_goprice_record)
    TextView tv_goprice_record;

    private String id;
    private static final int KREFRESH_TIME = 1000;

    private QMUIAlphaImageButton btnRight;
    private AuctionPlusOfferPriceAdapter offerPriceAdapter;
    private long time;
    private AuctionPlusModel auctionModel;
    private String img;
    private String name;
    private AuctionPlusListByIdModel auctionListByIdModel;
    private CustomCountDownTimer customCountDownTimer;
    private long millisUntilFinished;
    private String currentNum;
    private long clickTime;//出价按钮点击后记录时间
    private AlertDialog dialog;
    private DroidDialog payDialog;
    private boolean isCollect = false;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            upLoad();
            //每隔1秒循环执行run方法
            if (handler != null) {
                handler.postDelayed(this, KREFRESH_TIME);
            }
        }
    };
    private Handler mHandler = new Handler();

    public static Intent createActivity(Context context, String id, AuctionPlusModel model) {
        Intent intent = new Intent(context, AuctionPlusDetailActivity.class);
        intent.putExtra("id", id);
        Bundle bundle = new Bundle();
        bundle.putSerializable("AuctionPlusModel", model);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_auctionplus_detail);
    }

    @Override
    protected void initView() {
        id = getIntent().getStringExtra("id");
        AuctionPlusModel model = (AuctionPlusModel) getIntent().getSerializableExtra("AuctionPlusModel");
        progress.setProgress(Integer.parseInt(model.totalNumber));
        setAuctionModel(model);

        mTopBar.setTitle("Auction Plus");

        btnRight = mTopBar.addRightImageButton(R.drawable.icon_rating_uncollect, R.id.qmui_topbar_item_right, 20, 20);
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spUtil.getLogin()) {
                    if (!isCollect) {
                        isCollect = true;
                        btnRight.setImageResource(R.drawable.icon_rating_collect);
                        mPresenter.collect(id);
                    }
                } else {
                    jumpTo(LoginActivity.class);
                }
            }
        });

        offerPriceAdapter = new AuctionPlusOfferPriceAdapter();
        offerPriceAdapter.setHeaderFooterEmpty(true, true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(offerPriceAdapter);

        View footView = View.inflate(this, R.layout.footer_auction_detail, null);
        offerPriceAdapter.addFooterView(footView);
    }

    @Override
    protected AuctionPlusPresent createPresenter(AuctionPlusPresent mPresenter) {
        return new AuctionPlusPresent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (handler != null && runnable != null) {
            if (auctionModel == null || auctionModel.auctionStatus != 3) {
                handler.post(runnable);
            }
        }
        if (spUtil.getLogin()) {
            checkCollect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            handler = null;
            runnable = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        if (countDownDialogTimer != null) {
            countDownDialogTimer.cancel();
            countDownDialogTimer = null;
        }
        if (customCountDownTimer != null) {
            customCountDownTimer.cancel();
            customCountDownTimer = null;
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (payDialog != null && payDialog.isShowing()) {
            payDialog.dismiss();
        }
    }

    @OnClick(R.id.btn_trade)
    void btnTradeClick() {
        EventManage.sendEvent(new BaseEvent<>(EventConstant.JumpTradeIsBuyCode, new JumpTradeCodeIsBuyEvent(1, 237, "BTC", "ACU", true)));
    }

    @OnClick(R.id.btn_now)
    void btnNowClick() {
        if (spUtil.getLogin()) {
            if (TextUtils.equals(btn_now.getText().toString(), getResources().getString(R.string.auction_now_goprice))) {
                clickTime = System.currentTimeMillis();
                setBtnEnable(false, null);
                countDownTimer.start();
                mPresenter.auctionBuy(id, "1", 1);
            } else if (TextUtils.equals(btn_now.getText().toString(), getResources().getString(R.string.auction_list_paynow))) {
                upLoadUsdt();
            } else if (TextUtils.equals(btn_now.getText().toString(), getResources().getString(R.string.auction_go_activity))) {
                finish();
            }
        } else if (TextUtils.equals(btn_now.getText().toString(), getResources().getString(R.string.auction_go_activity))) {
            finish();
        } else {
            jumpTo(LoginActivity.class);
        }
    }

    @OnClick(R.id.btn_recharge)
    void btnRechargeClick() {
        if (spUtil.getLogin()) {
            EventManage.sendEvent(new BaseEvent<>(EventConstant.MainSwitchCode, new MainSwitchEvent(Constant.MAIN_ASSETS)));
        } else {
            jumpTo(LoginActivity.class);
        }
    }

    @OnClick(R.id.view_rules)
    void viewRulesClick() {
        if (tv_rules_details.getVisibility() == View.GONE) {
            tv_rules_details.setVisibility(View.VISIBLE);
            img_rules.setImageResource(R.drawable.icon_expand_less_grey);
        } else if (tv_rules_details.getVisibility() == View.VISIBLE) {
            tv_rules_details.setVisibility(View.GONE);
            img_rules.setImageResource(R.drawable.icon_expand_more_grey);
        }
    }

    @OnClick(R.id.img_goprice_record)
    void imgGopriceRecordClick() {
        jumpTo(AuctionPlusOfferActivity.createActivity(this, id));
    }

    @Override
    public void listPlusById(AuctionPlusListByIdModel model) {
        this.auctionListByIdModel = model;
        if (model != null) {
            if (model.arr != null) {
                tv_goprice_record.setText(getResources().getString(R.string.auction_goprice_record) + "(" + model.arr.total + ")");
                if (model.arr.list != null && model.arr.list.size() > 0) {
                    offerPriceAdapter.setAuctionPlusListByIdModel(model);
                    offerPriceAdapter.setNewData(model.arr.list);
                }
            }
            time = model.time;
            if (model.info != null) {
                auctionModel = model.info;
                if (auctionModel.auctionStatus == 3 && spUtil.getLogin()) {
                    mPresenter.auctionPlusListPay(id, 2);
                }
                setAuctionModel(auctionModel);
            }
        }
    }

    @Override
    public void customerCoinByOneCoin(AmountModel model, AuctionPlusModel auctionModel, int currencyId) {
        if (model != null && auctionModel != null) {
            mPresenter.auctionPayInfo(auctionModel, String.valueOf(model.attachment), 2);
        }
    }

    @Override
    public void collectCheck(boolean isCollect) {
        this.isCollect = isCollect;
        if (isCollect) {
            btnRight.setImageResource(R.drawable.icon_rating_collect);
        } else {
            btnRight.setImageResource(R.drawable.icon_rating_uncollect);
        }
    }

    @Override
    public void collectSuccess() {
        showToastSuccess(getResources().getString(R.string.collect_success));
    }

    @Override
    public void collectError() {
        showToastError(getResources().getString(R.string.collect_error));
        btnRight.setImageResource(R.drawable.icon_rating_uncollect);
    }

    @Override
    public void auctionBuySuccess() {
        dialogView();
    }

    @Override
    public void auctionPlusListPay(List<AuctionPayStatusModel> list) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (TextUtils.equals(list.get(i).auctionId, id)) {
                    auctionModel.payPlusStatus = list.get(i).status;
                    setAuctionModel(auctionModel);
                }
            }
        }
    }

    @Override
    public void auctionPayInfo(AuctionPlusPayInfoModel model, final AuctionPlusModel auctionPlusModel, String balance) {
        if (auctionPlusModel != null) {
            payDialog = AuctionPlusPayDialogUtils.dialogShow(AuctionPlusDetailActivity.this, model, auctionPlusModel, balance, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.auctionPay(id, 0, 1);
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpTo(AssetsActivity.createActivity(AuctionPlusDetailActivity.this, auctionPlusModel.payCurrencyName, auctionPlusModel.payCurrencyId, 0, true));
                }
            });
        }
    }

    @Override
    public void auctionPaySuccess() {
        payDialog.dismiss();
        showToastSuccess(getResources().getString(R.string.auction_pay_success));
        mPresenter.listPlusById(1, 10, id, 1, false);
    }

    private void setAuctionModel(AuctionPlusModel model) {
        progress.setProgress(Integer.parseInt(model.surplusNumber));

        //如果当前价变动 就给个动画
        if (TextUtils.isEmpty(currentNum) || !TextUtils.equals(currentNum, model.surplusNumber)) {
            currentNum = model.surplusNumber;
            if (!TextUtils.isEmpty(tv_count.getText().toString())) {
                AnimUtils.startTextViewBottomRightAnim(tv_count);
            }
        }

        if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW)) {
            img = model.img;
            name = model.auctionName;
        } else {
            img = model.imgEn;
            name = model.auctionNameEn;
        }
        GlideUtils.disPlay(this, img, img_coin);
        tv_name.setText(name);
        tv_count.setText(model.surplusNumber);
        tv_allcount.setText(model.totalNumber);
        tv_collection_num.setText(String.format(getResources().getString(R.string.auction_collection_num), model.followCount));
        tv_goprice_num.setText(String.format(getResources().getString(R.string.auction_goprice_num), model.currentCountPeo));
        tv_watch_num.setText(String.format(getResources().getString(R.string.auction_watch_num), model.watchCount));

        tv_every_num.setText(getResources().getString(R.string.plus_every_num) + "(" + model.currencyName + ")：" + FormatterUtils.getFormatValue(model.eachNum));
        tv_count_time.setText(getResources().getString(R.string.auction_count_time) + ": " + model.timeLength + "s");
        tv_every_price.setText(getResources().getString(R.string.plus_every_price) + "(" + model.payCurrencyName + ")：" + FormatterUtils.getFormatValue(model.eachPrice));
        tv_fee.setText(getResources().getString(R.string.plus_fee) + "(" + model.feeCurrencyName + "): " + FormatterUtils.getFormatValue(model.fee));
        tv_all_num.setText(getResources().getString(R.string.auctionplus_all_time) + ": " + model.totalNumber);
        tv_add_price.setText(getResources().getString(R.string.plus_add_price) + "(" + model.feeCurrencyName + "): " + ": " + FormatterUtils.getFormatValue(model.feeRang));

        statusView(model);

        switch (model.auctionStatus) {
            case 1://进行中
                setCountDownTime(model);
                break;
            case 2://即将开始
                chronometer.setText(getResources().getString(R.string.auction_starttime) + " " + model.startTime);
                break;
            case 3://已结束
                chronometer.setText(String.format(getResources().getString(R.string.auction_finish_time), model.endTime));
                if (handler != null && runnable != null) {
                    handler.removeCallbacks(runnable);
                }
                break;
        }
    }

    private void upLoad() {
        if (auctionListByIdModel != null && auctionListByIdModel.info != null && auctionListByIdModel.info.auctionStatus == 3) {
            return;
        }
        mPresenter.listPlusById(1, 10, id, 1, false);
    }

    private void upLoadUsdt() {
        mPresenter.customerCoinByOneCoin(auctionModel.payCurrencyId, auctionModel, 2);
    }

    private void checkCollect() {
        mPresenter.collectCheck(id);
    }

    private void statusView(AuctionPlusModel model) {
        switch (model.auctionStatus) {
            case 1://进行中
                ((QMUIRoundButtonDrawable) rl_status.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.color_status_ing));
                tv_status.setText(getResources().getString(R.string.auction_list_going));

                if (System.currentTimeMillis() - clickTime > 2000) {
                    setBtnEnable(true, getResources().getString(R.string.auction_now_goprice));
                }
                break;
            case 2://即将开始
                ((QMUIRoundButtonDrawable) rl_status.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.color_status_soonstart));
                tv_status.setText(getResources().getString(R.string.auctionplus_list_start));

                setBtnEnable(false, getResources().getString(R.string.auction_now_goprice));
                break;
            case 3://结束
                ((QMUIRoundButtonDrawable) rl_status.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.color_status_finish));
                tv_status.setText(getResources().getString(R.string.auction_list_finish));


                if (spUtil.getLogin() && model.payPlusStatus != 0) {
                    switch (model.payPlusStatus) {
                        case 1://待支付
                            setBtnEnable(true, getResources().getString(R.string.auction_list_paynow));
                            break;
                        case 2://已支付
                            setBtnEnable(false, getResources().getString(R.string.auction_list_payafter));
                            break;
                        case 3://支付过期
                            setBtnEnable(false, getResources().getString(R.string.auction_list_paygotime));
                            break;
                    }
                } else {
                    setBtnEnable(true, getResources().getString(R.string.auction_go_activity));
                }
                break;
        }
    }

    /**
     * 按钮是否可以使用它
     *
     * @param isEnable
     */
    private void setBtnEnable(boolean isEnable, String str) {
        if (isEnable) {
            ((QMUIRoundButtonDrawable) btn_now.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.color_pay_now));
            btn_now.setEnabled(true);
        } else {
            ((QMUIRoundButtonDrawable) btn_now.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.color_grey));
            btn_now.setEnabled(false);
        }
        if (!TextUtils.isEmpty(str)) {
            btn_now.setText(str);
        }
    }

    //进行中倒计时
    private void setCountDownTime(AuctionPlusModel model) {
        if (model != null && model.timestamp != 0 && time != 0 && model.auctionStatus == 1) {
            millisUntilFinished = model.timestamp - time;
            if (millisUntilFinished > 0) {
                chronometer.setText(String.format(getResources().getString(R.string.auction_estimate), DateUtils.getCountDownTime(millisUntilFinished)));
                setCustomCountDownTimer(millisUntilFinished);
            } else {
                chronometer.setText(String.format(getResources().getString(R.string.auction_estimate), "00:00:00"));
            }
        }
    }

    private void dialogView() {
        View view = View.inflate(this, R.layout.view_dialog_pay_success, null);
        view.findViewById(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(view)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        countDownDialogTimer.start();
    }

    private void setCustomCountDownTimer(long millisInFuture) {
        customCountDownTimer = new CustomCountDownTimer(millisInFuture, chronometer);
        customCountDownTimer.start();
    }

    // 第一个参数表示总时间，第二个参数表示间隔时间。意思就是每隔一秒会回调一次方法onTick，然后60秒之后会回调onFinish方法
    private CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {

        @Override
        public void onTick(long arg0) {
        }

        @Override
        public void onFinish() {
            setBtnEnable(true, null);
        }
    };

    private CountDownTimer countDownDialogTimer = new CountDownTimer(3000, 1000) {

        @Override
        public void onTick(long arg0) {
        }

        @Override
        public void onFinish() {
            dialog.dismiss();
        }
    };
}
