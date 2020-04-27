package com.android.tacu.module.payinfo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.base.MyApplication;
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.module.ZoomImageViewActivity;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.otc.dialog.OtcPwdDialogUtils;
import com.android.tacu.module.otc.dialog.OtcTradeDialogUtils;
import com.android.tacu.module.payinfo.contract.PayInfoContract;
import com.android.tacu.module.payinfo.model.PayInfoModel;
import com.android.tacu.module.payinfo.presenter.PayInfoPresenter;
import com.android.tacu.utils.ActivityStack;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.permission.PermissionUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundImageView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundRelativeLayout;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;

public class BindingInfoWxActivity extends BaseActivity<PayInfoPresenter> implements PayInfoContract.IDetailView {

    @BindView(R.id.lin_edit)
    LinearLayout lin_edit;
    @BindView(R.id.edit_account_owner)
    EditText edit_account_owner;
    @BindView(R.id.edit_wx_name)
    EditText edit_wx_name;
    @BindView(R.id.img_wx_shoukuan)
    QMUIRoundImageView img_wx_shoukuan;
    @BindView(R.id.rl_upload)
    QMUIRoundRelativeLayout rl_upload;
    @BindView(R.id.tv_upload_tip)
    TextView tv_upload_tip;

    @BindView(R.id.lin_list)
    LinearLayout lin_list;
    @BindView(R.id.tv_account_owner1)
    TextView tv_account_owner1;
    @BindView(R.id.tv_wx_name)
    TextView tv_wx_name;
    @BindView(R.id.img_wx_shoukuan1)
    QMUIRoundImageView img_wx_shoukuan1;

    private final int TAKE_PIC = 1001;
    private PayInfoModel payInfoModel;

    //oss
    private String uploadImageName;
    private File uploadFile;

    private String name;
    private String weChatNo;
    private String pwdString;

    private OSS mOss = null;
    private String bucketName;
    private List<OSSAsyncTask> ossAsynTaskList = new ArrayList<>();

