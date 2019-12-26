package com.android.tacu.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * LinearLayoutManager设置Item间距的的一个辅助类
 * Created by jiazhen on 2019/3/13.
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int leftSpace;
    private int rightSpace;
    private int topSpace;
    private int bottomSpace;

    public SpacesItemDecoration(int space) {
        this.leftSpace = space;
        this.rightSpace = space;
        this.topSpace = space;
        this.bottomSpace = space;
    }

    public SpacesItemDecoration(int leftSpace, int rightSpace, int topSpace, int bottomSpace) {
        this.leftSpace = leftSpace;
        this.rightSpace = rightSpace;
        this.topSpace = topSpace;
        this.bottomSpace = bottomSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = leftSpace;
        outRect.right = rightSpace;
        outRect.bottom = bottomSpace;

        if (parent.getChildPosition(view) == 0)
            outRect.top = topSpace;
    }
}