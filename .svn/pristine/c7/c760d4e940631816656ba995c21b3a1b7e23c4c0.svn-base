package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.data.CommonQueue;
import com.tv.xeeng.base.protocol.messages.ReceiveMessageRequest;
import com.tv.xeeng.base.protocol.messages.ReceiveMessageResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.Message;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class ReceiveMessageJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(ReceiveMessageJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            ReceiveMessageRequest send = (ReceiveMessageRequest) aDecodingObj;
            
            if(jsonData.has("uid")) 
            	send.uid = jsonData.getLong("uid");
            
            if(jsonData.has("v"))
            {
                try
                {
                    send.pageIndex = jsonData.getInt("v");
                }
                catch(Exception ex)
                {
                    
                }
            }
            
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }
    private String finalProtocol(ReceiveMessageResponse send, 
    		String separate_ele, String seperate_arr, String seperate_fiff) {
    	 StringBuilder sb = new StringBuilder();
         
         if(send.value != null) //mxh
             return send.value;
         
         int size = send.mess.size();
         for(int i = 0; i< size; i++)
         {
             Message m = send.mess.get(i);
             sb.append(m.sName).append(separate_ele);
             sb.append(m.sID).append(separate_ele);
             
             sb.append(m.id).append(separate_ele);
             sb.append(m.title).append(separate_ele);
             sb.append(m.detail).append(separate_ele);
             sb.append(m.date).append(seperate_arr);
         }
         if(sb.length()>0)
          sb.deleteCharAt(sb.length()-1);
         
    	 return sb.toString();
    }
    
    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            
            ReceiveMessageResponse send = (ReceiveMessageResponse) aResponseMessage;
            if(send.session != null && send.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(send.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (send.mCode == ResponseCode.FAILURE) {
                	 sb.append(send.errMgs);
                }else {
                	sb.append(finalProtocol(send, AIOConstants.SEPERATOR_BYTE_1,
                			AIOConstants.SEPERATOR_BYTE_2, AIOConstants.SEPERATOR_BYTE_3));
                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }
            encodingObj.put("mid", aResponseMessage.getID());
            encodingObj.put("code", send.mCode);
            if (send.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", send.errMgs);
            }else {
                if(send.session != null && send.session.getByteProtocol()> AIOConstants.PROTOCOL_PRIMITIVE)
                {
                    StringBuilder sb = new StringBuilder();
                    sb.append(finalProtocol(send, AIOConstants.SEPERATOR_ELEMENT,
                			AIOConstants.SEPERATOR_ARRAY, AIOConstants.SEPERATOR_DIFF_ELEMENT));
//                    sb.append(AIOConstants.SEPERATOR_DIFF_ELEMENT);
//                    CommonQueue queue = new CommonQueue();
//                    
//                    sb.append(queue.getAdv());
//                    
//                    encodingObj.put("v", sb.toString());
                    
                    
                    
                    return encodingObj;
                }
            	JSONArray arrValues = new JSONArray();
            	for(Message m : send.mess){
            		JSONObject jCell = new JSONObject();
            		jCell.put("s_uid", m.sID);
            		jCell.put("d_uid", m.dID);
            		jCell.put("s_name", m.sName);
            		jCell.put("d_name", m.dName);
            		jCell.put("message", m.detail);
            		jCell.put("title", m.title);
            		jCell.put("date", m.date);
            		jCell.put("id", m.id);
            		arrValues.put(jCell);
            	}
            	encodingObj.put("messages", arrValues);
            }
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
