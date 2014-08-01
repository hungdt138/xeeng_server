package com.tv.xeeng.base.protocol.messages;

import java.util.ArrayList;

import com.tv.xeeng.game.phom.data.Poker;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class GetOtherPokerResponse extends AbstractResponseMessage {

    public long uid;
    public ArrayList<Poker> phomCards;
    public byte[] tienlenCards;
    public boolean isNew;
    public String samCard;

    public void setSamCard(String sC) {
        samCard = sC;
    }

    public void setPhomCards(ArrayList<com.tv.xeeng.game.phom.data.Poker> cards) {
        this.phomCards = cards;
    }

    public void setTienLenCards(byte[] cards) {
        this.tienlenCards = cards;
    }

    public void setSuccess(int aCode, long id, boolean i) {
        mCode = aCode;
        uid = id;
        isNew = i;
    }

    @Override
    public IResponseMessage createNew() {
        return new GetOtherPokerResponse();
    }
}
