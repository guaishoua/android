package com.android.tacu.module.my.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.payinfo.model.PayInfoModel;
import com.android.tacu.module.auth.view.AuthActivity;
import com.android.tacu.module.auth.view.AuthMerchantActivity;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.my.contract.EditPersonalDataContact;
import com.android.tacu.module.my.presenter.EditPersonalDataPresenter;
import com.android.tacu.module.payinfo.view.PayInfoListActivity;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.permission.PermissionUtils;
import com.android.tacu.utils.user.UserManageUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;

public class EditPersonalDataActivity extends BaseActivity<EditPersonalDataPresenter> implements EditPersonalDataContact.IView {

    @BindView(R.id.img_head_sculpture)
    QMUIRadiusImageView img_head_sculpture;
    @BindView(R.id.tv_account)
    TextView tv_account;
    @BindView(R.id.tv_uid)
    TextView tv_uid;
    @BindView(R.id.tv_nickname)
    TextView tv_nickname;
    @BindView(R.id.tv_binding_tel)
    TextView tv_binding_tel;
    @BindView(R.id.tv_binding_email)
    TextView tv_binding_email;
    @BindView(R.id.tv_apply_kyc)
    TextView tv_apply_kyc;
    @BindView(R.id.tv_apply_buniness)
    TextView tv_apply_buniness;
    @BindView(R.id.tv_binding_payinfo)
    TextView tv_binding_payinfo;

    private final int TAKE_PIC = 1001;
    private DroidDialog droidDialog;

    //oss
    private String imageName;
    private OSS mOss = null;
    private String bucketName;
    private List<OSSAsyncTask> ossAsynTaskList = new ArrayList<>();

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

