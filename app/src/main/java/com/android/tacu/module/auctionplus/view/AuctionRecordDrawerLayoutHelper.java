package com.android.tacu.module.auctionplus.view;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.module.assets.model.CoinListModel;
import com.android.tacu.module.auctionplus.modal.AuctionRecordParam;
import com.android.tacu.utils.UIUtils;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundTextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiazhen on 2018/10/15.
 */
public class AuctionRecordDrawerLayoutHelper implements View.OnClickListener {

    private Activity mActivity;
    private Resources mResources;
    private View viewDrawer;

    private AuctionRecordParam param;

    //筛选
    private RelativeLayout rl_screen;
    private TextView tv_currency;
    private RecyclerView rv_currency;
    private RecyclerView rv_status;
    private QMUIRoundTextView tv_start_time;
    private QMUIRoundTextView tv_end_time;
    private Button btn_confirm;
    private QMUIRoundButton btn_reset;

    //币种列表
    private RelativeLayout rl_currency;
    private ImageView iv_back;
    private EditText et_search;
    private RecyclerView recyclerView;

    private RecordClickListener clickListener;

    private CurrencyAdapter currencyAdapter;
    private SearchAdapter searchAdapter;
    private StatusAdapter statusAdapter;

    private List<CoinListModel.AttachmentBean> coinsList = new ArrayList<>();
    private List<CoinListModel.AttachmentBean> currencyList = new ArrayList<>();
    private List<CoinListModel.AttachmentBean> currencySearchList = new ArrayList<>();
    private List<StatusModel> statusModelList = new ArrayList<>();

    public AuctionRecordDrawerLayoutHelper(Activity activity, View view) {
        this.mActivity = activity;
        this.mResources = mActivity.getResources();
        this.viewDrawer = view;
        param = new AuctionRecordParam(0, 4);
    }

    public void clearActivity() {
        mActivity = null;
    }

