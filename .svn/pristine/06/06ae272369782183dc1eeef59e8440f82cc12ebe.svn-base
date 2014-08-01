package com.tv.xeeng.base.business;

import java.util.Vector;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetAvatarListResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DatabaseDriver;
import com.tv.xeeng.databaseDriven.InfoDB;
import com.tv.xeeng.game.data.AvatarEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class GetAvatarListBusiness extends AbstractBusiness{

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetAvatarListBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg)
    {
        
        mLog.debug("[Get Avatar] : Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetAvatarListResponse resGetAvatarList = 
        	(GetAvatarListResponse)msgFactory.getResponseMessage(aReqMsg.getID());
        try{
        	Vector<AvatarEntity> avaList = new Vector<AvatarEntity>();//InfoDB.getLstAvatars();//DatabaseDriver.getAvatarList();
        	resGetAvatarList.setSuccess(ResponseCode.SUCCESS, avaList);
                resGetAvatarList.session = aSession;
        }catch (Exception e) {
        	mLog.debug("Get avatar list error:"+e.getCause());
			resGetAvatarList.setFailure(ResponseCode.FAILURE, "Không thể kết nối đến cơ sở dữ liệu");
		} finally{
            if ((resGetAvatarList != null)){
                aResPkg.addMessage(resGetAvatarList);
            }
		}
		return 1;
    }   
}
