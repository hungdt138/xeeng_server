package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class GetUserInfoResponse extends AbstractResponseMessage {
    public String mErrorMsg;
    public long mUid;
    public String loginName;
    public String vipName;
    public int mAge;
    public boolean mIsMale;
    public int level;

    public long money;
    public long xeeng;
    public int playsNumber;
    public int AvatarID;
    public boolean isFriend;
    public int experience;

    public String mUsername;
    public String cmnd;
    public String xePhoneNumber;
    
    public int partnerId;
    public String mobileVersion;

    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public void setSuccess(int aCode, long aUid, String loginName,
            int aAge, boolean aIsMale, long m, int pN, int aID, boolean is, int l, int e, long xeeng, String aUsername, String cmnd, String xePhoneNumber) {
        mCode = aCode;
        mUid = aUid;
        this.loginName = loginName;
        mAge = aAge;
        mIsMale = aIsMale;
        money = m;
        playsNumber = pN;
        AvatarID = aID;
        isFriend = is;
        level = l;
        experience = e;
        this.xeeng = xeeng;

        mUsername = aUsername;
        this.cmnd = cmnd;
        this.xePhoneNumber = xePhoneNumber;
    }

    public IResponseMessage createNew() {
        return new GetUserInfoResponse();
    }

}
