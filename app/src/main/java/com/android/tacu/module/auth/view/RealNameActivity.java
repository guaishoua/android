package com.android.tacu.module.auth.view;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.tacu.BuildConfig;
import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.auth.contract.RealNameContract;
import com.android.tacu.module.auth.model.UserInfoModel;
import com.android.tacu.module.auth.presenter.RealNamePresenter;
import com.android.tacu.module.login.view.CityListActivity;
import com.android.tacu.utils.IdentityAuthUtils;
import com.android.tacu.utils.ShowToast;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundLinearLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundRelativeLayout;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xiaohong on 2018/8/24.
 * <p>
 * 实名认证第一步
 */

public class RealNameActivity extends BaseActivity<RealNamePresenter> implements RealNameContract.IRealView, View.OnClickListener {

    //中国大陆
    @BindView(R.id.view_email)
    View view_email;
    @BindView(R.id.view_phone)
    View view_phone;
    @BindView(R.id.tv_mode_email)
    TextView tv_mode_email;
    @BindView(R.id.tv_mode_phone)
    TextView tv_mode_phone;
    @BindView(R.id.tv_id)
    TextView tv_id;
    @BindView(R.id.tv_id_number)
    TextView tv_id_number;
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
    @BindView(R.id.ll_china)
    QMUIRoundLinearLayout ll_china;

    //其他国家
    @BindView(R.id.tv_id_other)
    TextView tv_id_other;
    @BindView(R.id.tv_id_number_other)
    TextView tv_id_number_other;
    @BindView(R.id.tv_other_hint)
    TextView tv_other_hint;
    @BindView(R.id.tv_country_other)
    TextView tv_country_other;
    //姓氏
    @BindView(R.id.et_surname_other)
    EditText et_surname_other;
    //名字
    @BindView(R.id.et_name_other)
    EditText et_name_other;
    //性別
    @BindView(R.id.rbtn_gril_other)
    RadioButton rbtn_gril_other;
    @BindView(R.id.rbtn_boy_other)
    RadioButton rbtn_boy_other;
    //出生日期
    @BindView(R.id.tv_birthday_other)
    TextView tv_birthday_other;
    //證件號碼
    @BindView(R.id.et_id_number_other)
    EditText et_id_number_other;
    //證件開始時間
    @BindView(R.id.tv_id_start_time_other)
    TextView tv_id_start_time_other;
    //證件結束時間
    @BindView(R.id.tv_id_end_time_other)
    TextView tv_id_end_time_other;
    @BindView(R.id.cb_time_other)
    CheckBox cb_time_other;
    @BindView(R.id.ll_other_country)
    QMUIRoundLinearLayout ll_other_country;
    @BindView(R.id.rl_area)
    QMUIRoundRelativeLayout rl_area;
    @BindView(R.id.tv_current_area)
    TextView tvCurrentArea;

    private int request = 1000;
    //1:中国大陆   0：其他国家地区
    private String isChina = "1";
    //点击"国家/地区"进入此页面不进行网络请求
    private boolean isCity = true;
    private UserInfoModel userInfoModel;

    private int currentLocation = -1;

    private QMUIBottomSheet mBottomSheet;

