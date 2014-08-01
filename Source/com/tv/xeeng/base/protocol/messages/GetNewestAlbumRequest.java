

package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

/**
 *
 * @author Dinhpv
 */
public class GetNewestAlbumRequest extends AbstractRequestMessage {
    public int page;
    public int size;
    
    public IRequestMessage createNew()
    {
        return new GetNewestAlbumRequest();
    }
}
