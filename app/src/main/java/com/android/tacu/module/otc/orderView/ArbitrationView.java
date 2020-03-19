package com.android.tacu.module.otc.orderView;

import android.app.Dialog;
import android.os.Environment;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
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
import com.android.tacu.module.otc.dialog.OtcTradeDialogUtils;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.presenter.OtcOrderDetailPresenter;
import com.android.tacu.module.otc.view.OtcOrderDetailActivity;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.ShowToast;
import com.android.tacu.utils.permission.PermissionUtils;
import com.android.tacu.utils.user.UserInfoUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundImageView;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;

public class ArbitrationView implements View.OnClickListener {

    private OtcOrderDetailActivity activity;
    private OtcOrderDetailPresenter mPresenter;
    private int current;

    private TextView tv_order_id;
    private TextView tv_pay_method;
    private TextView tv_trade_get;
    private TextView tv_trade_coin;

    private TextView tv_order_make;
    private TextView tv_order_pay;
    private TextView tv_order_finish;
    private TextView tv_order_finish_status;

    private QMUIRoundImageView img_voucher;

    private TextView tv_arbitration_reason;
    private QMUIRoundImageView img_arbitration_reason;
    private TextView tv_appeal_reason;
    private QMUIRoundImageView img_appeal_reason;
    private TextView tv_arbitration_result;

    private QMUIRoundEditText edit_submit_arbitration;
    private LinearLayout lin_upload;
    private ImageView img_add;
    private TextView tv_add;
    private ImageView img_url;

    private ConstraintLayout lin_coin;
    private QMUIRoundEditText et_pwd;
    private QMUIRoundButton btn_coined;

    private QMUIRoundButton btn_submit_arbitration;
    private QMUIRoundButton btn_return;

    private ImageView img_trade_coin;

    private OtcTradeModel tradeModel;
    private String imageUrl;
    private String imageUrlAritrotion;
    private String imageUrlBeAritrotion;
    private File uploadFile;
    private String uploadImageName;

    private UserInfoUtils spUtil;

    private List<OSSAsyncTask> ossAsynTaskList = new ArrayList<>();
    private DroidDialog droidDialog;

    private Handler mHandler = new Handler();

