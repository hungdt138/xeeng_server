package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

/**
 *
 * @author tuanda
 */
public class NewRequest extends AbstractRequestMessage {

    public long mid;
    public long moneyBet;
    public int roomType;
    public long uid;
    public String password;
    public int size;
    public String roomName;
    public int phongID;
    public int tableIndex;
    
    //Caro
    public int mRow;
    public int mCol;
    
    //Phom
    public boolean isKhan = true;
    public boolean isAn = true;
    public boolean isTai = true;
    public int testCode = 0;
    public int available = 0;

    //New_Pika
    public boolean advevntureMode;
    public int matrixSize;
    public int pikaLevel;
    
    @Override
    public IRequestMessage createNew() {
        return new NewRequest();
    }
}
