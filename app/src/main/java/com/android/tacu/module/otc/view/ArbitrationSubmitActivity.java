package com.android.tacu.module.otc.view;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
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
import com.android.tacu.EventBus.model.OtcDetailNotifyEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.base.MyApplication;
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.otc.contract.OtcOrderDetailContract;
import com.android.tacu.module.otc.presenter.OtcOrderDetailPresenter;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.permission.PermissionUtils;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundLinearLayout;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;

public class ArbitrationSubmitActivity extends BaseActivity<OtcOrderDetailPresenter> implements OtcOrderDetailContract.IAView {

    private static final int TAKE_PIC = 1001;

    @BindView(R.id.tv_tip)
    TextView tv_tip;
    @BindView(R.id.tv_content1)
    TextView tv_content1;
    @BindView(R.id.edit_submit_arbitration)
    QMUIRoundEditText edit_submit_arbitration;
    @BindView(R.id.tv_content2)
    TextView tv_content2;

    @BindView(R.id.lin_upload)
    QMUIRoundLinearLayout lin_upload;
    @BindView(R.id.img_add)
    ImageView img_add;
    @BindView(R.id.img_url)
    ImageView img_url;

    @BindView(R.id.btn_coined)
    QMUIRoundButton btn_coined;

    //true=仲裁 false=申诉
    private boolean isArbitration = true;
    private String id;

    private File uploadFile;
    private String uploadImageName;
    private List<OSSAsyncTask> ossAsynTaskList = new ArrayList<>();

    public static Intent createActivity(Context context, boolean isArbitration, String id) {
        Intent intent = new Intent(context, ArbitrationSubmitActivity.class);
        intent.putExtra("isArbitration", isArbitration);
        intent.putExtra("id", id);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_arbitration_submit);
    }

    @Override
    protected void initView() {
        isArbitration = getIntent().getBooleanExtra("isArbitration", true);
        id = getIntent().getStringExtra("id");

        if (isArbitration) {
            mTopBar.setTitle(getResources().getString(R.string.submit_arbitration));
            tv_tip.setText(getResources().getString(R.string.arbitration_tip));
            tv_content1.setText(getResources().getString(R.string.arbitration_content));
            edit_submit_arbitration.setHint(getResources().getString(R.string.please_submit_arbitration_content));
            tv_content2.setText(getResources().getString(R.string.upload_arbitration_img));
            btn_coined.setText(getResources().getString(R.string.submit_arbitration));
        } else {
            mTopBar.setTitle(getResources().getString(R.string.submit_appeal));
            tv_tip.setText(getResources().getString(R.string.bearbitration_tip));
            tv_content1.setText(getResources().getString(R.string.bearbitration_content));
            edit_submit_arbitration.setHint(getResources().getString(R.string.please_submit_bearbitration_content));
            tv_content2.setText(getResources().getString(R.string.upload_bearbitration_img));
            btn_coined.setText(getResources().getString(R.string.submit_appeal));
        }
    }

    @Override
    protected OtcOrderDetailPresenter createPresenter(OtcOrderDetailPresenter mPresenter) {
        return new OtcOrderDetailPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ossAsynTaskList != null && ossAsynTaskList.size() > 0) {
            for (OSSAsyncTask ossAsyncTask : ossAsynTaskList) {
                ossAsyncTask.cancel();
            }
        }
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
                uploadFile = fileOrgin;
                img_add.setVisibility(View.GONE);
                img_url.setVisibility(View.VISIBLE);
                GlideUtils.disPlay(this, "file://" + fileOrgin.getPath(), img_url);
            }
        }
    }

    @OnClick(R.id.btn_coined)
    void coinedClick() {
        String content = edit_submit_arbitration.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            if (isArbitration) {
                showToastError(getResources().getString(R.string.please_submit_arbitration_content));
            } else {
                showToastError(getResources().getString(R.string.please_submit_bearbitration_content));
            }
            return;
        }
        if (uploadFile == null) {
            if (isArbitration) {
                showToastError(getResources().getString(R.string.upload_arbitration_img));
            } else {
                showToastError(getResources().getString(R.string.upload_bearbitration_img));
            }
            return;
        }

        showLoadingView();
        mPresenter.getOssSetting();
    }

    @OnClick(R.id.lin_upload)
    void uploadClick() {
        PermissionUtils.requestPermissions(this, new OnPermissionListener() {
            @Override
            public void onPermissionSucceed() {
                boolean mTakePhotoEnabled = true;
                // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
                File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "tacu");
                startActivityForResult(BGAPhotoPickerActivity.newIntent(ArbitrationSubmitActivity.this, mTakePhotoEnabled ? takePhotoDir : null, 1, null, mTakePhotoEnabled), TAKE_PIC);
            }

            @Override
            public void onPermissionFailed() {
            }
        }, Permission.Group.STORAGE, Permission.Group.CAMERA);
    }

    @Override
    public void getOssSetting(AuthOssModel authOssModel) {
        if (authOssModel != null) {
            OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(authOssModel.AccessKeyId, authOssModel.AccessKeySecret, authOssModel.SecurityToken);
            ClientConfiguration conf = new ClientConfiguration();
            conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
            conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
            conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
            conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
            OSSLog.enableLog();

            OSS mOss = new OSSClient(MyApplication.getInstance(), Constant.OSS_ENDPOINT, credentialProvider);
            String bucketName = authOssModel.bucket;

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

    @Override
    public void arbitrationOrderSuccess() {
        showToastSuccess(getResources().getString(R.string.submit_success));
        finish();
        EventManage.sendEvent(new BaseEvent<>(EventConstant.OTCDetailCode, new OtcDetailNotifyEvent(true)));
    }

    @Override
    public void beArbitrationOrderSuccess() {
        showToastSuccess(getResources().getString(R.string.submit_success));
        finish();
        EventManage.sendEvent(new BaseEvent<>(EventConstant.OTCDetailCode, new OtcDetailNotifyEvent(true)));
    }

    private void dealValue(final int flag) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (flag == 1) {
                    String arbitrateExp = edit_submit_arbitration.getText().toString();
                   /* if (isArbitration){
                        mPresenter.arbitrationOrder(id, arbitrateExp, uploadImageName);
                    }else{
                        mPresenter.beArbitrationOrder(id, arbitrateExp, uploadImageName);
                    }*/
                }
                hideLoadingView();
            }
        });
    }
}
