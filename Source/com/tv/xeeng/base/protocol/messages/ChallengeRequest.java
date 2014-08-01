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
public class ChallengeRequest extends AbstractRequestMessage {
    public long matchID;
    public long uid=-1;
    public boolean isChan;
//    public long chan;
//    public long le;
    public long money;
    
        
    public IRequestMessage createNew()
    {
        return new ChallengeRequest();
    }

    
}
