package com.android.tacu.module.my.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.main.model.UploadModel;

/**
 * Created by xiaohong on 2018/8/24.
 */

public class MyContract {

    public interface IMyView extends IBaseMvpView {
        void success();

        void upload(UploadModel model);
    }

    public interface IMyPresenter {
        void upload(String version, String channel);

        void logout();
    }
}
