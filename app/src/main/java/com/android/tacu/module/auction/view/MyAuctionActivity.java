package com.android.tacu.module.auction.view;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.auction.contract.AuctionContract;
import com.android.tacu.module.auction.model.AuctionLogsListModel;
import com.android.tacu.module.auction.model.AuctionLogsModel;
import com.android.tacu.module.auction.model.AuctionModel;
import com.android.tacu.module.auction.presenter.AuctionPresenter;
import com.android.tacu.utils.DateUtils;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundTextView;

import butterknife.BindView;
import butterknife.OnClick;

import static com.android.tacu.module.auction.view.AuctionDetailsActivity.KREFRESH_TIME;

public class MyAuctionActivity extends BaseActivity<AuctionPresenter> implements AuctionContract.IMyView {

    @BindView(R.id.scrollview)
    NestedScrollView scrollview;
    @BindView(R.id.img_status)
    ImageView img_status;

    //开奖倒计时
    @BindView(R.id.lin_success)
    LinearLayout lin_success;
    @BindView(R.id.tv_hour)
    QMUIRoundTextView tv_hour;
    @BindView(R.id.tv_minute)
    QMUIRoundTextView tv_minute;
    @BindView(R.id.tv_second)
    QMUIRoundTextView tv_second;

    //获奖没填写地址和填写过
    @BindView(R.id.lin_address)
    LinearLayout lin_address;
    @BindView(R.id.tv_wait)
    TextView tv_wait;
    @BindView(R.id.tv_get)
    TextView tv_get;

    //失败
    @BindView(R.id.lin_congratulation)
    LinearLayout lin_congratulation;
    @BindView(R.id.tv_failure)
    TextView tv_failure;

    @BindView(R.id.tv_my_shipping_address)
    TextView tv_my_shipping_address;

    @BindView(R.id.btn_add)
    QMUIRoundButton btn_add;
    @BindView(R.id.tv_doubt)
    TextView tv_doubt;

    @BindView(R.id.tv_uid)
    TextView tv_uid;
    @BindView(R.id.tv_amount)
    TextView tv_amount;
    @BindView(R.id.tv_price)
    TextView tv_price;
    @BindView(R.id.tv_time)
    TextView tv_time;

    @BindView(R.id.empty)
    RelativeLayout empty;

    private Integer id;
    private AuctionLogsModel logsModel;

    private AuctionModel auctionModel;
    private Long currentTime;
    private CountDownTimer time;
    private String[] getCountDownTimes;

    private Handler kHandler = new Handler();
    private Runnable kRunnable = new Runnable() {
        @Override
        public void run() {
            upLoad();

            if (kHandler != null) {
                kHandler.postDelayed(this, KREFRESH_TIME);
            }
        }
    };

