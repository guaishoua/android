package com.android.tacu.module.market.view;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.market.contract.NoticeContract;
import com.android.tacu.module.market.model.NoticeModel;
import com.android.tacu.module.market.presenter.NoticePresenter;
import com.android.tacu.module.webview.view.WebviewActivity;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 公告
 */
public class NoticeActivity extends BaseActivity<NoticePresenter> implements NoticeContract.IView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshlayout;

    private int start = 1;
    private final int SIZE = 10;

    private View emptyView;
    private NoticeAdapter adapter;
    private List<NoticeModel> noticeModelList = new ArrayList<>();

    @Override
    protected void setView() {
        setContentView(R.layout.activity_notice);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.news));

        emptyView = View.inflate(this, R.layout.view_empty, null);

        CustomTextHeaderView header = new CustomTextHeaderView(this);
        header.setPrimaryColors(ContextCompat.getColor(this, R.color.content_bg_color), ContextCompat.getColor(this, R.color.text_color));
        refreshlayout.setRefreshHeader(header);
        refreshlayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_default)));
        refreshlayout.setEnableLoadmore(false);
        refreshlayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                start = 1;
                noticeModelList.clear();
                getNoticeList(false);
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getNoticeList(false);
            }
        });

        //添加分割线
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoticeAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (noticeModelList != null && noticeModelList.size() > 0) {
                    jumpTo(WebviewActivity.createActivity(NoticeActivity.this, noticeModelList.get(position).htmlUrl));
                }
            }
        });
    }

    @Override
    protected NoticePresenter createPresenter(NoticePresenter mPresenter) {
        return new NoticePresenter();
    }

    @Override
    protected void onPresenterCreated(NoticePresenter mPresenter) {
        super.onPresenterCreated(mPresenter);
        getNoticeList(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (refreshlayout != null && refreshlayout.isRefreshing()) {
            refreshlayout.finishRefresh();
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

    @Override
    public void showNoticeList(List<NoticeModel> list) {
        if (list != null && list.size() > 0) {
            if (list.size() < SIZE) {
                refreshlayout.setEnableLoadmore(false);
            } else {
                start++;
                refreshlayout.setEnableLoadmore(true);
            }
        }

        if (noticeModelList != null && noticeModelList.size() > 0) {
            noticeModelList.addAll(list);
        } else {
            noticeModelList = list;
        }

        if (noticeModelList != null && noticeModelList.size() > 0) {
            adapter.setNewData(noticeModelList);
        } else {
            adapter.setEmptyView(emptyView);
        }
    }

    private void getNoticeList(boolean isShowLoadingView) {
        mPresenter.getNoticeInfo(start, SIZE, isShowLoadingView);
    }

    public class NoticeAdapter extends BaseQuickAdapter<NoticeModel, BaseViewHolder> {
        public NoticeAdapter() {
            super(R.layout.item_notice);
        }

        @Override
        protected void convert(BaseViewHolder helper, NoticeModel item) {
            helper.setText(R.id.tv_notice_title, "【" + item.type + "】" + item.title);
            helper.setText(R.id.tv_time, item.createTime);
        }
    }
}
