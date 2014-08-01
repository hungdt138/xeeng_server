/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

/**
 *
 * @author Dinhpv
 */
public class PostCommentRequest extends AbstractRequestMessage {

    public String name;
    public String note;
    public int postID;

    public IRequestMessage createNew() {
        return new PostCommentRequest();
    }
}
