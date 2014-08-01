/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.xam.data.SamPlayer;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;
import java.util.ArrayList;

/**
 *
 * @author ThangTD
 */
public class BatCaiResponse extends AbstractResponseMessage {

    public String message;
    public ArrayList<SamPlayer> samPlayers;

    @Override
    public IResponseMessage createNew() {
        return new BatCaiResponse();
    }

    public void setSuccess(int aCode, ArrayList<SamPlayer> samPlayers) {
        mCode = aCode;
        this.samPlayers = samPlayers;
    }

    public void setFailure(int aCode, String msg) {
        mCode = aCode;
        message = msg;
    }

    @Override
    public IResponseMessage clone(ISession session) {
        BatCaiResponse resMsg = (BatCaiResponse) createNew();

        resMsg.session = session;
        resMsg.setID(this.getID());
        resMsg.mCode = mCode;
        resMsg.samPlayers = samPlayers;
        resMsg.message = message;

        return resMsg;
    }
}
