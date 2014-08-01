package com.tv.xeeng.base.protocol.messages.json;


import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.AnPhomRequest;
import com.tv.xeeng.base.protocol.messages.AnPhomResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;
import static com.tv.xeeng.server.Server.REAL_GOT_MONEY;
import org.json.JSONObject;
import org.slf4j.Logger;



public class AnPhomJSON implements IMessageProtocol
{
    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(AnPhomJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException
    {
        try {
        	// request data
            JSONObject jsonData = (JSONObject) aEncodedObj;
            // cancel request message
            AnPhomRequest an = (AnPhomRequest) aDecodingObj;
            if (jsonData.has("v"))
            {
                try
                {
                    String v = jsonData.getString("v");
                    an.matchID = Long.parseLong(v);
                    return true;
                }
                catch(Exception ex)
                {
                    mLog.error(ex.getMessage(), ex);
                }
            }
            
            an.matchID = jsonData.getLong("match_id");
        	return true;
        } catch (Exception e) {
            return false;
	}
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException
    {
        try
        {
            JSONObject encodingObj = new JSONObject();
            AnPhomResponse an = (AnPhomResponse) aResponseMessage;
            if(an.session != null && an.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(an.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (an.mCode == ResponseCode.FAILURE) {
                	 sb.append(an.message);
                }else {
                        sb.append(an.uid).append(AIOConstants.SEPERATOR_BYTE_1);
                	sb.append(an.chot ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
//                        if(!an.session.isMobileDevice())
//                        {
                            long moneyWin = (long)((double)an.money * REAL_GOT_MONEY);
                            sb.append(Long.toString(moneyWin)).append(AIOConstants.SEPERATOR_BYTE_1);
                            sb.append(Long.toString(an.money));
//                        }
                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }
            
            encodingObj.put("mid", aResponseMessage.getID());
            encodingObj.put("code", an.mCode);
            if (an.mCode == ResponseCode.SUCCESS){
                if(an.session!= null && an.session.getByteProtocol()>0)
                {
                    StringBuilder sb = new StringBuilder();
//                    sb.append(an.money).append(AIOConstants.SEPERATOR_ELEMENT);
                    sb.append(an.uid).append(AIOConstants.SEPERATOR_ELEMENT);
                    String chot;
                    if(an.chot)
                    {
                        chot = "1";
                    }
                    else
                    {
                        chot = "0";
                    }
                    sb.append(chot);
                    encodingObj.put("v", sb.toString());
                    return encodingObj;
                }
                
                encodingObj.put("money", an.money);
                encodingObj.put("moneyWin", (long)an.money*0.9);
                encodingObj.put("uid", an.uid);
                encodingObj.put("p_uid", an.p_uid);
                encodingObj.put("chot", an.chot);
                if(an.isHaBai)
                {
                    encodingObj.put("isHaBai", an.isHaBai);
                }
               
            }else {
            	encodingObj.put("error", an.message);
            }
            // response encoded obj
            return encodingObj;
        } catch (Throwable t)
        {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
