package com.android.tacu.module.otc.orderView;

import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.module.ZoomImageViewActivity;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.presenter.OtcOrderDetailPresenter;
import com.android.tacu.module.otc.view.OtcOrderDetailActivity;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.permission.PermissionUtils;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;

//待付款
public class PayedView implements View.OnClickListener {

    private OtcOrderDetailActivity activity;
    private OtcOrderDetailPresenter mPresenter;

    private TextView tv_countdown;
    private TextView tv_order_id;
    private TextView tv_need_pay;
    private TextView tv_pay_method;
    private QMUIRadiusImageView img_payment_code;
    private TextView tv_payment_code_tip;

    private LinearLayout lin_pay;
    private TextView tv_cardholder_name1;
    private TextView tv_bank_name;
    private TextView tv_open_bank_name;
    private TextView tv_open_bank_address;
    private TextView tv_bank_id;

    private LinearLayout lin_upload;
    private ImageView img_add;
    private TextView tv_add;
    private ImageView img_url;

    private QMUIRoundButton btn_pay;
    private QMUIRoundButton btn_giveup;

    private OtcTradeModel tradeModel;
    private Long currentTime;
    private CountDownTimer time;
    private String imageUrl;
    private File uploadFile;
    private String uploadImageName;

    private List<OSSAsyncTask> ossAsynTaskList = new ArrayList<>();

    private Handler mHandler = new Handler();

    public View create(OtcOrderDetailActivity activity, OtcOrderDetailPresenter mPresenter) {
        this.activity = activity;
        this.mPresenter = mPresenter;
        View statusView = View.inflate(activity, R.layout.view_otc_order_payed, null);
        initPayedView(statusView);
        return statusView;
    }

