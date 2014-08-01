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
public class LineGetOtherTableOutResponse extends AbstractResponseMessage {
    public long uid;
    public void setSuccess(String n) {
        mCode = ResponseCode.SUCCESS;
    }

    public IResponseMessage createNew() {
        return new LineGetOtherTableOutResponse();
    }
}
