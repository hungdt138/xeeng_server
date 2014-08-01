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
public class InsertLikeRequest extends AbstractRequestMessage {
   
    public int systemObjectId;
    public long systemObjectRecordId;
        
    public IRequestMessage createNew()
    {
        return new InsertLikeRequest();
    }
}
