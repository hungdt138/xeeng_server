package com.tv.xeeng.base.business;

import java.util.Vector;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.PostDetailRequest;
import com.tv.xeeng.base.protocol.messages.PostDetailResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DatabaseDriver;
import com.tv.xeeng.game.data.PostEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class PostDetailBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetRichestsBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[GET Wal]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        PostDetailResponse resPostListResponse = (PostDetailResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            long uid = aSession.getUID();
            mLog.debug("[GET RICHEST]: for" + uid);
            PostDetailRequest rqSuggest = (PostDetailRequest) aReqMsg;
            Vector<PostEntity> lists = DatabaseDriver.getCommentList(uid, rqSuggest.postID);
            DatabaseDriver.updateNewComment(rqSuggest.postID, 0);
            resPostListResponse.setSuccess(ResponseCode.SUCCESS, lists);
        } catch (Throwable t) {
            resPostListResponse.setFailure(ResponseCode.FAILURE, "Không thể kết nối đến cơ sở dữ liệu ");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if ((resPostListResponse != null)) {
                aResPkg.addMessage(resPostListResponse);
            }
        }
        return 1;
    }
}
