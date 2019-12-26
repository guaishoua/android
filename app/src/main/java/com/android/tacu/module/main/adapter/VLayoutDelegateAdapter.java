package com.android.tacu.module.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * Vlayout框架类适配器
 * Created by jiazhen on 2018/9/8.
 */
public class VLayoutDelegateAdapter extends DelegateAdapter.Adapter<BaseViewHolder> {

    private Context mContext;
    private LayoutHelper mLayoutHelper;
    private int mLayoutId = -1;
    private int mCount = -1;
    private int mViewTypeItem = -1;

    public VLayoutDelegateAdapter(Context context, LayoutHelper layoutHelper, int layoutId, int count, int viewTypeItem) {
        this.mContext = context;
        this.mLayoutHelper = layoutHelper;
        this.mLayoutId = layoutId;
        this.mCount = count;
        this.mViewTypeItem = viewTypeItem;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return mLayoutHelper;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == mViewTypeItem) {
            return new BaseViewHolder(LayoutInflater.from(mContext).inflate(mLayoutId, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

    }

    //必须重写不然会出现滑动不流畅的情况
    @Override
    public int getItemViewType(int position) {
        return mViewTypeItem;
    }

    @Override
    public int getItemCount() {
        return mCount;
    }
}
