

package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

/**
 *
 * @author Dinhpv
 */
public class GetNewsDetailRequest extends AbstractRequestMessage {
    public int pageIndex;
    public long newsId;
    public int categoryId;
    
    public IRequestMessage createNew()
    {
        return new GetNewsDetailRequest();
    }
}
