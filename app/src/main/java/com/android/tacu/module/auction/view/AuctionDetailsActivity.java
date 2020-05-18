package com.android.tacu.module.auction.view;

import android.app.Dialog;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.auction.adapter.AuctionRecordAdapter;
import com.android.tacu.module.auction.contract.AuctionContract;
import com.android.tacu.module.auction.model.AuctionLogsListModel;
import com.android.tacu.module.auction.model.AuctionModel;
import com.android.tacu.module.auction.presenter.AuctionPresenter;
import com.android.tacu.module.my.view.SecurityCenterActivity;
import com.android.tacu.module.webview.view.WebviewActivity;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.ShowToast;
import com.android.tacu.widget.BubbleProgressView;
import com.android.tacu.widget.dialog.DroidDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;
import com.qmuiteam.qmui.widget.textview.QMUIScrollTextView;
import com.stx.xhb.xbanner.XBanner;
import com.stx.xhb.xbanner.entity.CustomViewsInfo;
import com.stx.xhb.xbanner.transformers.Transformer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class AuctionDetailsActivity extends BaseActivity<AuctionPresenter> implements AuctionContract.IDetailsView {

    @BindView(R.id.banner)
    XBanner banner;
    @BindView(R.id.lin_scroll)
    LinearLayout lin_scroll;
    @BindView(R.id.text_scroll)
    QMUIScrollTextView text_scroll;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_details)
    TextView tv_details;

    @BindView(R.id.tv_hour)
    TextView tv_hour;
    @BindView(R.id.tv_minute)
    TextView tv_minute;
    @BindView(R.id.tv_second)
    TextView tv_second;

    @BindView(R.id.tv_auction_number)
    TextView tv_auction_number;
    @BindView(R.id.tv_bid_price)
    TextView tv_bid_price;

    @BindView(R.id.auction_time)
    TextView auction_time;

    @BindView(R.id.progress)
    BubbleProgressView progress;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    public static final int KREFRESH_TIME = 1000 * 10;
    private boolean isFirst = true;

    private View emptyView;
    private AuctionRecordAdapter recordAdapter;

    private AuctionModel auctionModel;
    private Long currentTime;
    private CountDownTimer time;
    private String[] getCountDownTimes;
    private List<CustomViewsInfo> bannerImageList = new ArrayList<>();

    private Handler kHandler = new Handler();
    private Runnable kRunnable = new Runnable() {
        @Override
        public void run() {
            upload2();
            if (kHandler != null) {
                kHandler.postDelayed(this, KREFRESH_TIME);
            }
        }
    };

    @Override
    protected void setView() {
        setContentView(R.layout.activity_auction_details);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.auction_paimai));
        mTopBar.addRightTextButton(getResources().getString(R.string.my_auction), R.id.qmui_topbar_item_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auctionModel == null) {
                    return;
                }
                jumpTo(MyAuctionActivity.createActivity(AuctionDetailsActivity.this, auctionModel.id));
            }
        });

        banner.setPageTransformer(Transformer.Default);
        banner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                ImageView imageView = (ImageView) view;
                imageView.setScaleType(CENTER_CROP);
                CustomViewsInfo customViewsInfo = (CustomViewsInfo) model;
                GlideUtils.disPlay(AuctionDetailsActivity.this, customViewsInfo.getXBannerUrl(), imageView);
            }
        });

        recordAdapter = new AuctionRecordAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_recyclerview_divider_dp10));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(recordAdapter);

        emptyView = View.inflate(this, R.layout.view_empty, null);
    }

    @Override
    protected AuctionPresenter createPresenter(AuctionPresenter mPresenter) {
        return new AuctionPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (kHandler != null && kRunnable != null) {
            kHandler.post(kRunnable);
        }
        upload();
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

    @OnClick(R.id.btn_join)
    void joinClick() {
        if (spUtil.getVip() == 0) {
            showVipDialog();
            return;
        }
        if (!spUtil.getValidatePass()) {
            showNotPwdDialog();
            return;
        }
        showPayDialog();
    }

    @OnClick(R.id.tv_look_more)
    void lookMoreClick() {
        if (auctionModel == null) {
            return;
        }
        jumpTo(AuctionRecordActivity.createActivity(this, auctionModel.id));
    }

    @Override
    public void auctionDetail(AuctionModel model) {
        this.auctionModel = model;
        if (model != null) {
            upload2();

            bannerImageList.clear();

            if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN)) {
                tv_name.setText(model.auctionName);
                tv_details.setText("\u3000\u3000" + model.introduce);

                if (!TextUtils.isEmpty(auctionModel.imgOne)) {
                    bannerImageList.add(new CustomViewsInfo("", auctionModel.imgOne));
                }
                if (!TextUtils.isEmpty(auctionModel.imgTwo)) {
                    bannerImageList.add(new CustomViewsInfo("", auctionModel.imgTwo));
                }
                if (!TextUtils.isEmpty(auctionModel.imgThree)) {
                    bannerImageList.add(new CustomViewsInfo("", auctionModel.imgThree));
                }
                banner.setBannerData(bannerImageList);
            } else {
                tv_name.setText(model.auctionNameEn);
                tv_details.setText("\u3000\u3000" + model.introduceEn);

                if (!TextUtils.isEmpty(auctionModel.imgOneEn)) {
                    bannerImageList.add(new CustomViewsInfo("", auctionModel.imgOneEn));
                }
                if (!TextUtils.isEmpty(auctionModel.imgTwoEn)) {
                    bannerImageList.add(new CustomViewsInfo("", auctionModel.imgTwoEn));
                }
                if (!TextUtils.isEmpty(auctionModel.imgThreeEn)) {
                    bannerImageList.add(new CustomViewsInfo("", auctionModel.imgThreeEn));
                }
                banner.setBannerData(bannerImageList);
            }

            dealTime();

            tv_auction_number.setText(String.valueOf(model.num));
            tv_bid_price.setText(model.price.stripTrailingZeros().toPlainString());

           /* if (model.already != null && model.total != null) {
                progress.setProgress(model.already, model.total);
            }
            auction_time.setText(getResources().getText(R.string.auction_time) + model.already.toString() + "/" + model.total.toString());*/
        }
    }

    @Override
    public void currentTime(Long time) {
        this.currentTime = time;
        dealTime();
    }

    @Override
    public void auctionBuySuccess() {
        if (auctionModel == null) {
            return;
        }
        jumpTo(MyAuctionActivity.createActivity(AuctionDetailsActivity.this, auctionModel.id));
    }

    @Override
    public void auctionBuyFailure() {
        showNotMoneyDialog();
    }

    @Override
    public void auctionLogsAll(AuctionLogsListModel model) {
        if (model != null) {
            if (model.total != null && auctionModel.total != null) {
                progress.setProgress(model.total, auctionModel.total);
            }
            auction_time.setText(getResources().getText(R.string.auction_time) + model.total.toString() + "/" + auctionModel.total.toString());

            if (model.list != null && model.list.size() > 0) {
                recordAdapter.setNewData(model.list);

                List<String> infos = new ArrayList<>();
                // 添加三个相同的，实现轮播效果
                if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN)) {
                    infos.add(String.format(getResources().getString(R.string.conngratulation_user), model.list.get(0).uid, DateUtils.getStrToStr(model.list.get(0).createTime, DateUtils.DEFAULT_PATTERN, DateUtils.FORMAT_DATE_HM), auctionModel.auctionName));
                    infos.add(String.format(getResources().getString(R.string.conngratulation_user), model.list.get(0).uid, DateUtils.getStrToStr(model.list.get(0).createTime, DateUtils.DEFAULT_PATTERN, DateUtils.FORMAT_DATE_HM), auctionModel.auctionName));
                    infos.add(String.format(getResources().getString(R.string.conngratulation_user), model.list.get(0).uid, DateUtils.getStrToStr(model.list.get(0).createTime, DateUtils.DEFAULT_PATTERN, DateUtils.FORMAT_DATE_HM), auctionModel.auctionName));
                } else {
                    infos.add(String.format(getResources().getString(R.string.conngratulation_user), model.list.get(0).uid, DateUtils.getStrToStr(model.list.get(0).createTime, DateUtils.DEFAULT_PATTERN, DateUtils.FORMAT_DATE_HM), auctionModel.auctionNameEn));
                    infos.add(String.format(getResources().getString(R.string.conngratulation_user), model.list.get(0).uid, DateUtils.getStrToStr(model.list.get(0).createTime, DateUtils.DEFAULT_PATTERN, DateUtils.FORMAT_DATE_HM), auctionModel.auctionNameEn));
                    infos.add(String.format(getResources().getString(R.string.conngratulation_user), model.list.get(0).uid, DateUtils.getStrToStr(model.list.get(0).createTime, DateUtils.DEFAULT_PATTERN, DateUtils.FORMAT_DATE_HM), auctionModel.auctionNameEn));
                }
                lin_scroll.setVisibility(View.VISIBLE);
                text_scroll.startWithList(infos);
            } else {
                lin_scroll.setVisibility(View.GONE);
                recordAdapter.setNewData(null);
                recordAdapter.setEmptyView(emptyView);
            }
        } else {
            recordAdapter.setNewData(null);
            recordAdapter.setEmptyView(emptyView);
        }
    }

    private void upload() {
        mPresenter.auctionDetail(1, isFirst);
        mPresenter.currentTime(1, isFirst);

        isFirst = false;
    }

    private void upload2() {
        if (auctionModel == null) {
            return;
        }
        mPresenter.auctionLogsAll(1, auctionModel.id, 1, 5);
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

    private void showVipDialog() {
        View view = View.inflate(this, R.layout.view_dialog_auction_vip, null);
        new DroidDialog.Builder(this)
                .viewCustomLayout(view)
                .positiveButton(getResources().getString(R.string.go_open), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        if (isKeyc()) {
                            jumpTo(WebviewActivity.createActivity(AuctionDetailsActivity.this, Constant.MEMBERSHIP));
                        }
                    }
                })
                .show();
    }

    private void showNotPwdDialog() {
        View view = View.inflate(this, R.layout.view_dialog_auction_notpwd, null);
        new DroidDialog.Builder(this)
                .viewCustomLayout(view)
                .positiveButton(getResources().getString(R.string.go_setting), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        if (isKeyc()) {
                            jumpTo(SecurityCenterActivity.class);
                        }
                    }
                })
                .show();
    }

    private void showPayDialog() {
        if (auctionModel == null) {
            return;
        }
        View view = View.inflate(this, R.layout.view_dialog_auction_pay, null);
        TextView tv_price = view.findViewById(R.id.tv_price);
        final QMUIRoundEditText edit_trade_pwd = view.findViewById(R.id.edit_trade_pwd);

        if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN)) {
            tv_price.setText(String.format(getResources().getString(R.string.pay_confirm_pay), auctionModel.price.stripTrailingZeros().toPlainString(), auctionModel.auctionName));
        } else {
            tv_price.setText(String.format(getResources().getString(R.string.pay_confirm_pay), auctionModel.price.stripTrailingZeros().toPlainString(), auctionModel.auctionNameEn));
        }
        if (spUtil.getPwdVisibility()) {
            edit_trade_pwd.setVisibility(View.VISIBLE);
        } else {
            edit_trade_pwd.setVisibility(View.GONE);
        }

        new DroidDialog.Builder(this)
                .title(getResources().getString(R.string.auction_pay))
                .viewCustomLayout(view)
                .positiveButton(getResources().getString(R.string.sure), false, new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        String pwdString = edit_trade_pwd.getText().toString();
                        if (spUtil.getPwdVisibility() && TextUtils.isEmpty(pwdString)) {
                            ShowToast.error(getResources().getString(R.string.please_input_trade_password));
                            return;
                        }

                        droidDialog.dismiss();
                        mPresenter.auctionBuy(auctionModel.id, spUtil.getPwdVisibility() ? Md5Utils.encryptFdPwd(pwdString, spUtil.getUserUid()).toLowerCase() : null);
                    }
                })
                .negativeButton(getResources().getString(R.string.think_again), null)
                .cancelable(false, false)
                .show();
    }

    private void showNotMoneyDialog() {
        View view = View.inflate(this, R.layout.view_dialog_auction_notmoney, null);
        new DroidDialog.Builder(this)
                .viewCustomLayout(view)
                .positiveButton(getResources().getString(R.string.sure), null)
                .show();
    }

    private boolean isKeyc() {
        boolean boo = true;
        if (spUtil.getIsAuthSenior() < 2) {
            ShowToast.error(getResources().getString(R.string.please_get_the_level_of_KYC));
            boo = false;
        }
        return boo;
    }
}
