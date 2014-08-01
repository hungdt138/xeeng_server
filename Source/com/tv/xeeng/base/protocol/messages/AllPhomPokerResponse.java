/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages;

import java.util.ArrayList;

import com.tv.xeeng.game.phom.data.PhomPlayer;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class AllPhomPokerResponse extends AbstractResponseMessage
{

    public String mErrorMsg;
    public ArrayList<PhomPlayer> playings = new ArrayList<PhomPlayer>();
    public void setSuccess(int aCode , ArrayList<PhomPlayer> p)
    {
        mCode = aCode;
        playings = p;
    }

    public void setFailure(int aCode, String aErrorMsg)
    {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public IResponseMessage createNew()
    {
        return new AllPhomPokerResponse();
    }
}
