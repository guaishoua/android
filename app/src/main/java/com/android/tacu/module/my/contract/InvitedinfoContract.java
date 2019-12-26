package com.android.tacu.module.my.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.my.model.InvitedAllModel;

/**
 * Created by jiazhen on 2018/8/26.
 */
public class InvitedinfoContract {

    public interface IView extends IBaseMvpView {
        void showInvitedInfo(InvitedAllModel model);
    }

    public interface IPresenter {
        void getInvitedInfo(Integer page, Integer size);
    }
}
