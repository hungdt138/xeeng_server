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
public class AcceptMXHRequest extends AbstractRequestMessage
{
    public int requestId;
    public boolean isAccept;
    public long destUid;
    public long matchId; // Added by ThangTD
    
    public IRequestMessage createNew()
    {
        return new AcceptMXHRequest();
    }
}
