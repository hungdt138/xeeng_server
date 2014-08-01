/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.PostCommentRequest;
import com.tv.xeeng.base.protocol.messages.PostCommentResponse;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



/**
 *
 * @author Dinhpv
 */
public class PostCommentJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
            SuggestJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
            throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            PostCommentRequest matchStart = (PostCommentRequest) aDecodingObj;
            matchStart.name = jsonData.getString("name");
            matchStart.note = jsonData.getString("post");
            matchStart.postID = jsonData.getInt("postID");
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage)
            throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            encodingObj.put("mid", aResponseMessage.getID());
            PostCommentResponse suggest = (PostCommentResponse) aResponseMessage;
            encodingObj.put("code", suggest.mCode);
            if (suggest.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", suggest.mErrorMsg);
            }
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