    public View create(OtcOrderDetailActivity activity, OtcOrderDetailPresenter mPresenter, int current) {
        this.activity = activity;
        this.mPresenter = mPresenter;
        this.current = current;
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
        img_arbitration_reason = view.findViewById(R.id.img_arbitration_reason);
        tv_appeal_reason = view.findViewById(R.id.tv_appeal_reason);
        img_appeal_reason = view.findViewById(R.id.img_appeal_reason);
        tv_arbitration_result = view.findViewById(R.id.tv_arbitration_result);

        edit_submit_arbitration = view.findViewById(R.id.edit_submit_arbitration);
        lin_upload = view.findViewById(R.id.lin_upload);
        img_add = view.findViewById(R.id.img_add);
        tv_add = view.findViewById(R.id.tv_add);
        img_url = view.findViewById(R.id.img_url);

        lin_coin = view.findViewById(R.id.lin_coin);
        et_pwd = view.findViewById(R.id.et_pwd);
        btn_coined = view.findViewById(R.id.btn_coined);
        btn_submit_arbitration = view.findViewById(R.id.btn_submit_arbitration);
        btn_return = view.findViewById(R.id.btn_return);

        img_trade_coin = view.findViewById(R.id.img_trade_coin);

        if (current == OtcOrderDetailActivity.ORDER_ARBITRATION_BUY) {
            edit_submit_arbitration.setVisibility(View.GONE);
            lin_upload.setVisibility(View.GONE);
            btn_submit_arbitration.setVisibility(View.GONE);
            lin_coin.setVisibility(View.VISIBLE);
        } else if (current == OtcOrderDetailActivity.ORDER_ARBITRATION_SELL) {
            edit_submit_arbitration.setVisibility(View.VISIBLE);
            lin_upload.setVisibility(View.VISIBLE);
            btn_submit_arbitration.setVisibility(View.VISIBLE);
            lin_coin.setVisibility(View.GONE);
        } else if (current == OtcOrderDetailActivity.ORDER_ARBITRATION_SUCCESS) {
            edit_submit_arbitration.setVisibility(View.GONE);
            lin_upload.setVisibility(View.GONE);
            btn_submit_arbitration.setVisibility(View.GONE);
            lin_coin.setVisibility(View.GONE);
        }

        img_voucher.setOnClickListener(this);
        img_arbitration_reason.setOnClickListener(this);
        img_appeal_reason.setOnClickListener(this);
        lin_upload.setOnClickListener(this);
        btn_coined.setOnClickListener(this);
        btn_submit_arbitration.setOnClickListener(this);
        btn_return.setOnClickListener(this);

        spUtil = UserInfoUtils.getInstance();
        if (spUtil.getPwdVisibility()) {
            et_pwd.setVisibility(View.VISIBLE);
        } else {
            et_pwd.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_voucher:
                if (!TextUtils.isEmpty(imageUrl)) {
                    activity.jumpTo(ZoomImageViewActivity.createActivity(activity, imageUrl));
                }
                break;
            case R.id.img_arbitration_reason:
                if (!TextUtils.isEmpty(imageUrlAritrotion)) {
                    activity.jumpTo(ZoomImageViewActivity.createActivity(activity, imageUrlAritrotion));
                }
                break;
            case R.id.img_appeal_reason:
                if (!TextUtils.isEmpty(imageUrlBeAritrotion)) {
                    activity.jumpTo(ZoomImageViewActivity.createActivity(activity, imageUrlBeAritrotion));
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
            case R.id.btn_coined:
                if (!OtcTradeDialogUtils.isDialogShow(activity)) {
                    String pwdString = et_pwd.getText().toString().trim();
                    if (spUtil.getPwdVisibility() && TextUtils.isEmpty(pwdString)) {
                        ShowToast.error(activity.getResources().getString(R.string.please_input_trade_password));
                        return;
                    }
                    showSure(pwdString);
                }
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

    public void uselectUserInfoArbitration(int type, String imageUrl) {
        switch (type) {
            case 1:
                this.imageUrlAritrotion = imageUrl;
                if (!TextUtils.isEmpty(imageUrl)) {
                    GlideUtils.disPlay(activity, imageUrl, img_arbitration_reason);
                    img_arbitration_reason.setVisibility(View.VISIBLE);
                } else {
                    img_arbitration_reason.setVisibility(View.GONE);
                }
                break;
            case 2:
                this.imageUrlBeAritrotion = imageUrl;
                if (!TextUtils.isEmpty(imageUrl)) {
                    GlideUtils.disPlay(activity, imageUrl, img_appeal_reason);
                    img_appeal_reason.setVisibility(View.VISIBLE);
                } else {
                    img_appeal_reason.setVisibility(View.GONE);
                }
                break;
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
            if (tradeModel.status != null) {
                //12 买家成功 13 卖家成功
                if (tradeModel.status == 12) {
                    img_trade_coin.setImageResource(R.drawable.icon_auth_success);
                    tv_order_finish_status.setText(activity.getResources().getString(R.string.order_finish));
                    tv_order_finish_status.setTextColor(ContextCompat.getColor(activity, R.color.color_otc_buy));
                } else if (tradeModel.status == 13) {
                    img_trade_coin.setImageResource(R.drawable.icon_auth_failure);
                    tv_order_finish_status.setText(activity.getResources().getString(R.string.not_coined));
                    tv_order_finish_status.setTextColor(ContextCompat.getColor(activity, R.color.color_otc_sell));
                }
            }

            if (!TextUtils.isEmpty(tradeModel.payInfo)) {
                img_voucher.setVisibility(View.VISIBLE);
            } else {
                img_voucher.setVisibility(View.GONE);
            }

            if (tradeModel.beArbitrateUid != null && tradeModel.beArbitrateUid != 0) {
                edit_submit_arbitration.setVisibility(View.GONE);
                lin_upload.setVisibility(View.GONE);
                btn_submit_arbitration.setVisibility(View.GONE);
            } else {
                edit_submit_arbitration.setVisibility(View.VISIBLE);
                lin_upload.setVisibility(View.VISIBLE);
                btn_submit_arbitration.setVisibility(View.VISIBLE);
            }

            tv_order_id.setText(tradeModel.orderNo);
            tv_trade_get.setText(tradeModel.amount + " " + Constant.CNY);
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
            tv_appeal_reason.setText(tradeModel.beArbitrateExp);
            tv_arbitration_result.setText(tradeModel.arbitrateResults);

            if (!TextUtils.isEmpty(tradeModel.arbitrateImg)) {
                mPresenter.uselectUserInfoArbitration(1, tradeModel.arbitrateImg);
            }
            if (!TextUtils.isEmpty(tradeModel.beArbitrateImg)) {
                mPresenter.uselectUserInfoArbitration(2, tradeModel.beArbitrateImg);
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

    private void showSure(final String pwdString) {
        droidDialog = new DroidDialog.Builder(activity)
                .title(activity.getResources().getString(R.string.sure_again))
                .content(activity.getResources().getString(R.string.please_sure_again_coined))
                .contentGravity(Gravity.CENTER)
                .positiveButton(activity.getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        if (tradeModel != null) {
                            mPresenter.finishOrder(tradeModel.id, spUtil.getPwdVisibility() ? Md5Utils.encryptFdPwd(pwdString, spUtil.getUserUid()).toLowerCase() : null);
                        }
                    }
                })
                .negativeButton(activity.getResources().getString(R.string.cancel), null)
                .cancelable(false, false)
                .show();
    }

    public void destory() {
        activity = null;
        mPresenter = null;
        if (droidDialog != null && droidDialog.isShowing()) {
            droidDialog.dismiss();
        }
        if (ossAsynTaskList != null && ossAsynTaskList.size() > 0) {
            for (OSSAsyncTask ossAsyncTask : ossAsynTaskList) {
                ossAsyncTask.cancel();
            }
        }
    }
}
