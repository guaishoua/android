package com.android.tacu.socket;

import io.socket.client.Socket;

/**
 * Created by jiazhen on 2018/8/1.
 */

public class MainSocketManager extends BaseSocketManager {

    private EmitterEvent mEmitterEvent;
    private Socket mSocket;

    public MainSocketManager(Socket mSocket) {
        this.mSocket = mSocket;
    }

    public void initEmitterEvent(String... strings) {
        if (mEmitterEvent == null) {
            mEmitterEvent = new EmitterEvent(this, strings);
        }
        mEmitterEvent.initEmitterEvent(mSocket);
    }

    public void initOnceEmitterEvent(String... strings) {
        if (mEmitterEvent == null) {
            mEmitterEvent = new EmitterEvent(this, strings);
        }
        mEmitterEvent.initOnceEmitterEvent(mSocket);
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
