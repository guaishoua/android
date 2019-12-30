package com.android.tacu.module.assets.view;

import android.content.Context;
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
import com.android.tacu.module.assets.model.MoneyFlowEvent;
import com.android.tacu.module.assets.model.MoneyFlowModel;
import com.android.tacu.utils.UIUtils;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaohong on 2018/10/8.
 * 交易记录
 */

public class MoneyFlowDrawerLayoutHelper implements View.OnClickListener {
    //筛选
    private TextView tv_currency;
    private RecyclerView rv_type;
    private RecyclerView rv_currency;
    private RelativeLayout rl_screen;

    //币种
    private ImageView iv_back;
    private ImageView iv_search;
    private EditText et_search;
    private RecyclerView recyclerView;
    private RelativeLayout rl_currency;

    //时间
    private Button btn_confirm;
    private QMUIRoundTextView tv_end_time;
    private QMUIRoundTextView tv_start_time;
    private Button btn_reset;

    private View view;
    private Context mContext;
    private Resources mResources;

    private String search;
    private TypeAdapter typeAdapter;
    private SearchAdapter searchAdapter;
    private CurrencyAdapter currencyAdapter;
    private MoneyFlowEvent moneyFlowEvent;
    private RecordClickListener clickListener;

    private List<MoneyFlowModel.TypeModel> typeList = new ArrayList<>();
    private List<CoinListModel.AttachmentBean> coinsList = new ArrayList<>();
    private List<CoinListModel.AttachmentBean> currencyList = new ArrayList<>();
    private List<CoinListModel.AttachmentBean> currencySearchList = new ArrayList<>();

    public MoneyFlowDrawerLayoutHelper(Context context, View viewDrawer, Integer currencyId) {
        this.mContext = context;
        this.mResources = context.getResources();
        this.view = viewDrawer;
        moneyFlowEvent = new MoneyFlowEvent();
        moneyFlowEvent.setCurrencyId(currencyId == null ? 0 : currencyId);
        moneyFlowEvent.setType("0");
    }

    public void clearActivity() {
        mContext = null;
        mResources = null;
        view = null;
        typeList = null;
    }

    public void initDatas(List<MoneyFlowModel.TypeModel> list) {
        typeList.clear();

        MoneyFlowModel.TypeModel typeModel = new MoneyFlowModel.TypeModel();
        typeModel.value = mResources.getString(R.string.all);
        typeModel.code = "0";
        typeList.add(typeModel);

        typeList.addAll(list);
        typeAdapter.setNewData(typeList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                isShow(true);
                break;
            case R.id.iv_search:
                search();
                break;
            case R.id.btn_reset:
                tv_start_time.setText("");
                tv_end_time.setText("");
                tv_currency.setText("");
                moneyFlowEvent.setType("0");
//                moneyFlowEvent.setCurrencyId();
                typeAdapter.notifyDataSetChanged();
//                currencyAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_start_time:
                clickListener.timeClick(tv_start_time, tv_end_time.getText().toString().trim(), true);
                break;
            case R.id.tv_end_time:
                clickListener.timeClick(tv_end_time, tv_start_time.getText().toString().trim(), false);
                break;
            case R.id.btn_confirm:
                moneyFlowEvent.setEndTime(tv_end_time.getText().toString().trim());
                moneyFlowEvent.setStartTime(tv_start_time.getText().toString().trim());

                clickListener.RecordClick(moneyFlowEvent);
                break;

        }
    }

    public void setDatas(List<CoinListModel.AttachmentBean> currencyList) {
        this.currencyList = currencyList;

        coinsList.clear();
        //手动添加"全部"，"..."数据
        CoinListModel.AttachmentBean bean = new CoinListModel.AttachmentBean();
        bean.currencyNameEn = mResources.getString(R.string.all);
        bean.currencyId = 0;
        coinsList.add(bean);

        if (currencyList == null) coinsList = new ArrayList<>();
        int target = Math.min(7, currencyList.size());
        for (int i = 0; i < target; i++) {
            coinsList.add(currencyList.get(i));
        }
        CoinListModel.AttachmentBean bean2 = new CoinListModel.AttachmentBean();
        bean2.currencyNameEn = "...";
        bean2.currencyId = -1;
        coinsList.add(bean2);

        currencyAdapter.setNewData(coinsList);

        search();
    }

    public void updateSelectedCoin(Integer currencyId) {
        if (moneyFlowEvent != null) {
            moneyFlowEvent.setCurrencyId(currencyId);
            currencyAdapter.notifyDataSetChanged();
        }
    }

