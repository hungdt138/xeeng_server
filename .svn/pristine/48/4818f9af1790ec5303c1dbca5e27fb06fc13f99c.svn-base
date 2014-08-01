package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.XENewRequest;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.protocol.IRequestMessage;

public class XENewJSON extends XEMessageJSON {

    @Override
    public boolean decode(Object paramObject, IRequestMessage paramIRequestMessage) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) paramObject;
            XENewRequest req = (XENewRequest) paramIRequestMessage;
            String v = jsonData.getString("v");
            String[] values = v.split(AIOConstants.SEPERATOR_BYTE_1);
            req.zoneID = Integer.parseInt(values[0]);
            req.levelID = Integer.parseInt(values[1]);
            req.moneyBet = Long.parseLong(values[2]);
            req.maxPlayers = Integer.parseInt(values[3]);
            req.tableName = values[4];
            return true;
        } catch (JSONException e) {
            return false;
        }
    }
}
