package com.tv.xeeng.base.business;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.UpdateUserInfoRequest;
import com.tv.xeeng.base.protocol.messages.UpdateUserInfoResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class UpdateUserInfoBusiness extends AbstractBusiness {

	private static final Logger mLog = LoggerContext.getLoggerFactory()
			.getLogger(UpdateUserInfoBusiness.class);

	public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
			IResponsePackage aResPkg) throws ServerException {

		int rtn = PROCESS_FAILURE;
		mLog.debug("[UPDATE USER] : Catch");
		UserDB userDb = new UserDB();

		MessageFactory msgFactory = aSession.getMessageFactory();

		UpdateUserInfoResponse resUpdate = (UpdateUserInfoResponse) msgFactory
				.getResponseMessage(aReqMsg.getID());
		try {
			UpdateUserInfoRequest rqRegister = (UpdateUserInfoRequest) aReqMsg;
                        int retDb = 0;
                        if(aSession.isMXHDevice())
                        {
                            retDb = userDb.changePassword(aSession.getUID(),rqRegister.oldPassword, rqRegister.newPassword);
                        }
                        else
                        {
                            retDb = userDb.updateUserInfo(aSession.getUID(), rqRegister.number, 
                                rqRegister.email, rqRegister.oldPassword, rqRegister.newPassword, rqRegister.age, rqRegister.sex);
                        }
                        
                        
                        resUpdate.session = aSession;
                        switch(retDb)
                        {
                            case 1:
                                resUpdate.setFailure(ResponseCode.FAILURE,
						"Không tìm thấy tên bạn trong cơ sở dữ liệu!");
                                break;
                            case 2:
                                resUpdate.setFailure(ResponseCode.FAILURE, "Mật khẩu cũ không chính xác");
                                break;
                            case 0:
                                resUpdate.setSuccess(ResponseCode.SUCCESS);
                                CacheUserInfo cache = new CacheUserInfo();
                                cache.deleteCacheUser(aSession.getUserEntity());
                                break;    
                        }
                        
                        

			rtn = PROCESS_OK;
		} catch (Throwable t) {
			resUpdate.setFailure(ResponseCode.FAILURE,
					"Dữ liệu bạn nhập không chính xác!");
			rtn = PROCESS_OK;
			mLog.error("Process message " + aReqMsg.getID() + " error.", t);
		} finally {
			if ((resUpdate != null) && (rtn == PROCESS_OK)) {
				aResPkg.addMessage(resUpdate);
			}
		}
		return rtn;
	}
}
