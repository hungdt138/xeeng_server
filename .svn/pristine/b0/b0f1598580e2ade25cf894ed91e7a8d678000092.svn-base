package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.SetRoleRequest;
import com.tv.xeeng.base.protocol.messages.SetRoleResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.InfoDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;





public class SetRoleBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(SetRoleBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[set social avatar] : Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        SetRoleResponse resSocialAvar =
                (SetRoleResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            SetRoleRequest rqSocialAvar = (SetRoleRequest) aReqMsg;
            InfoDB db = new InfoDB();
            db.setRole(rqSocialAvar.systemObjectId, rqSocialAvar.roleId, rqSocialAvar.systemObjectRecordId);
            resSocialAvar.mCode = ResponseCode.SUCCESS;
            
        } catch (Exception e) {
            resSocialAvar.setFailure(ResponseCode.FAILURE, "Không thể kết nối đến cư sở dữ liệu");
            mLog.error(e.getMessage(), e);
            
        } finally {
            if ((resSocialAvar != null)) {
                aResPkg.addMessage(resSocialAvar);
            }
        }
        return 1;
    }
}
