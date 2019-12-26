package com.android.tacu.module.auth.contract;

import com.android.tacu.base.BaseModel;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.auth.model.AwsModel;
import com.android.tacu.module.auth.model.UserInfoModel;

/**
 * Created by xiaohong on 2018/9/18.
 */

public class RealNameContract {
    public interface IRealView extends IBaseMvpView {
        void authinfoNew(UserInfoModel userInfoModel);

        void authNewSuccess();
    }

    public interface IRealTwoView {
        void authThredSuccess(BaseModel baseModel);

        void authNewSuccess();

        void getOssSetting(AuthOssModel model);

        void onError();

        void getAwsSetting(AwsModel model);
    }

    public interface IRealNamePresenter {
        void authinfoNew();

        void authNew(String country, String firstName, String secondName, String idNumber, String birthday, String gender, String isChina, int step, String singnTime, String outofTime, int isAllTime, int currentLocation);

        void getOssSetting();

        void authnewPositive(String authnewPositive, String ischina, int step);

        void authnewOpposite(String authnewOpposite, String ischina, int step);

        void authnewHand(String authnewHand, String ischina, int step);

        void getAwsSetting();
    }
}