    public static Intent createActivity(Context context, Integer id) {
        Intent intent = new Intent(context, MyAuctionActivity.class);
        intent.putExtra("id", id);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_my_auction);
    }

    @Override
    protected void initView() {
        id = getIntent().getIntExtra("id", 0);

        mTopBar.setTitle(getResources().getString(R.string.my_auction));
    }

    @Override
    protected AuctionPresenter createPresenter(AuctionPresenter mPresenter) {
        return new AuctionPresenter();
    }

    @Override
    protected void onPresenterCreated(AuctionPresenter presenter) {
        super.onPresenterCreated(presenter);

        upLoad();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (kHandler != null && kRunnable != null) {
            kHandler.post(kRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (kHandler != null && kRunnable != null) {
            kHandler.removeCallbacks(kRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (time != null) {
            time.cancel();
            time = null;
        }
        if (kHandler != null && kRunnable != null) {
            kHandler.removeCallbacks(kRunnable);
            kHandler = null;
            kRunnable = null;
        }
    }

    @OnClick({R.id.btn_add, R.id.tv_my_shipping_address})
    void addClick() {
        if (logsModel != null) {
            jumpTo(MyAddressActivity.createActivity(this, logsModel.id));
        }
    }

    @Override
    public void auctionDetail(AuctionModel model) {
        this.auctionModel = model;
        if (logsModel != null && logsModel.status != null) {
            switch (logsModel.status) {
                case 1:
                    dealTime();
                    break;
                case 2:
                case 3:
                    if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN)) {
                        tv_get.setText(String.format(getResources().getString(R.string.congratulation_get), model.auctionName));
                    } else {
                        tv_get.setText(String.format(getResources().getString(R.string.congratulation_get), model.auctionNameEn));
                    }
                    break;
                case 4:
                    if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN)) {
                        tv_failure.setText(String.format(getResources().getString(R.string.auction_failure_wait_get), model.auctionName));
                    } else {
                        tv_failure.setText(String.format(getResources().getString(R.string.auction_failure_wait_get), model.auctionNameEn));
                    }
                    break;
            }
        }
    }

    @Override
    public void currentTime(Long time) {
        this.currentTime = time;
        dealTime();
    }

    @Override
    public void auctionLogs(AuctionLogsListModel model) {
        if (model != null && model.list != null && model.list.size() > 0) {
            empty.setVisibility(View.GONE);
            scrollview.setVisibility(View.VISIBLE);

            logsModel = model.list.get(0);
            if (logsModel.status != null) {
                lin_success.setVisibility(View.GONE);
                lin_address.setVisibility(View.GONE);
                lin_congratulation.setVisibility(View.GONE);
                tv_my_shipping_address.setVisibility(View.GONE);
                btn_add.setVisibility(View.GONE);
                tv_doubt.setVisibility(View.GONE);

                //1.竞拍成功  2.已开奖 3.等待收货 4未中奖
                mPresenter.auctionDetail(2, false);
                switch (logsModel.status) {
                    case 1:
                        mPresenter.currentTime(2, false);

                        img_status.setImageResource(R.mipmap.img_auction_success);
                        lin_success.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        img_status.setImageResource(R.mipmap.img_auction_pass);
                        lin_address.setVisibility(View.VISIBLE);
                        tv_wait.setVisibility(View.GONE);

                        btn_add.setVisibility(View.VISIBLE);
                        tv_doubt.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        img_status.setImageResource(R.mipmap.img_auction_wait_get);
                        lin_address.setVisibility(View.VISIBLE);
                        tv_wait.setVisibility(View.VISIBLE);

                        tv_my_shipping_address.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        img_status.setImageResource(R.mipmap.img_auction_pass);
                        lin_congratulation.setVisibility(View.VISIBLE);
                        break;
                }
            }

            tv_uid.setText(logsModel.uid);
            tv_amount.setText(logsModel.targetNum);
            tv_price.setText(logsModel.targetCurrentPrice.stripTrailingZeros().toPlainString());
            tv_time.setText(logsModel.createTime);
        } else {
            empty.setVisibility(View.VISIBLE);
            scrollview.setVisibility(View.GONE);
        }
    }

    /**
     * 需要加循环
     */
    private void upLoad() {
        mPresenter.auctionLogs(id);
    }

    private void dealTime() {
        if (currentTime != null && auctionModel != null && !TextUtils.isEmpty(auctionModel.endTime)) {
            long confirmEndTime = DateUtils.string2Millis(auctionModel.endTime, DateUtils.DEFAULT_PATTERN) - currentTime;
            if (confirmEndTime > 0) {
                startCountDownTimer(confirmEndTime);
            } else {
                tv_hour.setText("00");
                tv_minute.setText("00");
                tv_second.setText("00");
            }
        }
    }

    private void startCountDownTimer(long valueTime) {
        if (time != null) {
            time.cancel();
            time = null;
        }
        time = new CountDownTimer(valueTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                try {
                    if (millisUntilFinished >= 0) {
                        getCountDownTimes = DateUtils.getCountDownTime2(millisUntilFinished);
                        if (getCountDownTimes != null && getCountDownTimes.length == 3) {
                            tv_hour.setText(getCountDownTimes[0]);
                            tv_minute.setText(getCountDownTimes[1]);
                            tv_second.setText(getCountDownTimes[2]);
                        }
                    } else {
                        cancel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                cancel();
            }
        };
        time.start();
    }
}
