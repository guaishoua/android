package com.android.tacu.module.transaction.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.transaction.contract.DealDetailsConstract;
import com.android.tacu.module.transaction.model.DealDetailsModel;
import com.android.tacu.module.transaction.model.ShowOrderListModel;
import com.android.tacu.module.transaction.presenter.DealDetailsPresenter;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.FormatterUtils;

import java.util.List;

import butterknife.BindView;

/**
 * Created by jiazhen on 2018/10/15.
 */
public class DealDetailsActivity extends BaseActivity<DealDetailsPresenter> implements DealDetailsConstract.IView {

    @BindView(R.id.recyclerview_details)
    RecyclerView ryDetails;

    private TextView tvBuyorSell;
    private TextView tvCoinsName;
    private TextView tvDealAvgTitle;
    private TextView tvDealNumberTitle;
    private TextView tvDealAllTitle;
    private TextView tvDealAvg;
    private TextView tvDealNumber;
    private TextView tvDealAll;

    private ShowOrderListModel.ListBean listBean;

    private View headView;
    private DetailAdapter detailAdapter;

    public static Intent createActivity(Context context, ShowOrderListModel.ListBean listBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("listBean", listBean);
        Intent intent = new Intent(context, DealDetailsActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_deal_details);
    }

    @Override
    protected void initView() {
        listBean = (ShowOrderListModel.ListBean) getIntent().getSerializableExtra("listBean");

        mTopBar.setTitle(getResources().getString(R.string.trade_detail));

        detailAdapter = new DetailAdapter();
        detailAdapter.setHeaderFooterEmpty(true, false);
        ryDetails.setLayoutManager(new LinearLayoutManager(this));
        ryDetails.setAdapter(detailAdapter);

        headView = View.inflate(this, R.layout.header_deal_details, null);
        initHeader(headView);
    }

    @Override
    protected DealDetailsPresenter createPresenter(DealDetailsPresenter mPresenter) {
        return new DealDetailsPresenter();
    }

    @Override
    protected void onPresenterCreated(DealDetailsPresenter presenter) {
        super.onPresenterCreated(presenter);
        if (listBean != null) {
            mPresenter.dealDetail(listBean.orderNo);
        }
    }

    @Override
    public void dealDetail(List<DealDetailsModel> list) {
        if (list != null && list.size() > 0) {
            detailAdapter.setNewData(list);
        }
    }

    private void initHeader(View view) {
        tvBuyorSell = view.findViewById(R.id.tv_buyorSell);
        tvCoinsName = view.findViewById(R.id.tv_coins_name);
        tvDealAvgTitle = view.findViewById(R.id.tv_deal_avg_title);
        tvDealNumberTitle = view.findViewById(R.id.tv_deal_number_title);
        tvDealAllTitle = view.findViewById(R.id.tv_deal_all_title);
        tvDealAvg = view.findViewById(R.id.tv_deal_avg);
        tvDealNumber = view.findViewById(R.id.tv_deal_number);
        tvDealAll = view.findViewById(R.id.tv_deal_all);

        if (listBean != null) {
            tvBuyorSell.setText(listBean.getBuyOrSell());
            tvBuyorSell.setTextColor(listBean.getTextColor());
            tvCoinsName.setText(listBean.currencyNameEn + "/" + listBean.baseCurrencyNameEn);

            tvDealAvgTitle.setText(getResources().getString(R.string.transaction_amount) + "(" + listBean.baseCurrencyNameEn + ")");
            tvDealNumberTitle.setText(getResources().getString(R.string.vol) + "(" + listBean.currencyNameEn + ")");
            tvDealAllTitle.setText(getResources().getString(R.string.deal_all_amount) + "(" + listBean.baseCurrencyNameEn + ")");

            tvDealAvg.setText(FormatterUtils.getFormatValue(listBean.averagePrice));
            tvDealNumber.setText(FormatterUtils.getFormatValue(listBean.tradeNum));
            tvDealAll.setText(FormatterUtils.getFormatValue(listBean.dealAmount));
        }
        detailAdapter.addHeaderView(headView);
    }

    private class DetailAdapter extends BaseQuickAdapter<DealDetailsModel, BaseViewHolder> {

        public DetailAdapter() {
            super(R.layout.item_deal_details);
        }

        @Override
        protected void convert(BaseViewHolder helper, DealDetailsModel item) {
            if (listBean != null) {
                helper.setText(R.id.tv_deal_singleprice_title, getResources().getString(R.string.deal_single_price) + "(" + listBean.baseCurrencyNameEn + ")");
                helper.setText(R.id.tv_deal_number_title, getResources().getString(R.string.vol) + "(" + listBean.currencyNameEn + ")");

                if (listBean.buyOrSell == 1) {//买
                    if (!TextUtils.isEmpty(item.buyFeeCurrencyName)) {
                        helper.setText(R.id.tv_fee_title, getResources().getString(R.string.fee) + "(" + item.buyFeeCurrencyName + ")");
                    } else {
                        helper.setText(R.id.tv_fee_title, getResources().getString(R.string.fee) + "(" + listBean.currencyNameEn + ")");
                    }
                } else if (listBean.buyOrSell == 2) {
                    if (!TextUtils.isEmpty(item.sellFeeCurrencyName)) {
                        helper.setText(R.id.tv_fee_title, getResources().getString(R.string.fee) + "(" + item.sellFeeCurrencyName + ")");
                    } else {
                        helper.setText(R.id.tv_fee_title, getResources().getString(R.string.fee) + "(" + listBean.baseCurrencyNameEn + ")");
                    }
                }
            } else {
                helper.setText(R.id.tv_deal_singleprice_title, getResources().getString(R.string.deal_single_price));
                helper.setText(R.id.tv_deal_number_title, getResources().getString(R.string.vol));
                helper.setText(R.id.tv_fee_title, getResources().getString(R.string.fee));
            }

            helper.setText(R.id.tv_date, DateUtils.getStrToStr(item.tradeTime, DateUtils.DEFAULT_PATTERN, DateUtils.FORMAT_DATE_YMDHM));
            helper.setText(R.id.tv_deal_singleprice, FormatterUtils.getFormatValue(item.tradePrice));
            helper.setText(R.id.tv_deal_number, FormatterUtils.getFormatValue(item.tradeNum));
            helper.setText(R.id.tv_fee, FormatterUtils.getFormatValue(item.fee));
        }
    }
}
