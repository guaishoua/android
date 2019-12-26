package com.android.tacu.module.my.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by xiaohong on 2018/9/26.
 */

public class InvitedWinningActivity extends BaseActivity {
    @BindView(R.id.rl_title)
    RelativeLayout rl_title;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private List<String> list = new ArrayList<>();//测试数据
    private WinningAdapter winningAdapter;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_invited_record);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.winning_record));

        rl_title.setVisibility(View.GONE);
        for (int i = 0; i < 5; i++) {
            list.add("nieded" + i);
        }

        winningAdapter = new WinningAdapter();
        winningAdapter.setNewData(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(winningAdapter);
    }

    private class WinningAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public WinningAdapter() {
            super(R.layout.item_invited_winning);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.tv_out_date, item);
        }
    }
}
