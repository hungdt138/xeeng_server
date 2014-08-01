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
public class BidResponse extends AbstractResponseMessage {

    public String errMsg;
    public String mess;
    public void setSuccess(String m) {
        this.mCode = ResponseCode.SUCCESS;
        mess = m;
    }

    public void setFailure(String errorMsg) {
        this.mCode = ResponseCode.FAILURE;
        this.errMsg = errorMsg;
    }
@Override
    public IResponseMessage createNew() {
        return new BidResponse();
    }
}
