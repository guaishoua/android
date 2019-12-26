package com.android.tacu.EventBus;

import org.greenrobot.eventbus.EventBus;

/**
 * EventBus管理类
 * Created by jiazhen on 2018/9/3.
 */
public class EventManage {

    /**
     * 注册EventBus
     */
    public static void register(Object context){
        if (!EventBus.getDefault().isRegistered(context)) {
            EventBus.getDefault().register(context);
        }
    }

    /**
     * 注消EventBus
     */
    public static void unregister(Object context){
        if (EventBus.getDefault().isRegistered(context)) {
            EventBus.getDefault().unregister(context);
        }
    }

    /**
     * 发布订阅事件
     */
    public static void sendEvent(Object object){
        EventBus.getDefault().post(object);
    }

    /**
     * 发布粘性订阅事件
     */
    public static void sendStickyEvent(Object object){
        EventBus.getDefault().postSticky(object);
    }

    /**
     * 取消事件传送 事件取消仅限于ThreadMode.PostThread下才可以使用
     * 不取消事件就会一直存在
     */
    public static void cancelEventDelivery(Object event) {
        EventBus.getDefault().cancelEventDelivery(event);
    }

    /**
     * 移除指定的粘性订阅事件
     *
     * @param eventType 移除的内容
     */
    public static <T> void removeStickyEvent(Class<T> eventType) {
        T stickyEvent = EventBus.getDefault().getStickyEvent(eventType);
        if (stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
    }

    /**
     * 在栈底Activity里清除sticky数据
     */
    public static void removeAllStickyEvent(){
        EventBus.getDefault().removeAllStickyEvents();
    }
}
