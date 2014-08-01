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
public class GetAvatarsRequest extends AbstractRequestMessage {

    public int page;
    public int category;
    @Override
    public IRequestMessage createNew() {
        return new GetAvatarsRequest();
    }
}
