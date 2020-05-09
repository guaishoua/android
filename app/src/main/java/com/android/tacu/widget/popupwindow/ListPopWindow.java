package com.android.tacu.widget.popupwindow;

import android.content.Context;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

public class ListPopWindow extends PopupWindow {

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
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(null);

        ListView listView = new ListView(mContext);
        listView.setDividerHeight(0);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(onItemClickListener);
        setContentView(listView);
    }
}