    /**
     * 待付款
     */
    private void initPayedView(View view) {
        tv_countdown = view.findViewById(R.id.tv_countdown);
        tv_order_id = view.findViewById(R.id.tv_order_id);
        tv_need_pay = view.findViewById(R.id.tv_need_pay);
        tv_pay_method = view.findViewById(R.id.tv_pay_method);
        img_payment_code = view.findViewById(R.id.img_payment_code);
        tv_payment_code_tip = view.findViewById(R.id.tv_payment_code_tip);

        lin_pay = view.findViewById(R.id.lin_pay);
        tv_cardholder_name1 = view.findViewById(R.id.tv_cardholder_name1);
        tv_bank_name = view.findViewById(R.id.tv_bank_name);
        tv_open_bank_name = view.findViewById(R.id.tv_open_bank_name);
        tv_open_bank_address = view.findViewById(R.id.tv_open_bank_address);
        tv_bank_id = view.findViewById(R.id.tv_bank_id);

        lin_upload = view.findViewById(R.id.lin_upload);
        img_add = view.findViewById(R.id.img_add);
        tv_add = view.findViewById(R.id.tv_add);
        img_url = view.findViewById(R.id.img_url);

        btn_pay = view.findViewById(R.id.btn_pay);
        btn_giveup = view.findViewById(R.id.btn_giveup);

        img_payment_code.setOnClickListener(this);
        lin_upload.setOnClickListener(this);
        btn_pay.setOnClickListener(this);
        btn_giveup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_payment_code:
                if (!TextUtils.isEmpty(imageUrl)) {
                    activity.jumpTo(ZoomImageViewActivity.createActivity(activity, imageUrl));
                }
                break;
            case R.id.lin_upload:
                PermissionUtils.requestPermissions(activity, new OnPermissionListener() {
                    @Override
                    public void onPermissionSucceed() {
                        boolean mTakePhotoEnabled = true;
                        // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
                        File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "tacu");
                        activity.startActivityForResult(BGAPhotoPickerActivity.newIntent(activity, mTakePhotoEnabled ? takePhotoDir : null, 1, null, mTakePhotoEnabled), OtcOrderDetailActivity.TAKE_PIC);
                    }

                    @Override
                    public void onPermissionFailed() {
                    }
                }, Permission.Group.STORAGE, Permission.Group.CAMERA);
                break;
            case R.id.btn_pay:
                if (uploadFile == null) {
                    activity.showToastError(activity.getResources().getString(R.string.please_upload_your_payimg));
                    return;
                }
                activity.showLoadingView();
                mPresenter.getOssSetting();
                break;
            case R.id.btn_giveup:
                if (tradeModel != null) {
                    mPresenter.payCancelOrder(tradeModel.id);
                }
                break;
        }
    }

    public void selectTradeOne(OtcTradeModel model) {
        this.tradeModel = model;
        dealPayed();
        dealTime();
    }

    public void currentTime(Long currentTime) {
        this.currentTime = currentTime;
        dealTime();
    }

    public void selectPayInfoById(PayInfoModel model) {
        if (model != null && model.type != null) {
            switch (model.type) {
                case 1:
                    if (model.uid != null) {
                        mPresenter.userBaseInfo(null, model.uid);
                    }

                    img_payment_code.setVisibility(View.GONE);
                    tv_payment_code_tip.setVisibility(View.GONE);
                    lin_pay.setVisibility(View.VISIBLE);

                    tv_bank_name.setText(model.bankName);
                    tv_open_bank_name.setText(model.openBankName);
                    tv_open_bank_address.setText(model.openBankAdress);
                    tv_bank_id.setText(model.bankCard);
                    break;
                case 2:
                    mPresenter.uselectUserInfo(model.weChatImg);

                    img_payment_code.setVisibility(View.VISIBLE);
                    tv_payment_code_tip.setVisibility(View.VISIBLE);
                    tv_payment_code_tip.setText(activity.getResources().getString(R.string.please_scan_with_wx));
                    lin_pay.setVisibility(View.GONE);
                    break;
                case 3:
                    mPresenter.uselectUserInfo(model.aliPayImg);

                    img_payment_code.setVisibility(View.VISIBLE);
                    tv_payment_code_tip.setVisibility(View.VISIBLE);
                    tv_payment_code_tip.setText(activity.getResources().getString(R.string.please_scan_with_zfb));
                    lin_pay.setVisibility(View.GONE);
                    break;
            }
        }
    }

    public void uselectUserInfo(String imageUrl) {
        this.imageUrl = imageUrl;
        if (!TextUtils.isEmpty(imageUrl)) {
            GlideUtils.disPlay(activity, imageUrl, img_payment_code);
            img_payment_code.setVisibility(View.VISIBLE);
        } else {
            img_payment_code.setVisibility(View.GONE);
        }
    }

    public void userBaseInfo(OtcMarketInfoModel model) {
        if (model != null) {
            tv_cardholder_name1.setText(model.firstName + model.secondName);
        }
    }

    public void getPic(File file) {
        if (file != null) {
            uploadFile = file;
            img_add.setVisibility(View.GONE);
            tv_add.setVisibility(View.GONE);
            img_url.setVisibility(View.VISIBLE);
            GlideUtils.disPlay(activity, "file://" + file.getPath(), img_url);
        }
    }

    public void uploadImgs(OSS mOss, String bucketName) {
        if (!TextUtils.isEmpty(bucketName) && mOss != null) {
            // 构造上传请求
            uploadImageName = CommonUtils.getPayImageName();
            PutObjectRequest put = new PutObjectRequest(bucketName, uploadImageName, uploadFile.getPath());
            // 异步上传时可以设置进度回调
            put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                @Override
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                }
            });
            OSSAsyncTask task = mOss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    dealValue(1);
                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                    dealValue(2);
                }
            });
            ossAsynTaskList.add(task);
            //可以取消任务
            //task.cancel();
            //可以等待直到任务完成
            //task.waitUntilFinished();
        }
    }

    private void dealTime() {
        if (currentTime != null && tradeModel != null && tradeModel.payEndTime != null) {
            long payEndTime = DateUtils.string2Millis(tradeModel.payEndTime, DateUtils.DEFAULT_PATTERN) - currentTime;
            if (payEndTime > 0) {
                startCountDownTimer(payEndTime);
            }
        }
    }

    private void dealPayed() {
        if (tradeModel != null) {
            tv_order_id.setText(tradeModel.orderNo);
            tv_need_pay.setText(tradeModel.amount + " " + Constant.CNY);
            if (tradeModel.payType != null) {
                switch (tradeModel.payType) {//支付类型 1 银行 2微信3支付宝
                    case 1:
                        tv_pay_method.setText(activity.getResources().getString(R.string.yinhanngka));
                        break;
                    case 2:
                        tv_pay_method.setText(activity.getResources().getString(R.string.weixin));
                        break;
                    case 3:
                        tv_pay_method.setText(activity.getResources().getString(R.string.zhifubao));
                        break;
                }
            }
        }
    }

    private void dealValue(final int flag) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (flag == 1 && tradeModel != null) {
                    mPresenter.payOrder(tradeModel.id, uploadImageName);
                }
                activity.hideLoadingView();
            }
        });
    }

    private void startCountDownTimer(long valueTime) {
        if (time != null) {
            return;
        }
        time = new CountDownTimer(valueTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                try {
                    tv_countdown.setText(DateUtils.getCountDownTime1(millisUntilFinished));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                cancel();
                activity.finish();
            }
        };
        time.start();
    }

    public void destory() {
        activity = null;
        mPresenter = null;
        if (ossAsynTaskList != null && ossAsynTaskList.size() > 0) {
            for (OSSAsyncTask ossAsyncTask : ossAsynTaskList) {
                ossAsyncTask.cancel();
            }
        }
        if (time != null) {
            time.cancel();
            time = null;
        }
    }
}
