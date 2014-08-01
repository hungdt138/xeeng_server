

package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

/**
 *
 * @author Dinhpv
 */
public class UseCardRequest extends AbstractRequestMessage {

    public String cardId;
    public String cardCode;
    public String serviceId;
    public String refCode;
    
    public IRequestMessage createNew()
    {
        return new UseCardRequest();
    }
}
