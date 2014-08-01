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
public class JoinRequest extends AbstractRequestMessage {
    public long mMatchId;
    public long uid;
    public String password;
    public int zone_id=-1;
    public int roomID;
    public int phongId;
    
    //New Pikachu
    public int matrixSize;
    @Override
    public IRequestMessage createNew()
    {
        return new JoinRequest();
    }
}
