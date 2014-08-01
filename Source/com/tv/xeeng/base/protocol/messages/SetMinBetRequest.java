/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

/**
 *
 * @author Thomc
 */
public class SetMinBetRequest extends AbstractRequestMessage {

    public long mMatchId;
    public String errMsg;
    public long moneyBet;

    public IRequestMessage createNew() {
        return new SetMinBetRequest();
    }
}
