package com.android.tacu.view.popup;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.module.assets.model.CoinListModel;
import com.android.tacu.module.assets.model.RecordEvent;
import com.android.tacu.utils.GlideUtils;

import java.util.ArrayList;
import java.util.List;

public class CoinFilterView extends PopupBaseView {

    RecyclerView list;
    EditText search;
    View all;
    MyAdapter adapter;
    private List<CoinListModel.AttachmentBean> originList;
    Listener listener;

    public interface Listener {
        void onItemPress(RecordEvent event);
    }


    public CoinFilterView(Context context, Listener l) {
        super(context);
        listener = l;
        adapter = new MyAdapter();
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.view_coin_filter;
    }

    @Override
    protected void initView(View root) {
        list = root.findViewById(R.id.recyclerView);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setAdapter(adapter);
        search = root.findViewById(R.id.et_search);
        all = root.findViewById(R.id.btn_all);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordEvent event = new RecordEvent(
                        0,
                        getContext().getString(R.string.all),
                        "",
                        ""
                );
                if (listener != null) {
                    listener.onItemPress(event);
                }
                dismiss();
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                notifyDataChangedWithSearch();
            }
        });
    }

    public void setList(List<CoinListModel.AttachmentBean> list) {
        originList = list;
        if (adapter != null) {
            adapter.setNewData(list);
            notifyDataChangedWithSearch();
        }
    }

    private void notifyDataChangedWithSearch() {
        if(search == null) {
            return;
        }

        List<CoinListModel.AttachmentBean> searchedList = new ArrayList<>();
        String searchStr = search.getText().toString().trim().toUpperCase();
        if (TextUtils.isEmpty(searchStr)) {
            setAdapter(originList);
            return;
        }

        if (originList != null && originList.size() > 0) {
            for (int i = 0; i < originList.size(); i++) {
                if (originList.get(i).currencyNameEn.toUpperCase().contains(searchStr) || originList.get(i).currencyName.toUpperCase().contains(searchStr)) {
                    searchedList.add(originList.get(i));
                }
            }
        }
        setAdapter(searchedList);
    }

    private void setAdapter(List<CoinListModel.AttachmentBean> list) {
        if (list != null && list.size() > 0) {
            adapter.setNewData(list);
        } else {
            adapter.setNewData(null);
        }
        adapter.notifyDataSetChanged();

    }

    class MyAdapter extends BaseQuickAdapter<CoinListModel.AttachmentBean, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_search_currency_global);
        }

        @Override
        protected void convert(BaseViewHolder helper, final CoinListModel.AttachmentBean item) {
            helper.setText(R.id.tv_item_title, item.currencyNameEn);
            helper.setText(R.id.tv_item_sub_title, item.currencyName);
            GlideUtils.disPlay(getContext(), Constant.API_QINIU_URL + item.icoUrl, (ImageView) helper.getView(R.id.iv_item_icon));
            helper.itemView.setTag(item);
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CoinListModel.AttachmentBean model = (CoinListModel.AttachmentBean) v.getTag();
                    RecordEvent event = new RecordEvent(
                            model.currencyId,
                            model.currencyNameEn,
                            "",
                            ""
                    );
                    if (listener != null)
                        listener.onItemPress(event);
                    dismiss();
                }
            });
        }
    }


}
