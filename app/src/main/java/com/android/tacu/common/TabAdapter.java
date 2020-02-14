package com.android.tacu.common;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.utils.UIUtils;
import com.shizhefei.view.indicator.IndicatorViewPager;

import java.util.List;

public class TabAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

    private Activity context;
    private String[] tabTitle;
    private List<Fragment> fragmentList;

    public TabAdapter(FragmentManager fragmentManager, Activity context, String[] tabTitle, List<Fragment> fragmentList) {
        super(fragmentManager);
        this.context = context;
        this.tabTitle = tabTitle;
        this.fragmentList = fragmentList;
    }

    @Override
    public int getCount() {
        return tabTitle != null ? tabTitle.length : 0;
    }

    @Override
    public View getViewForTab(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = context.getLayoutInflater().inflate(R.layout.view_tab, container, false);
        }
        TextView textView = (TextView) convertView;
        textView.setText(tabTitle[position]);
        int padding = UIUtils.dp2px(10);
        textView.setPadding(padding, 0, padding, 0);
        return convertView;
    }

    @Override
    public Fragment getFragmentForPage(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        //这是ViewPager适配器的特点,有两个值 POSITION_NONE，POSITION_UNCHANGED，默认就是POSITION_UNCHANGED,
        // 表示数据没变化不用更新.notifyDataChange的时候重新调用getViewForPage
        return PagerAdapter.POSITION_UNCHANGED;
    }
}
