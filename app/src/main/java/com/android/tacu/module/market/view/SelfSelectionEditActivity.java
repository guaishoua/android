package com.android.tacu.module.market.view;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.market.contract.SelfSelectionEditContract;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.model.SelfModel;
import com.android.tacu.module.market.presenter.SelfSelectionEditPresenter;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemStateChangedListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 自选编辑页面
 */
public class SelfSelectionEditActivity extends BaseActivity<SelfSelectionEditPresenter> implements SelfSelectionEditContract.IView {

    @BindView(R.id.lin_layout)
    LinearLayout linLayout;
    @BindView(R.id.recyclerView)
    SwipeMenuRecyclerView recyclerView;
    @BindView(R.id.img_select)
    ImageView imgSelect;
    @BindView(R.id.tv_select)
    TextView tvSelect;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.rl_bottom)
    RelativeLayout rlBottom;

    private final int requestCodeValue = 1001;

    private int dragStatus = 0;//0：代表没有拖拽 1：代表长按获取拖拽的焦点 2：代表正在拖拽
    //无数据页面
    private View nodataView;
    private Gson gson = new Gson();
    private EditAdapter editAdapter;
    private List<MarketNewModel.TradeCoinsBean> allTradeCoinList = new ArrayList<>();
    private List<MarketNewModel.TradeCoinsBean> tradeCoinList = new ArrayList<>();
    private List<MarketNewModel.TradeCoinsBean> batchList = new ArrayList<>();//用于记录需要批量删除的数据
    private String selfModelString;
    private SelfModel selfModel;

    //temp
    private boolean isCheck = false;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_self_selection_edit);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.edit_selfselection));
        mTopBar.removeAllLeftViews();
        mTopBar.addLeftTextButton(getResources().getString(R.string.btn_complete), R.id.qmui_topbar_item_left_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLeaveDialog();
            }
        });
        mTopBar.addRightImageButton(R.drawable.icon_add_white, R.id.qmui_topbar_item_right, 20, 20).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spUtil.getLogin()) {
                    Intent intent = SelectedCoinsActivity.createActivity(SelfSelectionEditActivity.this, true, false, gson.toJson(listToSelfModel()), "", "");
                    jumpTo(intent, requestCodeValue);
                } else {
                    jumpTo(LoginActivity.class);
                }
            }
        });

        nodataView = View.inflate(SelfSelectionEditActivity.this, R.layout.view_empty, null);

        recyclerView.setLayoutManager(new LinearLayoutManager(SelfSelectionEditActivity.this));
        editAdapter = new EditAdapter();
        recyclerView.setAdapter(editAdapter);
        //关闭拖拽  点击按钮在拖拽
        recyclerView.setLongPressDragEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(SelfSelectionEditActivity.this));
        recyclerView.setOnItemMoveListener(mItemMoveListener);
        recyclerView.setOnItemStateChangedListener(mStateChangedListener);

        setBuildData();
    }

    @Override
    protected SelfSelectionEditPresenter createPresenter(SelfSelectionEditPresenter mPresenter) {
        return new SelfSelectionEditPresenter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCodeValue && resultCode == RESULT_OK) {
            String selfModelString = data.getStringExtra("selfModel");
            selfModel = gson.fromJson(selfModelString, SelfModel.class);
            if (selfModel == null) {
                selfModel = new SelfModel();
            }
            changeData();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showLeaveDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.img_select)
    void selectBtn() {
        if (tradeCoinList != null && tradeCoinList.size() > 0) {
            if (batchList.size() == 0 || batchList.size() != tradeCoinList.size()) {//全选
                batchList.clear();
                batchList.addAll(tradeCoinList);
            } else if (batchList != null) {//取消全选
                batchList.clear();
            }
            setBottomText();
        }
    }

    @OnClick(R.id.tv_delete)
    void deleteBtn() {
        if (batchList != null && batchList.size() > 0) {
            tradeCoinList.removeAll(batchList);
            batchList.clear();
            setBottomText();
            setNoDataView();
        }
    }

    @Override
    public void uploadSelfSuccess() {
        SPUtils.getInstance().put(Constant.SELFCOIN_LIST, gson.toJson(listToSelfModel()));
        showToastSuccess(getResources().getString(R.string.business_edit_collect));
        finish();
    }

    @Override
    public void uploadSelfError() {
        showToastError(getResources().getString(R.string.business_edit_collect_error));
    }

    private void showLeaveDialog() {
        String gsonString = gson.toJson(listToSelfModel());
        if (!TextUtils.equals(selfModelString, gsonString)) {
            new DroidDialog.Builder(SelfSelectionEditActivity.this)
                    .title(getResources().getString(R.string.self_selection_save))
                    .positiveButton(getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                        @Override
                        public void onPositive(Dialog droidDialog) {
                            if (spUtil.getLogin()) {
                                mPresenter.uploadSelfList(gson.toJson(listToSelfModel()));
                            }
                        }
                    })
                    .negativeButton(getResources().getString(R.string.cancel), new DroidDialog.onNegativeListener() {
                        @Override
                        public void onNegative(Dialog droidDialog) {
                            finish();
                        }
                    })
                    .cancelable(false, false)
                    .show();
        } else {
            finish();
        }
    }

    private void setBuildData() {
        String cacheString = SPUtils.getInstance().getString(Constant.SELECT_COIN_GROUP_CACHE);
        List<MarketNewModel> cacheList = gson.fromJson(cacheString, new TypeToken<List<MarketNewModel>>() {
        }.getType());
        if (cacheList != null && cacheList.size() > 0) {
            allTradeCoinList.clear();
            for (int i = 0; i < cacheList.size(); i++) {
                if (cacheList.get(i).tradeCoinsList != null && cacheList.get(i).tradeCoinsList.size() > 0) {
                    for (int j = 0; j < cacheList.get(i).tradeCoinsList.size(); j++) {
                        allTradeCoinList.add(cacheList.get(i).tradeCoinsList.get(j));
                    }
                }
            }
        }

        selfModelString = SPUtils.getInstance().getString(Constant.SELFCOIN_LIST);
        selfModel = gson.fromJson(selfModelString, SelfModel.class);
        if (selfModel == null) {
            selfModel = new SelfModel();
        }
        changeData();
    }

    private void changeData() {
        dealData();
        setNoDataView();
        setBottomText();
        if (tradeCoinList != null && tradeCoinList.size() > 0) {
            editAdapter.setNewData(tradeCoinList);
        }
    }

    private void dealData() {
        tradeCoinList.clear();
        if (selfModel != null && selfModel.checkedList != null && selfModel.checkedList.size() > 0) {
            for (int i = 0; i < selfModel.checkedList.size(); i++) {
                for (int j = 0; j < allTradeCoinList.size(); j++) {
                    if (TextUtils.equals(selfModel.checkedList.get(i).symbol, allTradeCoinList.get(j).currencyId + "," + allTradeCoinList.get(j).baseCurrencyId)) {
                        tradeCoinList.add(allTradeCoinList.get(j));
                        break;
                    }
                }
            }
        }
    }

    /**
     * 如果没数据 显示无数据图片
     */
    private void setNoDataView() {
        if (tradeCoinList != null && tradeCoinList.size() > 0) {
            removewErrorView();
        } else {
            tradeCoinList = new ArrayList<>();
            setErrorView(nodataView);
        }
    }

    private void setBottomText() {
        if (batchList.size() > 0) {
            tvDelete.setText(getResources().getString(R.string.btn_delete) + "(" + batchList.size() + ")");
        } else {
            tvDelete.setText(getResources().getString(R.string.btn_delete) + "(0)");
        }

        if (batchList.size() > 0 && tradeCoinList.size() == batchList.size()) {
            imgSelect.setBackgroundResource(R.drawable.icon_check_box);
            tvSelect.setText(getResources().getString(R.string.btn_cancel_select));
        } else {
            imgSelect.setBackgroundResource(R.drawable.icon_check_box_outline);
            tvSelect.setText(getResources().getString(R.string.btn_all_select));
        }

        if (tradeCoinList != null && tradeCoinList.size() > 0) {
            rlBottom.setVisibility(View.VISIBLE);
        } else {
            rlBottom.setVisibility(View.GONE);
        }
        editAdapter.notifyDataSetChanged();
    }

    /**
     * 设置无数据和无网络视图
     */
    private void setErrorView(View view) {
        if (linLayout != null) {
            linLayout.removeAllViews();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            linLayout.addView(view, params);
            linLayout.setVisibility(View.VISIBLE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
    }

    /**
     * 还原
     */
    private void removewErrorView() {
        linLayout.removeAllViews();
        linLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Item被拖拽 置顶时，交换数据，并更新adapter。
     */
    private void replaceDataItem(int fromPosition, int toPosition) {
        //Item被拖拽时，交换数据，并更新adapter。
        Collections.swap(tradeCoinList, fromPosition, toPosition);
        editAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * 将list列表转换成SelfModel
     */
    private SelfModel listToSelfModel() {
        SelfModel selfModel = new SelfModel();
        if (tradeCoinList != null && tradeCoinList.size() > 0) {
            for (int i = 0; i < tradeCoinList.size(); i++) {
                SelfModel.SymbolBean bean = new SelfModel.SymbolBean();
                bean.symbol = tradeCoinList.get(i).currencyId + "," + tradeCoinList.get(i).baseCurrencyId;
                selfModel.checkedList.add(bean);
            }
        }
        return selfModel;
    }

    private class EditAdapter extends BaseQuickAdapter<MarketNewModel.TradeCoinsBean, BaseViewHolder> {

        public EditAdapter() {
            super(R.layout.item_self_edit);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final MarketNewModel.TradeCoinsBean item) {
            helper.setText(R.id.tv_coins_name, item.currencyNameEn + "/" + item.baseCurrencyNameEn);
            helper.setText(R.id.tv_amount, BigDecimal.valueOf(item.volume).setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
            helper.setBackgroundColor(R.id.ll_layout, ContextCompat.getColor(SelfSelectionEditActivity.this, R.color.content_bg_color));

            isCheck = false;
            helper.setBackgroundRes(R.id.img_ok, R.drawable.icon_check_box_outline);
            for (int i = 0; i < batchList.size(); i++) {
                if (TextUtils.equals(item.baseCurrencyNameEn, batchList.get(i).baseCurrencyNameEn) && TextUtils.equals(item.currencyNameEn, batchList.get(i).currencyNameEn)) {
                    helper.setBackgroundRes(R.id.img_ok, R.drawable.icon_check_box);
                    isCheck = true;
                }
            }
            final boolean isCheckFlag = isCheck;
            helper.setOnClickListener(R.id.rl_ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isCheckFlag) {
                        batchList.remove(item);
                    } else {
                        batchList.add(item);
                    }
                    setBottomText();
                    notifyItemChanged(helper.getLayoutPosition());
                }
            });
            helper.setOnClickListener(R.id.img_top, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = tradeCoinList.lastIndexOf(item);
                    if (position != -1) {
                        replaceDataItem(position, 0);
                        notifyDataSetChanged();
                    }
                }
            });
            helper.setOnLongClickListener(R.id.img_drag, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    recyclerView.setLongPressDragEnabled(true);
                    recyclerView.startDrag(helper);
                    return true;
                }
            });
        }
    }

    //拖拽
    private OnItemMoveListener mItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            int fromPosition = srcHolder.getLayoutPosition();
            int toPosition = targetHolder.getLayoutPosition();
            replaceDataItem(fromPosition, toPosition);
            dragStatus = 2;
            return true;
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
        }
    };

    //侧滑删除和拖拽Item时的手指状态
    private OnItemStateChangedListener mStateChangedListener = new OnItemStateChangedListener() {
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
                //状态正在拖拽
                dragStatus = 1;
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
                //状态手指松开
                //如果之前的状态是正在拖拽 就刷新数据库
                if (dragStatus == 2) {
                    recyclerView.setLongPressDragEnabled(false);
                    editAdapter.notifyDataSetChanged();
                }
                dragStatus = 0;
            }
        }
    };
}
