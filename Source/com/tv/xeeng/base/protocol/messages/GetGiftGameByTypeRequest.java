/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

/**
 *
 * @author tuanda
 */
public class GetGiftGameByTypeRequest extends AbstractRequestMessage {

    public int idType;

    @Override
    public IRequestMessage createNew() {
        return new GetGiftGameByTypeRequest();
    }
}
