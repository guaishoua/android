package com.android.tacu.module.auth.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.auth.model.SelectAuthLevelModel;

/**
 * Created by xiaohong on 2018/8/24.
 */

public class AuthContract {
    public interface IAuthView extends IBaseMvpView {
        void selectAuthLevel(SelectAuthLevelModel model);
    }

    public interface IAuthPresenter {
        void selectAuthLevel(boolean isShowView);
    }
}
