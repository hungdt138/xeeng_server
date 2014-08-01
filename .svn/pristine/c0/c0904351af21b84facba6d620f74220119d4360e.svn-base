package com.tv.xeeng.base.business;

import java.util.Vector;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetFriendResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.FriendDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


public class GetFriendBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetFriendBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[GET FRIENDLIST]: Catch");
        
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetFriendResponse resGetFriendList = (GetFriendResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        
        try {
            long uid = aSession.getUID();
            mLog.debug("[GET FRIENDLIST]: for" + uid);
            FriendDB friendDB = new FriendDB();
             Vector<UserEntity> frientlist = friendDB.getFrientList(uid, false);
             
            StringBuilder sb = new StringBuilder();
            int size = frientlist.size();
            for(int i = 0; i< size; i++)
            {
                UserEntity entity = frientlist.get(i);
                sb.append(entity.mUid).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(entity.mUsername).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(entity.isLogin?"1":"0").append(AIOConstants.SEPERATOR_BYTE_2);
            }
            
            if(size>0)
                sb.deleteCharAt(sb.length() -1);
            
             resGetFriendList.setSuccess(sb.toString());
        } catch (Throwable t) {
            resGetFriendList.setFailure("Có lỗi xảy ra");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if ((resGetFriendList != null)) {
                aResPkg.addMessage(resGetFriendList);
            }
        }
        return 1;
    }
}
