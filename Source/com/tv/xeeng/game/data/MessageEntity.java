/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author tuandavrus
 */
public class MessageEntity implements Serializable{
    private List<Message> lstMessage;
    private int unReadMessage;
    private int numPage;
    
    public MessageEntity(List<Message> lstMessages)
    {
        this.lstMessage = lstMessages;
        numPage = lstMessages.size()/AIOConstants.PAGE_10_DEFAULT_SIZE + 1;
        unReadMessage = 0;
        int numMessage = lstMessages.size();
        
        for(int i = 0; i< numMessage; i++)
        {
            Message  m = lstMessages.get(i);
            if(!m.isRead)
                unReadMessage++;
        }
    }

    /**
     * @return the lstMessage
     */
    public List<Message> getLstMessage() {
        return lstMessage;
    }

    /**
     * @param lstMessage the lstMessage to set
     */
    public void setLstMessage(List<Message> lstMessage) {
        this.lstMessage = lstMessage;
    }

    /**
     * @return the unReadMessage
     */
    public int getUnReadMessage() {
        return unReadMessage;
    }

    /**
     * @param unReadMessage the unReadMessage to set
     */
    public void setUnReadMessage(int unReadMessage) {
        this.unReadMessage = unReadMessage;
    }

    /**
     * @return the numPage
     */
    public int getNumPage() {
        return numPage;
    }

    /**
     * @param numPage the numPage to set
     */
    public void setNumPage(int numPage) {
        this.numPage = numPage;
    }
  
}
