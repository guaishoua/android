package com.android.tacu.socket;

import com.android.tacu.api.ApiHost;

import org.json.JSONObject;

import io.socket.client.Socket;

/**
 * Created by jiazhen on 2018/8/1.
 */

public class AppSocket extends BaseSocket {

    public AppSocket() {
        super(new Builder(ApiHost.SOCKET_IP));
    }

    public Socket getSocket() {
        return mSocket;
    }

    public void clearSocket() {
        mSocket = null;
    }

    /**
     * 币种总列表
     */
    public void coinAllList() {
        try {
            if (mSocket != null) {
                JSONObject obj = new JSONObject();
                obj.put("baseCurrencyId", 1);
                mSocket.emit(SocketConstant.LOGINAFTERNEW, obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 币种信息
     */
    public void coinInfo(int currencyId, int baseCurrencyId) {
        try {
            if (mSocket != null) {
                JSONObject obj = new JSONObject();
                obj.put("tradeCurrencyId", currencyId);
                obj.put("baseCurrencyId", baseCurrencyId);
                mSocket.emit(SocketConstant.LOGINAFTERCHANGETRADECOIN, obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户信息
     */
    public void userAccount(int currencyId, int baseCurrencyId, String token, int userUid) {
        try {
            if (mSocket != null) {
                JSONObject obj = new JSONObject();
                obj.put("tradeCurrencyId", currencyId);
                obj.put("baseCurrencyId", baseCurrencyId);
                obj.put("token", token);
                obj.put("uid", userUid);
                mSocket.emit(SocketConstant.USERACCOUNT, obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 挂单
     */
    public void entrust(int currencyId, int baseCurrencyId) {
        try {
            if (mSocket != null) {
                JSONObject obj = new JSONObject();
                obj.put("tradeCurrencyId", currencyId);
                obj.put("baseCurrencyId", baseCurrencyId);
                mSocket.emit(SocketConstant.ENTRUST, obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 成交记录
     */
    public void tradeHistory(int currencyId, int baseCurrencyId) {
        try {
            if (mSocket != null) {
                JSONObject obj = new JSONObject();
                obj.put("tradeCurrencyId", currencyId);
                obj.put("baseCurrencyId", baseCurrencyId);
                mSocket.emit(SocketConstant.TRADEHISTORY, obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
