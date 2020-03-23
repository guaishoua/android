package com.android.tacu.module.login.contract;

import com.android.tacu.base.IBaseMvpView;

/**
 * Created by jiazhen on 2018/8/16.
 */
public class RegisterContract {

    public interface IView extends IBaseMvpView {
        void showCodeMsg();

        void success();
    }

    public interface IPresenter {
        void sendEmailForRegister(String phoneCode, String email, String token);

        void register(String phoneCode, String email, String pwd, String vercode, String inviteId);
    }
}
