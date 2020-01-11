package com.android.tacu.module.auctionplus.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.auctionplus.adapter.AuctionPlusOfferPriceAdapter;
import com.android.tacu.module.auctionplus.contract.AuctionPlusContract;
import com.android.tacu.module.auctionplus.modal.AuctionPlusListByIdModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusOfferPriceModel;
import com.android.tacu.module.auctionplus.presenter.AuctionPlusPresent;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class AuctionPlusOfferActivity extends BaseActivity<AuctionPlusPresent> implements AuctionPlusContract.IOfferView {

    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshlayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private String id;
    private int start = 1;

    private AuctionPlusOfferPriceAdapter offerPriceAdapter;
    private List<AuctionPlusOfferPriceModel> offerPriceModelList = new ArrayList<>();

    public static Intent createActivity(Context context, String id) {
        Intent intent = new Intent(context, AuctionPlusOfferActivity.class);
        intent.putExtra("id", id);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_auctionplus_offer);
    }

    @Override
    protected void initView() {
        id = getIntent().getStringExtra("id");

        mTopBar.setTitle(getString(R.string.auction_goprice_record));
        CustomTextHeaderView header = new CustomTextHeaderView(this);
        header.setPrimaryColors(ContextCompat.getColor(this, R.color.content_bg_color), ContextCompat.getColor(this, R.color.text_color));
        refreshlayout.setRefreshHeader(header);
        refreshlayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_default)));
        refreshlayout.setEnableLoadmore(false);
        refreshlayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                start = 1;
                upLoad(false);
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                upLoad(false);
            }
        });

        offerPriceAdapter = new AuctionPlusOfferPriceAdapter();
        offerPriceAdapter.setHeaderFooterEmpty(true, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(offerPriceAdapter);

        View headView = View.inflate(this, R.layout.header_auctionplus_offer, null);
        offerPriceAdapter.addHeaderView(headView);
    }

    @Override
    protected AuctionPlusPresent createPresenter(AuctionPlusPresent mPresenter) {
        return new AuctionPlusPresent();
    }

    @Override
    protected void onPresenterCreated(AuctionPlusPresent presenter) {
        super.onPresenterCreated(presenter);
        upLoad(true);
    }

    @Override
    public void hideRefreshView() {
        super.hideRefreshView();
        if (refreshlayout != null && (refreshlayout.isRefreshing() || refreshlayout.isLoading())) {
            refreshlayout.finishRefresh();
            refreshlayout.finishLoadmore();
        }
    }

    @Override
    public void listPlusById(AuctionPlusListByIdModel model) {
        if (model != null && model.arr != null && model.arr.list != null && model.arr.list.size() > 0) {
            if (offerPriceModelList != null && offerPriceModelList.size() > 0) {
                offerPriceModelList.addAll(model.arr.list);
            } else {
                offerPriceModelList = model.arr.list;
            }
            if (offerPriceModelList != null && offerPriceModelList.size() > 0) {
                offerPriceAdapter.setAuctionPlusListByIdModel(model);
                offerPriceAdapter.setNewData(offerPriceModelList);
                if (offerPriceModelList.size() >= model.arr.total) {
                    refreshlayout.setEnableLoadmore(false);
                } else {
                    start++;
                    refreshlayout.setEnableLoadmore(true);
                }
            }
        }
    }

    private void upLoad(boolean isShowLoadingView) {
        if (start == 1 && offerPriceModelList != null && offerPriceModelList.size() > 0) {
            offerPriceModelList.clear();
        }
        mPresenter.listPlusById(start, 20, id, 2, isShowLoadingView);
    }
}
