package com.android.tacu.module.assets.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.module.assets.contract.BindingPayInfoContract;
import com.android.tacu.module.assets.presenter.BindingPayInfoPresenter;
import com.android.tacu.utils.permission.PermissionUtils;
import com.yanzhenjie.permission.Permission;

import butterknife.BindView;
import butterknife.OnClick;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class BindingInfoYhkFragment extends BaseFragment<BindingPayInfoPresenter> implements BindingPayInfoContract.IView {

    @BindView(R.id.edit_bank_id)
    EditText edit_bank_id;

    private final int MY_SCAN_REQUEST_CODE = 1001;

    public static BindingInfoYhkFragment newInstance() {
        Bundle bundle = new Bundle();
        BindingInfoYhkFragment fragment = new BindingInfoYhkFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_yhk;
    }

    @Override
    protected void initData(View view) {
    }

    @OnClick(R.id.img_saomiao)
    void saomiaoClick() {
        PermissionUtils.requestPermissions(getContext(), new OnPermissionListener() {
            @Override
            public void onPermissionSucceed() {
                Intent scanIntent = new Intent(getContext(), CardIOActivity.class);
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, false)
                        .putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false)
                        .putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true)//去除水印
                        .putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true)//去除键盘
                        .putExtra(CardIOActivity.EXTRA_LANGUAGE_OR_LOCALE, "zh-Hans");//设置提示为中文
                startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
            }

            @Override
            public void onPermissionFailed() {
            }
        }, Permission.Group.CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_SCAN_REQUEST_CODE) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                edit_bank_id.setText(scanResult.cardNumber);
            }
        }
    }
}