    public void setInitView(RecordClickListener clickListener) {
        this.clickListener = clickListener;

        rl_screen = viewDrawer.findViewById(R.id.rl_screen);
        tv_currency = viewDrawer.findViewById(R.id.tv_currency);
        rv_currency = viewDrawer.findViewById(R.id.rv_currency);
        rv_status = viewDrawer.findViewById(R.id.rv_status);
        tv_start_time = viewDrawer.findViewById(R.id.tv_start_time);
        tv_end_time = viewDrawer.findViewById(R.id.tv_end_time);
        btn_confirm = viewDrawer.findViewById(R.id.btn_confirm);
        btn_reset = viewDrawer.findViewById(R.id.btn_reset);
        rl_currency = viewDrawer.findViewById(R.id.rl_currency);
        iv_back = viewDrawer.findViewById(R.id.iv_back);
        et_search = viewDrawer.findViewById(R.id.et_search);
        recyclerView = viewDrawer.findViewById(R.id.recyclerView);

        initAdapter();

        tv_start_time.setOnClickListener(this);
        tv_end_time.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search();
            }
        });
    }

    private void initAdapter() {
        rv_currency.setLayoutManager(new GridLayoutManager(mActivity, 3));
        rv_status.setLayoutManager(new GridLayoutManager(mActivity, 2));
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        currencyAdapter = new CurrencyAdapter();
        rv_currency.setAdapter(currencyAdapter);

        searchAdapter = new SearchAdapter();
        recyclerView.setAdapter(searchAdapter);

        statusAdapter = new StatusAdapter();
        rv_status.setAdapter(statusAdapter);

        statusModelList.add(new StatusModel(4, mResources.getString(R.string.pay_all)));//全部
        statusModelList.add(new StatusModel(1, mResources.getString(R.string.pay_before)));//待支付
        statusModelList.add(new StatusModel(2, mResources.getString(R.string.pay_after)));//已支付
        statusModelList.add(new StatusModel(3, mResources.getString(R.string.pay_gotime)));//支付过期
        statusAdapter.setNewData(statusModelList);
    }

    private void search() {
        currencySearchList.clear();
        String search = et_search.getText().toString().trim().toLowerCase();
        if (TextUtils.isEmpty(search)) {
            setSearchValue(currencyList);
            return;
        }

        if (currencyList != null && currencyList.size() > 0) {
            for (int i = 0; i < currencyList.size(); i++) {
                if (currencyList.get(i).currencyNameEn.toLowerCase().contains(search) || currencyList.get(i).currencyName.toLowerCase().contains(search)) {
                    currencySearchList.add(currencyList.get(i));
                }
            }
        }
        setSearchValue(currencySearchList);
    }

    private void setSearchValue(List<CoinListModel.AttachmentBean> list) {
        if (list != null && list.size() > 0) {
            searchAdapter.setNewData(list);
        } else {
            searchAdapter.setNewData(null);
        }
    }

    public void setCoinList(List<CoinListModel.AttachmentBean> currencyList) {
        if (currencyList == null) {
            return;
        }

        this.currencyList = currencyList;
        coinsList.clear();
        //手动添加"全部"，"..."数据
        CoinListModel.AttachmentBean bean = new CoinListModel.AttachmentBean();
        bean.currencyNameEn = mResources.getString(R.string.all);
        bean.currencyId = 0;
        coinsList.add(bean);
        for (int i = 0; i < 7; i++) {
            coinsList.add(currencyList.get(i));
        }
        CoinListModel.AttachmentBean bean2 = new CoinListModel.AttachmentBean();
        bean2.currencyNameEn = "...";
        bean2.currencyId = -1;
        coinsList.add(bean2);

        currencyAdapter.setNewData(coinsList);

        search();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_start_time:
                clickListener.timeClick(tv_start_time, tv_end_time.getText().toString().trim(), true);
                break;
            case R.id.tv_end_time:
                clickListener.timeClick(tv_end_time, tv_start_time.getText().toString().trim(), false);
                break;
            case R.id.btn_confirm:
                param.setStartTime(tv_start_time.getText().toString().trim());
                param.setEndTime(tv_end_time.getText().toString().trim());

                clickListener.RecordClick(param);
                break;
            case R.id.btn_reset:
                tv_currency.setText("");
                tv_start_time.setText("");
                tv_end_time.setText("");
                param.setStatus(4);
                param.setCurrencyId(0);
                currencyAdapter.notifyDataSetChanged();
                statusAdapter.notifyDataSetChanged();
                break;
            case R.id.iv_back:
                isShow(true);
                break;
        }
    }

    private class CurrencyAdapter extends BaseQuickAdapter<CoinListModel.AttachmentBean, BaseViewHolder> {

        public CurrencyAdapter() {
            super(R.layout.item_asset);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final CoinListModel.AttachmentBean item) {
            helper.setText(R.id.tv_item, item.currencyNameEn);

            if (item.currencyId == param.getCurrencyId()) {
                ((QMUIRoundButtonDrawable) helper.getView(R.id.tv_item).getBackground()).setStrokeData(UIUtils.dp2px(1), ContextCompat.getColorStateList(mContext, R.color.color_default));
                helper.setTextColor(R.id.tv_item, ContextCompat.getColor(mContext, R.color.text_default));
                helper.setGone(R.id.iv_item, true);
            } else {
                ((QMUIRoundButtonDrawable) helper.getView(R.id.tv_item).getBackground()).setStrokeData(UIUtils.dp2px(1), ContextCompat.getColorStateList(mContext, R.color.text_grey_3));
                helper.setTextColor(R.id.tv_item, ContextCompat.getColor(mContext, R.color.text_grey));
                helper.setGone(R.id.iv_item, false);
            }

            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (helper.getLayoutPosition() == coinsList.size() - 1) {
                        isShow(false);
                        notifyDataSetChanged();
                    } else {
                        tv_currency.setText("");
                        param.setCurrencyId(item.currencyId);
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    private class SearchAdapter extends BaseQuickAdapter<CoinListModel.AttachmentBean, BaseViewHolder> {

        public SearchAdapter() {
            super(R.layout.item_search_currency);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final CoinListModel.AttachmentBean item) {
            helper.setText(R.id.tv_currency, item.currencyNameEn);
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isShow(true);
                    tv_currency.setText(item.currencyNameEn);
                    param.setCurrencyId(item.currencyId);
                }
            });
        }
    }

    private class StatusAdapter extends BaseQuickAdapter<StatusModel, BaseViewHolder> {

        public StatusAdapter() {
            super(R.layout.item_asset);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final StatusModel item) {
            helper.setText(R.id.tv_item, item.statusValue);

            if (item.status == param.getStatus()) {
                ((QMUIRoundButtonDrawable) helper.getView(R.id.tv_item).getBackground()).setStrokeData(UIUtils.dp2px(1), ContextCompat.getColorStateList(mContext, R.color.color_default));
                helper.setTextColor(R.id.tv_item, ContextCompat.getColor(mContext, R.color.text_default));
                helper.setGone(R.id.iv_item, true);
            } else {
                ((QMUIRoundButtonDrawable) helper.getView(R.id.tv_item).getBackground()).setStrokeData(UIUtils.dp2px(1), ContextCompat.getColorStateList(mContext, R.color.text_grey_3));
                helper.setTextColor(R.id.tv_item, ContextCompat.getColor(mContext, R.color.text_grey));
                helper.setGone(R.id.iv_item, false);
            }
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    param.setStatus(item.status);
                    notifyDataSetChanged();
                }
            });
        }
    }

    private class StatusModel implements Serializable {
        public int status;
        public String statusValue;

        public StatusModel(int status, String statusValue) {
            this.status = status;
            this.statusValue = statusValue;
        }
    }

    private void isShow(boolean isScreen) {
        rl_screen.setVisibility(isScreen == true ? View.VISIBLE : View.GONE);
        rl_currency.setVisibility(isScreen == true ? View.GONE : View.VISIBLE);
    }

    public interface RecordClickListener {
        void RecordClick(AuctionRecordParam param);

        void timeClick(TextView tv, String time, boolean isStart);
    }
}
