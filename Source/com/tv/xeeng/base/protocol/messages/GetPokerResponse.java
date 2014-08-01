package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.binh.data.BinhPoker;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.xam.data.Poker;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class GetPokerResponse extends AbstractResponseMessage {

    public long uid;
    public String name;
    public ArrayList<com.tv.xeeng.game.phom.data.Poker> phomCards = new ArrayList<com.tv.xeeng.game.phom.data.Poker>();
    public ArrayList<com.tv.xeeng.game.tienlen.data.Poker> tienlenCards = new ArrayList<com.tv.xeeng.game.tienlen.data.Poker>();
    private List<BinhPoker> binhCards;
    //Thomc for Tienlen
    public byte[] tienlenCards_new;
    public boolean isNewMatch = false;
    public int dutyType;

    public long first_id = 0;
    public long matchNum = 0;

    //NewBacay
    public String cards;
    public int zoneId;
    public JSONArray betInfo;

    public String value;

    //sam
    public String samCards;
    public int samPerfectType;

    public void setSamCards(ArrayList<com.tv.xeeng.game.xam.data.Poker> cards) {
        StringBuilder sb = new StringBuilder();
        for (Poker p : cards) {
            sb.append(String.valueOf(p.toInt())).append("#");
        }
        if (!cards.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        this.samCards = sb.toString();
    }

    public void setPhomCards(ArrayList<com.tv.xeeng.game.phom.data.Poker> cards) {
        this.phomCards = cards;
    }

    public void setTienLenCards(ArrayList<com.tv.xeeng.game.tienlen.data.Poker> cards) {
        this.tienlenCards = cards;
    }

    public void setTienLenCards(byte[] cards) {
        this.tienlenCards_new = cards;
    }
    public long beginID = -1;

    public void setBeginID(long b) {
        beginID = b;
    }

    public void setSuccess(int aCode, long id, String n) {
        mCode = aCode;
        uid = id;
        name = n;

    }

    public void setNewBacaySuccess(long uid, String cards, JSONArray betInfo) {
        this.uid = uid;
        this.cards = cards;
        this.mCode = ResponseCode.SUCCESS;
        this.zoneId = ZoneID.NEW_BA_CAY;
        this.betInfo = betInfo;
    }

    public void setSuccess(String c, long id) {
        mCode = ResponseCode.SUCCESS;
        cards = c;
        beginID = id;
    }
    
    public IResponseMessage createNew() {
        return new GetPokerResponse();
    }

    @Override
    public IResponseMessage clone(ISession session) {
        GetPokerResponse resMsg = (GetPokerResponse) createNew();

        resMsg.session = session;
        resMsg.setID(this.getID());
        resMsg.mCode = mCode;
//                resMsg.mErrorMsg = mErrorMsg;
        resMsg.uid = uid;
        resMsg.betInfo = betInfo;
        resMsg.zoneId = zoneId;
        resMsg.beginID = beginID;
        resMsg.value = value;
        return resMsg;
    }

    public int getSamPerfectType() {
        return samPerfectType;
    }

    public void setSamPerfectType(int samPerfectType) {
        this.samPerfectType = samPerfectType;
    }

    public List<BinhPoker> getBinhCards() {
        return binhCards;
    }

    public void setBinhCards(List<BinhPoker> binhCards) {
        this.binhCards = binhCards;
    }
}
