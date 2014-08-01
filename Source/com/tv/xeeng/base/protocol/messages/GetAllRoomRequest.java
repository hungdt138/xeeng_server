/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

/**
 *
 * @author Administrator
 */
public class GetAllRoomRequest extends AbstractRequestMessage {
	public int level=0;
    public IRequestMessage createNew() {
        return new GetAllRoomRequest();
    }

}
