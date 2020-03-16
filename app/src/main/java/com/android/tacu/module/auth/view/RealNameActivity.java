package com.android.tacu.module.auth.view;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.TabIndicatorAdapter;
import com.android.tacu.module.auth.contract.RealNameContract;
import com.android.tacu.module.auth.model.UserInfoModel;
import com.android.tacu.module.auth.presenter.RealNamePresenter;
import com.android.tacu.utils.IdentityAuthUtils;
import com.android.tacu.utils.LogUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xiaohong on 2018/8/24.
 * <p>
 * 实名认证第一步
 */

public class RealNameActivity extends BaseActivity<RealNamePresenter> implements RealNameContract.IRealView, View.OnClickListener {

    @BindView(R.id.indicator)
    ScrollIndicatorView indicator;
    //姓氏
    @BindView(R.id.et_realname)
    EditText et_realname;
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

    //1:中国大陆   0：其他国家地区
    private String isChina = "1";
    //1身份证  2 护照
    private Integer currentLocation = 0;
    private UserInfoModel userInfoModel;
    private QMUIBottomSheet mBottomSheet;

    private final int requestCodeValue = 1001;
    private List<String> tabTitle = new ArrayList<>();

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

        tabTitle.add(getResources().getString(R.string.china));
        tabTitle.add(getResources().getString(R.string.other_country));

