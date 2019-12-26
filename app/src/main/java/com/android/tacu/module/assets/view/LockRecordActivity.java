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
import com.android.tacu.module.assets.contract.LockRecordContract;
import com.android.tacu.module.assets.model.LockChexListModel;
import com.android.tacu.module.assets.presenter.LockRecordPresenter;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import butterknife.BindView;

public class LockRecordActivity extends BaseActivity<LockRecordPresenter> implements LockRecordContract.IView {

    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private final int RECORDREQUESTCODE = 1001;
    private View emptyView;

    private int page = 1;
    private String startTime;
    private String endTime;

    private LockAdapter lockAdapter;
    private LockChexListModel lockChexListModel;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_lock_record);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.lock_record));
        mTopBar.addRightImageButton(R.drawable.icon_canlendar, R.id.qmui_topbar_item_right, 25, 25).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpTo(SelectDateActivity.class, RECORDREQUESTCODE);
            }
        });

        emptyView = View.inflate(this, R.layout.view_empty, null);

        CustomTextHeaderView headerView = new CustomTextHeaderView(this);
        headerView.setPrimaryColors(ContextCompat.getColor(this, R.color.content_bg_color), ContextCompat.getColor(this, R.color.text_color));
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setRefreshHeader(headerView);
        refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_blue_2)));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                upLoad();
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page++;
                upLoad();
            }
        });

        lockAdapter = new LockAdapter();
        recyclerView.setAdapter(lockAdapter);
    }

    @Override
    protected LockRecordPresenter createPresenter(LockRecordPresenter mPresenter) {
        return new LockRecordPresenter();
    }

    @Override
    protected void onPresenterCreated(LockRecordPresenter presenter) {
        super.onPresenterCreated(presenter);
        upLoad();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == RECORDREQUESTCODE && resultCode == SelectDateActivity.SELECT_TIME) {
                startTime = data.getStringExtra("startTime");
                endTime = data.getStringExtra("endTime");
                page = 1;
                upLoad();
            }
        }
    }

    @Override
    public void setLockChexListSuccess(LockChexListModel model) {
        long total = 0;
        if (model != null) {
            total = model.total;
        }
        if (lockChexListModel != null && lockChexListModel.list != null && lockChexListModel.list.size() > 0) {
            lockChexListModel.list.addAll(model.list);
        } else {
            lockChexListModel = model;
        }
        if (lockChexListModel != null && lockChexListModel.list != null && lockChexListModel.list.size() > 0) {
            lockAdapter.setNewData(lockChexListModel.list);
        } else {
            lockAdapter.setNewData(null);
            lockAdapter.setEmptyView(emptyView);
        }

        if (lockChexListModel == null || lockChexListModel.list == null || lockChexListModel.list.size() >= total) {
            refreshLayout.setEnableLoadmore(false);
        } else {
            refreshLayout.setEnableLoadmore(true);
        }
    }

    @Override
    public void setLockChexListError() {
        refreshLayout.setEnableLoadmore(true);
        page--;
    }

    private void upLoad() {
        if (page == 1 && lockChexListModel != null && lockChexListModel.list != null && lockChexListModel.list.size() > 0) {
            lockChexListModel.list.clear();
        }
        if (startTime != null && endTime != null) {
            mPresenter.getLockChexList(startTime, endTime, page);
        } else {
            mPresenter.getLockChexList(null, null, page);
        }
    }

    private class LockAdapter extends BaseQuickAdapter<LockChexListModel.LockModel, BaseViewHolder> {

        public LockAdapter() {
            super(R.layout.item_lock_record);
        }

        @Override
        protected void convert(BaseViewHolder helper, LockChexListModel.LockModel item) {
            switch (item.status) {
                case 1:
                    helper.setText(R.id.tv_profit, getResources().getString(R.string.profit) + "：" + item.profit + "CHEX");
                    helper.setText(R.id.tv_profit_status, getResources().getString(R.string.locking_chex));
                    break;
                case 2:
                    helper.setText(R.id.tv_profit, getResources().getString(R.string.profit) + "：" + item.historyProfit + "CHEX");
                    helper.setText(R.id.tv_profit_status, getResources().getString(R.string.locked_chex));
                    break;
            }
            helper.setText(R.id.tv_lock_chex_num, String.valueOf(Integer.parseInt(item.num) / 10000));
            helper.setText(R.id.tv_year_rate, item.rate + "%");
            switch (item.dateVal) {
                case 1:
                    helper.setText(R.id.tv_lock_cycle, getResources().getString(R.string.month_6));
                    break;
                case 2:
                    helper.setText(R.id.tv_lock_cycle, getResources().getString(R.string.month_12));
                    break;
            }
            helper.setText(R.id.tv_lock_time, item.createTime);
            helper.setText(R.id.tv_end_time, item.lockTime);
        }
    }
}
