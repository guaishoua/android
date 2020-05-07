package com.android.tacu.module.my.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.my.contract.InvitedinfoContract;
import com.android.tacu.module.my.model.InvitedAllDetailModel;
import com.android.tacu.module.my.presenter.InvitedinfoPresenter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import butterknife.BindView;

public class InvitedRecordFragment extends BaseFragment<InvitedinfoPresenter> implements InvitedinfoContract.IRecordChildView {

    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshlayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private int page = 1;
    private int size = 10;

    private int status;//1 已实名   0 未实名
    private RecordAdapter recordAdapter;
    private InvitedAllDetailModel detailModel;

    private View emptyView;

    public static InvitedRecordFragment newInstance(int status) {
        Bundle bundle = new Bundle();
        bundle.putInt("status", status);
        InvitedRecordFragment fragment = new InvitedRecordFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            status = bundle.getInt("status");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_invited_record;
    }

    @Override
    protected void initData(View view) {
    }

    @Override
    protected InvitedinfoPresenter createPresenter(InvitedinfoPresenter mPresenter) {
        return new InvitedinfoPresenter();
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();

        emptyView = View.inflate(getContext(), R.layout.view_empty, null);

        MaterialHeader header = new MaterialHeader(getContext());
        header.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.color_default));
        refreshlayout.setRefreshHeader(header);
        refreshlayout.setRefreshFooter(new BallPulseFooter(getContext()).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(getContext(), R.color.color_default)));
        refreshlayout.setEnableLoadmore(false);
        refreshlayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                upload(false);
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                upload(false);
            }
        });

        recordAdapter = new RecordAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.item_recyclerview_divider));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(recordAdapter);

        upload(true);
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
    public void showInvitedInfo(InvitedAllDetailModel model) {
        if (detailModel != null && detailModel.list != null && detailModel.list.size() > 0) {
            if (model == null || model.list == null || model.list.size() <= 0) {
                detailModel.total = model.total;
                detailModel.list.addAll(model.list);
            }
        } else {
            detailModel = model;
        }

        if (detailModel != null && detailModel.list != null && detailModel.list.size() > 0) {
            recordAdapter.setNewData(model.list);
        } else {
            recordAdapter.setNewData(null);
            recordAdapter.setEmptyView(emptyView);
        }

        if (detailModel == null || detailModel.list == null || detailModel.list.size() >= model.total) {
            refreshlayout.setEnableLoadmore(false);
        } else {
            refreshlayout.setEnableLoadmore(true);
        }
    }

    private void upload(boolean isShow) {
        mPresenter.getInvitedAllDetail(isShow, page, size, status);
    }

    public class RecordAdapter extends BaseQuickAdapter<InvitedAllDetailModel.InvitedRecordModel, BaseViewHolder> {

        public RecordAdapter() {
            super(R.layout.item_invited_record);
        }

        @Override
        protected void convert(BaseViewHolder holder, InvitedAllDetailModel.InvitedRecordModel item) {
            holder.setText(R.id.tv_account, item.name);
            holder.setText(R.id.tv_uid, item.uid);
            holder.setText(R.id.tv_time, item.createTime);

            //1 已实名 0 未实名
            if (status == 1) {
                holder.setText(R.id.tv_account, item.name);
                holder.setText(R.id.tv_realname, getResources().getString(R.string.real_name));
                holder.setTextColor(R.id.tv_realname, ContextCompat.getColor(getContext(), R.color.text_color));
                holder.setGone(R.id.img_realname, false);
            } else if (status == 0) {
                holder.setText(R.id.tv_account, item.name);
                holder.setText(R.id.tv_realname, getResources().getString(R.string.not_real_name));
                holder.setTextColor(R.id.tv_realname, ContextCompat.getColor(getContext(), R.color.color_otc_sell));
                holder.setGone(R.id.img_realname, true);
            }
        }
    }
}
