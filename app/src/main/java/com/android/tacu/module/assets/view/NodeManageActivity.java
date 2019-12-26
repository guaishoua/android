package com.android.tacu.module.assets.view;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.assets.contract.NodeManageContract;
import com.android.tacu.module.assets.model.NodeListModel;
import com.android.tacu.module.assets.presenter.NodeManagePresenter;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.math.BigDecimal;

import butterknife.BindView;

/**
 * Created by xiaohong on 2018/8/22.
 */

public class NodeManageActivity extends BaseActivity<NodeManagePresenter> implements NodeManageContract.INodeManageView {

    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshlayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_all_profit)
    TextView tv_all_profit;

    private int page = 1;
    private View emptyView;
    //isFlag: false代表申请节点 true代表解锁节点
    private boolean isFlag = true;
    private NodeAdapter nodeAdapter;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_node_manage);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.node_manage));
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addRightTextButton(getResources().getString(R.string.node_profit_record), R.id.qmui_topbar_item_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpTo(ProfitRecordActivity.class);
            }
        });

        emptyView = View.inflate(this, R.layout.view_empty, null);
        CustomTextHeaderView headerView = new CustomTextHeaderView(this);
        headerView.setPrimaryColors(ContextCompat.getColor(this, R.color.content_bg_color_grey), ContextCompat.getColor(this, R.color.text_color));
        refreshlayout.setRefreshHeader(headerView);
        refreshlayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_blue_2)));
        refreshlayout.setEnableLoadmore(false);
        refreshlayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (isAvailable()) {
                    page++;
                    upload(false);
                } else {
                    showToastError(getResources().getString(R.string.net_unavailable));
                }
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                upload(false);
                mPresenter.getNodePrifit(false);
            }
        });

        nodeAdapter = new NodeAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(nodeAdapter);
    }

    @Override
    protected NodeManagePresenter createPresenter(NodeManagePresenter mPresenter) {
        return new NodeManagePresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.getNodePrifit(true);
        page = 1;
        upload(true);
    }

    @Override
    public void hideRefreshView() {
        super.hideRefreshView();
        if (refreshlayout != null && (refreshlayout.isRefreshing() || refreshlayout.isLoading())) {
            refreshlayout.finishRefresh();
            refreshlayout.finishLoadmore(1000);
        }
    }

    @Override
    public void setNodeList(NodeListModel node) {
        if (node != null) {
            if (page == 1) {
                nodeAdapter.setNewData(null);
            }
            if (node.total == 0) {
                nodeAdapter.setEmptyView(emptyView);
                nodeAdapter.setNewData(null);
                nodeAdapter.notifyDataSetChanged();
            } else if (node.list != null && node.list.size() == 0) {
                refreshlayout.setEnableLoadmore(false);
            } else if (node.list != null && node.total <= 10) {
                refreshlayout.setEnableLoadmore(false);
                nodeAdapter.setNewData(node.list);
            } else if (node.list != null && node.total > 10) {
                refreshlayout.setEnableLoadmore(true);
                nodeAdapter.addData(node.list);
            }
        }
    }

    @Override
    public void getMessage(double chexAmount, double allProfit, double yesterdayProfit) {
        if (allProfit == 0.0d) {
            tv_all_profit.setText("0.0 CHEX");
        } else {
            tv_all_profit.setText(BigDecimal.valueOf(allProfit).setScale(8, BigDecimal.ROUND_DOWN).toPlainString() + " CHEX");
        }
    }

    private void upload(boolean showLoadding) {
        mPresenter.getNodeList(page, showLoadding);
    }

    private class NodeAdapter extends BaseQuickAdapter<NodeListModel.NodeModel, BaseViewHolder> {

        public NodeAdapter() {
            super(R.layout.item_node_manage);
        }

        @Override
        protected void convert(BaseViewHolder helper, final NodeListModel.NodeModel item) {
            helper.setText(R.id.tv_create, item.lockTime);
            helper.setText(R.id.tv_node_number, item.allNodeNum);
            helper.setText(R.id.tv_unlock_num, item.lockNodeNum);
            helper.setText(R.id.tv_total_profit, BigDecimal.valueOf(item.allProfit).toPlainString() + "CHEX");
            helper.setText(R.id.tv_yesterday_profit, BigDecimal.valueOf(item.yesterdayProfit).toPlainString());
            helper.setText(R.id.tv_create_profit, item.profitTime.substring(0, 10));

            helper.setOnClickListener(R.id.ll_layout, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpTo(NodeDetailsActivity.crateActivity(NodeManageActivity.this, item.nodeIds));
                }
            });
        }
    }
}
