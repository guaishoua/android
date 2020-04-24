package com.android.tacu.module.payinfo.view;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.payinfo.contract.PayInfoContract;
import com.android.tacu.module.payinfo.model.PayInfoModel;
import com.android.tacu.module.payinfo.presenter.PayInfoPresenter;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.user.UserManageUtils;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class PayInfoListActivity extends BaseActivity<PayInfoPresenter> implements PayInfoContract.IView {

    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshManage;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    //true代表认证商户，列表有按钮
    private boolean isShowCb = false;
    private boolean isFirst = true;
    private List<PayInfoModel> payInfoModelList = new ArrayList<>();
    private PayInfoAdapter payInfoAdapter;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_pay_info_list);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.get_setting));
        mTopBar.addRightImageButton(R.drawable.icon_add_white, R.id.qmui_topbar_item_right, 18, 18).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowCb) {
                    jumpTo(PayInfoTypeActivity.class);
                } else {
                    boolean isYHK = false, isWX = false, isZFB = false;
                    if (payInfoModelList != null && payInfoModelList.size() > 0) {
                        for (int i = 0; i < payInfoModelList.size(); i++) {
                            if (payInfoModelList.get(i).type == 1) {
                                isYHK = true;
                            }
                            if (payInfoModelList.get(i).type == 2) {
                                isWX = true;
                            }
                            if (payInfoModelList.get(i).type == 3) {
                                isZFB = true;
                            }
                        }
                    }
                    jumpTo(PayInfoTypeActivity.createActivity(PayInfoListActivity.this, isYHK, isWX, isZFB));
                }
            }
        });

        isShowCb = spUtil.getApplyAuthMerchantStatus() == 2;

        CustomTextHeaderView header = new CustomTextHeaderView(this);
        header.setPrimaryColors(ContextCompat.getColor(this, R.color.content_bg_color), ContextCompat.getColor(this, R.color.text_color));
        refreshManage.setRefreshHeader(header);
        refreshManage.setEnableLoadmore(false);
        refreshManage.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                upload();
            }
        });

        payInfoAdapter = new PayInfoAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_recyclerview_divider_dp10));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(payInfoAdapter);
    }

    @Override
    protected PayInfoPresenter createPresenter(PayInfoPresenter mPresenter) {
        return new PayInfoPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        upload();
    }

    @Override
    public void hideRefreshView() {
        super.hideRefreshView();
        if (refreshManage != null && (refreshManage.isRefreshing() || refreshManage.isLoading())) {
            refreshManage.finishRefresh();
            refreshManage.finishLoadmore();
        }
    }

    @Override
    public void selectBank(List<PayInfoModel> list) {
        this.payInfoModelList = list;
        UserManageUtils.setPeoplePayInfo(list);
        payInfoAdapter.setNewData(list);
    }

    private void upload() {
        mPresenter.selectBank(isFirst);
        if (isFirst) {
            isFirst = false;
        }
    }

    public class PayInfoAdapter extends BaseQuickAdapter<PayInfoModel, BaseViewHolder> {

        public PayInfoAdapter() {
            super(R.layout.item_pay_info);
        }

        @Override
        protected void convert(final BaseViewHolder holder, final PayInfoModel item) {
            if (item.type == 1) {
                holder.setBackgroundRes(R.id.img_pay, R.mipmap.img_yhk);
                holder.setText(R.id.tv_name, item.bankName);
                holder.setText(R.id.tv_no, CommonUtils.hideCardNo(item.bankCard));
            } else if (item.type == 2) {
                holder.setBackgroundRes(R.id.img_pay, R.mipmap.img_wx);
                holder.setText(R.id.tv_name, getResources().getString(R.string.weixin));
                holder.setText(R.id.tv_no, CommonUtils.hidePhoneNo(item.weChatNo));
            } else if (item.type == 3) {
                holder.setBackgroundRes(R.id.img_pay, R.mipmap.img_zfb);
                holder.setText(R.id.tv_name, getResources().getString(R.string.zhifubao));
                holder.setText(R.id.tv_no, CommonUtils.hidePhoneNo(item.aliPayNo));
            }
            holder.setGone(R.id.cb, isShowCb);
            holder.setOnClickListener(R.id.cb, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CheckBox) holder.getView(R.id.cb)).toggle();


                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.type == 1) {
                        jumpTo(BindingInfoYhkActivity.createActivity(PayInfoListActivity.this, item));
                    } else if (item.type == 2) {
                        jumpTo(BindingInfoWxActivity.createActivity(PayInfoListActivity.this, item));
                    } else if (item.type == 3) {
                        jumpTo(BindingInfoZfbActivity.createActivity(PayInfoListActivity.this, item));
                    }
                }
            });
        }
    }
}
