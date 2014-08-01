package com.tv.xeeng.base.protocol.messages;

import java.util.ArrayList;
import java.util.List;

import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimplePlayer;
import com.tv.xeeng.game.phom.data.PhomPlayer;
import com.tv.xeeng.game.tienlen.data.TienLenPlayer;
import com.tv.xeeng.game.xam.data.SamPlayer;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class ReconnectResponse extends AbstractResponseMessage {

    public String mErrorMsg;
    public ArrayList<PhomPlayer> mWaitingPlayerPhom;
    public ArrayList<PhomPlayer> mPlayerPhom;
    public int currCard;

    public List<? extends SimplePlayer>  mTienLenPlayer;
    public List<? extends SimplePlayer>  mWaitingPlayerTienlen;
    
    public ArrayList<SamPlayer> mPlayerSam;
    public ArrayList<SamPlayer> mWaitingPlayerSam;
    
    public List<? extends SimplePlayer> playing;
    public List<? extends SimplePlayer> waiting;
    
    public SimplePlayer roomOwner;
    public String roomName;
    public long mMatchId;
    public long minBet;
    public int zoneID;
    public int capacity;
    public boolean isAn;
    public boolean isTaiGui;
    public boolean isPlaying;
    public boolean isResume;
    public boolean isObserve;
    public long turn;
    public int deck;
    public String cards = "";
    public String myHandCards;
    public boolean isInvite = false;
    //Caro
    public boolean mIsStarting;
    public boolean mIsPlayer;
    public boolean mIsYourTurn;
    public int mType;
   
    public boolean isJoinAfterPlaying = false;
    public int available = 0; //cờ tướng: quân cờ chấp
    public long currentID = 0;
    public boolean truong;
    public boolean isNeeded = true;
    
    //tien len
    public boolean isHidePoker = true;
    public int duty;
    public long currUserHasDuty;

    public void setIsJoinAfterPlaying(boolean isTrue) {
        this.isJoinAfterPlaying = isTrue;
    }
    
    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public void setOwner(SimplePlayer owner) {
        this.roomOwner = owner;
    }

    public void setCapacity(int c) {
        capacity = c;
    }

    public void setPhomInfo(boolean isAn, boolean isTaiGui, boolean isPlaying, boolean isResume, long turn, String cards, int deck) {
        this.isAn = isAn;
        this.isTaiGui = isTaiGui;
        this.isPlaying = isPlaying;
        this.isResume = isResume;
        this.turn = turn;
        this.cards = cards;
        this.deck = deck;
    }
    public void setSuccess(){
    	mCode = ResponseCode.SUCCESS;
    }
    public void setSuccess(int aCode, String rName, long bet, int zone) {
        zoneID = zone;
        mCode = aCode;
        roomName = rName;
        minBet = bet;
    }

    public void setRoomID(long aRoomId) {
        mMatchId = aRoomId;
    }

    public void setSuccess(int aCode, boolean aIsPlayer, boolean aIsStarting) {
        mCode = aCode;
        mIsPlayer = aIsPlayer;
        mIsStarting = aIsStarting;
    }

    public void setValue(boolean aIsYourTurn, int aType) {
        mIsYourTurn = aIsYourTurn;
        mType = aType;
    }

    public void setCurrentPlayersTienLen(List<? extends SimplePlayer> aValues, List<? extends SimplePlayer> bValues,
            SimplePlayer owner) {
        roomOwner = owner;
        mTienLenPlayer = aValues;
        mWaitingPlayerTienlen = bValues;
    }

    public void setCurrentPlayersPhom(ArrayList<PhomPlayer> aValues, ArrayList<PhomPlayer> bValues, SimplePlayer owner) {
        roomOwner = owner;
        mPlayerPhom = aValues;
        mWaitingPlayerPhom = bValues;
    }
    
    public void setCurrentPlayersSam(ArrayList<SamPlayer> aValues, ArrayList<SamPlayer> bValues, SimplePlayer owner) {
        roomOwner = owner;
        mPlayerSam = aValues;
        mWaitingPlayerSam = bValues;
    }
    
    public void setCurrentPlayers(List<? extends SimplePlayer> playing,  List<? extends SimplePlayer> waiting, SimplePlayer owner)
    {   
        roomOwner = owner;
        this.playing = playing;
        this.waiting = waiting;
    }

    public IResponseMessage createNew() {
        return new ReconnectResponse();
    }
}
