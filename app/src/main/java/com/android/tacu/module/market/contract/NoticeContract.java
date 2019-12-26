package com.android.tacu.module.market.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.market.model.NoticeModel;

import java.util.List;

/**
 * Created by jiazhen on 2018/8/21.
 */
public class NoticeContract {

    public interface IView extends IBaseMvpView {
        void showNoticeList(List<NoticeModel> list);
    }

    public interface IPresenter {
        void getNoticeInfo(int page, int perPage, boolean isShowLoadingView);
    }
}
