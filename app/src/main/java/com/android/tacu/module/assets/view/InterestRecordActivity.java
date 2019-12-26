package com.android.tacu.module.assets.view;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.assets.contract.InterestRecordContract;
import com.android.tacu.module.assets.model.UnclaimedListModel;
import com.android.tacu.module.assets.model.UnclaimedModel;
import com.android.tacu.module.assets.presenter.InterestRecordPresenter;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import butterknife.BindView;

public class InterestRecordActivity extends BaseActivity<InterestRecordPresenter> implements InterestRecordContract.IView {

    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private TextView tv_interest_history;

    private final int RECORDREQUESTCODE = 1001;
    private View emptyView;
    private View headView;

    private int page = 1;
    private String startTime;
    private String endTime;

    private RecordAdapter recordAdapter;
    private UnclaimedListModel unclaimedListModel;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_interest_record);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.interest_record));
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

        recordAdapter = new RecordAdapter();
        recyclerView.setAdapter(recordAdapter);

        headView = View.inflate(this, R.layout.header_interest_record, null);
        tv_interest_history = headView.findViewById(R.id.tv_interest_history);
        recordAdapter.addHeaderView(headView);
    }

    @Override
    protected InterestRecordPresenter createPresenter(InterestRecordPresenter mPresenter) {
        return new InterestRecordPresenter();
    }

    @Override
    protected void onPresenterCreated(InterestRecordPresenter presenter) {
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
    public void setShanxibaoListSuccess(UnclaimedListModel model) {
        long total = 0;
        if (model != null) {
            total = model.total;
            tv_interest_history.setText(FormatterUtils.getFormatValue(model.rewardAll) + "CHEX");
        }
        if (unclaimedListModel != null && unclaimedListModel.list != null && unclaimedListModel.list.size() > 0) {
            unclaimedListModel.list.addAll(model.list);
        } else {
            unclaimedListModel = model;
        }
        if (unclaimedListModel != null && unclaimedListModel.list != null && unclaimedListModel.list.size() > 0) {
            recordAdapter.setNewData(unclaimedListModel.list);
        } else {
            recordAdapter.setNewData(null);
            recordAdapter.setEmptyView(emptyView);
        }

        if (unclaimedListModel == null || unclaimedListModel.list == null || unclaimedListModel.list.size() >= total) {
            refreshLayout.setEnableLoadmore(false);
        } else {
            refreshLayout.setEnableLoadmore(true);
        }
    }

    @Override
    public void setShanxibaoListError() {
        refreshLayout.setEnableLoadmore(true);
        page--;
    }

    private void upLoad() {
        if (page == 1 && unclaimedListModel != null && unclaimedListModel.list != null && unclaimedListModel.list.size() > 0) {
            unclaimedListModel.list.clear();
        }
        if (startTime != null && endTime != null) {
            mPresenter.getShanxibaoList(startTime, endTime, page);
        } else {
            mPresenter.getShanxibaoList("", "", page);
        }
    }

    private class RecordAdapter extends BaseQuickAdapter<UnclaimedModel, BaseViewHolder> {

        public RecordAdapter() {
            super(R.layout.item_interest_record);
        }

        @Override
        protected void convert(BaseViewHolder helper, UnclaimedModel item) {
            helper.setText(R.id.tv_reward_time, item.ds);
            switch (item.status) {
                case 1:
                    helper.setText(R.id.tv_reward_status, getResources().getString(R.string.nohave_received));
                    break;
                case 2:
                    helper.setText(R.id.tv_reward_status, getResources().getString(R.string.have_received));
                    break;
            }
            helper.setText(R.id.tv_asset_title, getResources().getString(R.string.assets) + "(USDT)");
            helper.setText(R.id.tv_asset, FormatterUtils.getFormatValue(item.amount));
            helper.setText(R.id.tv_intentest_title, getResources().getString(R.string.interest) + "(CHEX)");
            helper.setText(R.id.tv_intentest, FormatterUtils.getFormatValue(item.reward));
        }
    }
}