    @Override
    protected RealNamePresenter createPresenter(RealNamePresenter mPresenter) {
        return new RealNamePresenter();
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_real);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.identity_authentication));

        tv_mode_email.setText(getResources().getString(R.string.china));
        tv_mode_phone.setText(getResources().getString(R.string.other_country));

        setTextViewShow(View.VISIBLE, View.GONE);

        //中国大陆
        tv_birthday.setOnClickListener(this);
        tv_id_start_time.setOnClickListener(this);
        tv_id_end_time.setOnClickListener(this);

        //其他国家地区
        tv_birthday_other.setOnClickListener(this);
        tv_id_start_time_other.setOnClickListener(this);
        tv_id_end_time_other.setOnClickListener(this);

        rl_area.setOnClickListener(this);

        et_id_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String idNumber = et_id_number.getText().toString().trim();
                    if (!TextUtils.isEmpty(idNumber)) {
                        isChange(idNumber);
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cb_time.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tv_id_end_time.setEnabled(!isChecked);
            }
        });
        cb_time_other.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tv_id_end_time_other.setEnabled(!isChecked);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCity) {
            mPresenter.authinfoNew();
        }
    }

    /**
     * 中国大陆地区
     */
    @OnClick(R.id.tv_mode_email)
    void china() {
        isChina = "1";
        setTextViewShow(View.VISIBLE, View.GONE);
    }

    /**
     * 其他国家地区
     */
    @OnClick(R.id.tv_mode_phone)
    void other() {
        isChina = "0";
        setTextViewShow(View.GONE, View.VISIBLE);
    }

    /**
     * 国家/地区
     */
    @OnClick(R.id.ll_country_other)
    void setCountry() {
        jumpTo(CityListActivity.createActivty(this, true), request);
    }

    /**
     * 下一步
     */
    @OnClick(R.id.btn_next)
    void next() {
        if (BuildConfig.DEBUG) {
            jumpTo(RealNameTwoActivity.crestActivity(RealNameActivity.this, userInfoModel, 1, isChina, currentLocation));
            return;
        }

        String surname;
        String country = null;
        String name;
        String birthday;
        String idNumber;
        String startTime;
        String endTime;
        String genders = null;
        int isAllTime;

        if (TextUtils.equals(isChina, "1")) {
            surname = et_surname.getText().toString().trim();
            name = et_name.getText().toString().trim();
            birthday = tv_birthday.getText().toString().trim();
            idNumber = et_id_number.getText().toString().trim();
            startTime = tv_id_start_time.getText().toString().trim();
            endTime = tv_id_end_time.getText().toString().trim();
        } else {
            surname = et_surname_other.getText().toString().trim();
            country = tv_country_other.getText().toString().trim();
            name = et_name_other.getText().toString().trim();
            birthday = tv_birthday_other.getText().toString().trim();
            idNumber = et_id_number_other.getText().toString().trim();
            startTime = tv_id_start_time_other.getText().toString().trim();
            endTime = tv_id_end_time_other.getText().toString().trim();
        }
        if (TextUtils.equals(tvCurrentArea.getText().toString(), getResources().getString(R.string.current_area_one))) {
            currentLocation = 1;
        } else if (TextUtils.equals(tvCurrentArea.getText().toString(), getResources().getString(R.string.current_area_two))) {
            currentLocation = 0;
        }

        if (TextUtils.isEmpty(surname)) {
            ShowToast.error(getString(R.string.surname));
            return;
        }
        if (TextUtils.isEmpty(name)) {
            ShowToast.error(getString(R.string.name));
            return;
        }

        if (TextUtils.isEmpty(idNumber)) {
            ShowToast.error(getString(R.string.idNumber));
            return;
        }

        if (TextUtils.equals(isChina, "1") && userInfoModel == null && idNumber.length() < 17) {
            ShowToast.error(getString(R.string.idNumber));
            return;
        }

        if (TextUtils.equals(isChina, "1")) {
            if (cb_time.isChecked()) {
                isAllTime = 1;
            } else {
                isAllTime = 0;
            }
            if (rbtn_gril.isChecked()) {
                genders = "2";
            } else if (rbtn_boy.isChecked()) {
                genders = "1";
            }
        } else {
            if (TextUtils.isEmpty(birthday)) {
                ShowToast.error(getString(R.string.birthday));
                return;
            }

            if (rbtn_gril_other.isChecked()) {
                genders = "2";
            } else if (rbtn_boy_other.isChecked()) {
                genders = "1";
            }

            if (cb_time_other.isChecked()) {
                isAllTime = 1;
            } else {
                isAllTime = 0;
            }

            if (TextUtils.isEmpty(country)) {
                ShowToast.error(getString(R.string.country));
                return;
            }
        }

        if (TextUtils.isEmpty(startTime)) {
            ShowToast.error(getString(R.string.startTime));
            return;
        }
        if (TextUtils.equals(isChina, "1") && !cb_time.isChecked() && TextUtils.isEmpty(endTime)) {
            ShowToast.error(getString(R.string.endTime));
            return;
        }
        if (TextUtils.equals(isChina, "0") && !cb_time_other.isChecked() && TextUtils.isEmpty(endTime)) {
            ShowToast.error(getString(R.string.endTime));
            return;
        }
        if (cb_time.isChecked() || cb_time_other.isChecked()) {
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
            userInfoModel.isChina = isChina;
        }

        if (currentLocation == -1) {
            ShowToast.error(getString(R.string.current_area));
            return;
        }

        mPresenter.authNew(country, surname, name, idNumber, birthday, genders, isChina, 1, startTime, endTime, isAllTime, currentLocation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == request) {
            isCity = false;
            String country = data.getStringExtra("city");
            tv_country_other.setText(country);
        }
    }

    private void setTextViewShow(int china, int other) {
        view_email.setVisibility(china);
        view_phone.setVisibility(other);
        ll_china.setVisibility(china);
        tv_other_hint.setVisibility(other);
        ll_other_country.setVisibility(other);

        if (china == View.VISIBLE) {
            if (userInfoModel == null || userInfoModel.currentLocation == null) {
                currentLocation = 1;
                tvCurrentArea.setText(getResources().getString(R.string.current_area_one));
            }
            tv_mode_email.setTextColor(ContextCompat.getColor(this, R.color.color_default));
            tv_mode_phone.setTextColor(ContextCompat.getColor(this, R.color.text_grey));
        } else {
            if (userInfoModel == null || userInfoModel.currentLocation == null) {
                currentLocation = 0;
                tvCurrentArea.setText(getResources().getString(R.string.current_area_two));
            }
            tv_mode_email.setTextColor(ContextCompat.getColor(this, R.color.text_grey));
            tv_mode_phone.setTextColor(ContextCompat.getColor(this, R.color.color_default));
        }
    }

    @Override
    public void authinfoNew(UserInfoModel userInfoModel) {
        if (userInfoModel != null) {
            this.userInfoModel = userInfoModel;
            if (userInfoModel.isChina(userInfoModel.isChina)) {
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
                if (!TextUtils.isEmpty(userInfoModel.idNumber)) {
                    isChange(userInfoModel.idNumber);
                }
                if (userInfoModel.isAllTime == 0) {
                    cb_time.setChecked(false);
                } else {
                    cb_time.setChecked(true);
                }
            } else {
                et_surname_other.setText(userInfoModel.firstName);
                et_name_other.setText(userInfoModel.secondName);
                et_id_number_other.setText(userInfoModel.idNumber);
                tv_birthday_other.setText(userInfoModel.birthday);//
                if (TextUtils.equals(userInfoModel.gender, "1")) {
                    rbtn_boy_other.setChecked(true);
                } else if (TextUtils.equals(userInfoModel.gender, "2")) {
                    rbtn_gril_other.setChecked(true);
                }
                if (!TextUtils.isEmpty(userInfoModel.singnTime)) {
                    tv_id_start_time_other.setText(userInfoModel.singnTime.substring(0, 10));
                }
                if (!TextUtils.isEmpty(userInfoModel.outofTime) && userInfoModel.isAllTime == 0) {
                    tv_id_end_time_other.setText(userInfoModel.outofTime.substring(0, 10));
                }
                tv_country_other.setText(userInfoModel.country);
                if (userInfoModel.isAllTime == 0) {
                    cb_time_other.setChecked(false);
                } else {
                    cb_time_other.setChecked(true);
                }
            }
            if (userInfoModel.currentLocation != null) {
                //当前所在地是否中国大陆 0.不是 1.是
                if (userInfoModel.currentLocation == 0) {
                    tvCurrentArea.setText(getResources().getString(R.string.current_area_two));
                } else if (userInfoModel.currentLocation == 1) {
                    tvCurrentArea.setText(getResources().getString(R.string.current_area_one));
                }
            }
        }
    }

    private void isChange(String idNumber) {
        if (idNumber.length() > 14) {
            StringBuffer stringBuffer = new StringBuffer(idNumber.substring(6, 14));
            stringBuffer.insert(4, "-");
            stringBuffer.insert(7, "-");
            tv_birthday.setText(stringBuffer.toString());
            if (idNumber.length() > 17) {
                int gender = Integer.parseInt(idNumber.substring(16, 17));
                if (gender % 2 == 0) {
                    rbtn_gril.setChecked(true);
                    rbtn_boy.setChecked(false);
                } else {
                    rbtn_gril.setChecked(false);
                    rbtn_boy.setChecked(true);
                }
            }
        }
    }

    @Override
    public void authNewSuccess() {
        jumpTo(RealNameTwoActivity.crestActivity(RealNameActivity.this, userInfoModel, 1, isChina, currentLocation));
    }

    @Override
    public void onClick(View v) {
        IdentityAuthUtils.closeKeyBoard(this);
        switch (v.getId()) {
            case R.id.tv_birthday:
                IdentityAuthUtils.setBirthday(this, tv_birthday, 200, 200);
                break;
            case R.id.tv_birthday_other:
                IdentityAuthUtils.setBirthday(this, tv_birthday_other, 200, 200);
                break;
            case R.id.tv_id_end_time:
                IdentityAuthUtils.setBirthday(this, tv_id_end_time, 100, 100);
                break;
            case R.id.tv_id_end_time_other:
                IdentityAuthUtils.setBirthday(this, tv_id_end_time_other, 100, 100);
                break;
            case R.id.tv_id_start_time:
                IdentityAuthUtils.setBirthday(this, tv_id_start_time, 100, 100);
                break;
            case R.id.tv_id_start_time_other:
                IdentityAuthUtils.setBirthday(this, tv_id_start_time_other, 100, 100);
                break;
            case R.id.rl_area:
                if (mBottomSheet == null) {
                    mBottomSheet = new QMUIBottomSheet.BottomListSheetBuilder(this)
                            .addItem(getResources().getString(R.string.current_area_two))
                            .addItem(getResources().getString(R.string.current_area_one))
                            .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                                @Override
                                public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                                    //当前所在地是否中国大陆 0.不是 1.是
                                    if (position == 0) {
                                        tvCurrentArea.setText(getResources().getString(R.string.current_area_two));
                                    } else if (position == 1) {
                                        tvCurrentArea.setText(getResources().getString(R.string.current_area_one));
                                    }
                                    dialog.dismiss();
                                }
                            })
                            .build();
                }
                mBottomSheet.show();
                break;
        }
    }
}
