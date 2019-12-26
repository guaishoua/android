package com.android.tacu.module.assets.view;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.assets.contract.NodeManageContract;
import com.android.tacu.module.assets.model.NodeDetailsModel;
import com.android.tacu.module.assets.model.NodeProfitModel;
import com.android.tacu.module.assets.presenter.NodeManagePresenter;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;

/**
 * Created by xiaohong on 2018/8/22.
 */

public class ProfitRecordActivity extends BaseActivity<NodeManagePresenter> implements NodeManageContract.INodeView {

    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private View emptyView;
    private int page = 1;
    private String recordStartTime;
    private String recordEndTime;
    private ProfitRecordAdapter profitRecordAdapter;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_node_list);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.node_profit_record));
        mTopBar.addRightImageButton(R.drawable.icon_canlendar, R.id.qmui_topbar_item_right, 25, 25).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpTo(SelectDateActivity.class, 1);
            }
        });

        emptyView = View.inflate(this, R.layout.view_empty, null);

        CustomTextHeaderView headerView = new CustomTextHeaderView(this);
        headerView.setPrimaryColors(ContextCompat.getColor(this, R.color.content_bg_color_grey), ContextCompat.getColor(this, R.color.text_color));
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setRefreshHeader(headerView);
        refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_blue_2)));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                upLoad(false);
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (isAvailable()) {
                    page++;
                    upLoad(false);
                } else {
                    showToastError(getResources().getString(R.string.net_unavailable));
                }
            }
        });

        profitRecordAdapter = new ProfitRecordAdapter();
        recyclerView.setAdapter(profitRecordAdapter);
    }

    @Override
    protected NodeManagePresenter createPresenter(NodeManagePresenter mPresenter) {
        return new NodeManagePresenter();
    }

    @Override
    protected void onPresenterCreated(NodeManagePresenter presenter) {
        super.onPresenterCreated(presenter);
        upLoad(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == SelectDateActivity.SELECT_TIME) {
                recordStartTime = data.getStringExtra("startTime");
                recordEndTime = data.getStringExtra("endTime");
                profitRecordAdapter.getData().clear();
                page = 1;
                upLoad(true);
            }
        }
    }

    @Override
    public void hideRefreshView() {
        super.hideRefreshView();
        if (refreshLayout != null && (refreshLayout.isRefreshing() || refreshLayout.isLoading())) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }

    @Override
    public void profitList(NodeProfitModel model) {
        if (page == 1) {
            profitRecordAdapter.setNewData(null);
        }

        if (model != null) {
            if (model.total == 0) {
                profitRecordAdapter.setEmptyView(emptyView);
                profitRecordAdapter.notifyDataSetChanged();
            } else if (model.list != null && model.list.size() == 0) {
                refreshLayout.setEnableLoadmore(false);
            } else if (model.list != null && model.total > 10) {
                refreshLayout.setEnableLoadmore(true);
                profitRecordAdapter.addData(model.list);
            } else if (model.list != null && model.total <= 10) {
                refreshLayout.setEnableLoadmore(false);
                profitRecordAdapter.setNewData(model.list);
            }
        }
    }

    @Override
    public void detailsList(NodeDetailsModel model) {
    }

    private void upLoad(boolean isShowLoadingView) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(c.getTime().getTime() - 1000 * 60 * 60 * 24));
        if (recordEndTime != null && recordStartTime != null) {
            mPresenter.getProfitList(recordStartTime, recordEndTime, page, isShowLoadingView);
        } else {
            mPresenter.getProfitList(String.valueOf(sDateFormat.format(c.getTime())), String.valueOf(sDateFormat.format(new Date())), page, isShowLoadingView);
        }
    }

    private class ProfitRecordAdapter extends BaseQuickAdapter<NodeProfitModel.NodeTotalModel, BaseViewHolder> {

        public ProfitRecordAdapter() {
            super(R.layout.item_profit_record);
        }

        @Override
        protected void convert(BaseViewHolder helper, NodeProfitModel.NodeTotalModel item) {
            helper.setText(R.id.tv_create, item.putProfitTime.substring(0, 10));
            helper.setText(R.id.tv_state_profit, item.nodeState());
            helper.setText(R.id.tv_one_profit, BigDecimal.valueOf(item.oneProfit).toPlainString());
            helper.setText(R.id.tv_profit_number, item.profitNodeNum);
            helper.setText(R.id.tv_total_profit, BigDecimal.valueOf(item.allProfit).toPlainString());
        }
    }
}
