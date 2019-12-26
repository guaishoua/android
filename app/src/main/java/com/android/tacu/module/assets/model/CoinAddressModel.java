package com.android.tacu.module.assets.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * ================================
 *
 * @作者: 魏玉帅
 * @创建日期:2017/7/21
 * @版本 1.0
 * @描述: ================================
 */

public class CoinAddressModel implements Serializable {


    /**
     * attachment : {"image":"iVBORw0KGgoAAAANSUhEUgAAAMgAAADICAIAAAAiOjnJAAADpUlEQVR42u3aQZKjQAwEQP7/6dk3\nbCCVJJx1nbEN6uRQIZ4/kYY8RiBgCVgClghYApaAJQKWgCVgiYAlYAlYImAJWAKWyBisJ5U3VxX7\n638N580kT5wCWGCBBRZYYIEFFli7YVVWiRej7LvmwqsqHN3UKYAFFlhggQUWWGCB9SFYhU2n8P4L\nC1ffNKZ+FyywwAILLLDAAgsssLKw+rYWfXT6niuwwAILLLDAAgsssMC6A2tnhYwtqcACCyywwAIL\nLLDAAqvuh98Pa8rZ1D8vOQWwwAILLLDAAgsssC7D6ksfu+/9NXYKYIEFFlhggQUWWGCdgjWVE0b7\njv/vVMACCyywwAILLLDAug8rNtnP752WzwossMACCyywwAILrB2wClcNO6304Sjsm1MKwQILLLDA\nAgsssMD6GVixBUjhZUxV5sJZ5Z2BBRZYYIEFFlhggbVypTN1S7Hf7SPb91Ux32CBBRZYYIEFFlhg\nXYa1ZP9T2Df7QE816EALBgsssMACCyywwAJr5UpneT3Z/M2xJVW+YIIFFlhggQUWWGCBtRLWiTq2\nZD3S184Ku/mWVggWWGCBBRZYYIEFVroV7jz+2Fom9lz1DQcssMACCyywwAILrA/BKjz+GMoTtbdv\nOIFlEVhggQUWWGCBBRZY92HFykvsd2NLqqknByywwAILLLDAAgusn4G1s1EuueZ8WSu/ZrDAAgss\nsMACCyywTsHauVqJ7UMKcUzV7aUrHbDAAgsssMACCyywVq90dh5D3wLk2ZGO+wULLLDAAgsssMAC\n6yCsvt5X+NkPCJ76LFhggQUWWGCBBRZYu2EVHn/MSh+dvgMuPO/C/Q9YYIEFFlhggQUWWD8Da8l6\npK80FfqOTRIssMACCyywwAILrO/C6rv/qVeO+r6qcDkWY7flfSywwAILLLDAAgsssNpbYV8K1zKF\nvS+27+qbVUffBAsssMACCyywwAJrB6wnlcJTmapUsbfEph4VsMACCyywwAILLLBOwbrY+/rmHoM1\ndUZggQUWWGCBBRZYYH0I1pIedKL3xdp34WMGFlhggQUWWGCBBRZY1f0rVj9jq5WYM7DAAgsssMAC\nCyywwMrCir2QdAJ0oAaCBRZYYIEFFlhggXUQ1qurWfnNfVupvmcjQAcssMACCyywwAILrIOw+jJ1\n/H2CY8Ppe+8NLLDAAgsssMACC6xTsETAErAELBGwBCwBSwQsAUvAEgFLwBKwRMASsAQsEbAELLmc\nf3SxevBc6C8KAAAAAElFTkSuQmCC","address":"CXxs9u48JzozBh2WEgqCc3hdoWxNB75enx"}
     * status : 200
     * message : null
     */

    /**
     * image : iVBORw0KGgoAAAANSUhEUgAAAMgAAADICAIAAAAiOjnJAAADpUlEQVR42u3aQZKjQAwEQP7/6dk3
     * bCCVJJx1nbEN6uRQIZ4/kYY8RiBgCVgClghYApaAJQKWgCVgiYAlYAlYImAJWAKWyBisJ5U3VxX7
     * 638N580kT5wCWGCBBRZYYIEFFli7YVVWiRej7LvmwqsqHN3UKYAFFlhggQUWWGCB9SFYhU2n8P4L
     * C1ffNKZ+FyywwAILLLDAAgsssLKw+rYWfXT6niuwwAILLLDAAgsssMC6A2tnhYwtqcACCyywwAIL
     * LLDAAqvuh98Pa8rZ1D8vOQWwwAILLLDAAgsssC7D6ksfu+/9NXYKYIEFFlhggQUWWGCdgjWVE0b7
     * jv/vVMACCyywwAILLLDAug8rNtnP752WzwossMACCyywwAILrB2wClcNO6304Sjsm1MKwQILLLDA
     * AgsssMD6GVixBUjhZUxV5sJZ5Z2BBRZYYIEFFlhggbVypTN1S7Hf7SPb91Ux32CBBRZYYIEFFlhg
     * XYa1ZP9T2Df7QE816EALBgsssMACCyywwAJr5UpneT3Z/M2xJVW+YIIFFlhggQUWWGCBtRLWiTq2
     * ZD3S184Ku/mWVggWWGCBBRZYYIEFVroV7jz+2Fom9lz1DQcssMACCyywwAILrA/BKjz+GMoTtbdv
     * OIFlEVhggQUWWGCBBRZY92HFykvsd2NLqqknByywwAILLLDAAgusn4G1s1EuueZ8WSu/ZrDAAgss
     * sMACCyywTsHauVqJ7UMKcUzV7aUrHbDAAgsssMACCyywVq90dh5D3wLk2ZGO+wULLLDAAgsssMAC
     * 6yCsvt5X+NkPCJ76LFhggQUWWGCBBRZYu2EVHn/MSh+dvgMuPO/C/Q9YYIEFFlhggQUWWD8Da8l6
     * pK80FfqOTRIssMACCyywwAILrO/C6rv/qVeO+r6qcDkWY7flfSywwAILLLDAAgsssNpbYV8K1zKF
     * vS+27+qbVUffBAsssMACCyywwAJrB6wnlcJTmapUsbfEph4VsMACCyywwAILLLBOwbrY+/rmHoM1
     * dUZggQUWWGCBBRZYYH0I1pIedKL3xdp34WMGFlhggQUWWGCBBRZY1f0rVj9jq5WYM7DAAgsssMAC
     * CyywwMrCir2QdAJ0oAaCBRZYYIEFFlhggXUQ1qurWfnNfVupvmcjQAcssMACCyywwAILrIOw+jJ1
     * /H2CY8Ppe+8NLLDAAgsssMACC6xTsETAErAELBGwBCwBSwQsAUvAEgFLwBKwRMASsAQsEbAELLmc
     * f3SxevBc6C8KAAAAAElFTkSuQmCC
     * address : CXxs9u48JzozBh2WEgqCc3hdoWxNB75enx
     */
    @SerializedName("image")
    public String image;
    @SerializedName("address")
    public String address;
    @SerializedName("fee")
    public String fee;
    @SerializedName("msgCode")
    public String msgCode;
    @SerializedName("msgName")
    public String msgName;
    @SerializedName("confirmNum")
    public int confirmNum;
}
