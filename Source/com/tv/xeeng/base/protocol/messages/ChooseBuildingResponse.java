/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


/**
 *
 * @author tuanda
 */
public class ChooseBuildingResponse extends AbstractResponseMessage {

    public String mErrorMsg;

    public void setFailure(String aErrorMsg) {
        mCode = ResponseCode.FAILURE;
        mErrorMsg = aErrorMsg;
    }

    public void setSuccess() {
        mCode = ResponseCode.SUCCESS;
    }

    @Override
    public IResponseMessage createNew() {
        return new ChooseBuildingResponse();
    }
}
