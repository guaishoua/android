package com.android.tacu.module.auctionplus.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.model.AuctionPlusListVisibleHintEvent;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.assets.model.AmountModel;
import com.android.tacu.module.assets.view.AssetsActivity;
import com.android.tacu.module.auctionplus.adapter.AuctionPlusListAdapter;
import com.android.tacu.module.auctionplus.contract.AuctionPlusContract;
import com.android.tacu.module.auctionplus.dialog.AuctionPlusPayDialogUtils;
import com.android.tacu.module.auctionplus.modal.AuctionPayStatusModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusDataBaseModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusDataModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusListModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusPayInfoModel;
import com.android.tacu.module.auctionplus.presenter.AuctionPlusPresent;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.android.tacu.view.smartrefreshlayout.FooterUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by jiazhen on 2019/6/3.
 */
public class AuctionPlusFragment extends BaseFragment<AuctionPlusPresent> implements AuctionPlusContract.IListView {

    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshlayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_index)
    TextView tvIndex;

    private final long LIST_TIME = 10 * 1000;
    private final long DATA_TIME = 1000;
    private int size = 6;

    private int status = 0;//0=全部 1=进行中 2=未开始 3=已结束 4=待支付 5=收藏
    private int currentPage = 1;//分页的当前页
    private boolean isList = false;
    private boolean isData = false;

    private boolean isAuctionPlusListVisToUser = true;

    private AuctionPlusListAdapter auctionPlusListAdapter;
    private List<AuctionPlusModel> auctionList = new ArrayList<>();
    private String idString;
    private String idPayString;
    private AuctionPlusModel auctionPlusModel;
    private AuctionPlusDataModel auctionPlusDataModel;

    private String oldCurrentCount;
    private String newCurrentCount;

    private DroidDialog dialog;
    private AlertDialog alertDialog;
    private View emptyView;

    //请求列表信息
    private Handler listHandler = new Handler();
    private Runnable listRunnable = new Runnable() {
        @Override
        public void run() {
            auctionList(false, false);
            if (listHandler != null) {
                listHandler.postDelayed(this, LIST_TIME);
            }
        }
    };
    //列表中需要变更的数据
    private Handler dataHandler = new Handler();
    private Runnable dataRunnable = new Runnable() {
        @Override
        public void run() {
            auctionData();
            if (dataHandler != null) {
                dataHandler.postDelayed(this, DATA_TIME);
            }
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mPresenter != null) {
            if (isVisibleToUser) {
                postList();
                postData();
            } else {
                removeList();
                removeData();
            }
        }
    }

    public static AuctionPlusFragment newInstance(int status) {
        Bundle bundle = new Bundle();
        bundle.putInt("status", status);
        AuctionPlusFragment fragment = new AuctionPlusFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            status = bundle.getInt("status", 0);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_auction_plus;
    }

    @Override
    protected void initData() {
        FooterUtils.setFooterText();

        emptyView = View.inflate(getActivity(), R.layout.view_empty, null);

        CustomTextHeaderView header = new CustomTextHeaderView(getContext());
        header.setPrimaryColors(ContextCompat.getColor(getContext(), R.color.content_bg_color), ContextCompat.getColor(getContext(), R.color.text_color));
        refreshlayout.setRefreshHeader(header);
        refreshlayout.setRefreshFooter(new ClassicsFooter(getContext()));
        refreshlayout.setEnableAutoLoadmore(false);
        refreshlayout.setFooterHeight(80);
        refreshlayout.setEnableLoadmore(false);
        refreshlayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                currentPage = 1;
                auctionList(false, true);
                tvIndex.setText(String.valueOf(currentPage));
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                currentPage++;
                auctionList(true, true);
                tvIndex.setText(String.valueOf(currentPage));
            }
        });

        auctionPlusListAdapter = new AuctionPlusListAdapter();
        auctionPlusListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.img_whitepaper:
                    case R.id.tv_whitepaper:
                        String paper;
                        if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW)) {
                            paper = auctionList.get(position).paper;
                        } else {
                            paper = auctionList.get(position).paperEn;
                        }
                        jumpTo(new Intent(Intent.ACTION_VIEW, Uri.parse(paper)));
                        break;
                }
            }
        });
        auctionPlusListAdapter.setPayClickListener(new AuctionPlusListAdapter.PayClick() {
            @Override
            public void payClickListener(View view, int position) {
                TextView tvPay = (TextView) view;
                if (TextUtils.equals(tvPay.getText().toString(), getResources().getString(R.string.auction_now_goprice))) {//立即出价
                    mPresenter.auctionBuy(auctionList.get(position).id, "1", 2);
                } else if (TextUtils.equals(tvPay.getText().toString(), getResources().getString(R.string.auction_list_paynow))) {//立即支付
                    mPresenter.customerCoinByOneCoin(auctionList.get(position).payCurrencyId, auctionList.get(position), 1);
                }
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(auctionPlusListAdapter);
    }

    @Override
    protected AuctionPlusPresent createPresenter(AuctionPlusPresent mPresenter) {
        return new AuctionPlusPresent();
    }

    @Override
    public void onResume() {
        super.onResume();
        postList();
        postData();
    }

    @Override
    public void onPause() {
        super.onPause();
        removeList();
        removeData();
        auctionPlusListAdapter.removeHandle();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeList();
        removeData();
        auctionPlusListAdapter.removeHandle();
        auctionPlusListAdapter.cancelTime();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        if (countDownDialogTimer != null) {
            countDownDialogTimer.cancel();
            countDownDialogTimer = null;
        }
    }

    @Override
    protected void receiveEvent(BaseEvent event) {
        super.receiveEvent(event);
        if (event != null) {
            switch (event.getCode()) {
                case EventConstant.AuctionPlusListCode:
                    AuctionPlusListVisibleHintEvent auctionPlusListVisibleHintEvent = (AuctionPlusListVisibleHintEvent) event.getData();
                    isAuctionPlusListVisToUser = auctionPlusListVisibleHintEvent.isVisibleToUser();
                    if (isAuctionPlusListVisToUser) {
                        postList();
                        postData();
                    } else {
                        removeList();
                        removeData();
                    }
                    break;
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

    @Override
    public void auctionPlusList(AuctionPlusListModel model, boolean isTop, boolean isFlag) {
        if (model != null && model.list != null && model.list.size() > 0) {
            if (((currentPage - 1) * size + model.list.size()) >= model.total) {
                refreshlayout.setEnableLoadmore(false);
            } else {
                refreshlayout.setEnableLoadmore(true);
            }
            auctionList = model.list;
            auctionPlusListAdapter.setTime(model.time);
            auctionPlusListAdapter.setDataA(auctionList);
            if (isTop) {
                recyclerView.scrollToPosition(0);
            }
            if (isFlag) {
                auctionPlusListAdapter.cancelTime();
            }
            if (status != 2 && status != 3 && status != 4 && spUtil.getLogin()) {
                auctionPayStatus();
            }
            if (status == 4) {
                for (int i = 0; i < auctionList.size(); i++) {
                    auctionList.get(i).payPlusStatus = 1;
                }
            }
        } else {
            auctionList.clear();
            auctionPlusListAdapter.setDataA(null);
            auctionPlusListAdapter.setEmptyView(emptyView);
            refreshlayout.setEnableLoadmore(false);
        }
    }

    @Override
    public void auctionPlusData(AuctionPlusDataBaseModel model) {
        if (model != null && model.data != null && model.data.size() > 0) {
            auctionPlusListAdapter.setTime(model.time);

            for (int i = 0; i < model.data.size(); i++) {
                for (int j = 0; j < auctionList.size(); j++) {
                    if (TextUtils.equals(model.data.get(i).a, auctionList.get(j).id)) {
                        auctionPlusModel = auctionList.get(j);
                        auctionPlusDataModel = model.data.get(i);

                        auctionPlusModel.eachPrice = auctionPlusDataModel.p;
                        auctionPlusModel.timestamp = auctionPlusDataModel.t;
                        auctionPlusModel.auctionStatus = auctionPlusDataModel.s;
                        auctionPlusModel.eachNum = auctionPlusDataModel.n;
                        auctionPlusModel.totalNumber = auctionPlusDataModel.o;
                        auctionPlusModel.startTime = auctionPlusDataModel.st;
                        auctionPlusModel.surplusNumber = auctionPlusDataModel.c;

                        oldCurrentCount = auctionPlusModel.surplusNumber;
                        newCurrentCount = auctionPlusDataModel.c;

                        auctionPlusModel.surplusNumber = auctionPlusDataModel.c;

                        auctionList.set(j, auctionPlusModel);
                        if (!TextUtils.equals(oldCurrentCount, newCurrentCount)) {
                            auctionPlusListAdapter.setDataL(j, auctionList.get(j), 1);
                        } else {
                            auctionPlusListAdapter.setDataL(j, auctionList.get(j), 2);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void customerCoinByOneCoin(AmountModel model, AuctionPlusModel auctionModel) {
        if (model != null && auctionModel != null) {
            mPresenter.auctionPayInfo(auctionModel, String.valueOf(model.attachment), 1);
        }
    }

    @Override
    public void auctionPlusListPay(List<AuctionPayStatusModel> list) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < auctionList.size(); j++) {
                    if (TextUtils.equals(list.get(i).auctionId, auctionList.get(j).id)) {
                        auctionList.get(j).payPlusStatus = list.get(i).status;
                    }
                }
            }
            auctionPlusListAdapter.setDataA(auctionList);
        }
    }

    @Override
    public void auctionBuySuccess() {
        dialogView();
    }

    @Override
    public void auctionPayInfo(AuctionPlusPayInfoModel model, final AuctionPlusModel auctionPlusModel, String balance) {
        if (auctionPlusModel != null) {
            dialog = AuctionPlusPayDialogUtils.dialogShow(getContext(), model, auctionPlusModel, balance, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (auctionPlusModel != null) {
                        mPresenter.auctionPay(auctionPlusModel.id, 0, 0);
                    }
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpTo(AssetsActivity.createActivity(getActivity(), auctionPlusModel.payCurrencyName, auctionPlusModel.payCurrencyId, 0, true));
                }
            });
        }
    }

    @Override
    public void auctionPaySuccess() {
        dialog.dismiss();
        showToastSuccess(getResources().getString(R.string.auction_pay_success));
        auctionList(false, false);
    }

    /**
     * @param isTop  是否置顶
     * @param isFlag 是否清理 adapter中的数据集合
     */
    private void auctionList(boolean isTop, boolean isFlag) {
        switch (status) {
            case 0://全部
            case 1://进行中
            case 2://未开始
            case 3://已结束
                mPresenter.auctionPlusList(currentPage, size, status, isTop, isFlag);
                break;
            case 4://待支付
                mPresenter.auctionPlusListWait(currentPage, size, isTop, isFlag);
                break;
            case 5://收藏
                mPresenter.auctionPlusListByType(currentPage, size, 1, isTop, isFlag);
                break;
        }
    }

    private void auctionData() {
        if (auctionList != null && auctionList.size() > 0) {
            idString = "";
            for (int i = 0; i < auctionList.size(); i++) {
                if (auctionList.get(i).auctionStatus == 1 || auctionList.get(i).auctionStatus == 2) {
                    if (TextUtils.isEmpty(idString)) {
                        idString += auctionList.get(i).id;
                    } else {
                        idString += "," + auctionList.get(i).id;
                    }
                }
            }
            if (!TextUtils.isEmpty(idString)) {
                mPresenter.auctionPlusData(idString);
            }
        }
    }

    private void auctionPayStatus() {
        if (auctionList != null && auctionList.size() > 0) {
            idPayString = "";
            for (int i = 0; i < auctionList.size(); i++) {
                if (auctionList.get(i).auctionStatus == 3) {
                    if (TextUtils.isEmpty(idPayString)) {
                        idPayString += auctionList.get(i).id;
                    } else {
                        idPayString += "," + auctionList.get(i).id;
                    }
                }
            }
            if (!TextUtils.isEmpty(idPayString)) {
                mPresenter.auctionPlusListPay(idPayString, 1);
            }
        }
    }

    private void postList() {
        if (listHandler != null && listRunnable != null && !isList && isAuctionPlusListVisToUser && isVisibleToUser && isVisibleActivity) {
            listHandler.post(listRunnable);
            isList = true;
        }
    }

    private void removeList() {
        if (listHandler != null && listRunnable != null && isList) {
            listHandler.removeCallbacks(listRunnable);
            isList = false;
        }
    }

    private void postData() {
        if (dataHandler != null && dataRunnable != null && !isData && isAuctionPlusListVisToUser && isVisibleToUser && isVisibleActivity && status != 3 && status != 7) {
            dataHandler.post(dataRunnable);
            isData = true;
        }
    }

    private void removeData() {
        if (dataHandler != null && dataRunnable != null && isData && status != 3 && status != 7) {
            dataHandler.removeCallbacks(dataRunnable);
            isData = false;
        }
    }

    private void dialogView() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }

        View view = View.inflate(getContext(), R.layout.view_dialog_pay_success, null);
        view.findViewById(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setView(view)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        countDownDialogTimer.start();
    }

    private CountDownTimer countDownDialogTimer = new CountDownTimer(3000, 1000) {

        @Override
        public void onTick(long arg0) {
        }

        @Override
        public void onFinish() {
            alertDialog.dismiss();
        }
    };
}
