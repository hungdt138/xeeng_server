

package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

/**
 *
 * @author Dinhpv
 */
public class BetEuroMatchRequest extends AbstractRequestMessage {
    public long money;
    public int type;
    public int bet;
    public int matchID;
    
    public IRequestMessage createNew()
    {
        return new BetEuroMatchRequest();
    }
}
