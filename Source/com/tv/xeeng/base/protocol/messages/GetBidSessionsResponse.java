/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages;

import java.util.ArrayList;

import com.tv.xeeng.game.bet.MatchEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class GetBidSessionsResponse extends AbstractResponseMessage {

    public String values;
    public String mErrorMsg;

    public void setSuccess(String m) {
        mCode = ResponseCode.SUCCESS;
        values = m;
    }

    public void setFailure(String m) {
        mCode = ResponseCode.FAILURE;
        mErrorMsg = m;
    }

    public IResponseMessage createNew() {
        return new GetBidSessionsResponse();
    }
}
