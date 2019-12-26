package com.android.tacu.module.my.contract;

import com.android.tacu.base.BaseModel;
import com.android.tacu.base.IBaseMvpView;

/**
 * Created by xiaohong on 2018/8/28.
 */

public class BindContact {

    public interface IBindView extends IBaseMvpView {
        void verifySuccess();

        void bindStatus();

        void showCodeStatus(BaseModel model);
    }

    public interface IPresenter {
        void validCode(int type, String vercode);

        void bindPhone(String phoneCode, String code, String oldVercode, String newPhone, String oldPhone);

        void bindPhoneSendMsg(String phoneCode, String phone, int type, String token);
    }
}
