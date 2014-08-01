package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.XEGetAllUserLevelsRequest;
import com.tv.xeeng.base.protocol.messages.XEGetAllUserLevelsResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.XEUserLevelEntity;
import com.tv.xeeng.memcached.data.XEDataUtils;
import static com.tv.xeeng.memcached.data.XEDataUtils.serializeList;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import java.util.List;
import org.slf4j.Logger;

public class XEGetAllUserLevelsBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(XEGetAllUserLevelsBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) throws ServerException {
        mLog.debug("[GET ALL NEWS]: ");

        MessageFactory msgFactory = aSession.getMessageFactory();
        XEGetAllUserLevelsResponse res = (XEGetAllUserLevelsResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        res.session = aSession;
        XEGetAllUserLevelsRequest rq = (XEGetAllUserLevelsRequest) aReqMsg;

        List<XEUserLevelEntity> levels = XEDataUtils.getAllUserLevels();

        if (levels == null) {
            res.mCode = ResponseCode.FAILURE;
            res.setSerializedString("Không lấy được cấp độ người chơi.");
        } else {
            res.mCode = ResponseCode.SUCCESS;
            res.setSerializedString(serializeList(levels));
        }

        aResPkg.addMessage(res);

        return 1;
    }
}
