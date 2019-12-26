package com.android.tacu.view.animations;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

/**
 * BusinessFragment的 旋转动画
 * Created by jiazhen on 2018/4/24.
 */

public class LayoutAnimation extends Animation {

    private boolean isFlag = false;
    private int centerX, centerY;
    private Camera camera = new Camera();

    public LayoutAnimation() {
    }

    public LayoutAnimation(boolean isFlag) {
        this.isFlag = isFlag;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        //获取中心点坐标
        centerX = width / 2;
        centerY = height / 2;
        //动画执行时间  自行定义
        setDuration(600);
        setInterpolator(new DecelerateInterpolator());
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final Matrix matrix = t.getMatrix();
        camera.save();
        //中心是绕Y轴旋转  这里可以自行设置X轴 Y轴 Z轴
        if (!isFlag){//顺时针
            camera.rotateY(180 * interpolatedTime);
        }else {//逆时针
            camera.rotateY(-180 * interpolatedTime);
        }
        //把我们的摄像头加在变换矩阵上
        camera.getMatrix(matrix);
        //设置翻转中心点
        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
        camera.restore();
    }
}
