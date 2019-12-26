package com.android.tacu.socket;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * 发射器事件处理类
 * Created by jiazhen on 2018/8/1.
 */

public class EmitterEvent {

    private Map<String, Emitter.Listener> emitterEventMap = new HashMap<>();
    private IEmitterListener emitterListener;
    private Socket mSocket;

    public EmitterEvent(IEmitterListener emitterListener, String... strings) {
        this.emitterListener = emitterListener;
        for (String str : strings) {
            emitterEventMap.put(str, null);
        }
    }

    public void initEmitterEvent(Socket socket) {
        if (socket == null) return;
        this.mSocket = socket;

        Iterator iterator = emitterEventMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            final String event = (String) entry.getKey();
            Emitter.Listener listener = new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    emitterListener.emitterListenerResut(event, args);
                }
            };
            emitterEventMap.put(event, listener);
        }
    }

    public void initOnceEmitterEvent(Socket socket) {
        if (socket == null) return;
        this.mSocket = socket;
        //这里只执行一次
        mSocket.connect();

        Iterator iterator = emitterEventMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            final String event = (String) entry.getKey();
            Emitter.Listener listener = new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    emitterListener.emitterListenerResut(event, args);
                }
            };
            emitterEventMap.put(event, listener);
            mSocket.once(event, listener);
        }
    }

    public void onEmitterEvent() {
        if (mSocket == null) return;
        mSocket.connect();
        if (emitterEventMap != null && emitterEventMap.size() > 0) {
            Iterator iterator = emitterEventMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Emitter.Listener listener = (Emitter.Listener) entry.getValue();
                if (listener != null) {
                    String event = (String) entry.getKey();
                    if (mSocket.hasListeners(event)) return;
                    mSocket.on(event, listener);
                }
            }
        }
    }

    public void disconnectEmitterEvent() {
        if (mSocket == null) return;
        mSocket.disconnect();
        mSocket.off();
    }
}
