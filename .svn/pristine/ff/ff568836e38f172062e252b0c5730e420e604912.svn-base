package com.tv.xeeng.base.protocol.messages;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tv.xeeng.base.room.NRoomEntity;
import com.tv.xeeng.game.data.ChargingInfo;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.data.VersionEntity;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class GuestLoginResponse extends AbstractResponseMessage {
        
    public String mErrorMsg;
    public long mUid;
    public String username;
    public long money;
    public int avatarID;
    public int level;
    public boolean isNewProtocol;
    public Date lastLogin;
    public String TuocVi;
    public String avatarVerion;
    public int playNumber;
    public long moneyUpdateLevel;

    public String linkDown="";
    public String newVer="";

    public long lastRoom=-1;
    public int zone_id;
    public String lastRoomName="";
    public VersionEntity version;
    public boolean isMobile = true;
    public String avatar;
    public String cellPhone;
    public long lastMatchId = -1;
    
    //charging info
    public List<ChargingInfo> chargingInfo = new ArrayList<ChargingInfo>();
    public List<NRoomEntity> lstRooms = null;
    public List<SimpleTable> lstTables = null;
    public int newZoneId;
    public String active;
    public boolean isMxh;
    public UserEntity usrEntity;
    public boolean isNeedUpdate = false;
    
    public String deviceUId;
    public String mobileVersion;
    
    public String numberOnline = "";
        
    public void setLastMatch(int zoneId, long matchId)
    {
        zone_id = zoneId;
        lastMatchId = matchId;
    }
    
    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public void setSuccess(int aCode, long aUid, long mn, Date time) {
        mCode = aCode;
        mUid = aUid;
        money = mn;
        lastLogin = time;
    }

    public void setLastRoom(long l,String r,int z)
    {
        lastRoom=l;
        lastRoomName=r;
        zone_id=z;
    }
    
    public IResponseMessage createNew() {
        return new GuestLoginResponse();
    }
}
