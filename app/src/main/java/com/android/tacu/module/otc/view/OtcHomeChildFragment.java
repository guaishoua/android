package com.android.tacu.module.otc.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.otc.contract.OtcHomeChildContract;
import com.android.tacu.module.otc.presenter.OtcHomeChildPresenter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import butterknife.BindView;

public class OtcHomeChildFragment extends BaseFragment<OtcHomeChildPresenter> implements OtcHomeChildContract.IView, View.OnClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private TextView tv_buy_sell;
    private TextView tv_real_price;
    private QMUIRoundButton btn_gointo_market;
    private TextView tv_24h_range;
    private ImageView img_24h_range;
    private TextView tv_24h_maxprice;
    private TextView tv_24h_minprice;

    private int currencyId;
    private String currencyNameEn;

    private OtcDepthAdapter otcDepthAdapter;

    public static OtcHomeChildFragment newInstance(int currencyId, String currencyNameEn) {
        Bundle bundle = new Bundle();
        bundle.putInt("currencyId", currencyId);
        bundle.putString("currencyNameEn", currencyNameEn);
        OtcHomeChildFragment fragment = new OtcHomeChildFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            currencyId = bundle.getInt("currencyId");
            currencyNameEn = bundle.getString("currencyNameEn");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_otc_home_child;
    }

    @Override
    protected void initData() {
        otcDepthAdapter = new OtcDepthAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(otcDepthAdapter);

        initHeader();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_gointo_market:
                jumpTo(OtcMarketListActivity.createActivity(getContext(), currencyNameEn));
                break;
        }
    }

    private void initHeader() {
        View headerView = View.inflate(getContext(), R.layout.header_otc_home_child, null);
        tv_buy_sell = headerView.findViewById(R.id.tv_buy_sell);
        tv_real_price = headerView.findViewById(R.id.tv_real_price);
        btn_gointo_market = headerView.findViewById(R.id.btn_gointo_market);
        tv_24h_range = headerView.findViewById(R.id.tv_24h_range);
        img_24h_range = headerView.findViewById(R.id.img_24h_range);
        tv_24h_maxprice = headerView.findViewById(R.id.tv_24h_maxprice);
        tv_24h_minprice = headerView.findViewById(R.id.tv_24h_minprice);

        btn_gointo_market.setOnClickListener(this);

        otcDepthAdapter.setHeaderView(headerView);
    }

    public class OtcDepthAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public OtcDepthAdapter() {
            super(R.layout.item_otc_depth);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {

        }
    }
}
