package com.android.tacu.module.otc.view;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.MyFragmentPagerAdapter;
import com.android.tacu.module.auth.view.AuthMerchantActivity;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.otc.contract.OtcManageContract;
import com.android.tacu.module.otc.presenter.OtcManagePresenter;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.utils.user.UserManageUtils;
import com.android.tacu.widget.NoSlideViewPager;
import com.android.tacu.widget.popupwindow.ListPopWindow;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class OtcManageActivity extends BaseActivity<OtcManagePresenter> implements OtcManageContract.IView {

    @BindView(R.id.viewpager)
    NoSlideViewPager viewpager;
    @BindView(R.id.img_on)
    ImageView img_on;
    @BindView(R.id.tv_on)
    TextView tv_on;
    @BindView(R.id.lin_status)
    LinearLayout lin_status;
    @BindView(R.id.tv_status)
    TextView tv_status;

    private String[] tabTitle;
    private List<Fragment> fragmentList = new ArrayList<>();

    private ListPopWindow listPopup;

    private OwnCenterModel ownCenterModel;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_manage);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.otc_manage));

        tabTitle = new String[]{getResources().getString(R.string.all), getResources().getString(R.string.buy_order), getResources().getString(R.string.sell_order)};
        tv_status.setText(tabTitle[0]);

        fragmentList.add(OtcManageFragment.newInstance(0));
        fragmentList.add(OtcManageFragment.newInstance(1));
        fragmentList.add(OtcManageFragment.newInstance(2));

        viewpager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        viewpager.setOffscreenPageLimit(tabTitle.length - 1);
        setCurr(0);
    }

    @Override
    protected OtcManagePresenter createPresenter(OtcManagePresenter mPresenter) {
        return new OtcManagePresenter();
    }

    @Override
    protected void onPresenterCreated(OtcManagePresenter presenter) {
        super.onPresenterCreated(presenter);
        mPresenter.ownCenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listPopup != null && listPopup.isShowing()) {
            listPopup.dismiss();
        }
    }

    @OnClick(R.id.btn_on)
    void btnOnClick() {
        //0=下线 1=上线
        if (ownCenterModel != null) {
            if (ownCenterModel.merchantStatus != null && ownCenterModel.merchantStatus == 0) {
                mPresenter.merchanton();
            } else if (ownCenterModel.merchantStatus != null && ownCenterModel.merchantStatus == 1) {
                mPresenter.merchantoff();
            }
        }
    }

    @OnClick(R.id.tv_status)
    void statusClick() {
        showStatusType();
    }

    @OnClick(R.id.btn_guanggao)
    void publishClick() {
        if (spUtil.getApplyMerchantStatus() != 2 && spUtil.getApplyAuthMerchantStatus() != 2) {
            showToastError(getResources().getString(R.string.you_art_shoper_first));
            return;
        }
        jumpTo(OtcPublishActivity.class);
    }

    @OnClick(R.id.btn_auth)
    void certifiedShoperClick() {
        jumpTo(AuthMerchantActivity.class);
    }

    @Override
    public void ownCenter(OwnCenterModel model) {
        this.ownCenterModel = model;
        UserManageUtils.setPersonInfo(model);
        showMerchantStatus();
    }

    @Override
    public void merchantoff() {
        mPresenter.ownCenter();
    }

    @Override
    public void merchanton() {
        mPresenter.ownCenter();
    }

    //0=下线 1=上线
    private void showMerchantStatus() {
        if (ownCenterModel != null) {
            if (ownCenterModel.merchantStatus != null && ownCenterModel.merchantStatus == 0) {
                tv_on.setTextColor(ContextCompat.getColor(this, R.color.text_grey));
                tv_on.setText(getResources().getString(R.string.offline));
                img_on.setImageResource(R.drawable.icon_people_grey);
            } else if (ownCenterModel.merchantStatus != null && ownCenterModel.merchantStatus == 1) {
                tv_on.setTextColor(ContextCompat.getColor(this, R.color.text_default));
                tv_on.setText(getResources().getString(R.string.online));
                img_on.setImageResource(R.drawable.icon_people_default);
            }
        }
    }

    private void showStatusType() {
        if (listPopup != null && listPopup.isShowing()) {
            listPopup.dismiss();
            return;
        }
        if (listPopup == null) {
            ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, tabTitle);
            listPopup = new ListPopWindow(this, adapter);
            listPopup.create(UIUtils.dp2px(100), UIUtils.dp2px(120), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tv_status.setText(tabTitle[position]);
                    setCurr(position);
                    listPopup.dismiss();
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            listPopup.setDropDownGravity(Gravity.START);
            listPopup.setHorizontalOffset(UIUtils.dp2px(10));
        }
        listPopup.setAnchorView(lin_status);
        listPopup.show();
    }

    private void setCurr(int position) {
        viewpager.setCurrentItem(position, false);
    }
}
