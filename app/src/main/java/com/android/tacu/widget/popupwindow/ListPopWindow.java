package com.android.tacu.widget.popupwindow;

import android.content.Context;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListPopupWindow;

public class ListPopWindow extends ListPopupWindow {

    private Context mContext;
    private BaseAdapter mAdapter;

    public ListPopWindow(Context context, BaseAdapter adapter) {
        super(context);
        this.mContext = context;
        this.mAdapter = adapter;
    }

    public void create(int width, int maxHeight, AdapterView.OnItemClickListener onItemClickListener) {
        setWidth(width);
        setHeight(maxHeight);
        setBackgroundDrawable(null);

        setAdapter(mAdapter);
        setModal(true);

        setOnItemClickListener(onItemClickListener);
    }

    @Override
    public void show() {
        super.show();
        getListView().setDividerHeight(0);
    }
}
