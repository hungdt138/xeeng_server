package com.tv.xeeng.base.business;

import java.util.List;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.data.CommonQueue;
import com.tv.xeeng.base.protocol.messages.GetListEventResponse;
import com.tv.xeeng.base.protocol.messages.SendImageResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.EventDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.EventEntity;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.QueueEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.memcached.data.XEDataUtils;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import com.tv.xeeng.memcached.data.XEGlobalCache;
import java.sql.SQLException;
import java.util.logging.Level;

public class GetListEventBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(GetListEventBusiness.class);
    private static final String MEMCACHED_NAME = "ListEvent";

    private void insertQueue(MessageFactory msgFactory, String value, ISession aSession) {
        SendImageResponse res = (SendImageResponse) msgFactory.getResponseMessage(MessagesID.SEND_IMAGE_EVENT);
        QueueEntity entity = new QueueEntity(aSession, res);
        res.setSuccess(ResponseCode.SUCCESS);

        res.value = value;

        CommonQueue queue = new CommonQueue();
        queue.insertQueue(entity);

    }

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetListEventResponse resBoc = (GetListEventResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            Object raw = XEGlobalCache.getCache(MEMCACHED_NAME);
            List<EventEntity> lstEvents = null;
            
            if (raw != null) {
                lstEvents = (List<EventEntity>) raw;
            }

            if (lstEvents == null) {
                lstEvents = XEDataUtils.getEventFromDB();

                XEGlobalCache.setCache(MEMCACHED_NAME, lstEvents, XEGlobalCache.TIMEOUT_30_MIN);
            }
            
//            List<EventEntity> lstEvents = EventDB.getEvents(aSession.getUserEntity().partnerId);
            /*int size = lstEvents.size();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                EventEntity entity = lstEvents.get(i);
                sb.append(entity.getEventId()).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(entity.getTittle()).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(entity.isThumb() ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_2);

                if (entity.isThumb()) {
                    StringBuilder thumb = new StringBuilder();
                    thumb.append(entity.getEventId()).append(AIOConstants.SEPERATOR_BYTE_1);
                    thumb.append("1").append(AIOConstants.SEPERATOR_BYTE_1);
                    thumb.append(entity.getThumbDetail());
                    insertQueue(msgFactory, thumb.toString(), aSession);
                }
            }

            if (size > 0)
                sb.deleteCharAt(sb.length() - 1);*/
            resBoc.mCode = ResponseCode.SUCCESS;
            resBoc.value = XEDataUtils.serializeList(lstEvents, AIOConstants.SEPERATOR_BYTE_2);
            //resBoc.value = sb.toString();
        } catch (SQLException ex) {
            mLog.error(ex.getMessage());
        } finally {
            aResPkg.addMessage(resBoc);
        }
        return 1;
    }

}
