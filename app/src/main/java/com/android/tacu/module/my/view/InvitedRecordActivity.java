package com.android.tacu.module.my.view;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.my.contract.InvitedinfoContract;
import com.android.tacu.module.my.model.InvitedAllModel;
import com.android.tacu.module.my.presenter.InvitedinfoPresenter;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import butterknife.BindView;

/**
 * Created by xiaohong on 2018/9/26.
 */

public class InvitedRecordActivity extends BaseActivity<InvitedinfoPresenter> implements InvitedinfoContract.IView {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshlayout;

    private int size = 10;
    private int page = 1;
    private View empeyView;
    private InvitedAdapter adapter;

    @Override
    protected InvitedinfoPresenter createPresenter(InvitedinfoPresenter mPresenter) {
        return new InvitedinfoPresenter();
    }

    @Override
    protected void onPresenterCreated(InvitedinfoPresenter presenter) {
        super.onPresenterCreated(presenter);
        upLoad(true);
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_invited_record);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.invited_record));
        empeyView = View.inflate(this, R.layout.view_empty, null);

        CustomTextHeaderView headerView = new CustomTextHeaderView(this);
        headerView.setPrimaryColors(ContextCompat.getColor(this, R.color.content_bg_color_grey), ContextCompat.getColor(this, R.color.text_color));
        refreshlayout.setRefreshHeader(headerView);
        refreshlayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_blue_2)));
        refreshlayout.setEnableLoadmore(false);
        refreshlayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                upLoad(false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                upLoad(true);
            }
        });

        adapter = new InvitedAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void upLoad(boolean isRefresh) {
        if (isRefresh) {
            page = 1;
        } else {
            page++;
        }
        mPresenter.getInvitedInfo(page, size);
    }

    @Override
    public void showInvitedInfo(InvitedAllModel model) {
        if (page == 1) {
            adapter.setNewData(null);
        }
        if (model != null) {
            if (model.total == 0) {
                adapter.setEmptyView(empeyView);
                adapter.setNewData(null);
                adapter.notifyDataSetChanged();
            } else if (model.list != null && model.list.size() == 0) {
                refreshlayout.setEnableLoadmore(false);
            } else if (model.list != null && model.total <= 10) {
                adapter.setNewData(model.list);
                refreshlayout.setEnableLoadmore(false);
            } else if (model.total > 10 && model.list != null) {
                adapter.addData(model.list);
                refreshlayout.setEnableLoadmore(true);
            }
        }
    }

    @Override
    public void hideRefreshView() {
        super.hideRefreshView();
        if (refreshlayout != null && (refreshlayout.isRefreshing() || refreshlayout.isLoading())) {
            refreshlayout.finishRefresh();
            refreshlayout.finishLoadmore();
        }
    }

    private class InvitedAdapter extends BaseQuickAdapter<InvitedAllModel.InvitedRecordModel, BaseViewHolder> {

        public InvitedAdapter() {
            super(R.layout.item_invited);
        }

        @Override
        protected void convert(BaseViewHolder helper, InvitedAllModel.InvitedRecordModel item) {
            helper.setText(R.id.tv_item_one, item.uid);
            helper.setText(R.id.tv_item_three, item.createTime);

            if (TextUtils.equals(item.source, "1")) {
                helper.setText(R.id.tv_item_two, "Web");
            } else if (TextUtils.equals(item.source, "3")) {
                helper.setText(R.id.tv_item_two, "Android");
            } else if (TextUtils.equals(item.source, "4")) {
                helper.setText(R.id.tv_item_two, "IOS");
            } else {
                helper.setText(R.id.tv_item_two, "");
            }
        }
    }
}
