/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class StartedResponse extends AbstractResponseMessage {

    public long mUid;
    public boolean isFinalFight;
    public long starterID;
    
    public void setStarterID(long starterID) {
		this.starterID = starterID;
	}
    
    public void setIsFinalFight(boolean aIs){
    	isFinalFight = aIs;
    }
    public void setSuccess(int aCode) {
        mCode = aCode;
    }

    public void setSuccess(int aCode, long aUid) {
        mCode = aCode;
        mUid = aUid;
    }

    public IResponseMessage createNew() {
        return new StartedResponse();
    }
}
