package com.android.tacu.db.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 手势和指纹的信息
 * Created by jiazhen on 2018/8/13.
 */
@Entity
public class LockModel {

    @Id(autoincrement = true)//自增 大写的Long 可以传null 表示自增
    private Long id;
    private int uid;
    private String valString;

    @Generated(hash = 271037811)
    public LockModel(Long id, int uid, String valString) {
        this.id = id;
        this.uid = uid;
        this.valString = valString;
    }

    @Generated(hash = 718352615)
    public LockModel() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getUid() {
        return this.uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getValString() {
        return this.valString;
    }

    public void setValString(String valString) {
        this.valString = valString;
    }
}
