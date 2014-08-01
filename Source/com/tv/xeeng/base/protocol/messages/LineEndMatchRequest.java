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
public class LineEndMatchRequest extends AbstractRequestMessage {
    public long mMatchId;
    public String message;
    public String matrix;
    public boolean isWin;
    public int type;
    //Pikachu
    public int pikaPoint;
    public IRequestMessage createNew() {
        return new LineEndMatchRequest();
    }
}

