package com.android.tacu.module.my.contract;

import com.android.tacu.base.IBaseMvpView;

/**
 * Created by xiaohong on 2018/8/28.
 */

public class ChangePwdContact {
    public interface Iview extends IBaseMvpView {
        void bindStatus();
    }

    public interface ITradeView extends IBaseMvpView {
        void bindSendMsg();

        void validCode();
    }

    public interface IPresenter {
        void resetPwdInUserCenter(String newPwd, String pwd);

        void bindPhoneSendMsg(String phoneCode, String phone, int type, String token);

        void validCode(int type, String vercode);

        void bindFdpwd(String newPwd, String oldPwd, String loginPassWord);
    }
}
