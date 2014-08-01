/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.SimplePlayer;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class StartResponse extends AbstractResponseMessage {
    
    public long mMatchId;
    public String mErrorMsg;

    public boolean mIsYourTurn;
    public int mType;
    
    public long mRoomId;
   
    public int zoneID;
    public long starterID;
    
    public List<? extends SimplePlayer> lstplayer;
    public int g;
    
    //Pikachu
    public int pLevel;
    
    public String value;
    
    public void setFailure(int aCode, String aErrorMsg)
    {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }
    public void setSuccess(int aCode, int zone)
    {
        mCode = aCode;
        zoneID = zone;
    }
    

    public void setValue(boolean aIsYourTurn, int aType)
    {
        mIsYourTurn = aIsYourTurn;
        mType = aType;
    }

    public void setRoomID(long aRoomId)
    {
        mRoomId = aRoomId;
    }
    public IResponseMessage createNew()
    {
        return new StartResponse();
    }
    
     @Override
     public IResponseMessage clone(ISession session)
      {
          StartResponse resMsg = (StartResponse)createNew();
            
            resMsg.session = session;
            resMsg.setID(this.getID());
            resMsg.mCode = mCode;
            
            resMsg.mMatchId = mMatchId;
            resMsg.mErrorMsg = mErrorMsg;

            resMsg.mIsYourTurn = mIsYourTurn;
            resMsg.mType = mType;
    
            resMsg.mRoomId = mRoomId;
   
            resMsg.zoneID = zoneID;
            resMsg.starterID = starterID;
    
            resMsg.lstplayer = lstplayer;
    
            //Pikachu
            resMsg.pLevel = pLevel;
    
            return resMsg;
      }
    
}
