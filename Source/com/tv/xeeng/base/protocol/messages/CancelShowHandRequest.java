/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages;

import java.util.List;

import com.tv.xeeng.game.baucuatomca.data.BauCuaTomCaPlayer;
import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

/**
 *
 * @author tuanda
 */
public class CancelShowHandRequest extends AbstractRequestMessage {
    public List<BauCuaTomCaPlayer> players;
    public String lstPlayerId;
        
    public IRequestMessage createNew()
    {
        return new CancelShowHandRequest();
    }
}
