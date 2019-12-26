package com.android.tacu.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.android.tacu.R;
import com.android.tacu.utils.UIUtils;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by jiazhen on 2018/10/26.
 */
public abstract class BannerLoader extends ImageLoader {

    @Override
    public ImageView createImageView(Context context) {
        int imageWidth = UIUtils.getScreenWidth() - UIUtils.dp2px(30);
        int imageHeight = (int)(((double)imageWidth) / 5.7);
        QMUIRadiusImageView imageView = new QMUIRadiusImageView(context);
        imageView.setBackgroundColor(Color.TRANSPARENT);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setBorderColor(ContextCompat.getColor(context, R.color.color_transparent));
        imageView.setBorderWidth(UIUtils.dp2px(0));
        imageView.setSelectedBorderWidth(UIUtils.dp2px(0));
        imageView.setCornerRadius(imageHeight / 2);
        return imageView;
    }
}
