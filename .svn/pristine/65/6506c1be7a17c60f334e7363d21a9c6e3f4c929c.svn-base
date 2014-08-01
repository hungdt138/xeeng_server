package com.tv.xeeng.base.business;

import org.slf4j.Logger;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.TransferCashRequest;
import com.tv.xeeng.base.protocol.messages.TransferCashResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import com.tv.xeeng.server.Server;


import java.util.List;

public class TransferCashBusiness extends AbstractBusiness {

	private static final Logger mLog = LoggerContext.getLoggerFactory()
			.getLogger(TransferCashBusiness.class);
	
	public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
			IResponsePackage aResPkg) {
		
		// process's status
		int rtn = PROCESS_FAILURE;
		
		mLog.debug("[TRANSFER_CASH]: Catch");
		MessageFactory msgFactory = aSession.getMessageFactory();
		
		TransferCashResponse resTransferCash = (TransferCashResponse) msgFactory
				.getResponseMessage(aReqMsg.getID());
                resTransferCash.session = aSession;
		try {
			
			TransferCashRequest request = (TransferCashRequest) aReqMsg;
			long s_uid = aSession.getUID();
			
            List<ISession> lstSessions = aSession.getManager().findAllSession(s_uid);
            if(lstSessions.size()>1)
            {
                throw new BusinessException("Bạn không thể chuyển tiền vì đăng nhập bằng 2 nick"); 
            }
                        
			long d_uid;
			
			String d_name = request.destName;
			long money = request.money;

			if(aSession.getUserName().equals(request.destName)) {
				 throw new BusinessException("Không được chuyển tiền cho chính mình"); 
			}
            
			if (aSession.getRoom() != null) 
            {
                SimpleTable table = aSession.getRoom().getAttactmentData();
				if (table != null && table.isPlaying) {
					 throw new BusinessException("Bạn không được chuyển tiền khi bàn đang chơi"); 
				}
            }

			UserDB userDb = new UserDB();			
		    mLog.debug("Data transfer " + s_uid + d_name + money + Server.MONEY_TRANSFER_DAY_LIMIT + Server.MONEY_TRANSFER_TAX);
			d_uid = userDb.transferMoney(s_uid, d_name, money, Server.MONEY_TRANSFER_DAY_LIMIT, Server.MONEY_TRANSFER_TAX);
			
			if (d_uid == -1) {
				resTransferCash.setFailure(ResponseCode.FAILURE,
						"Tài khoản đích không tồn tại");

			} else if (d_uid == -2) {
				resTransferCash.setFailure(ResponseCode.FAILURE,
						"Bạn không đủ tiền để chuyển.");

			} else if (d_uid == -3) {
				resTransferCash.setFailure(ResponseCode.FAILURE,
						"Số tiền chuyển tối đa trong một ngày không quá " +  Server.MONEY_TRANSFER_DAY_LIMIT);
			
			} else if (d_uid == -4) {
					resTransferCash.setFailure(ResponseCode.FAILURE,
						"Không chuyển được tiền, không phải bạn bè ");	

			} else {
				
				// delete cached			
				CacheUserInfo.deleteCacheUserById(s_uid);
	            CacheUserInfo.deleteCacheUserById(d_uid);
				resTransferCash.setSuccess(ResponseCode.SUCCESS,"Chuyển thành công " + request.money + " Gold cho bạn " + request.destName);
			}
			
			// get destination session
			rtn = PROCESS_OK;
		} 
        catch(BusinessException be)
        {
            resTransferCash.setFailure(ResponseCode.FAILURE, be.getMessage());
            rtn = PROCESS_OK;

        }
        catch (Throwable t) {
			resTransferCash.setFailure(ResponseCode.FAILURE, "Bị lỗi: ");
			mLog.debug("Bị lỗi: " + t.getMessage());
			rtn = PROCESS_OK;
		} finally {
			if ((resTransferCash != null) && (rtn == PROCESS_OK)) {
				aResPkg.addMessage(resTransferCash);
			}
		}
		return rtn;
	}
}
