package com.android.tacu.module.my.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
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
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.assets.view.BindingPayInfoActivity;
import com.android.tacu.module.auth.view.AuthActivity;
import com.android.tacu.module.auth.view.AuthMerchantActivity;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.my.contract.EditPersonalDataContact;
import com.android.tacu.module.my.presenter.EditPersonalDataPresenter;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.permission.PermissionUtils;
import com.android.tacu.utils.user.UserManageUtils;
import com.android.tacu.view.GlideLoader;
import com.lcw.library.imagepicker.ImagePicker;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class EditPersonalDataActivity extends BaseActivity<EditPersonalDataPresenter> implements EditPersonalDataContact.IView {

    @BindView(R.id.img_head_sculpture)
    QMUIRadiusImageView img_head_sculpture;
    @BindView(R.id.tv_account)
    TextView tv_account;
    @BindView(R.id.tv_uid)
    TextView tv_uid;
    @BindView(R.id.edit_nickname)
    EditText edit_nickname;
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

    private static final int REQUEST_SELECT_IMAGES_CODE = 1001;
    private ArrayList<String> mImagePaths;

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

        edit_nickname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mPresenter.updateUserInfo(edit_nickname.getText().toString(), null);
                    return true;
                }
                return false;
            }
        });
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
        if (requestCode == REQUEST_SELECT_IMAGES_CODE && resultCode == RESULT_OK) {
            mImagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
            for (int i = 0; i < mImagePaths.size(); i++) {
                String imageUri = mImagePaths.get(i);
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
                                showLoadingView();
                                mPresenter.getOssSetting(file.getPath());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                            }
                        });
            }
        }
    }

    @OnClick({R.id.img_head_sculpture})
    void headClick() {
        PermissionUtils.requestPermissions(this, new OnPermissionListener() {
            @Override
            public void onPermissionSucceed() {
                ImagePicker.getInstance()
                        .setTitle("")//设置标题
                        .showCamera(true)//设置是否显示拍照按钮
                        .showImage(true)//设置是否展示图片
                        .showVideo(false)//设置是否展示视频
                        .setSingleType(true)//设置图片视频不能同时选择
                        .setMaxCount(1)//设置最大选择图片数目(默认为1，单选)
                        .setImagePaths(mImagePaths)//保存上一次选择图片的状态，如果不需要可以忽略
                        .setImageLoader(new GlideLoader())//设置自定义图片加载器
                        .start(EditPersonalDataActivity.this, REQUEST_SELECT_IMAGES_CODE);//REQEST_SELECT_IMAGES_CODE为Intent调用的requestCode
            }

            @Override
            public void onPermissionFailed() {
            }
        }, Permission.Group.STORAGE, Permission.Group.CAMERA);
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
            jumpTo(BindingPayInfoActivity.class);
        }
    }

    @Override
    public void updateUserInfoSuccess() {
        showToastSuccess(getResources().getString(R.string.setting_success));
        mPresenter.ownCenter();
    }

    @Override
    public void ownCenter(OwnCenterModel model) {
        UserManageUtils.setPersonInfo(model, null);
        setValue();
        if (model != null) {
            if (model.applyMerchantStatus != null) {
                tv_apply_buniness.setClickable(true);
                switch (model.applyMerchantStatus) {
                    case 0:
                        tv_apply_buniness.setText(getResources().getString(R.string.apply_business_not));
                        break;
                    case 1:
                        tv_apply_buniness.setText(getResources().getString(R.string.ordinary_merchant) + getResources().getString(R.string.to_be_examine));
                        tv_apply_buniness.setClickable(false);
                        break;
                    case 2:
                        if (model.applyAuthMerchantStatus != null) {
                            switch (model.applyAuthMerchantStatus) {
                                case 0:
                                    tv_apply_buniness.setText(getResources().getString(R.string.ordinary_merchant));
                                    break;
                                case 1:
                                    tv_apply_buniness.setText(getResources().getString(R.string.certified_shoper) + getResources().getString(R.string.to_be_examine));
                                    tv_apply_buniness.setClickable(false);
                                    break;
                                case 2:
                                    tv_apply_buniness.setText(getResources().getString(R.string.certified_shoper));
                                    tv_apply_buniness.setClickable(false);
                                    break;
                                case 3:
                                    tv_apply_buniness.setText(getResources().getString(R.string.certified_shoper) + getResources().getString(R.string.examine_failure));
                                    tv_apply_buniness.setClickable(true);
                                    break;
                            }
                        }
                        break;
                    case 3:
                        tv_apply_buniness.setText(getResources().getString(R.string.ordinary_merchant) + getResources().getString(R.string.examine_failure));
                        tv_apply_buniness.setClickable(true);
                        break;
                }
            }
        }
    }

    @Override
    public void selectBank(List<PayInfoModel> list) {
        UserManageUtils.setPersonInfo(null, list);
        if (list != null && list.size() > 0) {
            tv_binding_payinfo.setText(getResources().getString(R.string.binding_success));
        } else {
            tv_binding_payinfo.setText(getResources().getString(R.string.please_binding_pay_info));
        }
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
            mOss = new OSSClient(getApplicationContext(), Constant.OSS_ENDPOINT, credentialProvider);
            bucketName = model.bucket;

            uploadImgs(fileLocalNameAddress);
        }
    }

    private void setValue() {
        if (!TextUtils.isEmpty(spUtil.getHeadImg())) {
            GlideUtils.disPlay(this, Constant.UPLOAD_IMG_URL + spUtil.getHeadImg(), img_head_sculpture);
        }
        tv_account.setText(spUtil.getAccount());
        tv_uid.setText(String.valueOf(spUtil.getUserUid()));
        edit_nickname.setText(spUtil.getNickName());

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
            tv_apply_kyc.setClickable(false);
        } else if (spUtil.getIsAuthSenior() < 2) {
            tv_apply_kyc.setText("KYC1");
            tv_apply_kyc.setClickable(true);
        }
    }

    private void uploadImgs(String fileLocalNameAddress) {
        if (!TextUtils.isEmpty(bucketName) && mOss != null) {
            // 构造上传请求
            imageName = CommonUtils.getHeadImageName();
            PutObjectRequest put = new PutObjectRequest(bucketName, imageName, fileLocalNameAddress);
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
            mPresenter.updateUserInfo(null, imageName);
        } else {
            mHandler.sendEmptyMessage(1);
        }
    }
}
