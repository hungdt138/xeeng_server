/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.UpdateUserMxhInfoRequest;
import com.tv.xeeng.base.protocol.messages.UpdateUserMxhInfoResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


/**
 * 
 * @author tuanda
 */
public class UpdateUserMxhInfoBusiness extends AbstractBusiness {

	private static final Logger mLog = LoggerContext.getLoggerFactory()
			.getLogger(UpdateUserMxhInfoBusiness.class);

	public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
			IResponsePackage aResPkg) throws ServerException {

		int rtn = PROCESS_FAILURE;
		mLog.debug("[UPDATE USER] : Catch");
		UserDB userDb = new UserDB();

		MessageFactory msgFactory = aSession.getMessageFactory();

		UpdateUserMxhInfoResponse resUpdate = (UpdateUserMxhInfoResponse) msgFactory
				.getResponseMessage(aReqMsg.getID());
		try {
			UpdateUserMxhInfoRequest rqRegister = (UpdateUserMxhInfoRequest) aReqMsg;
                        userDb.updateUserMxhInfo(aSession.getUID(), rqRegister.cityId, rqRegister.address,
                                rqRegister.jobId, rqRegister.birthday, rqRegister.hobby, rqRegister.nickSkype,
                                rqRegister.nickYahoo, rqRegister.phoneNumber, rqRegister.sex, rqRegister.avatarFileId,
                                rqRegister.status, rqRegister.characterId);
                        
                        resUpdate.mCode = ResponseCode.SUCCESS;
                        

			rtn = PROCESS_OK;
                        UserEntity entity = new UserEntity();
                        entity.mUid = aSession.getUID();
                        CacheUserInfo userInfo = new CacheUserInfo();
                        userInfo.deleteFullCacheUser(entity);
                                
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
