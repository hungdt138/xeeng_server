package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.ChargingResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.InfoDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ChargingInfo;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

public class ChargingBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(ChargingBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        MessageFactory msgFactory = aSession.getMessageFactory();
        ChargingResponse resCharg = (ChargingResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        mLog.debug("[GET CHARGING]: Catch");
        try {
            CacheUserInfo cache = new CacheUserInfo();

            UserEntity entity = cache.getUserInfo(aSession.getUID());

            int partnerId;
            partnerId = entity.partnerId;
            InfoDB db = new InfoDB();

            ArrayList<ChargingInfo> temp = db.getChargings(partnerId, entity.cellPhone);

//            if (aSession.getByteProtocol() >= AIOConstants.PROTOCOL_REFACTOR_BIENG) {
            StringBuilder sb = new StringBuilder();

            List<ChargingInfo> cardInfo = db.getLstCardInfo();

            int tempSize = temp.size();
            for (int i = 0; i < tempSize; i++) {
                ChargingInfo info = temp.get(i);
                sb.append(info.number).append(AIOConstants.SEPERATOR_BYTE_1);
                String activeValue = (entity.refCode != 0) ? info.value + " " + entity.refCode : info.value;
                sb.append(activeValue).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(info.desc).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(info.isSMS ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_2);
            }

            int cardInfoSize = cardInfo.size();
            for (int i = 0; i < cardInfoSize; i++) {
                ChargingInfo info = cardInfo.get(i);
                sb.append(info.number).append(AIOConstants.SEPERATOR_BYTE_1);
                //String activeValue = (entity.refCode != 0) ? info.value + " " + entity.refCode : info.value;
                sb.append(info.value).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(info.desc).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(info.isSMS ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_2);

            }

            if (cardInfoSize > 0 || tempSize > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }

            resCharg.value = sb.toString();

//            }
            resCharg.setSuccess(ResponseCode.SUCCESS, temp);
            resCharg.session = aSession;
            aSession.write(resCharg);
        } catch (Throwable t) {
            resCharg.setFailure(ResponseCode.FAILURE, "lỗi!");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
            aResPkg.addMessage(resCharg);
        }

        return 1;
    }
}
