/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages;

import java.util.List;

import com.tv.xeeng.game.data.SimplePlayer;
import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

/**
 *
 * @author tuanda
 */
public class BetRequest extends AbstractRequestMessage {
    public long matchID;
    public long uid=-1;
    public boolean isChan;
    public long money;
    public long chan;
    public long le;
    public long holo;
    public long tom;
    public long cua;
    public long ca;
    public long ga;
    public long huou;
    
    public int type;
    
    public List<SimplePlayer> playings;
        
    public IRequestMessage createNew()
    {
        return new BetRequest();
    }
}
