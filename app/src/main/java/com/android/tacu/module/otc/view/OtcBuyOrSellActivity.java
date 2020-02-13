package com.android.tacu.module.otc.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.utils.UIUtils;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class OtcBuyOrSellActivity extends BaseOtcHalfOrderActvity {

    @BindView(R.id.base_scrollIndicatorView)
    ScrollIndicatorView payIndicatorView;

    private boolean isBuy = true;
    private List<String> tabTitle = new ArrayList<>();

    public static Intent createActivity(Context context, boolean isBuy) {
        Intent intent = new Intent(context, OtcOrderDetailActivity.class);
        intent.putExtra("isBuy", isBuy);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_buy_sell);
    }

    @Override
    protected void initView() {
        isBuy = getIntent().getBooleanExtra("isBuy", true);
        if (isBuy) {
            mTopBar.setTitle(getResources().getString(R.string.otc_buy_page));
        } else {
            mTopBar.setTitle(getResources().getString(R.string.otc_sell_page));
        }

        tabTitle.add(getResources().getString(R.string.zhifubao));
        tabTitle.add(getResources().getString(R.string.weixin));
        tabTitle.add(getResources().getString(R.string.yinhanngka));

        payIndicatorView.setBackgroundColor(ContextCompat.getColor(this, R.color.tab_bg_color));
        payIndicatorView.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(this, R.color.text_default), ContextCompat.getColor(this, R.color.tab_text_color)).setSize(14, 14));
        payIndicatorView.setScrollBar(new ColorBar(this, ContextCompat.getColor(this, R.color.text_default), 4));
        payIndicatorView.setSplitAuto(true);
        payIndicatorView.setAdapter(new TabIndicatorAdapter());
        payIndicatorView.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
            }
        });
    }

    public class TabIndicatorAdapter extends Indicator.IndicatorAdapter {
        @Override
        public int getCount() {
            return tabTitle.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.view_tab, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(tabTitle.get(position));
            int padding = UIUtils.dp2px(10);
            textView.setPadding(padding, 0, padding, 0);
            return convertView;
        }
    }
}
