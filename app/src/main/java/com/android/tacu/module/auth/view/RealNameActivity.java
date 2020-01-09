package com.android.tacu.module.auth.view;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.auth.contract.RealNameContract;
import com.android.tacu.module.auth.model.UserInfoModel;
import com.android.tacu.module.auth.presenter.RealNamePresenter;
import com.android.tacu.utils.IdentityAuthUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xiaohong on 2018/8/24.
 * <p>
 * 实名认证第一步
 */

public class RealNameActivity extends BaseActivity<RealNamePresenter> implements RealNameContract.IRealView, View.OnClickListener {

    //姓氏
    @BindView(R.id.et_surname)
    EditText et_surname;
    //名字
    @BindView(R.id.et_name)
    EditText et_name;
    //性別
    @BindView(R.id.rbtn_gril)
    CheckBox rbtn_gril;
    @BindView(R.id.rbtn_boy)
    CheckBox rbtn_boy;
    //出生日期
    @BindView(R.id.tv_birthday)
    TextView tv_birthday;
    //证件类型
    @BindView(R.id.tv_passport_type)
    TextView tv_passport_type;
    //證件號碼
    @BindView(R.id.et_id_number)
    EditText et_id_number;
    //證件開始時間
    @BindView(R.id.tv_id_start_time)
    TextView tv_id_start_time;
    //證件結束時間
    @BindView(R.id.tv_id_end_time)
    TextView tv_id_end_time;
    @BindView(R.id.cb_time)
    CheckBox cb_time;

    private UserInfoModel userInfoModel;
    private QMUIBottomSheet mBottomSheet;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_real);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.identity_authentication));

        tv_passport_type.setOnClickListener(this);
        tv_birthday.setOnClickListener(this);
        tv_id_start_time.setOnClickListener(this);
        tv_id_end_time.setOnClickListener(this);

        cb_time.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tv_id_end_time.setEnabled(!isChecked);
            }
        });
    }

    @Override
    protected RealNamePresenter createPresenter(RealNamePresenter mPresenter) {
        return new RealNamePresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.authinfoNew();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBottomSheet != null) {
            mBottomSheet.dismiss();
        }
    }

    /**
     * 下一步
     */
    @OnClick(R.id.btn_next)
    void next() {
        String surname;
        String country = null;
        String name;
        String birthday;
        String idType;
        String idNumber;
        String startTime;
        String endTime;
        String genders;
        int isAllTime;

        surname = et_surname.getText().toString().trim();
        name = et_name.getText().toString().trim();
        idNumber = et_id_number.getText().toString().trim();
        birthday = tv_birthday.getText().toString().trim();
        startTime = tv_id_start_time.getText().toString().trim();
        endTime = tv_id_end_time.getText().toString().trim();

        if (TextUtils.isEmpty(surname)) {
            showToastError(getString(R.string.surname));
            return;
        }
        if (TextUtils.isEmpty(name)) {
            showToastError(getString(R.string.name));
            return;
        }

        if (TextUtils.isEmpty(idNumber)) {
            showToastError(getString(R.string.idNumber));
            return;
        }

        if (TextUtils.isEmpty(birthday)) {
            showToastError(getString(R.string.birthday));
            return;
        }

        if (rbtn_gril.isChecked()) {
            genders = "2";
        } else if (rbtn_boy.isChecked()) {
            genders = "1";
        } else {
            showToastError(getString(R.string.gender));
            return;
        }

        if (cb_time.isChecked()) {
            isAllTime = 1;
        } else {
            isAllTime = 0;
        }

        if (TextUtils.isEmpty(startTime)) {
            showToastError(getString(R.string.startTime));
            return;
        }
        if (!cb_time.isChecked() && TextUtils.isEmpty(endTime)) {
            showToastError(getString(R.string.endTime));
            return;
        }

        if (cb_time.isChecked()) {
            endTime = null;
        }
        if (userInfoModel != null) {
            //判断是否修改身份证号，出生日期，如果没有修改将不传该字段（因后台返回的身份证是加密之后的）
            if (TextUtils.equals(userInfoModel.idNumber, idNumber)) {
                idNumber = null;
            }
            if (TextUtils.equals(userInfoModel.birthday, birthday)) {
                birthday = null;
            }
        } else {
            userInfoModel = new UserInfoModel();
        }

        // 添加证件类型
        mPresenter.authNew(country, surname, name, idNumber, birthday, genders, isChina, 1, startTime, endTime, isAllTime, currentLocation);
    }

    @Override
    public void authinfoNew(UserInfoModel userInfoModel) {
        if (userInfoModel != null) {
            this.userInfoModel = userInfoModel;
            et_surname.setText(userInfoModel.firstName);
            et_name.setText(userInfoModel.secondName);
            et_id_number.setText(userInfoModel.idNumber);
            tv_birthday.setText(userInfoModel.birthday);//
            if (TextUtils.equals(userInfoModel.gender, "1")) {
                rbtn_boy.setChecked(true);
            } else if (TextUtils.equals(userInfoModel.gender, "2")) {
                rbtn_gril.setChecked(true);
            }
            if (!TextUtils.isEmpty(userInfoModel.singnTime)) {
                tv_id_start_time.setText(userInfoModel.singnTime.substring(0, 10));
            }
            if (!TextUtils.isEmpty(userInfoModel.outofTime) && userInfoModel.isAllTime == 0) {
                tv_id_end_time.setText(userInfoModel.outofTime.substring(0, 10));
            }
            if (userInfoModel.isAllTime == 0) {
                cb_time.setChecked(false);
            } else {
                cb_time.setChecked(true);
            }
        }
    }

    @Override
    public void authNewSuccess() {
        jumpTo(RealNameTwoActivity.crestActivity(RealNameActivity.this, userInfoModel, 1));
    }

    @Override
    public void onClick(View v) {
        IdentityAuthUtils.closeKeyBoard(this);
        switch (v.getId()) {
            case R.id.tv_passport_type:
                if (mBottomSheet == null) {
                    mBottomSheet = new QMUIBottomSheet.BottomListSheetBuilder(this)
                            .addItem()
                            .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                                @Override
                                public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                                    if (position == 0) {

                                    } else if (position == 1) {

                                    } else if (position == 2) {

                                    }
                                    dialog.dismiss();
                                }
                            })
                            .build();
                }
                mBottomSheet.show();
                break;
            case R.id.tv_birthday:
                IdentityAuthUtils.setBirthday(this, tv_birthday, 200, 200);
                break;
            case R.id.tv_id_start_time:
                IdentityAuthUtils.setBirthday(this, tv_id_start_time, 100, 100);
                break;
            case R.id.tv_id_end_time:
                IdentityAuthUtils.setBirthday(this, tv_id_end_time, 100, 100);
                break;
        }
    }
}
