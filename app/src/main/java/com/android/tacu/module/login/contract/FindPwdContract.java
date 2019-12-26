package com.android.tacu.module.login.contract;

import com.android.tacu.base.IBaseMvpView;

/**
 * Created by jiazhen on 2018/8/16.
 */
public class FindPwdContract {

    public interface IView extends IBaseMvpView {
        void sendMailSuccess();

        void resetSuccess();
    }

    public interface IPresenter {
        void sendMail(String phoneCode, String email, String token);

        void resetPwd(String phoneCode, String email, String newPwd, String vercode);
    }
}
