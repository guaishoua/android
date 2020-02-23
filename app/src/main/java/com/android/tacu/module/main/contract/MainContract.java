package com.android.tacu.module.main.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.main.model.ConvertModel;
import com.android.tacu.module.main.model.HomeModel;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.main.model.UploadModel;
import com.android.tacu.module.market.model.SelfModel;

import java.util.List;

/**
 * Created by jiazhen on 2018/8/13.
 */
public class MainContract {

    public interface IView extends IBaseMvpView {
        void upload(UploadModel model, boolean isTip);

        void home(HomeModel homeModel);

        void ownCenter(OwnCenterModel model);

        void getSelfSelectionValue(SelfModel selfModel);

        void convertMoney(ConvertModel model);

        void selectBank(List<PayInfoModel> list);
    }

    public interface IPresenter {
        void upload(String version, String channel, boolean isTip);

        void getHome(boolean isShowLoadingView);

        void ownCenter();

        void getSelfList();

        void logout();

        void getConvertModel();

        void selectBank();
    }
}