    @Override
    protected void setView() {
        setContentView(R.layout.activity_edit_person_data);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.edit_person_data));

        setValue();
    }

    @Override
    protected EditPersonalDataPresenter createPresenter(EditPersonalDataPresenter mPresenter) {
        return new EditPersonalDataPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.ownCenter();
        mPresenter.selectBank();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (ossAsynTaskList != null && ossAsynTaskList.size() > 0) {
            for (OSSAsyncTask ossAsyncTask : ossAsynTaskList) {
                ossAsyncTask.cancel();
            }
        }
        if (droidDialog != null && droidDialog.isShowing()) {
            droidDialog.dismiss();
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

                showLoadingView();
                GlideUtils.disPlay(EditPersonalDataActivity.this, "file://" + fileOrgin.getPath(), img_head_sculpture);
                mPresenter.getOssSetting(fileOrgin.getPath());
            }
        }
    }

    @OnClick({R.id.img_head_sculpture})
    void headClick() {
        PermissionUtils.requestPermissions(this, new OnPermissionListener() {
            @Override
            public void onPermissionSucceed() {
                boolean mTakePhotoEnabled = true;
                // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
                File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "tacu");
                startActivityForResult(BGAPhotoPickerActivity.newIntent(EditPersonalDataActivity.this, mTakePhotoEnabled ? takePhotoDir : null, 1, null, mTakePhotoEnabled), TAKE_PIC);
            }

            @Override
            public void onPermissionFailed() {
            }
        }, Permission.Group.STORAGE, Permission.Group.CAMERA);
    }

    @OnClick(R.id.tv_nickname)
    void nickNameClick() {
        showNickNameDialog(spUtil.getNickName());
    }

    @OnClick(R.id.tv_binding_tel)
    void bindingTelClick() {
        jumpTo(BindModeActivity.createActivity(this, 3));
    }

    @OnClick(R.id.tv_binding_email)
    void bindingEmailClick() {
        jumpTo(BindModeActivity.createActivity(this, 4));
    }

    @OnClick(R.id.tv_apply_kyc)
    void applyKycClick() {
        jumpTo(AuthActivity.class);
    }

    @OnClick(R.id.tv_apply_buniness)
    void applyBusinessClick() {
        jumpTo(AuthMerchantActivity.class);
    }

    @OnClick(R.id.tv_binding_payinfo)
    void bindingPayInfoClick() {
        if (spUtil.getIsAuthSenior() == -1 || spUtil.getIsAuthSenior() == 0 || spUtil.getIsAuthSenior() == 1) {
            showToastError(getResources().getString(R.string.please_get_the_level_of_KYC));
        } else {
            jumpTo(PayInfoListActivity.class);
        }
    }

    @Override
    public void updateUserInfoSuccess() {
        showToastSuccess(getResources().getString(R.string.setting_success));
        mPresenter.ownCenter();
    }

    @Override
    public void ownCenter(OwnCenterModel model) {
        UserManageUtils.setPersonInfo(model);
        setValue();
    }

    @Override
    public void selectBank(List<PayInfoModel> list) {
        UserManageUtils.setPeoplePayInfo(list);
        setValue();
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
            mOss = new OSSClient(getApplicationContext(), Constant.OSS_HEAD_ENDPOINT, credentialProvider);
            bucketName = Constant.OSS_HEAD_BUCKET;

            uploadImgs(fileLocalNameAddress);
        }
    }

    private void setValue() {
        if (!TextUtils.isEmpty(spUtil.getHeadImg())) {
            GlideUtils.disPlay(this, CommonUtils.getHead(spUtil.getHeadImg()), img_head_sculpture);
        } else {
            img_head_sculpture.setImageResource(R.mipmap.img_maindrawer_unlogin);
        }
        tv_account.setText(spUtil.getAccount());
        tv_uid.setText(String.valueOf(spUtil.getUserUid()));
        tv_nickname.setText(spUtil.getNickName());

        if (!spUtil.getPhoneStatus()) {
            tv_binding_tel.setText(getResources().getString(R.string.plesase_binding_telephone));
            tv_binding_tel.setTextColor(ContextCompat.getColorStateList(this, R.color.text_grey));
            tv_binding_tel.setClickable(true);
        } else {
            tv_binding_tel.setText(getResources().getString(R.string.binding_success));
            tv_binding_tel.setTextColor(ContextCompat.getColorStateList(this, R.color.text_color));
            tv_binding_tel.setClickable(false);
        }
        if (TextUtils.isEmpty(spUtil.getEmail())) {
            tv_binding_email.setText(getResources().getString(R.string.plesase_binding_email));
            tv_binding_email.setTextColor(ContextCompat.getColorStateList(this, R.color.text_grey));
            tv_binding_email.setClickable(true);
        } else {
            tv_binding_email.setText(getResources().getString(R.string.binding_success));
            tv_binding_email.setTextColor(ContextCompat.getColorStateList(this, R.color.text_color));
            tv_binding_email.setClickable(false);
        }
        if (spUtil.getIsAuthSenior() == 2) {
            tv_apply_kyc.setText("KYC2");
        } else if (spUtil.getIsAuthSenior() < 2) {
            tv_apply_kyc.setText("KYC1");
        }

        if (spUtil.getApplyMerchantStatus() != 2 && spUtil.getApplyAuthMerchantStatus() != 2) {
            tv_apply_buniness.setTextColor(ContextCompat.getColorStateList(this, R.color.text_grey));
            tv_apply_buniness.setText(getResources().getString(R.string.apply_business_not));
        } else {
            tv_apply_buniness.setTextColor(ContextCompat.getColorStateList(this, R.color.text_color));
            String value = "";
            if (spUtil.getApplyAuthMerchantStatus() == 2) {
                value = getResources().getString(R.string.certified_shoper);
            } else if (spUtil.getApplyMerchantStatus() == 2) {
                value = getResources().getString(R.string.ordinary_merchant);
            }
            tv_apply_buniness.setText(value);
        }

        if (spUtil.getIsPayInfo()) {
            tv_binding_payinfo.setText(getResources().getString(R.string.binding_success));
            tv_binding_payinfo.setTextColor(ContextCompat.getColorStateList(this, R.color.text_color));
        } else {
            tv_binding_payinfo.setText(getResources().getString(R.string.please_binding_pay_info));
            tv_binding_payinfo.setTextColor(ContextCompat.getColorStateList(this, R.color.text_grey));
        }
    }

    private void uploadImgs(String fileLocalNameAddress) {
        if (!TextUtils.isEmpty(bucketName) && mOss != null) {
            // 构造上传请求
            imageName = CommonUtils.getHeadImageName();
            PutObjectRequest put = new PutObjectRequest(bucketName, Constant.OSS_HEAD_DIR + imageName, fileLocalNameAddress);
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

    private void showNickNameDialog(String nickname) {
        final View view = View.inflate(this, R.layout.view_nickname, null);
        final EditText edit_nickname = view.findViewById(R.id.edit_nickname);

        if (!TextUtils.isEmpty(nickname)) {
            edit_nickname.setText(nickname);
        }

        droidDialog = new DroidDialog.Builder(this)
                .title(this.getResources().getString(R.string.please_set_nickname))
                .viewCustomLayout(view)
                .positiveButton(getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        String nickString = edit_nickname.getText().toString();
                        if (TextUtils.isEmpty(nickString)) {
                            showToastError(getResources().getString(R.string.please_set_nickname));
                            return;
                        }
                        mPresenter.updateUserInfo(edit_nickname.getText().toString(), null);
                    }
                })
                .negativeButton(getResources().getString(R.string.cancel), new DroidDialog.onNegativeListener() {
                    @Override
                    public void onNegative(Dialog droidDialog) {
                    }
                })
                .cancelable(false, false)
                .show();
    }

    private void dealValue(int flag) {
        if (flag == 1) {
            mHandler.sendEmptyMessage(0);
            mPresenter.updateUserInfo(null, imageName);
        } else {
            mHandler.sendEmptyMessage(1);
        }
    }
}
