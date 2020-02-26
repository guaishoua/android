package com.android.tacu.module.otc.orderView;

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
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.module.ZoomImageViewActivity;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.presenter.OtcOrderDetailPresenter;
import com.android.tacu.module.otc.view.OtcOrderDetailActivity;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.permission.PermissionUtils;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;

public class ArbitrationView implements View.OnClickListener {

    private OtcOrderDetailActivity activity;
    private OtcOrderDetailPresenter mPresenter;

    private TextView tv_order_id;
    private TextView tv_pay_method;
    private TextView tv_trade_get;
    private TextView tv_trade_coin;

    private TextView tv_order_make;
    private TextView tv_order_pay;
    private TextView tv_order_finish;
    private TextView tv_order_finish_status;

    private QMUIRadiusImageView img_voucher;

    private TextView tv_arbitration_reason;
    private TextView tv_arbitration_result;
    private QMUIRadiusImageView img_voucher_arbitration_reason;

    private QMUIRoundEditText edit_submit_arbitration;
    private LinearLayout lin_upload;
    private ImageView img_add;
    private TextView tv_add;
    private ImageView img_url;

    private QMUIRoundButton btn_submit_arbitration;
    private QMUIRoundButton btn_return;

    private OtcTradeModel tradeModel;
    private String imageUrl;
    private String imageUrlAritrotion;
    private File uploadFile;
    private String uploadImageName;

    private List<OSSAsyncTask> ossAsynTaskList = new ArrayList<>();

    private Handler mHandler = new Handler();

    public View create(OtcOrderDetailActivity activity, OtcOrderDetailPresenter mPresenter) {
        this.activity = activity;
        this.mPresenter = mPresenter;
        View statusView = View.inflate(activity, R.layout.view_otc_order_arbitration, null);
        initArbitrationView(statusView);
        return statusView;
    }

    private void initArbitrationView(View view) {
        tv_order_id = view.findViewById(R.id.tv_order_id);
        tv_pay_method = view.findViewById(R.id.tv_pay_method);
        tv_trade_get = view.findViewById(R.id.tv_trade_get);
        tv_trade_coin = view.findViewById(R.id.tv_trade_coin);

        img_voucher = view.findViewById(R.id.img_voucher);

        tv_order_make = view.findViewById(R.id.tv_order_make);
        tv_order_pay = view.findViewById(R.id.tv_order_pay);
        tv_order_finish = view.findViewById(R.id.tv_order_finish);
        tv_order_finish_status = view.findViewById(R.id.tv_order_finish_status);

        tv_arbitration_reason = view.findViewById(R.id.tv_arbitration_reason);
        tv_arbitration_result = view.findViewById(R.id.tv_arbitration_result);
        img_voucher_arbitration_reason = view.findViewById(R.id.img_voucher_arbitration_reason);

        edit_submit_arbitration = view.findViewById(R.id.edit_submit_arbitration);
        lin_upload = view.findViewById(R.id.lin_upload);
        img_add = view.findViewById(R.id.img_add);
        tv_add = view.findViewById(R.id.tv_add);
        img_url = view.findViewById(R.id.img_url);

        btn_submit_arbitration = view.findViewById(R.id.btn_submit_arbitration);
        btn_return = view.findViewById(R.id.btn_return);

        img_voucher.setOnClickListener(this);
        img_voucher_arbitration_reason.setOnClickListener(this);
        lin_upload.setOnClickListener(this);
        btn_submit_arbitration.setOnClickListener(this);
        btn_return.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_voucher:
                if (!TextUtils.isEmpty(imageUrl)) {
                    activity.jumpTo(ZoomImageViewActivity.createActivity(activity, imageUrl));
                }
                break;
            case R.id.img_voucher_arbitration_reason:
                if (!TextUtils.isEmpty(imageUrlAritrotion)) {
                    activity.jumpTo(ZoomImageViewActivity.createActivity(activity, imageUrlAritrotion));
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
            case R.id.btn_submit_arbitration:
                if (uploadFile != null) {
                    activity.showLoadingView();
                    mPresenter.getOssSetting();
                } else {
                    if (tradeModel != null) {
                        String beArbitrateExp = edit_submit_arbitration.getText().toString();
                        mPresenter.beArbitrationOrder(tradeModel.id, beArbitrateExp, null);
                    }
                }
                break;
            case R.id.btn_return:
                activity.finish();
                break;
        }
    }

    public void selectTradeOne(OtcTradeModel model) {
        this.tradeModel = model;
        dealArbitration();
    }

    public void uselectUserInfo(String imageUrl) {
        this.imageUrl = imageUrl;
        if (!TextUtils.isEmpty(imageUrl)) {
            GlideUtils.disPlay(activity, imageUrl, img_voucher);
            img_voucher.setVisibility(View.VISIBLE);
        } else {
            img_voucher.setVisibility(View.GONE);
        }
    }

    public void uselectUserInfoArbitration(String imageUrl) {
        this.imageUrlAritrotion = imageUrl;
        if (!TextUtils.isEmpty(imageUrl)) {
            GlideUtils.disPlay(activity, imageUrl, img_voucher_arbitration_reason);
            img_voucher_arbitration_reason.setVisibility(View.VISIBLE);
        } else {
            img_voucher_arbitration_reason.setVisibility(View.GONE);
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

    private void dealArbitration() {
        if (tradeModel != null) {
            tv_order_id.setText(tradeModel.orderNo);
            tv_trade_get.setText(tradeModel.amount + " CNY");
            tv_trade_coin.setText(tradeModel.num + " " + tradeModel.currencyName);
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

            tv_order_make.setText(tradeModel.createTime);
            tv_order_pay.setText(tradeModel.payTime);
            if (!TextUtils.isEmpty(tradeModel.updateTime)) {
                tv_order_finish.setVisibility(View.VISIBLE);
                tv_order_finish_status.setVisibility(View.VISIBLE);
                tv_order_finish.setText(tradeModel.updateTime);
            } else {
                tv_order_finish.setVisibility(View.GONE);
                tv_order_finish_status.setVisibility(View.GONE);
            }

            tv_arbitration_reason.setText(tradeModel.arbitrateExp);
            tv_arbitration_result.setText(tradeModel.arbitrateResults);

            if (!TextUtils.isEmpty(tradeModel.arbitrateImg)) {
                mPresenter.uselectUserInfoArbitration(tradeModel.arbitrateImg);
            }
        }
    }

    private void dealValue(final int flag) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (flag == 1 && tradeModel != null) {
                    String beArbitrateExp = edit_submit_arbitration.getText().toString();
                    mPresenter.beArbitrationOrder(tradeModel.id, beArbitrateExp, uploadImageName);
                }
                activity.hideLoadingView();
            }
        });
    }

    public void destory() {
        activity = null;
        mPresenter = null;
        if (ossAsynTaskList != null && ossAsynTaskList.size() > 0) {
            for (OSSAsyncTask ossAsyncTask : ossAsynTaskList) {
                ossAsyncTask.cancel();
            }
        }
    }
}
