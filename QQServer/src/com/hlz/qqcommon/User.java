package com.hlz.qqcommon;

import java.io.Serializable;

//表示一个用户信息
public class User implements Serializable {
    private static final  long serialVersionUID = 1L;//增强兼容性
    private String userId;
    private String userPwd;

    public User(String userId, String userPwd) {
        this.userId = userId;
        this.userPwd = userPwd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }
}
