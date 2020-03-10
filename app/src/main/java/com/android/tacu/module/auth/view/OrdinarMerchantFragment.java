package com.android.tacu.module.auth.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.MainSwitchEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.base.MyApplication;
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.auth.contract.AuthMerchantContract;
import com.android.tacu.module.auth.presenter.AuthMerchantPresenter;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.main.view.MainActivity;
import com.android.tacu.module.my.view.SecurityCenterActivity;
import com.android.tacu.module.vip.view.BuyVipActivity;
import com.android.tacu.module.vip.view.RechargeDepositActivity;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.permission.PermissionUtils;
import com.android.tacu.view.GlideLoader;
import com.lcw.library.imagepicker.ImagePicker;
import com.lcw.library.imagepicker.activity.ImagePickerActivity;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class OrdinarMerchantFragment extends BaseFragment<AuthMerchantPresenter> implements AuthMerchantContract.IOrdinarView {

    @BindView(R.id.img_membership)
    ImageView img_membership;

    @BindView(R.id.img_binding)
    ImageView img_binding;
    @BindView(R.id.tv_binding_right)
    TextView tv_binding_right;

    @BindView(R.id.img_asset)
    ImageView img_asset;

    @BindView(R.id.img_kyc)
    ImageView img_kyc;
    @BindView(R.id.tv_kyc_error)
    TextView tv_kyc_error;

    @BindView(R.id.img_otc)
    ImageView img_otc;
    @BindView(R.id.tv_otc_error)
    TextView tv_otc_error;

    @BindView(R.id.img_video)
    ImageView img_video;
    @BindView(R.id.btn_upload_video)
    QMUIRoundButton btn_upload_video;

    @BindView(R.id.cb_xieyi)
    CheckBox cb_xieyi;
    @BindView(R.id.tv_xieyi)
    TextView tv_xieyi;
    @BindView(R.id.btn_submit)
    QMUIRoundButton btn_submit;

    private boolean isMembership = false;
    private boolean isBind = false;
    private boolean isAsset = false;
    private boolean isKyc = false;
    private boolean isOtc = false;
    private boolean isVideo = false;

    private static final int REQUEST_SELECT_IMAGES_CODE = 1001;
    private ArrayList<String> mImagePaths;

    //oss
    private OSS mOss = null;
    private String bucketName;
    private List<OSSAsyncTask> ossAsynTaskList = new ArrayList<>();

    private OwnCenterModel ownCenterModel;
    private OtcAmountModel otcAmountModel;
    private Integer otcTradeNum = null;
    private File uploadFile;
    private String uploadVideoName;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    dealValue();
                    hideLoadingView();
                    break;
                case 1:
                    showToastError(getResources().getString(R.string.net_busy));
                    hideLoadingView();
                    break;
            }
        }
    };

    public static OrdinarMerchantFragment newInstance() {
        Bundle bundle = new Bundle();
        OrdinarMerchantFragment fragment = new OrdinarMerchantFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_ordinar_merchant;
    }

    @Override
    protected void initData(View view) {
        tv_xieyi.setText(Html.fromHtml(getResources().getString(R.string.auth_xieyi1) + "<font color=" + ContextCompat.getColor(getContext(), R.color.text_default) + ">" + getResources().getString(R.string.auth_xieyi2) + "</font>"));

        dealValue();
    }

    @Override
    protected AuthMerchantPresenter createPresenter(AuthMerchantPresenter mPresenter) {
        return new AuthMerchantPresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
        upload();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (ossAsynTaskList != null && ossAsynTaskList.size() > 0) {
            for (OSSAsyncTask ossAsyncTask : ossAsynTaskList) {
                ossAsyncTask.cancel();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == REQUEST_SELECT_IMAGES_CODE) {
            mImagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
            for (int i = 0; i < mImagePaths.size(); i++) {
                String imageUri = mImagePaths.get(i);
                uploadFile = new File(imageUri);
                showLoadingView();
                mPresenter.getOssSetting();
            }
        }
    }

    @OnClick(R.id.tv_xieyi)
    void xieyiClick() {
        Intent intent = new Intent();
        Uri content_url = Uri.parse(Constant.SERVICE_APPLY_SYSTEM);
        intent.setAction("android.intent.action.VIEW");
        intent.setData(content_url);
        startActivity(intent);
    }

    @OnClick(R.id.tv_membership_right)
    void buyVip() {
        jumpTo(BuyVipActivity.class);
    }

    @OnClick(R.id.tv_binding_right)
    void BindingClick() {
        jumpTo(SecurityCenterActivity.class);
    }

    @OnClick(R.id.btn_asset_right)
    void assetClick() {
        jumpTo(RechargeDepositActivity.class);
    }

    @OnClick(R.id.btn_kyc_right)
    void kycClick() {
        jumpTo(AuthActivity.class);
    }

    @OnClick(R.id.btn_otc_right)
    void otcClick() {
        jumpTo(MainActivity.createActivity(getContext(), true));
        EventManage.sendEvent(new BaseEvent<>(EventConstant.MainSwitchCode, new MainSwitchEvent(Constant.MAIN_OTC)));
    }

    @OnClick(R.id.btn_upload_video)
    void uploadClick() {
        PermissionUtils.requestPermissions(getContext(), new OnPermissionListener() {
            @Override
            public void onPermissionSucceed() {
                ImagePicker.getInstance()
                        .setTitle("")//设置标题
                        .showCamera(false)//设置是否显示拍照按钮
                        .showImage(false)//设置是否展示图片
                        .showVideo(true)//设置是否展示视频
                        .setSingleType(true)//设置图片视频不能同时选择
                        .setMaxCount(1)//设置最大选择图片数目(默认为1，单选)
                        .setImagePaths(mImagePaths)//保存上一次选择图片的状态，如果不需要可以忽略
                        .setImageLoader(new GlideLoader());//设置自定义图片加载器
                jumpTo(ImagePickerActivity.class, REQUEST_SELECT_IMAGES_CODE);
            }

            @Override
            public void onPermissionFailed() {
            }
        }, Permission.Group.STORAGE, Permission.Group.CAMERA);
    }

    @OnClick(R.id.btn_submit)
    void submitClick() {
        if (!cb_xieyi.isChecked()) {
            showToastError(getResources().getString(R.string.please_check_xieyi));
            return;
        }
        if (spUtil.getDisclaimer() == 0) {
            mPresenter.disclaimer();
        }
        mPresenter.applyMerchant(uploadVideoName);
    }

    @Override
    public void countTrade(Integer num) {
        this.otcTradeNum = num;
        dealValue();
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
            mOss = new OSSClient(MyApplication.getInstance(), Constant.OSS_ENDPOINT, credentialProvider);
            bucketName = model.bucket;

            if (uploadFile != null) {
                uploadImgs(uploadFile.getPath());
            }
        }
    }

    @Override
    public void applyMerchantSuccess() {
        showToastSuccess(getResources().getString(R.string.apply_success));
        getHostActivity().finish();
    }

    @Override
    public void disclaimerSuccess() {
        spUtil.setDisclaimer(1);
    }

    private void upload() {
        mPresenter.countTrade();
    }

    public void setValue(OwnCenterModel model) {
        this.ownCenterModel = model;
        dealValue();
    }

    public void setBondAccount(OtcAmountModel model) {
        this.otcAmountModel = model;
        dealValue();
    }

    private void dealValue() {
        if (spUtil.getDisclaimer() == 1) {
            cb_xieyi.setChecked(true);
        }
        if (spUtil.getVip() != 0) {
            isMembership = true;
            img_membership.setImageResource(R.drawable.icon_auth_success);
        } else {
            isMembership = false;
            img_membership.setImageResource(R.drawable.icon_auth_failure);
        }
        if (spUtil.getEmailStatus() && spUtil.getPhoneStatus()) {
            isBind = true;
            img_binding.setImageResource(R.drawable.icon_auth_success);
            tv_binding_right.setText(getResources().getString(R.string.binded));
            tv_binding_right.setClickable(false);
        } else {
            isBind = false;
            img_binding.setImageResource(R.drawable.icon_auth_failure);
            tv_binding_right.setText(getResources().getString(R.string.go_binding));
            tv_binding_right.setClickable(true);
        }
        if (otcAmountModel != null && !TextUtils.isEmpty(otcAmountModel.cashAmount) && Double.valueOf(otcAmountModel.cashAmount) >= 5000) {
            isAsset = true;
            img_asset.setImageResource(R.drawable.icon_auth_success);
        } else {
            isAsset = false;
            img_asset.setImageResource(R.drawable.icon_auth_failure);
        }
        if (ownCenterModel != null && !TextUtils.isEmpty(ownCenterModel.keytowAdoptTime)) {
            int between = DateUtils.differentDaysByMillisecond(System.currentTimeMillis(), DateUtils.string2Millis(ownCenterModel.keytowAdoptTime, DateUtils.DEFAULT_PATTERN));
            int waitTime = 60 - between;
            if (between >= 60) {
                isKyc = true;
                img_kyc.setImageResource(R.drawable.icon_auth_success);
                tv_kyc_error.setText("");
            } else {
                isKyc = false;
                img_kyc.setImageResource(R.drawable.icon_auth_failure);
                tv_kyc_error.setText(String.format(getResources().getString(R.string.wait_day), String.valueOf(waitTime)));
            }
        } else {
            isKyc = false;
            img_kyc.setImageResource(R.drawable.icon_auth_failure);
        }
        if (otcTradeNum != null) {
            if (otcTradeNum >= 10) {
                isOtc = true;
                img_otc.setImageResource(R.drawable.icon_auth_success);
            } else {
                isOtc = false;
                img_otc.setImageResource(R.drawable.icon_auth_failure);
                tv_otc_error.setText(String.format(getResources().getString(R.string.cha_bi), String.valueOf(10 - otcTradeNum)));
            }
        } else {
            isOtc = false;
            img_otc.setImageResource(R.drawable.icon_auth_failure);
        }
        if (isVideo) {
            img_video.setImageResource(R.drawable.icon_auth_success);
        } else {
            img_video.setImageResource(R.drawable.icon_auth_failure);
        }

        if (isMembership && isBind && isAsset && isKyc && isOtc && isVideo) {
            btn_submit.setEnabled(true);
            ((QMUIRoundButtonDrawable) btn_submit.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_default));
        } else {
            btn_submit.setEnabled(false);
            ((QMUIRoundButtonDrawable) btn_submit.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_grey));
        }
        if (spUtil.getApplyMerchantStatus() == 1 || spUtil.getApplyMerchantStatus() == 2) {
            btn_submit.setEnabled(false);
            ((QMUIRoundButtonDrawable) btn_submit.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_grey));
            if (spUtil.getApplyMerchantStatus() == 1) {
                btn_submit.setText(getResources().getString(R.string.to_be_examine));
            } else if (spUtil.getApplyMerchantStatus() == 2) {
                btn_submit.setText(getResources().getString(R.string.apply_success1));
            }
            img_video.setImageResource(R.drawable.icon_auth_success);
            btn_upload_video.setVisibility(View.GONE);
        } else {
            btn_submit.setText(getResources().getString(R.string.confirm_apply_submit));
            btn_upload_video.setVisibility(View.VISIBLE);
        }
    }

    private void uploadImgs(String fileLocalNameAddress) {
        if (!TextUtils.isEmpty(bucketName) && mOss != null) {
            // 构造上传请求
            uploadVideoName = CommonUtils.getVideoName();
            PutObjectRequest put = new PutObjectRequest(bucketName, uploadVideoName, fileLocalNameAddress);
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

    private void dealValue(int flag) {
        if (flag == 1) {
            isVideo = true;
            mHandler.sendEmptyMessage(0);
        } else {
            isVideo = false;
            uploadFile = null;
            uploadVideoName = null;
            mHandler.sendEmptyMessage(1);
        }
    }
}