        indicator.setBackgroundColor(ContextCompat.getColor(this, R.color.content_bg_color_grey));
        indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(this, R.color.text_default), ContextCompat.getColor(this, R.color.tab_text_color)).setSize(14, 14));
        indicator.setScrollBar(new TextWidthColorBar(this, indicator, ContextCompat.getColor(this, R.color.text_default), 4));
        indicator.setSplitAuto(true);
        indicator.setAdapter(new TabIndicatorAdapter(this, tabTitle));
        indicator.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                if (select == 0) {
                    isChina = "1";
                } else {
                    isChina = "0";
                }
                if (TextUtils.equals(isChina, "1") && currentLocation == 2) {
                    currentLocation = 1;
                }
                if (currentLocation != null && currentLocation == 2) {
                    tv_passport_type.setText(getResources().getString(R.string.huzhao));
                } else {
                    tv_passport_type.setText(getResources().getString(R.string.shenfenzheng));
                }
            }
        });

        cb_time.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tv_id_end_time.setText("");
                }
                tv_id_end_time.setEnabled(!isChecked);
            }
        });
    }

    @Override
    protected RealNamePresenter createPresenter(RealNamePresenter mPresenter) {
        return new RealNamePresenter();
    }

    @Override
    protected void onPresenterCreated(RealNamePresenter presenter) {
        super.onPresenterCreated(presenter);
        upLoad();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBottomSheet != null) {
            mBottomSheet.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCodeValue && resultCode == RESULT_OK) {
            upLoad();
        }
    }

    @OnClick(R.id.rbtn_gril)
    void rbtnGrilClick() {
        rbtn_gril.setChecked(true);
        rbtn_boy.setChecked(false);
    }

    @OnClick(R.id.rbtn_boy)
    void rbtnBoyClick() {
        rbtn_gril.setChecked(false);
        rbtn_boy.setChecked(true);
    }

    /**
     * 下一步
     */
    @OnClick(R.id.btn_next)
    void next() {
        String country = null;
        String allName;
        String birthday;
        String idNumber;
        String startTime;
        String endTime;
        String genders;
        int isAllTime;

        allName = et_realname.getText().toString();
        idNumber = et_id_number.getText().toString().trim();
        birthday = tv_birthday.getText().toString().trim();
        startTime = tv_id_start_time.getText().toString().trim();
        endTime = tv_id_end_time.getText().toString().trim();

        if (TextUtils.isEmpty(allName)) {
            showToastError(getString(R.string.realname));
            return;
        }

        if (currentLocation == 0) {
            showToastError(getResources().getString(R.string.passport_type));
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

       /* if (TextUtils.isEmpty(startTime)) {
            showToastError(getString(R.string.startTime));
            return;
        }
        if (!cb_time.isChecked() && TextUtils.isEmpty(endTime)) {
            showToastError(getString(R.string.endTime));
            return;
        }*/

       /* if (cb_time.isChecked()) {
            endTime = null;
        }*/
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

        String surname = "";
        String name = "";
        //1:中国大陆   0：其他国家地区
        if (TextUtils.equals(isChina, "1")) {
            surname = allName.substring(0, 1);
            name = allName.substring(1);
        } else if (TextUtils.equals(isChina, "0")) {
            try {
                String[] strings = allName.split(" ");
                if (strings != null && strings.length > 1) {
                    surname = strings[0] + " ";
                    for (int i = 0; i < strings.length; i++) {
                        if (i > 0) {
                            if (i == strings.length - 1) {
                                name += strings[i];
                            } else {
                                name += strings[i] + " ";
                            }
                        }
                    }
                } else {
                    surname = allName.substring(0, 1);
                    name = allName.substring(1);
                }
            } catch (Exception e) {
                surname = allName.substring(0, 1);
                name = allName.substring(1);
            }
        }
        LogUtils.i("jiazhen", "surname=" + surname + " name=" + name);

        if (TextUtils.equals(isChina, "1") && currentLocation == 2) {
            currentLocation = 1;
        }

        // 添加证件类型
        mPresenter.authNew(country, surname, name, idNumber, birthday, genders, isChina, 1, "", "", isAllTime, currentLocation);
    }

    @Override
    public void authinfoNew(UserInfoModel userInfoModel) {
        if (userInfoModel != null) {
            this.userInfoModel = userInfoModel;
            String firstString = "";
            String secondString = "";
            if (!TextUtils.isEmpty(userInfoModel.firstName)) {
                firstString = userInfoModel.firstName;
            }
            if (!TextUtils.isEmpty(userInfoModel.secondName)) {
                secondString = userInfoModel.secondName;
            }
            et_realname.setText(firstString + secondString);
            et_id_number.setText(userInfoModel.idNumber);
            tv_birthday.setText(userInfoModel.birthday);

            isChina = userInfoModel.isChina;
            if (TextUtils.isEmpty(isChina)) {
                isChina = "1";
            }
            if (TextUtils.equals(userInfoModel.isChina, "1")) {
                indicator.setCurrentItem(0);
            } else if (TextUtils.equals(userInfoModel.isChina, "0")) {
                indicator.setCurrentItem(1);
            }
            if (userInfoModel.currentLocation != null && userInfoModel.currentLocation == 2) {
                currentLocation = 2;
                tv_passport_type.setText(getResources().getString(R.string.huzhao));
            } else {
                currentLocation = 1;
                tv_passport_type.setText(getResources().getString(R.string.shenfenzheng));
            }
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
        jumpTo(RealNameTwoActivity.crestActivity(RealNameActivity.this, userInfoModel, 1, isChina), requestCodeValue);
    }

    @Override
    public void onClick(View v) {
        IdentityAuthUtils.closeKeyBoard(this);
        switch (v.getId()) {
            case R.id.tv_passport_type:
                QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(this)
                        .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                            @Override
                            public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                                if (position == 0) {
                                    currentLocation = 1;
                                    tv_passport_type.setText(getResources().getString(R.string.shenfenzheng));
                                } else if (position == 1) {
                                    currentLocation = 2;
                                    tv_passport_type.setText(getResources().getString(R.string.huzhao));
                                }
                                dialog.dismiss();
                            }
                        });
                //1:中国大陆   0：其他国家地区
                if (TextUtils.equals(isChina, "1")) {
                    builder.addItem(getResources().getString(R.string.shenfenzheng));
                } else if (TextUtils.equals(isChina, "0")) {
                    builder.addItem(getResources().getString(R.string.shenfenzheng))
                            .addItem(getResources().getString(R.string.huzhao));
                }
                mBottomSheet = builder.build();
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

    private void upLoad() {
        mPresenter.authinfoNew();
    }
}
