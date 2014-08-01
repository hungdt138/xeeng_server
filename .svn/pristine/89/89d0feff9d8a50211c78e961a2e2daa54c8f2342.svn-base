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
public class CancelRequest extends AbstractRequestMessage
{

    public long mMatchId;
    public long uid;
    public boolean isLogout;
    public boolean isOutOfGame;
    public int roomID;
    public boolean isSendMe = true;
    
    public IRequestMessage createNew()
    {
        return new CancelRequest();
    }
}
