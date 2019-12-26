package com.android.tacu.socket;

/**
 * Created by jiazhen on 2018/8/1.
 */

public interface IEmitterListener {

    /**
     * 监听结果
     *
     * @param key
     * @param args
     */
    void emitterListenerResut(String key, Object... args);
}
