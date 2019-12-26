package com.android.tacu.module.market.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.model.SelfModel;
import com.android.tacu.utils.user.UserInfoUtils;

/**
 * Created by jiazhen on 2018/8/21.
 */
public class SelectedCoinsAdapter extends BaseQuickAdapter<MarketNewModel.TradeCoinsBean, BaseViewHolder> {

    private Context context;
    private boolean isFlag;

    private UserInfoUtils spUtil;
    private SelfModel selfModel;
    private SelfModel.SymbolBean symbolBean;

    public SelfModel getSelfModel() {
        return selfModel;
    }

    /**
     * @param context
     * @param isFlag  false: 代表选择币种  true: 添加自选
     */
    public SelectedCoinsAdapter(Context context, boolean isFlag) {
        super(R.layout.item_select_coins);
        this.context = context;
        this.isFlag = isFlag;
        spUtil = UserInfoUtils.getInstance();
    }

    public void setData(SelfModel selfModel) {
        this.selfModel = selfModel;
    }

    protected void convert(final BaseViewHolder helper, final MarketNewModel.TradeCoinsBean item) {
        helper.setText(R.id.tv_coins_name, item.currencyNameEn + "/" + item.baseCurrencyNameEn);
        helper.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.content_bg_color));

        symbolBean = null;
        helper.setImageResource(R.id.img_select, R.drawable.icon_rating_uncollect_grey);
        if (isFlag && spUtil.getLogin()) {
            helper.setVisible(R.id.rl_select, true);
            if (selfModel != null && selfModel.checkedList != null && selfModel.checkedList.size() > 0) {
                helper.setImageResource(R.id.img_select, R.drawable.icon_rating_uncollect_grey);
                for (int i = 0; i < selfModel.checkedList.size(); i++) {
                    if (TextUtils.equals(selfModel.checkedList.get(i).symbol, item.currencyId + "," + item.baseCurrencyId)) {
                        symbolBean = selfModel.checkedList.get(i);
                        helper.setImageResource(R.id.img_select, R.drawable.icon_rating_collect);
                        break;
                    }
                }
            }
        } else if (isFlag && !spUtil.getLogin()) {
            helper.setVisible(R.id.rl_select, true);
        }

        helper.setOnClickListener(R.id.tv_coins_name, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFlag) {
                    //选择币种
                }
            }
        });

        final SelfModel.SymbolBean tempSelectCoin = symbolBean;
        helper.setOnClickListener(R.id.rl_select, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spUtil.getLogin()) {
                    if (tempSelectCoin != null) {
                        selfModel.checkedList.remove(tempSelectCoin);
                    } else {
                        SelfModel.SymbolBean bean = new SelfModel.SymbolBean();
                        bean.symbol = item.currencyId + "," + item.baseCurrencyId;
                        selfModel.checkedList.add(0, bean);
                    }
                    notifyItemChanged(helper.getLayoutPosition());
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                }
            }
        });
    }
}
