package com.android.tacu.module.my.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.main.model.GoogleAuthModel;

/**
 * Created by jiazhen on 2018/8/27.
 */
public class GoogleAuthContact {

    public interface IView extends IBaseMvpView {
        void success();

        void sendMsgSuccess();

        void colseSuccess();
    }

    public interface IGAView extends IBaseMvpView {
        void getSecretKey(GoogleAuthModel model);
    }

    public interface IPresenter {
        void getSecretKey();

        void bindgaSendMsg(int type, String sliderToken);

        void bindGoogleAuth(String gaPwd, String pwd, String vercode, int type);

        void closeGoogleAuth(String clientPassword, String loginPassword, String vercode, int type);
    }
}
