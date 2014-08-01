/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.BatCaiResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.xam.data.SamPlayer;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;
import org.json.JSONObject;
import org.slf4j.Logger;

/**
 *
 * @author ThangTD
 */
public class BatCaiJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(BatCaiJSON.class);

    @Override
    public boolean decode(Object paramObject, IRequestMessage paramIRequestMessage) throws ServerException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object encode(IResponseMessage paramIResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            BatCaiResponse batCaiResponse = (BatCaiResponse) paramIResponseMessage;

            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(paramIResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(batCaiResponse.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
            
            if (batCaiResponse.mCode == ResponseCode.SUCCESS) {
                int playingSize = batCaiResponse.samPlayers.size();
                StringBuilder sb1 = new StringBuilder();
                for (int i = 0; i < playingSize; i++) {
                    SamPlayer p = batCaiResponse.samPlayers.get(i);
                    sb1.append(p.id).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb1.append(p.caiCard.toInt()).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb1.append(p.isCai ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_2);
                }
                sb1.deleteCharAt(sb1.length() - 1);
                sb.append(sb1);
            }
            
            encodingObj.put("v", sb);
            
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + paramIResponseMessage.getID(), t);
            return null;
        }
    }

}
