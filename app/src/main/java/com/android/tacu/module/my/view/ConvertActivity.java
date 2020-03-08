package com.android.tacu.module.my.view;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.assets.view.AssetsActivity;
import com.android.tacu.module.assets.view.AssetsCenterActivity;
import com.android.tacu.module.main.view.MainActivity;
import com.android.tacu.utils.ActivityStack;
import com.android.tacu.utils.ConvertMoneyUtils;
import com.android.tacu.utils.UIUtils;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by jiazhen on 2018/10/20.
 */
public class ConvertActivity extends BaseActivity {

    @BindView(R.id.groupListView)
    QMUIGroupListView groupListView;

    private ImageView imgUSD;
    private ImageView imgEUR;
    private ImageView imgCNY;

    private List<ImageView> imgList = new ArrayList<>();

    private String USDString;
    private String EURString;
    private String CNYString;

    private String currentConvert;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_convert);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.convert_money));

        USDString = getResources().getString(R.string.convert_USD);
        EURString = getResources().getString(R.string.convert_EUR);
        CNYString = getResources().getString(R.string.convert_CNY);

        QMUICommonListItemView itemWithUSD = groupListView.createItemView(USDString);
        itemWithUSD.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        imgUSD = getImageView();
        itemWithUSD.addAccessoryCustomView(imgUSD);
        setItemViewLine(itemWithUSD, false);

        QMUICommonListItemView itemWithEUR = groupListView.createItemView(EURString);
        itemWithEUR.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        imgEUR = getImageView();
        itemWithEUR.addAccessoryCustomView(imgEUR);
        setItemViewLine(itemWithEUR, true);

        QMUICommonListItemView itemWithCNY = groupListView.createItemView(CNYString);
        itemWithCNY.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        imgCNY = getImageView();
        itemWithCNY.addAccessoryCustomView(imgCNY);
        setItemViewLine(itemWithCNY, true);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof QMUICommonListItemView) {
                    String text = (String) ((QMUICommonListItemView) v).getText();
                    mOnClickListenter(text);
                }
            }
        };

        QMUIGroupListView.newSection(this)
                .setUseTitleViewForSectionSpace(false)
                .addItemView(itemWithUSD, onClickListener)
                .addItemView(itemWithEUR, onClickListener)
                .addItemView(itemWithCNY, onClickListener)
                .addTo(groupListView);

        imgList.add(imgUSD);
        imgList.add(imgEUR);
        imgList.add(imgCNY);

        currentConvert = spUtil.getConvertMoney();
        switch (currentConvert) {
            case Constant.USD:
                imgUSD.setImageResource(R.drawable.icon_check_circle_default);
                break;
            case Constant.EUR:
                imgEUR.setImageResource(R.drawable.icon_check_circle_default);
                break;
            case Constant.CNY:
                imgCNY.setImageResource(R.drawable.icon_check_circle_default);
                break;
        }
    }

    private void setItemViewLine(QMUICommonListItemView itemView, boolean isboolean) {
        if (isboolean) {
            itemView.setLineView(ContextCompat.getColor(this, R.color.content_bg_color_grey));
        } else {
            itemView.setLineView(ContextCompat.getColor(this, R.color.color_transparent));
        }
    }

    private void mOnClickListenter(String text) {
        for (int i = 0; i < imgList.size(); i++) {
            imgList.get(i).setImageResource(R.drawable.icon_check_circle_grey);
        }
        if (TextUtils.equals(text, USDString)) {
            imgUSD.setImageResource(R.drawable.icon_check_circle_default);
            if (!TextUtils.equals(currentConvert, Constant.USD)) {
                currentConvert = Constant.USD;
                spUtil.setConvertMoney(Constant.USD);
                ConvertMoneyUtils.changeConvertMoney(Constant.USD);
                restartActivity();
            }
        } else if (TextUtils.equals(text, EURString)) {
            imgEUR.setImageResource(R.drawable.icon_check_circle_default);
            if (!TextUtils.equals(currentConvert, Constant.EUR)) {
                currentConvert = Constant.EUR;
                spUtil.setConvertMoney(Constant.EUR);
                ConvertMoneyUtils.changeConvertMoney(Constant.EUR);
                restartActivity();
            }
        } else if (TextUtils.equals(text, CNYString)) {
            imgCNY.setImageResource(R.drawable.icon_check_circle_default);
            if (!TextUtils.equals(currentConvert, Constant.CNY)) {
                currentConvert = Constant.CNY;
                spUtil.setConvertMoney(Constant.CNY);
                ConvertMoneyUtils.changeConvertMoney(Constant.CNY);
                restartActivity();
            }
        }
    }

    private ImageView getImageView() {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(UIUtils.dp2px(20), UIUtils.dp2px(20)));
        imageView.setImageResource(R.drawable.icon_check_circle_grey);
        return imageView;
    }

    private void restartActivity() {
        ActivityStack.getInstance().finishActivity(MainActivity.class);
        ActivityStack.getInstance().finishActivity(AssetsCenterActivity.class);
        Intent[] intents = {new Intent(this, MainActivity.class), new Intent(this, ConvertActivity.class)};
        startActivities(intents);
        finish();
        //这里设置 是为了防止动画影响体验效果 加上overridePendingTransition(0, 0)这句话 让用户感觉不到Activity被重新启动了一次
        overridePendingTransition(0, 0);
    }
}
