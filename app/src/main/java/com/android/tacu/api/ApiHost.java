package com.android.tacu.api;

import com.android.tacu.BuildConfig;
import com.android.tacu.http.network.Retrofit2Host;

/**
 * Created by jiazhen on 2017/5/22.
 */

public enum ApiHost implements Retrofit2Host {

    NODEHOST {
        public String getHost() {
            return host + "/";
        }
    },
    /**
     * 用户相关
     */
    USER {
        public String getHost() {
            return host + "/user/";
        }
    },
    /**
     * 版本更新
     */
    UPLOAD {
        public String getHost() {
            return host + "/updateVersion/";
        }
    },
    /**
     * 资产详情
     */
    ASSET {
        public String getHost() {
            return host + "/coin/";
        }
    },
    /**
     * 下委托单
     */
    ORDER {
        public String getHost() {
            return host + "/order/";
        }
    },
    DATAOP {
        public String getHost() {
            return SOCKET_IP + "/dataop/";
        }
    },
    /**
     * k线图数据
     */
    KLINE {
        public String getHost() {
            return host + "/kline/";
        }
    },
    /**
     * ZENDESK
     */
    ZEN {
        public String getHost() {
            return host + "/zen/";
        }
    },
    /**
     * OTCTacu
     */
    OTCTACU {
        public String getHost() {
            return host + "/otcTacu/";
        }
    },
    /**
     * C2CTacu
     */
    C2C {
        public String getHost() {
            return host + "/c2c/";
        }
    },
    /**
     * ali
     */
    ALI {
        public String getHost() {
            return host + "/ali/";
        }
    },
    /**
     * 地址
     */
    USEADDRESS {
        public String getHost() {
            return host + "/userAddress/";
        }
    },
    /**
     * Auction
     */
    AUCTIONNEW {
        public String getHost() {
            return host + "/auctionnew/";
        }
    };

    public static String host = BuildConfig.API_HOST;
    public static String SOCKET_IP = BuildConfig.API_HOST.replace("/unique", "");

}
