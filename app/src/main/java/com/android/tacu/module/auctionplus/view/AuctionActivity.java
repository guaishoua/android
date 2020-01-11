package com.android.tacu.module.auctionplus.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.widget.NoSlideViewPager;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;

import butterknife.BindView;

/**
 * Created by jiazhen on 2019/6/3.
 */
public class AuctionActivity extends BaseActivity {

    @BindView(R.id.viewpager)
    NoSlideViewPager viewpager;

    private Fragment[] fragments;
    private AuctionPlusListFragment auctionPlusFragment;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_auction);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle("Auction Plus");
        mTopBar.addRightView(generateTopBarTextRightButton(getResources().getString(R.string.plus_win_record)), R.id.qmui_topbar_item_right);

        initFragment();
    }

    private QMUIAlphaButton generateTopBarTextRightButton(String text) {
        QMUIAlphaButton button = new QMUIAlphaButton(this);
        button.setBackgroundResource(0);
        button.setMinWidth(0);
        button.setMinHeight(0);
        button.setMinimumWidth(0);
        button.setMinimumHeight(0);
        button.setPadding(UIUtils.dp2px(10), UIUtils.dp2px(8), 0, 0);
        button.setTextColor(ContextCompat.getColor(this, R.color.text_color));
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, UIUtils.sp2px(12));
        button.setGravity(Gravity.RIGHT);
        button.setText(text);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spUtil.getLogin()) {
                    jumpTo(AuctionWinRecordActivity.class);
                } else {
                    jumpTo(LoginActivity.class);
                }
            }
        });
        return button;
    }

    private void initFragment() {
        auctionPlusFragment = AuctionPlusListFragment.newInstance();
        fragments = new Fragment[]{auctionPlusFragment};

        viewpager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments));
        viewpager.setOffscreenPageLimit(2);
        viewpager.setCurrentItem(0);
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private Fragment[] list;

        public MyFragmentPagerAdapter(FragmentManager fm, Fragment[] list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list[position];
        }

        @Override
        public int getCount() {
            return list.length;
        }
    }
}
