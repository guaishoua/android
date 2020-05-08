package com.android.tacu.module.my.view;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.InvitedRecordEvent;
import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.TabAdapter;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by xiaohong on 2018/9/26.
 */

public class InvitedRecordActivity extends BaseActivity {

    @BindView(R.id.lin_invited_me)
    LinearLayout lin_invited_me;
    @BindView(R.id.tv_invitedme_uid)
    TextView tv_invitedme_uid;
    @BindView(R.id.tv_invitedme_account)
    TextView tv_invitedme_account;

    @BindView(R.id.view_line)
    View view_line;

    @BindView(R.id.magic_indicator)
    ScrollIndicatorView magic_indicator;
    @BindView(R.id.vp)
    ViewPager viewPager;

    private List<String> tabTitle = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void setView() {
        setContentView(R.layout.activity_invited_record);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.invited_record));

        tabTitle.add(getResources().getString(R.string.real_name));
        tabTitle.add(getResources().getString(R.string.not_real_name));

        fragmentList.add(InvitedRecordFragment.newInstance(1));
        fragmentList.add(InvitedRecordFragment.newInstance(0));

        magic_indicator.setBackgroundColor(ContextCompat.getColor(this, R.color.tab_bg_color));
        magic_indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(this, R.color.text_default), ContextCompat.getColor(this, R.color.tab_text_color)).setSize(14, 14));
        magic_indicator.setScrollBar(new TextWidthColorBar(this, magic_indicator, ContextCompat.getColor(this, R.color.text_default), 4));
        magic_indicator.setSplitAuto(true);

        IndicatorViewPager indicatorViewPager = new IndicatorViewPager(magic_indicator, viewPager);
        indicatorViewPager.setAdapter(new TabAdapter(getSupportFragmentManager(), this, tabTitle, fragmentList));
        viewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        viewPager.setCurrentItem(0, false);
    }

    @Override
    protected void receiveEvent(BaseEvent event) {
        super.receiveEvent(event);

        if (event != null) {
            switch (event.getCode()) {
                case EventConstant.Invited_Record:
                    InvitedRecordEvent recordEvent = (InvitedRecordEvent) event.getData();
                    String uid = recordEvent.getInvited_uid();
                    String name = recordEvent.getInvited_name();
                    if (!TextUtils.isEmpty(uid) || !TextUtils.isEmpty(name)) {
                        lin_invited_me.setVisibility(View.VISIBLE);
                        view_line.setVisibility(View.VISIBLE);
                        tv_invitedme_uid.setText(uid);
                        tv_invitedme_account.setText(name);
                    } else {
                        lin_invited_me.setVisibility(View.GONE);
                        view_line.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    }
}
