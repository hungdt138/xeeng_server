/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages;




import java.util.ArrayList;

import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class TurnResponse extends AbstractResponseMessage {
    public String mErrorMsg;
    public int mRow;
    public int mCol;
    public long money;
    public int mType;
    public boolean mIsEnd;
    public long nextID;
    public long preID;
    public int timeReq;
    public boolean isPlaying;
    
    public long sttTo;//(-1,0,n) (n*minbet)
    public int zoneID;
    public int phomCard;
    // Tien len mien nam
//    Thomc
//    public int[] tienlenCards ;
    public String tienlenCards;
    public boolean isNewRound;
    public boolean isGiveup;
    public long currID;
    public boolean isDuty;
    
    //starwar
    public double power;
    public double angle;
    
    public ArrayList<long[]> fightInfo = new ArrayList<long[]>();// gửi về khi xảy ra chặt heo/hàng

    public void setSuccess(int aCode, long mn, long id, int time, int zone) {
        zoneID = zone;
        mCode = aCode;
        money = mn;
        //mType = aType;
        mIsEnd = false;
        nextID = id;
        timeReq = time;
    }

    public void setPreID(long id) {
        this.preID = id;
    }

    public void setcurrID(long id) {
        this.currID = id;
    }

    public void setIsGiveup(boolean istrue) {
        this.isGiveup = istrue;
    }

    public void setSuccess(int aCode, int aRow, int aCol, int aType, int zone) {
        mCode = aCode;
        mRow = aRow;
        mCol = aCol;
        mType = aType;
        mIsEnd = false;
        zoneID = zone;
    }
    
    //Phom
    public void setSuccess(int aCode, int p, long nID, int zone) {
        mCode = aCode;
        phomCard = p;
        nextID = nID;
        zoneID = zone;
    }
    
    //Co tuong
   
    //tien len
    public void setSuccessTienLen(int aCode, String cards, long nID, boolean isNewRound_, int zone) {
        mCode = aCode;
        zoneID = zone;
        tienlenCards = cards;
        nextID = nID;
        isNewRound = isNewRound_;
    }

    //Sam
    public void setSuccessSam(int aCode, String cards, long nID, boolean isNewRound_, int zone) {
        mCode = aCode;
        zoneID = zone;
        tienlenCards = cards;
        nextID = nID;
        isNewRound = isNewRound_;
    }
    
    //Star war
    public void setSuccessStarWar(double power, double angle, long nextId, long preId, int zoneId)
    {
        mCode = ResponseCode.SUCCESS;
        this.power = power;
        this.angle = angle;
        this.nextID = nextId;
        this.zoneID = zoneId;
        this.preID = preId;
    }
    
    //chặt heo/hàng phải gửi thông tin $ về room
    public void setFightInfo(ArrayList<long[]> fightInfo_) {
        this.fightInfo = fightInfo_;
    }

    public void setSTTTo(long stt) {
        sttTo = stt;
    }

    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public IResponseMessage createNew() {
        return new TurnResponse();
    }
    
    public IResponseMessage clone(ISession session)
    {
       TurnResponse resMsg = (TurnResponse)createNew();
        resMsg.fightInfo = fightInfo;
        resMsg.session = session;
        resMsg.setID(this.getID());
        resMsg.mCode = mCode;
        resMsg.money = money;
        resMsg.mType = mType;
        resMsg.mIsEnd = mIsEnd;
        resMsg.nextID = nextID;
        resMsg.preID= preID;
        resMsg.timeReq = timeReq;
        resMsg.isPlaying = isPlaying;
    
        resMsg.sttTo = sttTo;//(-1,0,n) (n*minbet)
        resMsg.zoneID = zoneID;
        resMsg.phomCard = phomCard;
    // Tien len mien nam
//    Thomc
//    public int[] tienlenCards ;
        resMsg.tienlenCards = tienlenCards;
        resMsg.isNewRound = isNewRound;
        resMsg.isGiveup = isGiveup;
        resMsg.currID = currID;
        resMsg.isDuty = isDuty;
    
       return resMsg;
  }
    
}
