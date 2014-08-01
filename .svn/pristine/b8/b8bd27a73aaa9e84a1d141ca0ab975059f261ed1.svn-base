package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.SendQuestionResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.trieuphu.data.TrieuPhuPlayer;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class SendQuestionJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
            AcceptJoinJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
            throws ServerException {
        throw new ServerException("");
    }

    public Object encode(IResponseMessage aResponseMessage)
            throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            SendQuestionResponse acceptJoin = (SendQuestionResponse) aResponseMessage;
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(aResponseMessage.getID())).append(
                    AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(acceptJoin.mCode)).append(
                    AIOConstants.SEPERATOR_NEW_MID);
            if (acceptJoin.mCode == ResponseCode.FAILURE) {
                sb.append(acceptJoin.mErrorMsg);
            } else {
                sb.append(Integer.toString(acceptJoin.point)).append(
                        AIOConstants.SEPERATOR_BYTE_3);
                switch (acceptJoin.zone) {
                    case ZoneID.AILATRIEUPHU: {
                        sb.append(acceptJoin.detail).append(
                                AIOConstants.SEPERATOR_BYTE_3);
//				sb.append(encode(acceptJoin.best)).append(
//						AIOConstants.SEPERATOR_BYTE_3);
                        for (String s : acceptJoin.answer) {
                            sb.append(s).append(AIOConstants.SEPERATOR_BYTE_1);
                        }
                        sb.deleteCharAt(sb.length() - 1);
                        
//                        if (acceptJoin.point > 1) {
//                            sb.append(AIOConstants.SEPERATOR_BYTE_3);
//                            for (TrieuPhuPlayer p : acceptJoin.data) {
//                                sb.append(p.id).append(AIOConstants.SEPERATOR_BYTE_1);
//                                sb.append(p.currentAnswerPos).append(AIOConstants.SEPERATOR_BYTE_2);
//                                /*sb.append(p.preAnswer?"1":"0").append(AIOConstants.SEPERATOR_BYTE_2);*/
//                            }
//                            if (!acceptJoin.data.isEmpty()) {
//                                sb.deleteCharAt(sb.length() - 1);
//                            }
//                        }
                        break;
                    }
                }
            }
            encodingObj.put("v", sb.toString());
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