    private String imageUrl;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    hideLoadingView();
                    break;
                case 1:
                    showToastError(getResources().getString(R.string.net_busy));
                    hideLoadingView();
                    break;
            }
        }
    };

    public static Intent createActivity(Context context, PayInfoModel payInfoModel) {
        Intent intent = new Intent(context, BindingInfoWxActivity.class);
        intent.putExtra("payInfoModel", payInfoModel);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_wx);
    }

    @Override
    protected void initView() {
        payInfoModel = (PayInfoModel) getIntent().getSerializableExtra("payInfoModel");

        mTopBar.setTitle(getResources().getString(R.string.add) + getResources().getString(R.string.weixin));
    }

    @Override
    protected PayInfoPresenter createPresenter(PayInfoPresenter mPresenter) {
        return new PayInfoPresenter();
    }

    @Override
    protected void onPresenterCreated(PayInfoPresenter presenter) {
        super.onPresenterCreated(presenter);
        setValue(payInfoModel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ossAsynTaskList != null && ossAsynTaskList.size() > 0) {
            for (OSSAsyncTask ossAsyncTask : ossAsynTaskList) {
                ossAsyncTask.cancel();
            }
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == TAKE_PIC) {
            ArrayList<String> imageList = BGAPhotoPickerActivity.getSelectedImages(data);
            for (int i = 0; i < imageList.size(); i++) {
                String imageUri = imageList.get(i);
                File fileOrgin = new File(imageUri);

                uploadFile = fileOrgin;
                rl_upload.setVisibility(View.GONE);
                tv_upload_tip.setVisibility(View.GONE);
                GlideUtils.disPlay(this, "file://" + fileOrgin.getPath(), img_wx_shoukuan);
            }
        }
    }

    @OnClick({R.id.img_wx_shoukuan, R.id.rl_upload})
    void wxImageClick() {
        PermissionUtils.requestPermissions(this, new OnPermissionListener() {
            @Override
            public void onPermissionSucceed() {
                boolean mTakePhotoEnabled = true;
                // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
                File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "tacu");
                startActivityForResult(BGAPhotoPickerActivity.newIntent(BindingInfoWxActivity.this, mTakePhotoEnabled ? takePhotoDir : null, 1, null, mTakePhotoEnabled), TAKE_PIC);
            }

            @Override
            public void onPermissionFailed() {
            }
        }, Permission.Group.STORAGE, Permission.Group.CAMERA);
    }

    @OnClick(R.id.img_wx_shoukuan1)
    void lookBigClick() {
        if (!TextUtils.isEmpty(imageUrl)) {
            jumpTo(ZoomImageViewActivity.createActivity(this, imageUrl));
        }
    }

    @OnClick(R.id.btn_bindinng)
    void bindingClick() {
        if (!OtcTradeDialogUtils.isDialogShow(this)) {
            name = edit_account_owner.getText().toString().trim();
            weChatNo = edit_wx_name.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                showToastError(getResources().getString(R.string.please_input_username));
                return;
            }
            if (TextUtils.isEmpty(weChatNo)) {
                showToastError(getResources().getString(R.string.please_input_wx_account));
                return;
            }
            if (uploadFile == null) {
                showToastError(getResources().getString(R.string.please_upload_wx_shoukuanma));
                return;
            }
            if (spUtil.getPwdVisibility()) {
                OtcPwdDialogUtils.showPwdDiaglog(this, getResources().getString(R.string.please_input_trade_password), new OtcPwdDialogUtils.OnPassListener() {
                    @Override
                    public void onPass(String pwd) {
                        pwdString = pwd;
                        showLoadingView();
                        mPresenter.getOssSetting(uploadFile.getPath());
                    }
                });
                return;
            }
            showLoadingView();
            mPresenter.getOssSetting(uploadFile.getPath());
        }
    }

    @OnClick(R.id.btn_delete)
    void deleteClick() {
        showDelete();
    }

    @Override
    public void insertSuccess() {
        ActivityStack.getInstance().finishActivity(PayInfoTypeActivity.class);
        finish();
    }

    @Override
    public void deleteSuccess() {
        ActivityStack.getInstance().finishActivity(PayInfoTypeActivity.class);
        finish();
    }

    @Override
    public void getOssSetting(AuthOssModel model, String fileLocalNameAddress) {
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

            uploadImgs(fileLocalNameAddress);
        }
    }

    @Override
    public void uselectUserInfo(String imageUrl) {
        this.imageUrl = imageUrl;
        if (!TextUtils.isEmpty(imageUrl)) {
            GlideUtils.disPlay(this, imageUrl, img_wx_shoukuan1);
        }
    }

    private void showDelete() {
        new DroidDialog.Builder(this)
                .title(getResources().getString(R.string.tips))
                .content(getResources().getString(R.string.is_delete_account))
                .contentGravity(Gravity.CENTER)
                .positiveButton(getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        mPresenter.delete(payInfoModel.id);
                    }
                })
                .negativeButton(getResources().getString(R.string.cancel), null)
                .cancelable(false, false)
                .show();
    }

    private void setValue(PayInfoModel model) {
        if (model != null) {
            lin_edit.setVisibility(View.GONE);
            lin_list.setVisibility(View.VISIBLE);

            tv_account_owner1.setText(model.name);
            tv_wx_name.setText(model.weChatNo);
            mPresenter.uselectUserInfo(model.weChatImg);
        } else {
            lin_edit.setVisibility(View.VISIBLE);
            lin_list.setVisibility(View.GONE);

            if (spUtil.getApplyAuthMerchantStatus() != 2) {
                edit_account_owner.setEnabled(false);
                edit_account_owner.setText(spUtil.getKYCName());
            }
        }
    }

    private void uploadImgs(String fileLocalNameAddress) {
        if (!TextUtils.isEmpty(bucketName) && mOss != null) {
            // 构造上传请求
            uploadImageName = CommonUtils.getWxImageName();
            PutObjectRequest put = new PutObjectRequest(bucketName, uploadImageName, fileLocalNameAddress);
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
            mHandler.sendEmptyMessage(0);
            mPresenter.insert(2, name, null, null, null, weChatNo, uploadImageName, null, null, spUtil.getPwdVisibility() ? Md5Utils.encryptFdPwd(pwdString, spUtil.getUserUid()).toLowerCase() : null);
        } else {
            mHandler.sendEmptyMessage(1);
        }
    }
}
