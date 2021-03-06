package com.android.tacu.common;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.utils.UIUtils;
import com.shizhefei.view.indicator.Indicator;

import java.util.Arrays;
import java.util.List;

public class TabIndicatorAdapter extends Indicator.IndicatorAdapter {

    private Context context;
    private List<String> tabTitle;

    public TabIndicatorAdapter(Context context, String[] tabTitle) {
        this.context = context;
        this.tabTitle = Arrays.asList(tabTitle);
    }

    public TabIndicatorAdapter(Context context, List<String> tabTitle) {
        this.context = context;
        this.tabTitle = tabTitle;
    }

    public void setTabTitle(List<String> tabTitle) {
        this.tabTitle = tabTitle;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return tabTitle.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = ((Activity) context).getLayoutInflater().inflate(R.layout.view_tab, container, false);
        }
        TextView textView = (TextView) convertView;
        textView.setText(tabTitle.get(position));
        int padding = UIUtils.dp2px(10);
        textView.setPadding(padding, 0, padding, 0);
        return convertView;
    }
}
