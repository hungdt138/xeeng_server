package com.tv.xeeng.base.protocol.messages;

import java.util.ArrayList;



import java.util.List;

import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.phom.data.PhomTable;
import com.tv.xeeng.game.tienlen.data.TienLenTable;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class EnterRoomResponse extends AbstractResponseMessage {

    public String mErrorMsg;
    public int zoneID;
    public ArrayList<PhomTable> phomTables= new ArrayList<PhomTable>();
   
    public ArrayList<TienLenTable> tienlenTables = new ArrayList<TienLenTable>();
    public List<SimpleTable> tables = null;
    
    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }
    public void setZoneID (int id){
    	this.zoneID = id;
    }
    
   
    public void setSuccessTienLen(int aCode, ArrayList<TienLenTable> t) {
        mCode = aCode;
        this.tienlenTables = t;
    }
    public void setSuccessPhom(int aCode, ArrayList<PhomTable> t) {
        mCode = aCode;
        this.phomTables = t;
    }
    
    public void setSuccess(int aCode, List<SimpleTable> t) {
        mCode = aCode;
        this.tables = t;
    }
    
    public IResponseMessage createNew() {
        return new EnterRoomResponse();
    }
}
