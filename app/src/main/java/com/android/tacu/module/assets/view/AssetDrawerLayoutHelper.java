package com.android.tacu.module.assets.view;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.module.assets.model.CoinListModel;
import com.android.tacu.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaohong on 2018/10/11.
 */

public class AssetDrawerLayoutHelper implements View.OnClickListener {

    private LinearLayout lin_search;
    private ImageView iv_search;
    private EditText et_search;
    private RecyclerView recyclerView;

    private View view;
    private Context mContext;

    private SearchAdapter searchAdapter;
    private RecordClickListener clickListener;

    private List<CoinListModel.AttachmentBean> currencyList = new ArrayList<>();
    private List<CoinListModel.AttachmentBean> currencySearchList = new ArrayList<>();

    public AssetDrawerLayoutHelper(Context context, View viewDrawer) {
        this.mContext = context;
        this.view = viewDrawer;
    }

    public void clearActivity() {
        mContext = null;
        view = null;
    }

    public void setDrawerMenuView(RecordClickListener clickListener) {
        this.clickListener = clickListener;

        lin_search = view.findViewById(R.id.lin_search);
        iv_search = view.findViewById(R.id.iv_search);
        iv_search.setOnClickListener(this);
        et_search = view.findViewById(R.id.et_search);
        recyclerView = view.findViewById(R.id.recyclerView);

        StatusBarUtils.moveViewStatusBarHeight((Activity) mContext, lin_search);
        initView();
        textChanged();
    }

    private void textChanged() {
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search();
            }
        });
    }

    public void initDatas(List<CoinListModel.AttachmentBean> attachment) {
        this.currencyList = attachment;
        search();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search:
                search();
                break;
        }
    }

    private void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        searchAdapter = new SearchAdapter();
        recyclerView.setAdapter(searchAdapter);
    }

    private void search() {
        currencySearchList.clear();
        String search = et_search.getText().toString().trim().toUpperCase();
        if (TextUtils.isEmpty(search)) {
            setAdapter(currencyList);
            return;
        }
        if (currencyList != null && currencyList.size() > 0) {
            for (int i = 0; i < currencyList.size(); i++) {
                if (currencyList.get(i).currencyNameEn.toUpperCase().contains(search) || currencyList.get(i).currencyName.toUpperCase().contains(search)) {
                    currencySearchList.add(currencyList.get(i));
                }
            }
        }
        setAdapter(currencySearchList);
    }

    private void setAdapter(List<CoinListModel.AttachmentBean> list) {
        if (list != null && list.size() > 0) {
            searchAdapter.setNewData(list);
        } else {
            searchAdapter.setNewData(null);
        }
    }

    private class SearchAdapter extends BaseQuickAdapter<CoinListModel.AttachmentBean, BaseViewHolder> {

        public SearchAdapter() {
            super(R.layout.item_search_currency);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final CoinListModel.AttachmentBean item) {
            helper.setText(R.id.tv_currency, item.currencyNameEn);
            ((TextView) helper.getView(R.id.tv_currency)).setGravity(Gravity.LEFT);
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.RecordClick(item.currencyNameEn, item.currencyId);
                }
            });
        }
    }

    public interface RecordClickListener {
        void RecordClick(String currencyName, int currencyId);
    }
}
