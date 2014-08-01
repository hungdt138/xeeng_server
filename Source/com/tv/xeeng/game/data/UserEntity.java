package com.tv.xeeng.game.data;

import java.io.Serializable;
import java.util.Date;

public class UserEntity implements Serializable{

    public long mUid;
    public String mUsername;
    public String mPassword;
    public int mAge;
    public boolean mIsMale;
    public long money;
    public long xeeng;
    public int level;
    public int avatarID;
    public int playsNumber;
    public boolean isLogin = false;
    public Date lastLogin;
    public long lastMatch;
    public String avatarM;
    public String avatarVersion;
    public String avatarF;
    //public String TuocVi;
    public String cellPhone;
    public int experience;
    public String vipName;
    public int vipId;
    public boolean isOnline = false;
    public String loginName;
    public String cmnd;
    public String xePhoneNumber;
    private Date lockExpired;
    private Date chatLockExpired;
    
    public int point;//for euro
    public int bonus;//for euro
    
    public long avFileId;
    public long biaFileId;
    public String stt;
    public boolean hasBia;
    public  int partnerId;
    public boolean isActive;
    
    public int glasses;
    public int shirt;
    public int jeans;
    public int hair;
    
    public int timesQuay;
    
    public int refCode;
    
    public boolean isLocked;
    
    public UserInfoEntity usrInfoEntity;
    
    public UserEntity() {
    }

    public Date getLockExpired() {
        return lockExpired;
    }

    public void setLockExpired(Date lockExpired) {
        this.lockExpired = lockExpired;
    }

    public Date getChatLockExpired() {
        return chatLockExpired;
    }

    public void setChatLockExpired(Date chatLockExpired) {
        this.chatLockExpired = chatLockExpired;
    }
}
