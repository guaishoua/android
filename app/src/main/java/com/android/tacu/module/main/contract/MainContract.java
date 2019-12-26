package com.android.tacu.module.main.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.main.model.ConvertModel;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.main.model.UploadModel;
import com.android.tacu.module.main.model.UuexSignModal;
import com.android.tacu.module.market.model.SelfModel;

/**
 * Created by jiazhen on 2018/8/13.
 */
public class MainContract {

    public interface IView extends IBaseMvpView {
        void upload(UploadModel model, boolean isTip);

        void ownCenter(OwnCenterModel model);

        void getSelfSelectionValue(SelfModel selfModel);

        void convertMoney(ConvertModel model);

        void uuexSign(UuexSignModal modal);
    }

    public interface IPresenter {
        void upload(String version, String channel, boolean isTip);

        void ownCenter();

        void getSelfList();

        void logout();

        void getConvertModel();

        void uuexSign();
    }
}
