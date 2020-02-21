package com.android.tacu.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * glide图片加载工具
 * Created by jiazhen on 2018/4/11.
 */

public class GlideUtils {

    /**
     * 说明
     * placeholder：占位即加载中的图片
     * error：错误图片
     * fallback：当url为null的时候，判断是否设置了fallback，是的话则显示fallback图片，否的话显示error图片，如果error还是没有设置则显示placeholder图片
     * <p>
     * 1.禁止内存缓存：.skipMemoryCache(true)
     * 2.禁止磁盘缓存：.diskCacheStrategy(DiskCacheStrategy.NONE)
     * <p>
     * 遇到的问题：当Glide用于将图片加载到自定义的Imageview时，当设置占位图时，加载的图片无法显示。
     * 解决的方法：设置一个显示动画，原因不明，Glide.with(context).load(url).animate(R.anim.anim).placeholder(default_resId).error(default_resId).fallback(default_resId).into(imageView);
     */

    //默认图片
    private static int default_resId = 0;
    //默认圆形图片
    private static int default_circle_resId = 0;

    /**
     * 默认加载
     */
    public static void disPlay(Context context, String url, ImageView imageView) {
        disPlay(context, url, default_resId, imageView);
    }

    public static void disPlay(Context context, String url, int errorResId, ImageView imageView) {
        Glide.with(context).load(url).placeholder(errorResId).error(errorResId).fallback(errorResId).into(imageView);
    }

    /**
     * 资源文件
     *
     * @param context
     * @param url
     * @param imageView
     */
    public static void disPlay(Context context, int url, ImageView imageView) {
        Glide.with(context).load(url).placeholder(default_resId).error(default_resId).fallback(default_resId).into(imageView);
    }

    /**
     * 从手机sd路径获取图片
     */
    public static void disPlay(Context context, File file, ImageView imageView) {
        Glide.with(context).load(file).placeholder(default_resId).error(default_resId).fallback(default_resId).into(imageView);
    }

    /**
     * 默认加载圆形图片
     */
    public static void disPlayCircle(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).placeholder(default_circle_resId).error(default_circle_resId).fallback(default_circle_resId).bitmapTransform(new CropCircleTransformation(context)).into(imageView);
    }

    /**
     * 从手机sd路径获取圆形图片
     */
    public static void disPlayCircle(Context context, File file, ImageView imageView) {
        Glide.with(context).load(file).placeholder(default_circle_resId).error(default_circle_resId).fallback(default_circle_resId).bitmapTransform(new CropCircleTransformation(context)).into(imageView);
    }

    /**
     * 加载指定大小
     */
    public static void disPlay(Context context, String url, int width, int height, ImageView imageView) {
        Glide.with(context).load(url).override(width, height).placeholder(default_resId).error(default_resId).fallback(default_resId).into(imageView);
    }

    /**
     * 加载圆角图片
     */
    public static void disPlayRound(Context context, String url, int radius, int margin, ImageView imageView) {
        Glide.with(context).load(url).placeholder(default_resId).error(default_resId).fallback(default_resId).bitmapTransform(new RoundedCornersTransformation(context, radius, margin)).into(imageView);
    }

    /**
     * 设置缩略图支持
     * sizeMultiplier：指定0.0f~1.0f的原始图像大小（1.0f代表原图大小）
     */
    public static void disPlayThumbnail(Context context, String url, float sizeMultiplier, ImageView imageView) {
        Glide.with(context).load(url).thumbnail(sizeMultiplier).placeholder(default_resId).error(default_resId).fallback(default_resId).into(imageView);
    }

    /**
     * 设置加载动画
     * animate：可以设置xml文件定义的4种补间动画(alpha、scale、translate、rotate)
     */
    public static void disPlayAnim(Context context, String url, int anim, ImageView imageView) {
        Glide.with(context).load(url).animate(anim).placeholder(default_resId).error(default_resId).fallback(default_resId).into(imageView);
    }

    /**
     * 加载动态GIF
     * asGIF：希望加载的只是gif,如果不是gif就显示错误图片
     */
    public static void disPlayAsGif(Context context, int url, ImageView imageView) {
        Glide.with(context).load(url).asGif().placeholder(default_resId).error(default_resId).fallback(default_resId).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
    }

    /**
     * 加载静态GIF
     * asBitmap：希望加载gif时只加载gif的第一帧,把gif当作普通图片一样加载
     */
    public static void disPlayAsBitmap(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).asBitmap().placeholder(default_resId).error(default_resId).fallback(default_resId).into(imageView);
    }

    /**
     * 高斯模糊
     */
    public static void disPlayBlur(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).placeholder(default_resId).error(default_resId).fallback(default_resId).bitmapTransform(new BlurTransformation(context)).into(imageView);
    }

    /**
     * 清理内存缓存
     */
    public static void GuideClearMemory(Context context) {
        //清理内存缓存 必须在UI线程中调用
        Glide.get(context).clearMemory();
    }

    /**
     * 清理磁盘缓存
     */
    public static void glideClearDiskCache(Context context) {
        //理磁盘缓存 必须在子线程中执行
        Glide.get(context).clearDiskCache();
    }
}
