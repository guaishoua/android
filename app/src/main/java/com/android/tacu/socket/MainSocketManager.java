package com.android.tacu.socket;

/**
 * Created by jiazhen on 2018/8/1.
 */

public class MainSocketManager extends BaseSocketManager {

    private EmitterEvent mEmitterEvent;

    public void initEmitterEvent(String... strings) {
        if (mEmitterEvent == null) {
            mEmitterEvent = new EmitterEvent(this, strings);
        }
        mEmitterEvent.initEmitterEvent(AppSocket.getInstance().getSocket());
    }

    public void initOnceEmitterEvent(String... strings) {
        if (mEmitterEvent == null) {
            mEmitterEvent = new EmitterEvent(this, strings);
        }
        mEmitterEvent.initOnceEmitterEvent(AppSocket.getInstance().getSocket());
    }

    public void onEmitterListener() {
        if (mEmitterEvent != null) {
            mEmitterEvent.onEmitterEvent();
        }
    }

    public void disconnectEmitterListener() {
        if (mEmitterEvent != null) {
            mEmitterEvent.disconnectEmitterEvent();
        }
    }
}
