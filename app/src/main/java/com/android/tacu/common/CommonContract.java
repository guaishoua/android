package com.android.tacu.common;

import com.android.tacu.module.my.model.InvitedInfoModel;

public class CommonContract {

    public interface IView {
        void onInviteInfoReceieved(InvitedInfoModel model);
    }

    public interface IPresenter {
        void fetchInvitedInfo(int uid);
    }
}
