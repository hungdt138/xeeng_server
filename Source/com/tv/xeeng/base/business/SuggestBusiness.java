package com.tv.xeeng.base.business;


import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.SuggestRequest;
import com.tv.xeeng.base.protocol.messages.SuggestResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DatabaseDriver;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class SuggestBusiness extends AbstractBusiness
{

    private static final Logger mLog = 
    	LoggerContext.getLoggerFactory().getLogger(SuggestBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg)
    {
        mLog.debug("[ SUGGEST ]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        SuggestResponse resSuggest = (SuggestResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try
        {
            SuggestRequest rqSuggest = (SuggestRequest) aReqMsg;
            long uid = aSession.getUID();
            String note = rqSuggest.note;
            mLog.debug("[ SUGGEST ]: of" + uid);
            DatabaseDriver.insertSuggestion(uid, note);
            resSuggest.setSuccess(ResponseCode.SUCCESS);
        } catch (Throwable t){
        	resSuggest.setFailure(ResponseCode.FAILURE, "Quá trình góp ý bị lỗi");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
        	if(resSuggest != null){
        		aResPkg.addMessage(resSuggest);
        	}
        }
        return 1;
    }
}
