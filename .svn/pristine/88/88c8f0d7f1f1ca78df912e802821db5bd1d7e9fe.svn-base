/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.game.data.XELogEventGiftEntity;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;
import java.util.List;

/**
 *
 * @author ThangTD
 */
public class XEGetEventGiftHistoryResponse extends AbstractResponseMessage {

    String message;
    public List<XELogEventGiftEntity> itemsList;
    
    @Override
    public IResponseMessage createNew() {
        return new XEGetEventGiftHistoryResponse();
    }
    
    public void setFailure(int aCode, String msg) {
        mCode = aCode;
        message = msg;
    }
    
    public void setSuccess(int aCode, List<XELogEventGiftEntity> items) {
        mCode = aCode;
        itemsList = items;
    }
}
