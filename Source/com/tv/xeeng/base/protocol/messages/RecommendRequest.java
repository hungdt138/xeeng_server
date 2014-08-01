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
public class RecommendRequest extends AbstractRequestMessage {
   public boolean isLag;
    public boolean isDiffLogin;
    public boolean isDesign;
    public boolean isErrorGame;
    public boolean isHack;
    public String content;
            
    
    
    public IRequestMessage createNew()
    {
        return new RecommendRequest();
    }
}
