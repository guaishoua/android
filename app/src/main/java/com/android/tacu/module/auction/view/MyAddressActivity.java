package com.android.tacu.module.auction.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.auction.contract.MyAddressContract;
import com.android.tacu.module.auction.model.AddressModel;
import com.android.tacu.module.auction.model.AreaModel;
import com.android.tacu.module.auction.model.CityModel;
import com.android.tacu.module.auction.model.PCAModel;
import com.android.tacu.module.auction.model.ProvinceModel;
import com.android.tacu.module.auction.presenter.MyAddressPresenter;
import com.android.tacu.utils.UIUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MyAddressActivity extends BaseActivity<MyAddressPresenter> implements MyAddressContract.IView {

    @BindView(R.id.lin_edit)
    LinearLayout lin_edit;
    @BindView(R.id.edit_receiver)
    EditText edit_receiver;
    @BindView(R.id.edit_tel)
    EditText edit_tel;
    @BindView(R.id.edit_area)
    TextView edit_area;
    @BindView(R.id.edit_address)
    QMUIRoundEditText edit_address;
    @BindView(R.id.btn_confrim)
    QMUIRoundButton btn_confrim;

    @BindView(R.id.lin_details)
    LinearLayout lin_details;
    @BindView(R.id.tv_receiver)
    TextView tv_receiver;
    @BindView(R.id.tv_tel)
    TextView tv_tel;
    @BindView(R.id.tv_area)
    TextView tv_area;
    @BindView(R.id.tv_address)
    TextView tv_address;

    private Integer id;

    private FixedIndicatorView magic_indicator;
    private RecyclerView ry_bottom;

    private BaseIndicatorAdapter baseApapter;
    private BottomAdapter bottomAdapter;

    private AddressModel addressModel;

    private List<String> baseList = new ArrayList<>();
    private List<PCAModel> ProvinceList = new ArrayList<>();
    private List<PCAModel> CityList = new ArrayList<>();
    private List<PCAModel> AreaList = new ArrayList<>();

    // 0=省 1=市 2=区
    private int flag = 0;
    private int PFlag = -1;
    private int CFlag = -1;
    private int AFlag = -1;
    private String PString;
    private String CString;
    private String AString;
    private String PCode;
    private String CCode;
    private String ACode;

    private Dialog dialog;

    private Handler handler = new Handler();

    public static Intent createActivity(Context context, Integer id) {
        Intent intent = new Intent(context, MyAddressActivity.class);
        intent.putExtra("id", id);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_my_address);
    }

    @Override
    protected void initView() {
        id = getIntent().getIntExtra("id", 0);
        mTopBar.setTitle(getResources().getString(R.string.my_shipping_address));
    }

    @Override
    protected MyAddressPresenter createPresenter(MyAddressPresenter mPresenter) {
        return new MyAddressPresenter();
    }

    @Override
    protected void onPresenterCreated(MyAddressPresenter presenter) {
        super.onPresenterCreated(presenter);

        mPresenter.getAddressList(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    @OnClick(R.id.edit_area)
    void areaClick() {
        mPresenter.getProvince();
        setAddressDialog();
    }

    @OnClick(R.id.btn_confrim)
    void confirmClick() {
        String receiver = edit_receiver.getText().toString().trim();
        String tel = edit_tel.getText().toString().trim();
        String area = edit_area.getText().toString().trim();
        String address = edit_address.getText().toString().trim();

        if (TextUtils.isEmpty(receiver)) {
            showToastError(getResources().getString(R.string.please_input_receiver));
            return;
        }
        if (TextUtils.isEmpty(tel)) {
            showToastError(getResources().getString(R.string.please_input_telephone));
            return;
        }
        if (TextUtils.isEmpty(area)) {
            showToastError(getResources().getString(R.string.please_input_area));
            return;
        }
        if (TextUtils.isEmpty(address)) {
            showToastError(getResources().getString(R.string.please_input_address));
            return;
        }

        mPresenter.insertAddress(receiver, tel, PCode, PString, CCode, CString, ACode, AString, address, 1);
    }

    @Override
    public void getProvince(List<ProvinceModel> list) {
        if (list != null && list.size() > 0) {
            ProvinceList.clear();
            for (int i = 0; i < list.size(); i++) {
                ProvinceList.add(new PCAModel(list.get(i).name, list.get(i).code));
            }
            bottomAdapter.setNewData(ProvinceList);
        }
    }

    @Override
    public void getCity(List<CityModel> list) {
        if (list != null && list.size() > 0) {
            CityList.clear();
            for (int i = 0; i < list.size(); i++) {
                CityList.add(new PCAModel(list.get(i).name, list.get(i).code));
            }
            bottomAdapter.setNewData(CityList);
        }
    }

    @Override
    public void getArea(List<AreaModel> list) {
        if (list != null && list.size() > 0) {
            AreaList.clear();
            for (int i = 0; i < list.size(); i++) {
                AreaList.add(new PCAModel(list.get(i).name, list.get(i).code));
            }
            bottomAdapter.setNewData(AreaList);
        }
    }

    @Override
    public void insertAddressSuccess() {
        mPresenter.getAddressList(2);
    }

    @Override
    public void getAddressList(int type, List<AddressModel> list) {
        if (list != null && list.size() > 0) {
            addressModel = list.get(0);
        } else {
            addressModel = null;
        }

        if (addressModel != null) {
            if (type == 1) {
                lin_edit.setVisibility(View.GONE);
                lin_details.setVisibility(View.VISIBLE);

                tv_receiver.setText(addressModel.consignee);
                tv_tel.setText(addressModel.phone);
                tv_area.setText(addressModel.provinceName + " " + addressModel.cityName + " " + addressModel.areaName);
                tv_address.setText(addressModel.detailedAddress);
            } else if (type == 2) {
                mPresenter.chooseAddr(id, addressModel.id);
            }
        } else {
            lin_edit.setVisibility(View.VISIBLE);
            lin_details.setVisibility(View.GONE);
        }
    }

    @Override
    public void chooseAddrSuccess() {
        showToastSuccess(getResources().getString(R.string.success));
        finish();
    }

    private void setAddressDialog() {
        clear();
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(this).inflate(R.layout.view_setting_pca, null);
        ImageView img_close = view.findViewById(R.id.img_close);
        magic_indicator = view.findViewById(R.id.magic_indicator);
        ry_bottom = view.findViewById(R.id.ry_bottom);

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
                dialog.dismiss();
            }
        });

        baseApapter = new BaseIndicatorAdapter();
        magic_indicator.setBackgroundColor(ContextCompat.getColor(this, R.color.tab_bg_color));
        magic_indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(this, R.color.text_default), ContextCompat.getColor(this, R.color.text_grey_2)).setSize(12, 12));
        magic_indicator.setScrollBar(new ColorBar(this, ContextCompat.getColor(this, R.color.text_default), 4));
        magic_indicator.setSplitMethod(FixedIndicatorView.SPLITMETHOD_WRAP);
        magic_indicator.setAdapter(baseApapter);
        magic_indicator.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                flag = select;
                if (select == 0) {
                    bottomAdapter.setNewData(ProvinceList);
                } else if (select == 1) {
                    bottomAdapter.setNewData(CityList);
                } else if (select == 2) {
                    bottomAdapter.setNewData(AreaList);
                }
            }
        });

        bottomAdapter = new BottomAdapter();
        ry_bottom.setLayoutManager(new LinearLayoutManager(this));
        ry_bottom.setAdapter(bottomAdapter);

        dialog.setContentView(view);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = UIUtils.getScreenHeight() / 2;
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setBackgroundDrawableResource(R.color.color_transparent);
        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private class BaseIndicatorAdapter extends Indicator.IndicatorAdapter {

        @Override
        public int getCount() {
            return baseList != null ? baseList.size() : 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.view_tab, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(baseList.get(position));
            int padding = UIUtils.dp2px(10);
            textView.setPadding(padding, 0, padding, 0);
            return convertView;
        }
    }

    private class BottomAdapter extends BaseQuickAdapter<PCAModel, BaseViewHolder> {

        public BottomAdapter() {
            super(R.layout.item_data);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final PCAModel item) {
            helper.setText(R.id.tv_data, item.name);
            if (flag == 0) {
                if (PFlag == helper.getLayoutPosition()) {
                    helper.setGone(R.id.img_check, true);
                } else {
                    helper.setGone(R.id.img_check, false);
                }
            } else if (flag == 1) {
                if (CFlag == helper.getLayoutPosition()) {
                    helper.setGone(R.id.img_check, true);
                } else {
                    helper.setGone(R.id.img_check, false);
                }
            } else if (flag == 2) {
                if (AFlag == helper.getLayoutPosition()) {
                    helper.setGone(R.id.img_check, true);
                } else {
                    helper.setGone(R.id.img_check, false);
                }
            }

            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (flag == 0) {
                        PFlag = helper.getLayoutPosition();
                        PString = item.name;
                        PCode = item.code;

                        clearCity();

                        flag = 1;
                        mPresenter.getCity(item.code);
                    } else if (flag == 1) {
                        CFlag = helper.getLayoutPosition();
                        CString = item.name;
                        CCode = item.code;

                        clearArea();

                        flag = 2;
                        mPresenter.getArea(item.code);
                    } else if (flag == 2) {
                        AFlag = helper.getLayoutPosition();
                        AString = item.name;
                        ACode = item.code;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                edit_area.setText(PString + " " + CString + " " + AString);
                                edit_area.setTextColor(ContextCompat.getColor(MyAddressActivity.this, R.color.text_color));
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                            }
                        }, 500);
                    }
                    exchange();
                    notifyDataSetChanged();
                }
            });
        }
    }

    private void exchange() {
        baseList.clear();
        if (!TextUtils.isEmpty(PString)) {
            baseList.add(PString);
        }
        if (!TextUtils.isEmpty(CString)) {
            baseList.add(CString);
        }
        if (!TextUtils.isEmpty(AString)) {
            baseList.add(AString);
        }
        if (flag != 2) {
            baseList.add(getResources().getString(R.string.please_select));
        }
        baseApapter.notifyDataSetChanged();
        magic_indicator.setCurrentItem(flag);
    }

    private void clearCity() {
        CFlag = -1;
        CString = "";
        CCode = "";

        AFlag = -1;
        AString = "";
        ACode = "";
    }

    private void clearArea() {
        AFlag = -1;
        AString = "";
        ACode = "";
    }

    private void clear() {
        baseList.clear();
        baseList.add(getResources().getString(R.string.please_select));
        ProvinceList.clear();
        CityList.clear();
        AreaList.clear();

        edit_area.setText("");

        flag = 0;
        PFlag = -1;
        CFlag = -1;
        AFlag = -1;
        PString = "";
        CString = "";
        AString = "";
        PCode = "";
        CCode = "";
        ACode = "";
    }
}
