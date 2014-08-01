

package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

/**
 *
 * @author Dinhpv
 */
public class GetTopGameRequest extends AbstractRequestMessage {
    public int gameId;
    
    public IRequestMessage createNew()
    {
        return new GetTopGameRequest();
    }
}
