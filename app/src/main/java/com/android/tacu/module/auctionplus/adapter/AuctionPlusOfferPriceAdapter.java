package com.android.tacu.module.auctionplus.adapter;

import android.text.TextUtils;

import com.android.tacu.module.auctionplus.modal.AuctionPlusListByIdModel;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.module.auctionplus.modal.AuctionPlusOfferPriceModel;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.user.UserInfoUtils;

/**
 * 出价记录
 * Created by jiazhen on 2019/4/23.
 */
public class AuctionPlusOfferPriceAdapter extends BaseQuickAdapter<AuctionPlusOfferPriceModel, BaseViewHolder> {

    private UserInfoUtils spUtil;
    private String feeCurrencyName;

    public AuctionPlusOfferPriceAdapter() {
        super(R.layout.item_offer_price_plus);
        spUtil = UserInfoUtils.getInstance();
    }

    public void setAuctionPlusListByIdModel(AuctionPlusListByIdModel model) {
        if (model != null && model.info != null) {
            feeCurrencyName = model.info.feeCurrencyName;
        }
    }

    @Override
    protected void convert(BaseViewHolder holder, final AuctionPlusOfferPriceModel item) {
        holder.setText(R.id.tv_id, item.uid);
        holder.setText(R.id.tv_fee, FormatterUtils.getFormatValue(item.fee) + feeCurrencyName);
        holder.setText(R.id.tv_bonus, FormatterUtils.getFormatValue(item.bonus) + item.bonusName);
        if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW)) {
            holder.setText(R.id.tv_location, item.location);
        } else {
            holder.setText(R.id.tv_location, item.locationEn);
        }
    }
}
