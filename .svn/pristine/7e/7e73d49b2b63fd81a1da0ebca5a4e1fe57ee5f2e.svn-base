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
public class TurnRequest extends AbstractRequestMessage {

    public long mMatchId;
    public long money; // for bacay
    public int ottObject; // for ott
    public int phomCard; // for phom
    public long uid;
    public boolean isTimeout;
    public boolean isTimeoutTL = false;
    public int zoneId; //onley for auto
    // Tien len
//    Thomc
//    public allinone.tienlen.data.Poker[] tienlenCards ;
    public String tienlenCards;
    public boolean isGiveup;
    // Caro
    public int mRow;
    public int mCol;
    public int mType;
    // CoTuong
  
    //Starwar
    public double power;
    public double angle;
    public long fired;
    public IRequestMessage createNew() {
        return new TurnRequest();
    }
}
