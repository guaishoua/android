package com.android.tacu.widget.popupwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListPopupWindow;

import com.android.tacu.R;

public class ListPopWindow extends ListPopupWindow {

    private Context mContext;
    private BaseAdapter mAdapter;

    public ListPopWindow(Context context, BaseAdapter adapter) {
        super(context);
        this.mContext = context;
        this.mAdapter = adapter;
    }

    public void create(int width, AdapterView.OnItemClickListener onItemClickListener) {
        create(width, ListPopupWindow.WRAP_CONTENT, onItemClickListener);
    }

    public void create(int width, int maxHeight, AdapterView.OnItemClickListener onItemClickListener) {
        setWidth(width);
        setHeight(maxHeight);
        setBackgroundDrawable(null);

        setAdapter(mAdapter);
        setModal(true);

        setOnItemClickListener(onItemClickListener);
    }
}
