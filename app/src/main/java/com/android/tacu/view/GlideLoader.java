package com.android.tacu.view;

import android.widget.ImageView;

import com.android.tacu.base.MyApplication;
import com.bumptech.glide.Glide;
import com.lcw.library.imagepicker.utils.ImageLoader;

public class GlideLoader implements ImageLoader {

    @Override
    public void loadImage(ImageView imageView, String imagePath) {
        //小图加载
        Glide.with(imageView.getContext()).load(imagePath).centerCrop().placeholder(0).error(0).fallback(0).into(imageView);
    }

    @Override
    public void loadPreImage(ImageView imageView, String imagePath) {
        //大图加载
        Glide.with(imageView.getContext()).load(imagePath).skipMemoryCache(true).placeholder(0).error(0).fallback(0).into(imageView);
    }

    @Override
    public void clearMemoryCache() {
        //清理缓存
        Glide.get(MyApplication.getInstance()).clearMemory();
    }
}
