package com.android.tacu.module.otc.view;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.payinfo.model.PayInfoModel;
import com.android.tacu.module.otc.contract.OtcManageBuySellContract;
import com.android.tacu.module.otc.model.OtcPublishParam;
import com.android.tacu.module.otc.model.OtcSelectFeeModel;
import com.android.tacu.module.otc.presenter.OtcManageBuySellPresenter;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.MathHelper;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.widget.popupwindow.ListPopWindow;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class OtcManageBuySellFragment extends BaseFragment<OtcManageBuySellPresenter> implements OtcManageBuySellContract.IChildView {

    @BindView(R.id.tv_order_operation_time_limit)
    TextView tv_order_operation_time_limit;
    @BindView(R.id.tv_currnet_bond)
    TextView tv_currnet_bond;

    @BindView(R.id.edit_trade_single_price)
    EditText edit_trade_single_price;
    @BindView(R.id.edit_trade_num)
    EditText edit_trade_num;
    @BindView(R.id.edit_min_limit)
    EditText edit_min_limit;
    @BindView(R.id.edit_max_limit)
    EditText edit_max_limit;
    @BindView(R.id.tv_trade_all_price)
    TextView tv_trade_all_price;
    @BindView(R.id.edit_otc_phone)
    TextView edit_otc_phone;

    @BindView(R.id.cb_zhifubao)
    CheckBox cb_zhifubao;
    @BindView(R.id.cb_weixin)
    CheckBox cb_weixin;
    @BindView(R.id.cb_yinhangka)
    CheckBox cb_yinhangka;
    @BindView(R.id.tv_zhifubao)
    TextView tv_zhifubao;
    @BindView(R.id.tv_weixin)
    TextView tv_weixin;
    @BindView(R.id.tv_yinhangka)
    TextView tv_yinhangka;

    private ListPopWindow timeLimitPopup;
    private Dialog dialog;

    private boolean isBuy = true; //默认true=买
    private OtcPublishParam param;
    private OtcSelectFeeModel otcSelectFeeModel;
    private String coinName = Constant.ACU_CURRENCY_NAME;
    private PayInfoModel yhkModel = null, wxModel = null, zfbModel = null;
    private boolean isFirst = true;

    public static OtcManageBuySellFragment newInstance(boolean isBuy) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isBuy", isBuy);
        OtcManageBuySellFragment fragment = new OtcManageBuySellFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        if (isFirst) {
            upload();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            isBuy = bundle.getBoolean("isBuy", true);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_otc_manage_buysell;
    }

    @Override
    protected void initData(View view) {
        param = new OtcPublishParam(isBuy);
        edit_trade_single_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!TextUtils.isEmpty(s.toString()) && !TextUtils.isEmpty(edit_trade_num.getText().toString())) {
                        double valueD = MathHelper.mul(Double.valueOf(s.toString()), Double.valueOf(edit_trade_num.getText().toString()));
                        tv_trade_all_price.setText(FormatterUtils.getFormatValue(valueD));
                        if (otcSelectFeeModel != null) {
                            double value;
                            if (!isBuy) {
                                value = MathHelper.mul(Double.valueOf(tv_trade_all_price.getText().toString()), otcSelectFeeModel.bondRate);
                                tv_currnet_bond.setText(getResources().getString(R.string.currnet_bond) + value + " " + coinName);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        edit_trade_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!TextUtils.isEmpty(s.toString()) && !TextUtils.isEmpty(edit_trade_single_price.getText().toString())) {
                        double valueD = MathHelper.mul(Double.valueOf(s.toString()), Double.valueOf(edit_trade_single_price.getText().toString()));
                        tv_trade_all_price.setText(FormatterUtils.getFormatValue(valueD));
                        if (otcSelectFeeModel != null) {
                            double value;
                            if (!isBuy) {
                                value = MathHelper.mul(Double.valueOf(tv_trade_all_price.getText().toString()), otcSelectFeeModel.bondRate);
                                tv_currnet_bond.setText(getResources().getString(R.string.currnet_bond) + value + " " + coinName);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cb_zhifubao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tv_zhifubao.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                } else {
                    tv_zhifubao.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
                }
            }
        });
        cb_weixin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tv_weixin.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                } else {
                    tv_weixin.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
                }
            }
        });
        cb_yinhangka.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tv_yinhangka.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                } else {
                    tv_yinhangka.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
                }
            }
        });

        if (isBuy) {
            tv_currnet_bond.setVisibility(View.GONE);
        }
    }

    @Override
    protected OtcManageBuySellPresenter createPresenter(OtcManageBuySellPresenter mPresenter) {
        return new OtcManageBuySellPresenter();
    }

    @Override
    protected void onPresenterCreated(OtcManageBuySellPresenter mPresenter) {
        super.onPresenterCreated(mPresenter);
        upload();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timeLimitPopup != null && timeLimitPopup.isShowing()) {
            timeLimitPopup.dismiss();
            timeLimitPopup = null;
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @OnClick(R.id.tv_order_operation_time_limit)
    void timeLimitClick() {
        showTimeType();
    }

    @OnClick(R.id.btn_confirm)
    void confirmClick() {
        if (!cb_yinhangka.isChecked() && !cb_weixin.isChecked() && !cb_zhifubao.isChecked()) {
            showToastError(getResources().getString(R.string.please_choose_pay_method));
            return;
        }
        if (cb_yinhangka.isChecked() && yhkModel == null) {
            showToastError(getResources().getString(R.string.no_pay_tip));
            return;
        }
        if (cb_weixin.isChecked() && wxModel == null) {
            showToastError(getResources().getString(R.string.no_pay_tip));
            return;
        }
        if (cb_zhifubao.isChecked() && zfbModel == null) {
            showToastError(getResources().getString(R.string.no_pay_tip));
            return;
        }
        if (cb_yinhangka.isChecked()) {
            param.payByCard = 1;
        } else {
            param.payByCard = 0;
        }
        if (cb_weixin.isChecked()) {
            param.payWechat = 1;
        } else {
            param.payWechat = 0;
        }
        if (cb_zhifubao.isChecked()) {
            param.payAlipay = 1;
        } else {
            param.payAlipay = 0;
        }

        String price = edit_trade_single_price.getText().toString();
        String num = edit_trade_num.getText().toString();
        String min = edit_min_limit.getText().toString();
        String max = edit_max_limit.getText().toString();
        String otcPhone = edit_otc_phone.getText().toString();

        if (TextUtils.isEmpty(price)) {
            showToastError(getResources().getString(R.string.please_input_trade_single_price));
            return;
        }
        if (TextUtils.isEmpty(num)) {
            showToastError(getResources().getString(R.string.please_input_trade_num));
            return;
        }
        if (TextUtils.isEmpty(min)) {
            showToastError(getResources().getString(R.string.please_input_min_limit));
            return;
        }
        if (TextUtils.isEmpty(max)) {
            showToastError(getResources().getString(R.string.please_input_max_limit));
            return;
        }
        if (TextUtils.isEmpty(otcPhone)) {
            showToastError(getResources().getString(R.string.please_otc_phone));
            return;
        }
        param.price = price;
        param.num = num;
        param.amount = tv_trade_all_price.getText().toString();
        param.lowLimit = min;
        param.highLimit = max;
        param.explain = otcPhone;
        showSure(param);
    }

    @Override
    public void selectBondFreerate(OtcSelectFeeModel model) {
        this.otcSelectFeeModel = model;
    }

    @Override
    public void orderSuccess() {
        showToastSuccess(getResources().getString(R.string.success));
        getHostActivity().finish();
    }

    public void setPayList(List<PayInfoModel> list) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).type != null && list.get(i).type == 1) {
                    yhkModel = list.get(i);
                }
                if (list.get(i).type != null && list.get(i).type == 2) {
                    wxModel = list.get(i);
                }
                if (list.get(i).type != null && list.get(i).type == 3) {
                    zfbModel = list.get(i);
                }
            }
        } else {
            yhkModel = null;
            wxModel = null;
            zfbModel = null;
        }
    }

    private void upload() {
        if (isVisibleToUser) {
            if (!isBuy) {
                mPresenter.selectBondFreerate(isFirst);
            }
            if (isFirst) {
                isFirst = false;
            }
        }
    }

    private void showSure(final OtcPublishParam param) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_publish, null);
        TextView tv_publish = view.findViewById(R.id.tv_publish);
        ImageView img_close = view.findViewById(R.id.img_close);
        TextView tv_price = view.findViewById(R.id.tv_price);
        TextView tv_num = view.findViewById(R.id.tv_num);
        TextView tv_limit = view.findViewById(R.id.tv_limit);
        TextView tv_time = view.findViewById(R.id.tv_time);
        QMUIRoundButton btn_sure = view.findViewById(R.id.btn_sure);

        if (isBuy) {
            tv_publish.setText(getResources().getString(R.string.publish_buy));
        } else {
            tv_publish.setText(getResources().getString(R.string.publish_sell));
        }
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        tv_price.setText(param.price);
        tv_num.setText(param.num);
        tv_limit.setText(param.lowLimit + "-" + param.highLimit);
        switch (param.timeOut) {
            case 15:
                tv_time.setText(getResources().getString(R.string.min_15));
                break;
            case 30:
                tv_time.setText(getResources().getString(R.string.min_30));
                break;
            case 60:
                tv_time.setText(getResources().getString(R.string.hour_1));
                break;
        }
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.order(param);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.width = UIUtils.getScreenWidth();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM | Gravity.CENTER;
        dialog.getWindow().setBackgroundDrawableResource(R.color.color_transparent);
        dialog.getWindow().setAttributes(params);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void showTimeType() {
        if (timeLimitPopup != null && timeLimitPopup.isShowing()) {
            timeLimitPopup.dismiss();
            return;
        }
        if (timeLimitPopup == null) {
            final List<String> data = new ArrayList<>();
            final List<Integer> dataId = new ArrayList<>();
            data.add(getResources().getString(R.string.min_15));
            data.add(getResources().getString(R.string.min_30));
            data.add(getResources().getString(R.string.hour_1));
            dataId.add(15);
            dataId.add(30);
            dataId.add(60);
            ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.simple_list_item, data);
            timeLimitPopup = new ListPopWindow(getContext(), adapter);
            timeLimitPopup.create(UIUtils.dp2px(120), UIUtils.dp2px(120), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tv_order_operation_time_limit.setText(data.get(position));
                    param.timeOut = dataId.get(position);
                    timeLimitPopup.dismiss();
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            timeLimitPopup.setDropDownGravity(Gravity.END);
        }
        timeLimitPopup.setAnchorView(tv_order_operation_time_limit);
        timeLimitPopup.show();
    }
}
