package com.android.tacu.module.assets.view;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.module.assets.contract.RechargeContract;
import com.android.tacu.module.assets.model.CoinAddressModel;
import com.android.tacu.module.assets.presenter.RechargePresenter;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.view.MarketDetailsActivity;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.ImgUtils;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.utils.permission.PermissionUtils;
import com.android.tacu.widget.CopyTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;
import com.yanzhenjie.permission.Permission;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xiaohong on 2018/8/21.
 */

public class RechargeFragment extends BaseFragment<RechargePresenter> implements RechargeContract.IRechargeView {

    @BindView(R.id.iv_coins_code)
    ImageView iv_coins_code;
    @BindView(R.id.tv_coin_address)
    CopyTextView tv_coin_address;
    @BindView(R.id.tv_id)
    TextView tv_id;
    @BindView(R.id.tv_fee)
    TextView tv_fee;
    @BindView(R.id.tv_coins_text)
    TextView tv_coins_text;
    @BindView(R.id.cp_payment_id)
    CopyTextView cp_payment_id;
    @BindView(R.id.ll_payment)
    LinearLayout ll_payment;
    @BindView(R.id.bt_save_img)
    QMUIAlphaButton bt_save_img;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.lin_layout)
    LinearLayout lin_layout;
    @BindView(R.id.et_txid)
    EditText et_txid;
    @BindView(R.id.et_amount)
    EditText et_amount;
    @BindView(R.id.btn_recharge)
    QMUIAlphaButton btn_recharge;
    @BindView(R.id.tv_address_tip)
    TextView tv_address_tip;
    @BindView(R.id.tv_usdt_tip)
    TextView tv_usdt_tip;

    private final int GRIN_ID = 197;
    private final int XLM_ID = 79;

    private int currencyId;
    private String currencyNameEn;
    private boolean isFlag;

    private Bitmap bitmap;
    private List<MarketNewModel.TradeCoinsBean> list;
    private RecommendAdapter adapter;

    public static RechargeFragment newInstance(int currencyId, String currencyNameEn, boolean isFlag) {
        Bundle bundle = new Bundle();
        bundle.putInt("currencyId", currencyId);
        bundle.putString("currencyNameEn", currencyNameEn);
        bundle.putBoolean("isFlag", isFlag);
        RechargeFragment fragment = new RechargeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        upLoad();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            currencyNameEn = bundle.getString("currencyNameEn");
            currencyId = bundle.getInt("currencyId");
            isFlag = bundle.getBoolean("isFlag");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_recharge;
    }

    @Override
    protected void initData() {
        switchClear();
        initCache();

        if (TextUtils.equals(currencyNameEn, "USDT")) {
            tv_usdt_tip.setVisibility(View.VISIBLE);
        }else{
            tv_usdt_tip.setVisibility(View.INVISIBLE);
        }
        tv_coin_address.setTextIsSelectable(true);

        setGrinAddress();
    }

    @Override
    protected RechargePresenter createPresenter(RechargePresenter mPresenter) {
        return new RechargePresenter();
    }

    @Override
    protected void onPresenterCreated(RechargePresenter mPresenter) {
        super.onPresenterCreated(mPresenter);
        if (isVisibleToUser && isFlag) {
            upLoad();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bitmap != null && !bitmap.isRecycled()) {
            // 回收并且置为null
            bitmap.recycle();
            bitmap = null;
        }
    }

    @OnClick(R.id.rl_copy)
    void copyCode() {
        if (TextUtils.equals(tv_coin_address.getText().toString().trim(), "--")) {
            return;
        }
        showToastSuccess(getResources().getString(R.string.copy_address));
        ClipboardManager cmb = (ClipboardManager) getHostActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(tv_coin_address.getText().toString().trim()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
    }

    @OnClick(R.id.iv_payment)
    void copyPaymentID() {
        showToastSuccess(getResources().getString(R.string.copy_address));
        ClipboardManager cmb = (ClipboardManager) getHostActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(cp_payment_id.getText().toString().trim()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
    }

    @OnClick(R.id.bt_save_img)
    void saveImg() {
        PermissionUtils.requestPermissions(getContext(), new OnPermissionListener() {
            @Override
            public void onPermissionSucceed() {
                if (bitmap != null) {
                    ImgUtils.saveImageToGallery(getContext(), bitmap);
                    showToastSuccess(getResources().getString(R.string.save_image));
                }
            }

            @Override
            public void onPermissionFailed() {
            }
        }, Permission.Group.STORAGE);
    }

    @OnClick(R.id.btn_recharge)
    void btnRecharge() {
        String txid = et_txid.getText().toString().trim();
        String amount = et_amount.getText().toString().trim();
        if (TextUtils.isEmpty(txid)) {
            showToastError(getResources().getString(R.string.txid_tips));
            return;
        }
        if (TextUtils.isEmpty(amount)) {
            showToastError(getResources().getString(R.string.amount_tips));
            return;
        }

        switch (currencyId) {
            case GRIN_ID:
                mPresenter.rechargeGrin(txid, amount);
                break;
        }
    }

    @Override
    public void showCoinsAddress(CoinAddressModel attachment) {
        if (attachment != null) {
            if (TextUtils.isEmpty(attachment.address)) {
                tv_coin_address.setText("--");
                iv_coins_code.setVisibility(View.INVISIBLE);
            } else {
                iv_coins_code.setVisibility(View.VISIBLE);
                String imCode = attachment.image;
                byte b[] = Base64.decode(imCode.getBytes(), Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                tv_coin_address.setText(attachment.address);
                iv_coins_code.setImageBitmap(bitmap);
            }
            String idFormat = getResources().getString(R.string.payment_id);
            String id = String.format(idFormat, attachment.msgName);
            tv_id.setText(id);
            tv_fee.setText(getString(R.string.fee2) + FormatterUtils.getFormatValue(attachment.fee));
            if (TextUtils.equals(attachment.msgCode, "1")) {
                ll_payment.setVisibility(View.GONE);
            } else {
                ll_payment.setVisibility(View.VISIBLE);
                cp_payment_id.setText(attachment.msgCode);
            }

            String sAgeFormat, sFinal = "";
            if (currencyId == XLM_ID) {
                sAgeFormat = getResources().getString(R.string.coins_text2);
                sFinal = String.format(sAgeFormat, currencyNameEn, currencyNameEn, currencyNameEn, currencyNameEn, currencyNameEn, currencyNameEn, String.valueOf(attachment.confirmNum));
            } else {
                sAgeFormat = getResources().getString(R.string.coins_text);
                sFinal = String.format(sAgeFormat, currencyNameEn, currencyNameEn, currencyNameEn, currencyNameEn, currencyNameEn, String.valueOf(attachment.confirmNum));
            }
            tv_coins_text.setText(sFinal);
        }
    }

    @Override
    public void showRechargeSuccess(String msg) {
        showToastSuccess(msg);
        et_txid.setText("");
        et_amount.setText("");
    }

    public void updateUI(int currencyId, String currencyNameEn) {
        this.currencyId = currencyId;
        this.currencyNameEn = currencyNameEn;

        if (isVisibleToUser) {
            upLoad();
        }
        if (TextUtils.equals(currencyNameEn, "USDT")) {
            tv_usdt_tip.setVisibility(View.VISIBLE);
        }else{
            tv_usdt_tip.setVisibility(View.INVISIBLE);
        }
        setGrinAddress();

        initCache();
    }

    /**
     * 加载缓存
     */
    private void initCache() {
        String cacheString = SPUtils.getInstance().getString(Constant.SELECT_COIN_GROUP_CACHE);
        List<MarketNewModel> cacheList = new Gson().fromJson(cacheString, new TypeToken<List<MarketNewModel>>() {
        }.getType());

        list = new ArrayList<>();
        MarketNewModel.TradeCoinsBean tradeBean;
        if (cacheList != null && cacheList.size() > 0) {
            for (int i = 0; i < cacheList.size(); i++) {
                for (int j = 0; j < cacheList.get(i).tradeCoinsList.size(); j++) {
                    if (cacheList.get(i).tradeCoinsList.get(j).currencyId == currencyId) {
                        tradeBean = cacheList.get(i).tradeCoinsList.get(j);
                        list.add(tradeBean);
                        break;
                    }
                }

            }
        }

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new RecommendAdapter();
        recyclerView.setAdapter(adapter);

        if (list != null && list.size() > 0) {
            adapter.setNewData(list);
        }
    }

    private void upLoad() {
        switchClear();
        mPresenter.selectUserAddress(1, currencyId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setGrinAddress() {
        if (currencyId == GRIN_ID) {
            lin_layout.setVisibility(View.VISIBLE);
            tv_address_tip.setText(String.format(getResources().getString(R.string.recharge_grin_address), currencyNameEn));
        } else {
            lin_layout.setVisibility(View.GONE);
        }
    }

    /**
     * 只要币种变化就清空
     */
    private void switchClear() {
        iv_coins_code.setVisibility(View.INVISIBLE);
        tv_coin_address.setText("--");
        tv_fee.setText(getString(R.string.fee2) + "--");
    }

    private class RecommendAdapter extends BaseQuickAdapter<MarketNewModel.TradeCoinsBean, BaseViewHolder> {

        public RecommendAdapter() {
            super(R.layout.item_recharge_recommend);
        }

        @Override
        protected void convert(BaseViewHolder helper, final MarketNewModel.TradeCoinsBean item) {
            helper.setText(R.id.tv_currency, item.currencyNameEn + "/" + item.baseCurrencyNameEn);
            if (item.changeRate >= 0) {
                helper.setText(R.id.tv_absolutely, "+" + BigDecimal.valueOf(item.changeRate).toPlainString() + "%");
                helper.setTextColor(R.id.tv_absolutely, ContextCompat.getColor(getContext(), R.color.color_riseup));
                helper.setTextColor(R.id.tv_situation, ContextCompat.getColor(getContext(), R.color.color_riseup));
            } else {
                helper.setText(R.id.tv_absolutely, BigDecimal.valueOf(item.changeRate).toPlainString() + "%");
                helper.setTextColor(R.id.tv_absolutely, ContextCompat.getColor(getContext(), R.color.color_risedown));
                helper.setTextColor(R.id.tv_situation, ContextCompat.getColor(getContext(), R.color.color_risedown));
            }

            helper.setText(R.id.tv_situation, BigDecimal.valueOf(item.currentAmount).setScale(item.pointPrice, BigDecimal.ROUND_DOWN).toPlainString());
            //后期有数据记得测试
            if (item.baseCurrencyId == 22) {
                helper.setText(R.id.tv_money, "");
            } else {
                helper.setText(R.id.tv_money, "≈" + getMcM(item.baseCurrencyId, item.currentAmount));
            }

            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpTo(MarketDetailsActivity.createActivity(getContext(), item.currencyId, item.baseCurrencyId, item.currencyNameEn, item.baseCurrencyNameEn));
                }
            });
        }
    }
}
