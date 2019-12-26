package com.android.tacu.module.assets.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.assets.contract.NodeManageContract;
import com.android.tacu.module.assets.model.NodeDetailsModel;
import com.android.tacu.module.assets.model.NodeProfitModel;
import com.android.tacu.module.assets.presenter.NodeManagePresenter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.math.BigDecimal;

import butterknife.BindView;

/**
 * Created by xiaohong on 2018/8/22.
 */

public class NodeDetailsActivity extends BaseActivity<NodeManagePresenter> implements NodeManageContract.INodeView{
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshLayout;

    private String nodeId;
    private NodeDetailsAdapter nodeDetailsAdapter;

    public static Intent crateActivity(Context context,String nodeId){
        Intent intent = new Intent(context, NodeDetailsActivity.class);
        intent.putExtra("nodeId", nodeId);
        return intent;
    }
    @Override
    protected void setView() {
        setContentView(R.layout.activity_node_list);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.node_details));

        nodeId = getIntent().getStringExtra("nodeId");

        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableRefresh(false);

        nodeDetailsAdapter = new NodeDetailsAdapter();
        //添加分割线
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(nodeDetailsAdapter);
    }

    @Override
    protected NodeManagePresenter createPresenter(NodeManagePresenter mPresenter) {
        return new NodeManagePresenter();
    }

    @Override
    protected void onPresenterCreated(NodeManagePresenter presenter) {
        super.onPresenterCreated(presenter);
        upLoad();
    }

    private void upLoad() {
        mPresenter.getNodeListByNodeIds(nodeId);
    }

    @Override
    public void profitList(NodeProfitModel model) {

    }

    @Override
    public void detailsList(NodeDetailsModel model) {
        if (model.attachment != null && model.attachment.size() > 0) {
            nodeDetailsAdapter.setNewData(model.attachment);
        }
    }

    private class NodeDetailsAdapter extends BaseQuickAdapter<NodeDetailsModel.DetailsModel, BaseViewHolder> {

        public NodeDetailsAdapter() {
            super(R.layout.item_node_details);
        }

        @Override
        protected void convert(BaseViewHolder helper, final NodeDetailsModel.DetailsModel item) {
            helper.setText(R.id.tv_node_state, item.nodeState());
            helper.setText(R.id.tv_lock_time, item.dayNum);
            helper.setText(R.id.tv_total_profit, BigDecimal.valueOf(item.allProfit).toPlainString());
            helper.setText(R.id.tv_unlock_date, item.unLockDate());
        }
    }
}
