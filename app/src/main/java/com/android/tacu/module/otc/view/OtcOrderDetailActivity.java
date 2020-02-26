package com.android.tacu.module.otc.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
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
import com.android.tacu.module.otc.orderView.ConfirmView;
import com.android.tacu.module.otc.orderView.PayGetView;
import com.android.tacu.module.otc.orderView.PayedView;
import com.android.tacu.module.otc.presenter.OtcOrderDetailPresenter;
import com.android.tacu.widget.NodeProgressView;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class OtcOrderDetailActivity extends BaseOtcOrderActvity<OtcOrderDetailPresenter> implements OtcOrderDetailContract.IView {

    private int current = -1;
    private Integer status = -1;
    private static final int ORDER_CONFIRMED = 1;//待确认
    private static final int ORDER_PAYED = 2;//待付款
    private static final int ORDER_PAYGET = 3;//待收款
    private static final int ORDER_COINED = 4;//待放币
    private static final int ORDER_COINGET = 5;//待收币
    private static final int ORDER_FINISHED = 6;//已完成
    private static final int ORDER_ARBITRATION = 7;//仲裁中
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

    //待收币

    //已完成

    //仲裁中

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
                new Compressor(this)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .compressToFileAsFlowable(fileOrgin)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<File>() {
                            @Override
                            public void accept(File file) {
                                switch (current) {
                                    case ORDER_PAYED:
                                        if (payedView != null) {
                                            payedView.getPic(file);
                                        }
                                        break;
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                            }
                        });
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

            // 1待确认 2 已确认待付款 3已付款待放币 4 仲裁 5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付 9 放币超时 10放币完成 11 待确认 待付款，待放币
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
                        if (model.buyuid == spUtil.getUserUid()) {
                            current = ORDER_COINGET;
                        } else if (model.selluid == spUtil.getUserUid()) {
                            current = ORDER_COINED;
                        }
                        break;
                    case 4:
                        current = ORDER_ARBITRATION;
                        break;
                    case 10:
                        current = ORDER_FINISHED;
                        break;
                }
                if (!status.equals(model.status)) {
                    status = model.status;
                    destoryAllView();
                    switchView();
                }
            }

            if (isFirst) {
                if (current == ORDER_CONFIRMED) {
                    mPresenter.currentTime();
                }
                if (current == ORDER_PAYED) {
                    mPresenter.currentTime();
                    mPresenter.selectPayInfoById(model.id);
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
            }
        }
    }

    @Override
    public void userBaseInfo(Integer buyOrSell, OtcMarketInfoModel model) {
        if (model != null) {
            if (buyOrSell == 1) {//买
                setBuyValue(model);
            } else if (buyOrSell == 2) {
                setSellValue(model);
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
            }
        }
    }

    @Override
    public void payOrderSuccess() {
        showToastSuccess(getResources().getString(R.string.success));
        upload(true);
    }

    private void upload(boolean isShowView) {
        mPresenter.selectTradeOne(isShowView, isFirst, orderNo);
        if (isFirst) {
            isFirst = false;
        }
    }

    private void switchView() {
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
                statusView = View.inflate(this, R.layout.view_otc_order_coined, null);
                initCoinedView(statusView);
                break;
            case ORDER_COINGET://待收币
                mTopBar.setTitle(getResources().getString(R.string.otc_order_coinget));
                statusView = View.inflate(this, R.layout.view_otc_order_coinget, null);
                initCoinGetView(statusView);
                break;
            case ORDER_FINISHED://已完成
                mTopBar.setTitle(getResources().getString(R.string.otc_order_finished));
                statusView = View.inflate(this, R.layout.view_otc_order_finished, null);
                initFinishedView(statusView);
                break;
            case ORDER_ARBITRATION://仲裁中
                mTopBar.setTitle(getResources().getString(R.string.otc_order_arbitration));
                statusView = View.inflate(this, R.layout.view_otc_order_arbitration, null);
                initArbitrationView(statusView);
                break;
        }
        if (statusView != null) {
            linSwitch.removeAllViews();
            linSwitch.addView(statusView);
        }
    }

    /**
     * 待放币
     */
    private void initCoinedView(View view) {
        nodeProgress.setCurentNode(2);
    }

    /**
     * 待收币
     */
    private void initCoinGetView(View view) {
        nodeProgress.setCurentNode(2);
    }

    /**
     * 已完成
     */
    private void initFinishedView(View view) {
        nodeProgress.setCurentNode(4);
    }

    /**
     * 仲裁中
     */
    private void initArbitrationView(View view) {
        nodeProgress.setCurentNode(4);
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
    }
}
