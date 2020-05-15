package com.android.tacu.module.auction.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.auction.adapter.AuctionRecordAdapter;
import com.android.tacu.module.auction.contract.AuctionContract;
import com.android.tacu.module.auction.model.AuctionLogsListModel;
import com.android.tacu.module.auction.model.AuctionLogsModel;
import com.android.tacu.module.auction.presenter.AuctionPresenter;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class AuctionRecordActivity extends BaseActivity<AuctionPresenter> implements AuctionContract.IRecordView {

    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshlayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private AuctionRecordAdapter recordAdapter;
    private View emptyView;

    private Integer id;

    private int page = 1;
    private int size = 10;

    private List<AuctionLogsModel> recordList = new ArrayList<>();

    public static Intent createActivity(Context context, Integer id) {
        Intent intent = new Intent(context, AuctionRecordActivity.class);
        intent.putExtra("id", id);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_auction_record);
    }

    @Override
    protected void initView() {
        id = getIntent().getIntExtra("id", 0);

        mTopBar.setTitle(getResources().getString(R.string.auction_record));

        CustomTextHeaderView header = new CustomTextHeaderView(this);
        header.setPrimaryColors(ContextCompat.getColor(this, R.color.content_bg_color), ContextCompat.getColor(this, R.color.text_color));
        refreshlayout.setRefreshHeader(header);
        refreshlayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_default)));
        refreshlayout.setEnableLoadmore(false);
        refreshlayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                upload();
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                upload();
            }
        });

        recordAdapter = new AuctionRecordAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_recyclerview_divider_dp10));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(recordAdapter);

        emptyView = View.inflate(this, R.layout.view_empty, null);
    }

    @Override
    protected AuctionPresenter createPresenter(AuctionPresenter mPresenter) {
        return new AuctionPresenter();
    }

    @Override
    protected void onPresenterCreated(AuctionPresenter presenter) {
        super.onPresenterCreated(presenter);

        upload();
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
    public void auctionLogsAll(AuctionLogsListModel model) {
        if (page == 1 && recordList != null && recordList.size() > 0) {
            recordList.clear();
        }
        if (model != null && model.list != null && model.list.size() > 0) {
            recordList.addAll(model.list);

            recordAdapter.setNewData(recordList);
            if (recordList.size() >= model.total) {
                refreshlayout.setEnableLoadmore(false);
            } else {
                page++;
                refreshlayout.setEnableLoadmore(true);
            }
        } else if (page == 1) {
            recordAdapter.setNewData(null);
            recordAdapter.setEmptyView(emptyView);
            refreshlayout.setEnableLoadmore(false);
        }
    }

    private void upload() {
        mPresenter.auctionLogsAll(2, id, page, size);
    }
}