    private void search() {
        currencySearchList.clear();
        search = et_search.getText().toString().trim().toLowerCase();
        if (TextUtils.isEmpty(search)) {
            setAdapter(currencyList);
            return;
        }

        if (currencyList != null && currencyList.size() > 0) {
            for (int i = 0; i < currencyList.size(); i++) {
                if (currencyList.get(i).currencyNameEn.toLowerCase().contains(search) || currencyList.get(i).currencyName.toLowerCase().contains(search)) {
                    currencySearchList.add(currencyList.get(i));
                }
            }
        }
        setAdapter(currencySearchList);
    }

    private void setAdapter(List<CoinListModel.AttachmentBean> list) {
        if (list != null && list.size() > 0) {
            searchAdapter.setNewData(list);
        } else {
            searchAdapter.setNewData(null);
        }
    }

    public void setDrawerMenuView(final RecordClickListener clickListener) {
        this.clickListener = clickListener;

        tv_currency = view.findViewById(R.id.tv_currency);
        rl_screen = view.findViewById(R.id.rl_screen);
        rv_currency = view.findViewById(R.id.rv_currency);
        rv_type = view.findViewById(R.id.rv_type);

        iv_back = view.findViewById(R.id.iv_back);
        iv_search = view.findViewById(R.id.iv_search);
        et_search = view.findViewById(R.id.et_search);
        recyclerView = view.findViewById(R.id.recyclerView);
        rl_currency = view.findViewById(R.id.rl_currency);

        tv_start_time = view.findViewById(R.id.tv_start_time);
        tv_end_time = view.findViewById(R.id.tv_end_time);
        btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_reset = view.findViewById(R.id.btn_reset);

        iv_back.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        tv_start_time.setOnClickListener(this);
        tv_end_time.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        btn_reset.setOnClickListener(this);

        initView();
        isShow(true);
        textChanged();
    }

    private void textChanged() {
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

    private void initView() {
        rv_currency.setLayoutManager(new GridLayoutManager(mContext, 3));
        rv_type.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        currencyAdapter = new CurrencyAdapter();
        rv_currency.setAdapter(currencyAdapter);

        typeAdapter = new TypeAdapter();
        rv_type.setAdapter(typeAdapter);

        searchAdapter = new SearchAdapter();
        recyclerView.setAdapter(searchAdapter);
    }

    private void isShow(boolean isScreen) {
        rl_screen.setVisibility(isScreen == true ? View.VISIBLE : View.GONE);
        rl_currency.setVisibility(isScreen == true ? View.GONE : View.VISIBLE);
    }

    private class CurrencyAdapter extends BaseQuickAdapter<CoinListModel.AttachmentBean, BaseViewHolder> {

        public CurrencyAdapter() {
            super(R.layout.item_asset);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final CoinListModel.AttachmentBean item) {
            helper.setText(R.id.tv_item, item.currencyNameEn);

            if (item.currencyId == moneyFlowEvent.getCurrencyId()) {
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
                        moneyFlowEvent.setCurrencyId(item.currencyId);
                        moneyFlowEvent.setCurrencyNameEn(item.currencyNameEn);

                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    private class TypeAdapter extends BaseQuickAdapter<MoneyFlowModel.TypeModel, BaseViewHolder> {

        public TypeAdapter() {
            super(R.layout.item_asset);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final MoneyFlowModel.TypeModel item) {
            helper.setText(R.id.tv_item, item.value);

            if (TextUtils.equals(item.code, moneyFlowEvent.getType())) {

                ((QMUIRoundButtonDrawable) helper.getView(R.id.tv_item).getBackground()).setBgData(ContextCompat.getColorStateList(mContext, R.color.color_default));
                helper.setTextColor(R.id.tv_item, ContextCompat.getColor(mContext, R.color.color_white));
                helper.setGone(R.id.iv_item, true);
            } else {
                ((QMUIRoundButtonDrawable) helper.getView(R.id.tv_item).getBackground()).setBgData(ContextCompat.getColorStateList(mContext, R.color.color_grey_3));

                helper.setTextColor(R.id.tv_item, ContextCompat.getColor(mContext, R.color.text_grey_3));
                helper.setGone(R.id.iv_item, true);
            }
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moneyFlowEvent.setType(item.code);
                    moneyFlowEvent.setTypeValue(item.value);
                    notifyDataSetChanged();
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
                    tv_currency.setVisibility(View.VISIBLE);
                    tv_currency.setText(item.currencyNameEn);

                    moneyFlowEvent.setCurrencyId(item.currencyId);
                    moneyFlowEvent.setCurrencyNameEn(item.currencyNameEn);
                }
            });
        }
    }

    public interface RecordClickListener {
        void RecordClick(MoneyFlowEvent moneyFlowEvent);

        void timeClick(TextView tv, String time, boolean isStart);
    }
}
