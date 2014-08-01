package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.XEGetNewsRequest;
import com.tv.xeeng.base.protocol.messages.XEGetNewsResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.XENewsEntity;
import com.tv.xeeng.memcached.data.XEDataUtils;
import com.tv.xeeng.memcached.data.XEGlobalCache;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

public class XEGetNewsBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(XEGetNewsBusiness.class);
    private static final String MEMCACHED_NAME = "News";

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) throws ServerException {
        mLog.debug("[GET NEWS BY ID]: ");

        MessageFactory msgFactory = aSession.getMessageFactory();
        XEGetNewsResponse res = (XEGetNewsResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        res.session = aSession;
        XEGetNewsRequest rq = (XEGetNewsRequest) aReqMsg;

        Object raw = XEGlobalCache.getCache(MEMCACHED_NAME + "_" + rq.getNewsId());
        XENewsEntity news = null;

        if (raw != null) {
            news = (XENewsEntity) raw;
        }

        if (news == null) {
            news = XEDataUtils.getNewsById(rq.getNewsId());

            XEGlobalCache.setCache(MEMCACHED_NAME, MEMCACHED_NAME + "_" + rq.getNewsId(), XEGlobalCache.TIMEOUT_30_MIN);
        }

        if (news == null) {
            res.mCode = ResponseCode.FAILURE;
            res.setSerializedString("Không tìm thấy nội dung");
        } else {
            news.setDetail(true); /* lấy thông tin chi tiết, sẽ trả về đầy đủ nội dung */

            res.mCode = ResponseCode.SUCCESS;
            res.setSerializedString(news.toString());
        }

        aResPkg.addMessage(res);

        return 1;
    }
}
