package com.android.tacu.module.auction.adapter;

import com.android.tacu.R;
import com.android.tacu.module.auction.model.AuctionLogsModel;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class AuctionRecordAdapter extends BaseQuickAdapter<AuctionLogsModel, BaseViewHolder> {

    public AuctionRecordAdapter() {
        super(R.layout.item_auction_record);
    }

    @Override
    protected void convert(BaseViewHolder holder, AuctionLogsModel item) {
        holder.setText(R.id.tv_uid, item.uid);
        holder.setText(R.id.tv_amount, item.targetNum);
        holder.setText(R.id.tv_price, item.targetCurrentPrice.stripTrailingZeros().toPlainString());
        holder.setText(R.id.tv_time, item.createTime);
    }
}
