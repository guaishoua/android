package com.android.tacu.widget.tab;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;

import java.util.List;

/**
 * 币种详情页 指标和时间
 * Created by jiazhen on 2018/9/24.
 */
public class TabPopup extends PopupWindow {

    private Context mContext;
    private int basePosition;
    private TabAdapter mAdapter;
    private int textcolor = 0;

    public TabPopup(Context context, int basePosition) {
        super(context);
        this.mContext = context;
        this.basePosition = basePosition;
        mAdapter = new TabAdapter();
    }

    public void create(int textcolor, int bgColor, int width, int maxHeight, int numColumn, List<String> data, final TabItemSelect tabItemSelect) {
        this.textcolor = textcolor;
        create(width, maxHeight, numColumn, data, tabItemSelect);
        setBackgroundDrawable(new ColorDrawable(bgColor));
    }

    public void create(int width, int maxHeight, int numColumn, List<String> data, final TabItemSelect tabItemSelect) {
        setWidth(width);
        setHeight(maxHeight);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(mContext, R.color.color_kline)));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, numColumn);
        gridLayoutManager.setAutoMeasureEnabled(true);
        RecyclerView recyclerView = new RecyclerView(mContext);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                basePosition = position;
                adapter.notifyDataSetChanged();
                if (tabItemSelect != null) {
                    tabItemSelect.onTabItemSelectListener(position);
                }
            }
        });
        mAdapter.setNewData(data);
        setContentView(recyclerView);
    }

    public void setWidthAndHeight(int width, int maxHeight) {
        setWidth(width);
        setHeight(maxHeight);
    }

    public class TabAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public TabAdapter() {
            super(R.layout.item_tab);
        }

        @Override
        protected void convert(final BaseViewHolder helper, String item) {
            helper.setText(R.id.tab_text, item);
            ((TextView) helper.getView(R.id.tab_text)).setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.text_size_sp_12));

            if (basePosition == helper.getLayoutPosition()) {
                helper.setTextColor(R.id.tab_text, ContextCompat.getColor(mContext, R.color.tab_default));
                helper.setBackgroundColor(R.id.indicator, ContextCompat.getColor(mContext, R.color.tab_default));
                helper.setGone(R.id.indicator, true);
            } else {
                if (textcolor != 0) {
                    helper.setTextColor(R.id.tab_text, textcolor);
                } else {
                    helper.setTextColor(R.id.tab_text, ContextCompat.getColor(mContext, R.color.tab_bg_color));
                }
                helper.setGone(R.id.indicator, false);
            }
        }
    }

    public interface TabItemSelect {
        void onTabItemSelectListener(int position);
    }
}
