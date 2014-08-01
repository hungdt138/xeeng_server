package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.base.room.NRoomEntity;
import com.tv.xeeng.game.data.ChargingInfo;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.data.VersionEntity;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//import bacay.data.VersionEntity;

public class LoginResponse extends AbstractResponseMessage {
    
    
    public String mErrorMsg;
    public long mUid;
    public long money;
//    public long xeeng;
    public int avatarID;
    public int level;
    //public Vector<AdvEntity> advs;
    public boolean isNewProtocol;
//    public VersionEntity lVersion;
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
    
    public long deviceId;//for audit and generate unique device
    public String mobileVersion;
    
    public String numberOnline = "";
    
    public void setLastMatch(int zoneId, long matchId)
    {
        zone_id = zoneId;
        lastMatchId = matchId;
    }
    
    public void setLastRoom(long l,String r,int z)
    {
        lastRoom = l;
        lastRoomName = r;
        zone_id = z;
    }
    
    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public void setSuccess(int aCode, long aUid, long mn, int avatar, int lev, Date time, String Tuocvi, int playNumbers,long updatelevel) {
        mCode = aCode;
        mUid = aUid;
        money = mn;
        avatarID = avatar;
        level = lev;
        lastLogin = time;
        TuocVi = Tuocvi;
        playNumber = playNumbers;
        moneyUpdateLevel = updatelevel;
//        this.xeeng = xeeng;
    }

    public IResponseMessage createNew() {
        return new LoginResponse();
    }
}
