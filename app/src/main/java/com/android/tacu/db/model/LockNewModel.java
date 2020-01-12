package com.android.tacu.db.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 账号和密码的信息
 * 手势和指纹的信息
 * Created by jiazhen on 2018/8/13.
 */
@Entity
public class LockNewModel {

    @Id(autoincrement = true)//自增 大写的Long 可以传null 表示自增
    private Long id;
    private String accountString;//账号
    private String accountPwd;//加密的密码
    private String gestureString;//手势信息
    private boolean isFinger = false;//手势信息

    @Generated(hash = 248578593)
    public LockNewModel(Long id, String accountString, String accountPwd, String gestureString, boolean isFinger) {
        this.id = id;
        this.accountString = accountString;
        this.accountPwd = accountPwd;
        this.gestureString = gestureString;
        this.isFinger = isFinger;
    }

    @Generated(hash = 1638777447)
    public LockNewModel() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountString() {
        return this.accountString;
    }

    public void setAccountString(String accountString) {
        this.accountString = accountString;
    }

    public String getAccountPwd() {
        return this.accountPwd;
    }

    public void setAccountPwd(String accountPwd) {
        this.accountPwd = accountPwd;
    }

    public String getGestureString() {
        return this.gestureString;
    }

    public void setGestureString(String gestureString) {
        this.gestureString = gestureString;
    }

    public boolean getIsFinger() {
        return this.isFinger;
    }

    public void setIsFinger(boolean isFinger) {
        this.isFinger = isFinger;
    }
}
