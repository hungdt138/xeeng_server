package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.data.CommonQueue;
import com.tv.xeeng.base.protocol.messages.GetEventDetailRequest;
import com.tv.xeeng.base.protocol.messages.GetEventDetailResponse;
import com.tv.xeeng.base.protocol.messages.SendImageResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.EventEntity;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.QueueEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.memcached.data.XEDataUtils;
import com.tv.xeeng.memcached.data.XEGlobalCache;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;

public class GetEventDetailBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(GetEventDetailBusiness.class);
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
        GetEventDetailResponse resBoc = (GetEventDetailResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            GetEventDetailRequest rqEvent = (GetEventDetailRequest) aReqMsg;
            
            Object raw = XEGlobalCache.getCache(MEMCACHED_NAME);
            List<EventEntity> lstEvents = null;
            
            if (raw != null) {
                lstEvents = (List<EventEntity>) raw;
            }

            if (lstEvents == null) {
                lstEvents = XEDataUtils.getEventFromDB();

                XEGlobalCache.setCache(MEMCACHED_NAME, lstEvents, XEGlobalCache.TIMEOUT_30_MIN);
            }
            
            StringBuilder sb = new StringBuilder();
            
            int size = lstEvents.size();
            for (int i = 0; i < size; i++) {
                EventEntity entity = lstEvents.get(i);
                if (entity.getEventId() == rqEvent.eventId) {
                    sb.append(entity.getEventId()).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(entity.getContent());

                    resBoc.mCode = ResponseCode.SUCCESS;
                    resBoc.value = sb.toString();

                    if (entity.isDetailImage()) {
                        StringBuilder thumb = new StringBuilder();
                        thumb.append(entity.getEventId()).append(AIOConstants.SEPERATOR_BYTE_1);
                        thumb.append("0").append(AIOConstants.SEPERATOR_BYTE_1);
                        thumb.append(entity.getPicDetail());
                        insertQueue(msgFactory, thumb.toString(), aSession);
                    }

                    return 1;
                }
            }

            resBoc.mCode = ResponseCode.FAILURE;
            resBoc.value = "Không tồn tại event này";

        } catch (SQLException ex) {
            mLog.error(ex.getMessage());
        } finally {
            aResPkg.addMessage(resBoc);
        }
        return 1;
    }

}
