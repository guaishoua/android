package com.android.tacu.EventBus.model;

/**
 * 跳转自选页面
 * Created by jiazhen on 2018/9/20.
 */
public class JumpToSelfEvent {

    private boolean isJumpSelf;

    public JumpToSelfEvent(boolean isJumpSelf) {
        this.isJumpSelf = isJumpSelf;
    }

    public boolean isJumpSelf() {
        return isJumpSelf;
    }

    public void setJumpSelf(boolean jumpSelf) {
        isJumpSelf = jumpSelf;
    }
}
