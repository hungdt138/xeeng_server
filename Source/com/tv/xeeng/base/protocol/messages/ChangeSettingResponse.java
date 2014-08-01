package com.tv.xeeng.base.protocol.messages;



import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class ChangeSettingResponse extends AbstractResponseMessage {

    public String mErrorMsg;
    public int zoneID;
    public int roomID;
    public long matchID;
    // Phom    
    public boolean isUKhan = false;
    public boolean anCayMatTien = true; // default
    public boolean taiGuiUDen = false;
    public long money;
    public int size;
    public boolean truong;
    
    
    //tienlen
    public boolean isHidePoker = true;
    
    public void setTruong(boolean truong) {
		this.truong = truong;
	}
    public void setUKhan(boolean isUKhan) {
		this.isUKhan = isUKhan;
	}
    public void setAnCayMatTien(boolean anCayMatTien) {
		this.anCayMatTien = anCayMatTien;
	}
    public void setMatchID(long matchID) {
		this.matchID = matchID;
	}
    public void setMoney(long money) {
		this.money = money;
	}
    public void setTaiGuiUDen(boolean taiGuiUDen) {
		this.taiGuiUDen = taiGuiUDen;
	}
    
    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }
    public void setZoneID (int id){
    	this.zoneID = id;
    }
    public void setSuccess(int aCode) {
        mCode = aCode;
    }

    public IResponseMessage createNew() {
        return new ChangeSettingResponse();
    }
    
    @Override
     public IResponseMessage clone(ISession session)
     {
          ChangeSettingResponse resMsg = (ChangeSettingResponse)createNew();

            resMsg.session = session;
            resMsg.setID(this.getID());
            resMsg.mCode = mCode;
            resMsg.mErrorMsg = mErrorMsg;
            resMsg.zoneID = zoneID;
            resMsg.roomID = roomID;
            resMsg.matchID = matchID;
    // Phom    
            resMsg.isUKhan = isUKhan;
            resMsg.anCayMatTien = anCayMatTien; // default
            resMsg.taiGuiUDen = taiGuiUDen;
            resMsg.money = money;
            resMsg.size = size;
            resMsg.truong = truong;
    
            resMsg.isHidePoker = isHidePoker;
          return resMsg;  
     }
    
                
}
