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
public class BaoSamResponse extends AbstractResponseMessage {

    public String message;
    public long uid;
    public boolean hasBaoSam;
    public boolean isBaoSamDone;

    public void setSuccess(long u, boolean hasBao, boolean baoDone) {
        mCode = ResponseCode.SUCCESS;
        uid = u;
        hasBaoSam = hasBao;
        isBaoSamDone = baoDone;
    }

    public void setFailure(String msg) {
        mCode = ResponseCode.FAILURE;
        message = msg;
    }

    public IResponseMessage createNew() {
        return new BaoSamResponse();
    }
}
