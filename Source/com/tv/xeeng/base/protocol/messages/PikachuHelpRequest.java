package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class PikachuHelpRequest extends AbstractRequestMessage {
    public long mMatchId;
    public boolean isHelp; // false : revert table
    public IRequestMessage createNew() {
        return new PikachuHelpRequest();
    }
}
