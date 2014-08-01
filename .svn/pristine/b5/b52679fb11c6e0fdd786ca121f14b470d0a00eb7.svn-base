/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class ReplyResponse extends AbstractResponseMessage
{

    public String mErrorMsg;
    public boolean mIsAccept;
    public long source_uid; // who are accepted
    public String username;

    public void setSuccess(int aCode, boolean aIsAccept, long source, String name)
    {
        mCode = aCode;
        mIsAccept = aIsAccept;
        source_uid = source;
        username = name;
    }

    public void setFailure(int aCode, String aErrorMsg)
    {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public IResponseMessage createNew()
    {
        return new ReplyResponse();
    }
}
