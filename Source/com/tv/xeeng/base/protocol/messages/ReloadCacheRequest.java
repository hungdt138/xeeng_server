

package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

/**
 *
 * @author Dinhpv
 */
public class ReloadCacheRequest extends AbstractRequestMessage {

    
    
    public IRequestMessage createNew()
    {
        return new ReloadCacheRequest();
    }
}
