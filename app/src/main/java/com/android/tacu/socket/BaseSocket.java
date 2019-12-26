package com.android.tacu.socket;

import android.text.TextUtils;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.WebSocket;

/**
 * Created by jiazhen on 2018/8/1.
 */

public class BaseSocket {

    protected Socket mSocket = null;

    BaseSocket(Builder builder) {
        IO.Options options = new IO.Options();
        options.timeout = builder.timeout;
        options.reconnection = builder.reconnection;
        options.reconnectionAttempts = builder.reconnectionAttempts;
        options.reconnectionDelay = builder.reconnectionDelay;
        options.reconnectionDelayMax = builder.reconnectionDelayMax;
        options.forceNew = builder.forceNew;
        options.transports = builder.transports;
        try {
            mSocket = IO.socket(builder.socketHost, options);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static class Builder {

        private String[] transports = new String[]{WebSocket.NAME};
        // 连接超时时间
        private int timeout = -1;
        // 是否自动重连
        private boolean reconnection = true;
        // 重连尝试次数
        private int reconnectionAttempts = 100;
        // 重连间隔
        private int reconnectionDelay = 3000;
        // 最大连接等待时间
        private int reconnectionDelayMax = 3000;
        private boolean forceNew = false;
        // Socket服务器地址
        private String socketHost = null;

        public BaseSocket build() {
            return new BaseSocket(this);
        }

        public Builder(String socketHost) {
            if (TextUtils.isEmpty(socketHost)) {
                throw new NullPointerException("socketHost not allow is null");
            }
            this.socketHost = socketHost;
        }

        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder reconnection(boolean reconnection) {
            this.reconnection = reconnection;
            return this;
        }

        public Builder reconnectionAttempts(int reconnectionAttempts) {
            this.reconnectionAttempts = reconnectionAttempts;
            return this;
        }

        public Builder reconnectionDelay(int reconnectionDelay) {
            this.reconnectionDelay = reconnectionDelay;
            return this;
        }

        public Builder reconnectionDelayMax(int reconnectionDelayMax) {
            this.reconnectionDelayMax = reconnectionDelayMax;
            return this;
        }

        public Builder forceNew(boolean forceNew) {
            this.forceNew = forceNew;
            return this;
        }
    }
}
