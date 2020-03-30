package com.android.tacu.widget.tab;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.PopupWindow;

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
    private TabAdapter mAdapter;

    public TabPopup(Context context) {
        super(context);
        this.mContext = context;
        mAdapter = new TabAdapter();
    }

    public void create(int width, int maxHeight, List<String> data, final TabItemSelect tabItemSelect) {
        setWidth(width);
        setHeight(maxHeight);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(mContext, R.color.content_bg_color)));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, data.size());
        gridLayoutManager.setAutoMeasureEnabled(true);
        RecyclerView recyclerView = new RecyclerView(mContext);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (tabItemSelect != null) {
                    tabItemSelect.onTabItemSelectListener(position);
                }
            }
        });
        mAdapter.setNewData(data);
        setContentView(recyclerView);
    }

    public class TabAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public TabAdapter() {
            super(R.layout.item_tab);
        }

        @Override
        protected void convert(final BaseViewHolder helper, String item) {
            helper.setText(R.id.tab_text, item);
            helper.setTextColor(R.id.tab_text, ContextCompat.getColor(mContext, R.color.text_color));
            helper.setGone(R.id.indicator, false);
        }
    }

    public interface TabItemSelect {
        void onTabItemSelectListener(int position);
    }
}
