/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;


/**
 *
 * @author Dinhpv
 */
public class LogoutJSON implements IMessageProtocol {

    public boolean decode(Object paramObject, IRequestMessage paramIRequestMessage) throws ServerException {
        return true;
    }

    public Object encode(IResponseMessage paramIResponseMessage) throws ServerException {
        return true;
    }
}
