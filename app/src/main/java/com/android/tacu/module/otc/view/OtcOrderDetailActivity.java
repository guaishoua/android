package com.android.tacu.module.otc.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.MyApplication;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.otc.contract.OtcOrderDetailContract;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.orderView.ArbitrationView;
import com.android.tacu.module.otc.orderView.CoinGetView;
import com.android.tacu.module.otc.orderView.CoinedView;
import com.android.tacu.module.otc.orderView.ConfirmView;
import com.android.tacu.module.otc.orderView.FinishView;
import com.android.tacu.module.otc.orderView.PayGetView;
import com.android.tacu.module.otc.orderView.PayedView;
import com.android.tacu.module.otc.presenter.OtcOrderDetailPresenter;
import com.android.tacu.widget.NodeProgressView;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;

public class OtcOrderDetailActivity extends BaseOtcOrderActvity<OtcOrderDetailPresenter> implements OtcOrderDetailContract.IView {

    private int current = -1;
    private Integer status = -1;

    // 1待确认 2 已确认待付款 3已付款待放币 4 仲裁 5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付 9 放币超时  10放币完成
    // 12 买家成功 13 卖家成功

    private static final int ORDER_CONFIRMED = 1;//待确认
    private static final int ORDER_PAYED = 2;//待付款
    private static final int ORDER_PAYGET = 3;//待收款
    private static final int ORDER_COINED = 4;//待放币
    private static final int ORDER_COINGET = 5;//待收币
    private static final int ORDER_FINISHED = 6;//已完成
    public static final int ORDER_ARBITRATION_BUY = 7;//买方仲裁中
    public static final int ORDER_ARBITRATION_SELL = 8;//卖方仲裁中
    private static final int ORDER_CANCEL = 9;//取消
    private static final int ORDER_TIMEOUT = 10;//超时
    public static final int ORDER_ARBITRATION_SUCCESS = 11;//裁决成功

    //接口30s轮训一次
    private static final int KREFRESH_TIME = 1000 * 30;

    public static final int TAKE_PIC = 1001;

    @BindView(R.id.node_progress)
    NodeProgressView nodeProgress;
    @BindView(R.id.lin_switch)
    LinearLayout linSwitch;

    //待确认
    private ConfirmView confirmView;
    //待付款
    private PayedView payedView;
    //待收款
    private PayGetView payGetView;
    //待放币
    private CoinedView coinedView;
    //待收币
    private CoinGetView coinGetView;
    //已完成
    private FinishView finishView;
    //仲裁中
    private ArbitrationView arbitrationView;

    private String orderNo;
    private boolean isFirst = true;

    private Handler kHandler = new Handler();
    private Runnable kRunnable = new Runnable() {
        @Override
        public void run() {
            upload(isFirst);
            if (kHandler != null) {
                kHandler.postDelayed(this, KREFRESH_TIME);
            }
        }
    };

