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
import com.android.tacu.module.main.view.MainActivity;
import com.android.tacu.utils.ActivityStack;
import com.android.tacu.utils.LanguageUtils;
import com.android.tacu.utils.UIUtils;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * 切换语言
 * public static final String DE_DE = "de_DE"; //德国
 * public static final String ES_ES = "es_ES"; //西班牙
 * public static final String FR_FR = "fr_FR"; //法国
 * public static final String IT_IT = "it_IT"; //意大利
 * public static final String NL_NL = "nl_NL"; //荷兰
 * public static final String RU_RU = "ru_RU"; //俄国
 * public static final String VI_VN = "vi_VN"; //越南
 * public static final String KO_KR = "ko_KR"; //韩国
 */
public class LanguageActivity extends BaseActivity {

    @BindView(R.id.groupListView)
    QMUIGroupListView groupListView;

    private ImageView imgZH_CN;
    private ImageView imgZH_TW;
    private ImageView imgEN_US;
    private List<ImageView> imgList = new ArrayList<>();

    private String ZH_CNString;
    private String ZH_TWString;
    private String EN_USString;

    private String currentLanguage;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_language);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.language));

        ZH_CNString = "中文(简体)";
        ZH_TWString = "中文(繁體)";
        EN_USString = "English";

        QMUICommonListItemView itemWithZH_CN = groupListView.createItemView(ZH_CNString);
        itemWithZH_CN.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        imgZH_CN = getImageView();
        itemWithZH_CN.addAccessoryCustomView(imgZH_CN);
        setItemViewLine(itemWithZH_CN, false);

        QMUICommonListItemView itemWithZH_TW = groupListView.createItemView(ZH_TWString);
        itemWithZH_TW.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        imgZH_TW = getImageView();
        itemWithZH_TW.addAccessoryCustomView(imgZH_TW);
        setItemViewLine(itemWithZH_TW, true);

        QMUICommonListItemView itemWithEN_US = groupListView.createItemView(EN_USString);
        itemWithEN_US.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        imgEN_US = getImageView();
        itemWithEN_US.addAccessoryCustomView(imgEN_US);
        setItemViewLine(itemWithEN_US, true);

        imgList.add(imgZH_CN);
        imgList.add(imgZH_TW);
        imgList.add(imgEN_US);

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
                .addItemView(itemWithZH_CN, onClickListener)
                .addItemView(itemWithZH_TW, onClickListener)
                .addItemView(itemWithEN_US, onClickListener)
                .addTo(groupListView);

        currentLanguage = spUtil.getLanguage();
        switch (currentLanguage) {
            case Constant.ZH_CN:
                imgZH_CN.setImageResource(R.drawable.icon_check_circle_default);
                break;
            case Constant.ZH_TW:
                imgZH_TW.setImageResource(R.drawable.icon_check_circle_default);
                break;
            case Constant.EN_US:
                imgEN_US.setImageResource(R.drawable.icon_check_circle_default);
                break;

        }
    }

    private ImageView getImageView() {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(UIUtils.dp2px(20), UIUtils.dp2px(20)));
        imageView.setImageResource(R.drawable.icon_check_circle_grey);
        return imageView;
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
        if (TextUtils.equals(text, ZH_CNString)) {
            setLanguage(imgZH_CN, Constant.ZH_CN, LanguageUtils.SIMPLIFIED_CHINESE);
        } else if (TextUtils.equals(text, ZH_TWString)) {
            setLanguage(imgZH_TW, Constant.ZH_TW, LanguageUtils.TRADITIONAL_CHINESE);
        } else if (TextUtils.equals(text, EN_USString)) {
            setLanguage(imgEN_US, Constant.EN_US, LanguageUtils.LOCALE_ENGLISH);
        }
    }

    private void setLanguage(ImageView image, String current, Locale locale) {
        image.setImageResource(R.drawable.icon_check_circle_default);
        if (!TextUtils.equals(currentLanguage, current)) {
            currentLanguage = current;
            spUtil.setLanguage(current);
            if (LanguageUtils.needUpdateLocale(locale)) {
                LanguageUtils.updateLocale(locale);
            }
            restartActivity();
        }
    }

    private void restartActivity() {
        ActivityStack.getInstance().finishActivity(MainActivity.class);
        Intent[] intents = {new Intent(this, MainActivity.class), new Intent(this, LanguageActivity.class)};
        startActivities(intents);
        finish();
        //这里设置 是为了防止动画影响体验效果 加上overridePendingTransition(0, 0)这句话 让用户感觉不到Activity被重新启动了一次
        overridePendingTransition(0, 0);
    }
}
