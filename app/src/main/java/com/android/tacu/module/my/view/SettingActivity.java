package com.android.tacu.module.my.view;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import butterknife.BindView;

/**
 * Created by jiazhen on 2018/10/21.
 */
public class SettingActivity extends BaseActivity {

    @BindView(R.id.groupListView)
    QMUIGroupListView groupListView;

    private String convertString;
    private String languageString;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_setting);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.settings));

        convertString = getResources().getString(R.string.convert_money);
        QMUICommonListItemView itemWithConvert = groupListView.createItemView(convertString);
        itemWithConvert.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        setItemViewLine(itemWithConvert, false);

        languageString = getResources().getString(R.string.language);
        QMUICommonListItemView itemLanguage = groupListView.createItemView(languageString);
        itemLanguage.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        setItemViewLine(itemLanguage, true);

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
                .addItemView(itemWithConvert, onClickListener)
                .addItemView(itemLanguage, onClickListener)
                .addTo(groupListView);
    }

    private void setItemViewLine(QMUICommonListItemView itemView, boolean isboolean) {
        if (isboolean) {
            itemView.setLineView(ContextCompat.getColor(this, R.color.content_bg_color_grey));
        } else {
            itemView.setLineView(ContextCompat.getColor(this, R.color.color_transparent));
        }
    }

    private void mOnClickListenter(String text) {
        if (TextUtils.equals(text, convertString)) {
            jumpTo(ConvertActivity.class);
        } else if (TextUtils.equals(text, languageString)) {
            jumpTo(LanguageActivity.class);
        }
    }
}