    public static Intent createActivity(Context context, String orderNo) {
        Intent intent = new Intent(context, OtcOrderDetailActivity.class);
        intent.putExtra("orderNo", orderNo);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_order_detail);
    }

    @Override
    protected void initView() {
        orderNo = getIntent().getStringExtra("orderNo");
    }

    @Override
    protected OtcOrderDetailPresenter createPresenter(OtcOrderDetailPresenter mPresenter) {
        return new OtcOrderDetailPresenter();
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
        if (kHandler != null && kRunnable != null) {
            kHandler.removeCallbacks(kRunnable);
            kHandler = null;
            kRunnable = null;
        }
        destoryAllView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == TAKE_PIC) {
            ArrayList<String> imageList = BGAPhotoPickerActivity.getSelectedImages(data);
            for (int i = 0; i < imageList.size(); i++) {
                String imageUri = imageList.get(i);
                File fileOrgin = new File(imageUri);

                switch (current) {
                    case ORDER_PAYED:
                        if (payedView != null) {
                            payedView.getPic(fileOrgin);
                        }
                        break;
                    case ORDER_COINGET:
                        if (coinGetView != null) {
                            coinGetView.getPic(fileOrgin);
                        }
                        break;
                    case ORDER_ARBITRATION_BUY:
                    case ORDER_ARBITRATION_SELL:
                    case ORDER_ARBITRATION_SUCCESS:
                        if (arbitrationView != null) {
                            arbitrationView.getPic(fileOrgin);
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void selectTradeOne(boolean isFirst, OtcTradeModel model) {
        if (model != null) {
            if (model.buyuid != null && isFirst) {
                mPresenter.userBaseInfo(1, model.buyuid);
            }
            if (model.selluid != null && isFirst) {
                mPresenter.userBaseInfo(2, model.selluid);
            }

            setBuyPayInfoString(model);
            setSellPayInfoString(model);

            // 1待确认 2 已确认待付款 3已付款待放币 4 仲裁 5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付 9 放币超时  10放币完成
            // 12 买家成功 13 卖家成功
            if (model.status != null) {
                switch (model.status) {
                    case 1:
                        current = ORDER_CONFIRMED;
                        break;
                    case 2:
                        if (model.buyuid == spUtil.getUserUid()) {
                            current = ORDER_PAYED;
                        } else if (model.selluid == spUtil.getUserUid()) {
                            current = ORDER_PAYGET;
                        }
                        break;
                    case 3:
                    case 9:
                        if (model.buyuid == spUtil.getUserUid()) {
                            current = ORDER_COINGET;
                        } else if (model.selluid == spUtil.getUserUid()) {
                            current = ORDER_COINED;
                        }
                        break;
                    case 4:
                        if (model.buyuid == spUtil.getUserUid()) {
                            current = ORDER_ARBITRATION_BUY;
                        } else if (model.selluid == spUtil.getUserUid()) {
                            current = ORDER_ARBITRATION_SELL;
                        }
                        break;
                    case 5:
                    case 7:
                        current = ORDER_TIMEOUT;
                        break;
                    case 6:
                    case 8:
                        current = ORDER_CANCEL;
                        break;
                    case 10:
                        current = ORDER_FINISHED;
                        break;
                    case 12:
                    case 13:
                        current = ORDER_ARBITRATION_SUCCESS;
                        break;
                }
                if (!status.equals(model.status)) {
                    status = model.status;
                    destoryAllView();
                    switchView(model.status);
                }
            }

            if (isFirst) {
                switch (current) {
                    case ORDER_CONFIRMED:
                    case ORDER_PAYGET:
                        mPresenter.currentTime();
                        break;
                    case ORDER_PAYED:
                        mPresenter.currentTime();
                        mPresenter.selectPayInfoById(model.payId);
                        break;
                    case ORDER_COINED:
                    case ORDER_COINGET:
                        mPresenter.currentTime();
                        mPresenter.uselectUserInfo(model.payInfo);
                        break;
                    case ORDER_CANCEL:
                    case ORDER_TIMEOUT:
                    case ORDER_FINISHED:
                    case ORDER_ARBITRATION_BUY:
                    case ORDER_ARBITRATION_SELL:
                    case ORDER_ARBITRATION_SUCCESS:
                        mPresenter.uselectUserInfo(model.payInfo);
                        break;
                }
            }

            switch (current) {
                case ORDER_CONFIRMED:
                    if (confirmView != null) {
                        confirmView.selectTradeOne(model);
                    }
                    break;
                case ORDER_PAYED:
                    if (payedView != null) {
                        payedView.selectTradeOne(model);
                    }
                    break;
                case ORDER_PAYGET:
                    if (payGetView != null) {
                        payGetView.selectTradeOne(model);
                    }
                    break;
                case ORDER_COINED:
                    if (coinedView != null) {
                        coinedView.selectTradeOne(model);
                    }
                    break;
                case ORDER_COINGET:
                    if (coinGetView != null) {
                        coinGetView.selectTradeOne(model);
                    }
                    break;
                case ORDER_CANCEL:
                case ORDER_TIMEOUT:
                case ORDER_FINISHED:
                    if (finishView != null) {
                        finishView.selectTradeOne(model);
                    }
                    break;
                case ORDER_ARBITRATION_BUY:
                case ORDER_ARBITRATION_SELL:
                case ORDER_ARBITRATION_SUCCESS:
                    if (arbitrationView != null) {
                        arbitrationView.selectTradeOne(model);
                    }
                    break;
            }
        }
    }

    @Override
    public void userBaseInfo(Integer buyOrSell, OtcMarketInfoModel model, Integer queryUid) {
        if (model != null) {
            if (buyOrSell != null && buyOrSell == 1) {//买
                setBuyValue(model, queryUid, true);
            } else if (buyOrSell != null && buyOrSell == 2) {
                setSellValue(model, queryUid, true);
            }
            if (buyOrSell == null) {
                switch (current) {
                    case ORDER_PAYED:
                        if (payedView != null) {
                            payedView.userBaseInfo(model);
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void currentTime(Long time) {
        switch (current) {
            case ORDER_CONFIRMED:
                if (confirmView != null) {
                    confirmView.currentTime(time);
                }
                break;
            case ORDER_PAYED:
                if (payedView != null) {
                    payedView.currentTime(time);
                }
                break;
            case ORDER_PAYGET:
                if (payGetView != null) {
                    payGetView.currentTime(time);
                }
                break;
            case ORDER_COINED:
                if (coinedView != null) {
                    coinedView.currentTime(time);
                }
                break;
            case ORDER_COINGET:
                if (coinGetView != null) {
                    coinGetView.currentTime(time);
                }
                break;
        }
    }

    @Override
    public void selectPayInfoById(PayInfoModel model) {
        switch (current) {
            case ORDER_PAYED:
                if (payedView != null) {
                    payedView.selectPayInfoById(model);
                }
                break;
        }
    }

    @Override
    public void uselectUserInfo(String imageUrl) {
        switch (current) {
            case ORDER_PAYED:
                if (payedView != null) {
                    payedView.uselectUserInfo(imageUrl);
                }
                break;
            case ORDER_COINED:
                if (coinedView != null) {
                    coinedView.uselectUserInfo(imageUrl);
                }
                break;
            case ORDER_COINGET:
                if (coinGetView != null) {
                    coinGetView.uselectUserInfo(imageUrl);
                }
                break;
            case ORDER_CANCEL:
            case ORDER_TIMEOUT:
            case ORDER_FINISHED:
                if (finishView != null) {
                    finishView.uselectUserInfo(imageUrl);
                }
                break;
            case ORDER_ARBITRATION_BUY:
            case ORDER_ARBITRATION_SELL:
            case ORDER_ARBITRATION_SUCCESS:
                if (arbitrationView != null) {
                    arbitrationView.uselectUserInfo(imageUrl);
                }
                break;
        }
    }

    @Override
    public void uselectUserInfoArbitration(int type, String imageUrl) {
        switch (current) {
            case ORDER_ARBITRATION_BUY:
            case ORDER_ARBITRATION_SELL:
            case ORDER_ARBITRATION_SUCCESS:
                if (arbitrationView != null) {
                    arbitrationView.uselectUserInfoArbitration(type, imageUrl);
                }
                break;
        }
    }

    @Override
    public void getOssSetting(AuthOssModel model) {
        if (model != null) {
            OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(model.AccessKeyId, model.AccessKeySecret, model.SecurityToken);
            ClientConfiguration conf = new ClientConfiguration();
            conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
            conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
            conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
            conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
            OSSLog.enableLog();

            OSS mOss = new OSSClient(MyApplication.getInstance(), Constant.OSS_ENDPOINT, credentialProvider);
            String bucketName = model.bucket;

            switch (current) {
                case ORDER_PAYED:
                    if (payedView != null) {
                        payedView.uploadImgs(mOss, bucketName);
                    }
                    break;
                case ORDER_COINGET:
                    if (coinGetView != null) {
                        coinGetView.uploadImgs(mOss, bucketName);
                    }
                    break;
                case ORDER_ARBITRATION_BUY:
                case ORDER_ARBITRATION_SELL:
                case ORDER_ARBITRATION_SUCCESS:
                    if (arbitrationView != null) {
                        arbitrationView.uploadImgs(mOss, bucketName);
                    }
                    break;
            }
        }
    }

    @Override
    public void payOrderSuccess() {
        showToastSuccess(getResources().getString(R.string.submit_success));
        upload(true);
    }

    @Override
    public void payCancelOrderSuccess() {
        showToastSuccess(getResources().getString(R.string.cancel_success));
        finish();
    }

    @Override
    public void finishOrderSuccess() {
        showToastSuccess(getResources().getString(R.string.submit_success));
        upload(true);
    }

    @Override
    public void arbitrationOrderSuccess() {
        showToastSuccess(getResources().getString(R.string.submit_success));
        upload(true);
    }

    @Override
    public void beArbitrationOrderSuccess() {
        showToastSuccess(getResources().getString(R.string.submit_success));
        finish();
    }

    private void upload(boolean isShowView) {
        mPresenter.selectTradeOne(isShowView, isFirst, orderNo);
        if (isFirst) {
            isFirst = false;
        }
    }

    private void switchView(Integer status) {
        View statusView = null;
        switch (current) {
            case ORDER_CONFIRMED://待确认
                mTopBar.setTitle(getResources().getString(R.string.otc_order_confirmed));
                confirmView = new ConfirmView();
                statusView = confirmView.create(this);
                nodeProgress.setCurentNode(1);
                break;
            case ORDER_PAYED://待付款
                mTopBar.setTitle(getResources().getString(R.string.otc_order_payed));
                payedView = new PayedView();
                statusView = payedView.create(this, mPresenter);
                nodeProgress.setCurentNode(1);
                break;
            case ORDER_PAYGET://待收款
                mTopBar.setTitle(getResources().getString(R.string.otc_order_payed));
                payGetView = new PayGetView();
                statusView = payGetView.create(this);
                nodeProgress.setCurentNode(1);
                break;
            case ORDER_COINED://待放币
                mTopBar.setTitle(getResources().getString(R.string.otc_order_coined));
                coinedView = new CoinedView();
                statusView = coinedView.create(this, mPresenter, status);
                nodeProgress.setCurentNode(2);
                break;
            case ORDER_COINGET://待收币
                mTopBar.setTitle(getResources().getString(R.string.otc_order_coined));
                coinGetView = new CoinGetView();
                statusView = coinGetView.create(this, mPresenter, status);
                nodeProgress.setCurentNode(2);
                break;
            case ORDER_CANCEL:
            case ORDER_TIMEOUT:
            case ORDER_FINISHED://已完成
                if (current == ORDER_CANCEL) {
                    mTopBar.setTitle(getResources().getString(R.string.otc_order_cancel));
                } else if (current == ORDER_TIMEOUT) {
                    mTopBar.setTitle(getResources().getString(R.string.otc_order_timeout));
                } else if (current == ORDER_FINISHED) {
                    mTopBar.setTitle(getResources().getString(R.string.otc_order_finished));
                }
                finishView = new FinishView();
                statusView = finishView.create(this, status);
                nodeProgress.setCurentNode(4);
                break;
            case ORDER_ARBITRATION_BUY://仲裁中
            case ORDER_ARBITRATION_SELL:
            case ORDER_ARBITRATION_SUCCESS:
                mTopBar.setTitle(getResources().getString(R.string.otc_order_arbitration));
                if (current == ORDER_ARBITRATION_SUCCESS) {
                    mTopBar.setTitle(getResources().getString(R.string.otc_order_adjude));
                }
                arbitrationView = new ArbitrationView();
                statusView = arbitrationView.create(this, mPresenter, current);
                nodeProgress.setCurentNode(4);
                break;
        }
        if (statusView != null) {
            linSwitch.removeAllViews();
            linSwitch.addView(statusView);
        }
    }

    private void destoryAllView() {
        if (confirmView != null) {
            confirmView.destory();
            confirmView = null;
        }
        if (payedView != null) {
            payedView.destory();
            payedView = null;
        }
        if (payGetView != null) {
            payGetView.destory();
            payGetView = null;
        }
        if (coinedView != null) {
            coinedView.destory();
            coinedView = null;
        }
        if (coinGetView != null) {
            coinGetView.destory();
            coinGetView = null;
        }
        if (finishView != null) {
            finishView.destory();
            finishView = null;
        }
        if (arbitrationView != null) {
            arbitrationView.destory();
            arbitrationView = null;
        }
    }
}
