package com.tv.xeeng.base.protocol.messages.json;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.ReconnectRequest;
import com.tv.xeeng.base.protocol.messages.ReconnectResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimplePlayer;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.phom.data.PhomPlayer;
import com.tv.xeeng.game.tienlen.data.TienLenPlayer;
import com.tv.xeeng.game.xam.data.SamPlayer;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;
import java.util.List;

public class ReconnectJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(ReconnectJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            ReconnectRequest reconnectRq = (ReconnectRequest) aDecodingObj;
            // reconnectRq.username = jsonData.getString("u");
            if (jsonData.has("v")) {
                try {
                    String v = jsonData.getString("v");
                    String[] arrMxh = v.split(AIOConstants.SEPERATOR_BYTE_3);
                    if (arrMxh.length > 1) {
                        String[] arrRecon = arrMxh[1].split(AIOConstants.SEPERATOR_BYTE_1);
                        reconnectRq.isMxh = arrRecon[0].equals("1");
                        if (arrRecon.length > 1) {
                            reconnectRq.protocol = Integer.parseInt(arrRecon[1]);
                        }
                    }

                    String[] arr = arrMxh[0].split(AIOConstants.SEPERATOR_BYTE_1);
                    reconnectRq.username = arr[1];
                    int type = Integer.parseInt(arr[0]);
                    reconnectRq.uid = Integer.parseInt(arr[2]);

                    reconnectRq.type = type;

                    switch (type) {
                    case 1:
                    case 2:
                    case 3:
                        // reconnectRq.username = arrValues[1];
                        reconnectRq.pass = arr[3];
                        if (type == 2 || type == 3) {
                            reconnectRq.zone = Integer.parseInt(arr[4]);
                            if (type == 3) {
                                reconnectRq.phong = Integer.parseInt(arr[5]);
                            }
                        }
                        break;
                    case 4:
                    case 5:
                        // reconnectRq.uid = Long.parseLong(arr[2]);
                        reconnectRq.matchId = Long.parseLong(arr[3]);
                        if (type == 4) {
                            reconnectRq.zone = Integer.parseInt(arr[4]);
                            reconnectRq.phong = Integer.parseInt(arr[5]);
                        }
                        if (type == 5)
                            reconnectRq.tourID = Integer.parseInt(arr[4]);
                        break;
                    default:
                        break;
                    }
                } catch (Exception ex) {
                    mLog.error(ex.getMessage(), ex);
                }
            }
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    private String newProtocol(ReconnectResponse matchJoin) throws JSONException {
        StringBuilder sb = new StringBuilder();
        sb.append(matchJoin.minBet).append(AIOConstants.SEPERATOR_BYTE_1);
        switch (matchJoin.zoneID) {
        case ZoneID.PHOM: {
            sb.append(matchJoin.isPlaying ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(matchJoin.isAn ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(matchJoin.isTaiGui ? "1" : "0");// .append(AIOConstants.SEPERATOR_BYTE_1);
            // if(matchJoin.isObserve){
            /*
             * sb.append(AIOConstants.SEPERATOR_BYTE_1) .append(matchJoin.duty)
             * .append(AIOConstants.SEPERATOR_BYTE_1);
             */
            sb.append(AIOConstants.SEPERATOR_BYTE_1).append(matchJoin.turn).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(matchJoin.deck).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(matchJoin.cards).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(matchJoin.currCard).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(matchJoin.mMatchId);
            // }
            sb.append(AIOConstants.SEPERATOR_BYTE_3);
            sb.append(phomData(matchJoin, matchJoin.mPlayerPhom, false));// .append(AIOConstants.SEPERATOR_BYTE_2);
            if (!matchJoin.mWaitingPlayerPhom.isEmpty()) {
                sb.append(AIOConstants.SEPERATOR_BYTE_2).append(phomData(matchJoin, matchJoin.mWaitingPlayerPhom, true));// .append(AIOConstants.SEPERATOR_BYTE_1);
            }

            break;
        }
        
        case ZoneID.TIENLEN: {
            sb.append(matchJoin.isPlaying ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(matchJoin.isHidePoker ? "1" : "0");// .append(AIOConstants.SEPERATOR_BYTE_1);

            // if(matchJoin.isObserve){
            sb.append(AIOConstants.SEPERATOR_BYTE_1).append(matchJoin.duty).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(matchJoin.turn).append(AIOConstants.SEPERATOR_BYTE_1);
            if (matchJoin.cards.compareTo("") != 0)
                sb.append(matchJoin.cards).append(AIOConstants.SEPERATOR_BYTE_1);
            else
                sb.append(0).append(AIOConstants.SEPERATOR_BYTE_1);

            sb.append(matchJoin.myHandCards).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(matchJoin.mMatchId).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(matchJoin.currUserHasDuty);
            // }
            sb.append(AIOConstants.SEPERATOR_BYTE_3);
            sb.append(tienLenData(matchJoin, matchJoin.mTienLenPlayer, false));// .append(AIOConstants.SEPERATOR_BYTE_1);
            if (!matchJoin.mWaitingPlayerTienlen.isEmpty())
                sb.append(AIOConstants.SEPERATOR_BYTE_2).append(tienLenData(matchJoin, matchJoin.mWaitingPlayerTienlen, true));// .append(AIOConstants.SEPERATOR_BYTE_1);

            break;
        }

        case ZoneID.SAM: {
            sb.append(matchJoin.isPlaying ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(matchJoin.isHidePoker ? "1" : "0");// .append(AIOConstants.SEPERATOR_BYTE_1);

            // if(matchJoin.isObserve){
            sb.append(AIOConstants.SEPERATOR_BYTE_1).append(matchJoin.duty).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(matchJoin.turn).append(AIOConstants.SEPERATOR_BYTE_1);
            if (matchJoin.cards.compareTo("") != 0)
                sb.append(matchJoin.cards).append(AIOConstants.SEPERATOR_BYTE_1);
            else
                sb.append(0).append(AIOConstants.SEPERATOR_BYTE_1);

            sb.append(matchJoin.myHandCards).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(matchJoin.mMatchId).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(matchJoin.currUserHasDuty);
            // }
            sb.append(AIOConstants.SEPERATOR_BYTE_3);
            sb.append(samData(matchJoin, matchJoin.mPlayerSam, false));// .append(AIOConstants.SEPERATOR_BYTE_1);
            if (!matchJoin.mWaitingPlayerSam.isEmpty())
                sb.append(AIOConstants.SEPERATOR_BYTE_2).append(samData(matchJoin, matchJoin.mWaitingPlayerSam, true));// .append(AIOConstants.SEPERATOR_BYTE_1);

            break;
        }

        default: {
            break;
        }
        }
        return sb.toString();
    }

    private StringBuilder samData(ReconnectResponse matchJoin, List<? extends SimplePlayer> players, boolean isObserver) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < players.size(); i++) {
            SamPlayer player = (SamPlayer) players.get(i);
            sb.append(player.id).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.username).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.avatarID).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.cash).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.isReady ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(isObserver ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(player.level));
            if (!isObserver) {
                sb.append(AIOConstants.SEPERATOR_BYTE_1).append(player.playingCardSize());
            }
            if (i < players.size() - 1)
                sb.append(AIOConstants.SEPERATOR_BYTE_2);
        }
        return sb;
    }

    private StringBuilder tienLenData(ReconnectResponse matchJoin, List<? extends SimplePlayer> players, boolean isObserver) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < players.size(); i++) {
            TienLenPlayer player = (TienLenPlayer) players.get(i);
            sb.append(player.id).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.username).append(AIOConstants.SEPERATOR_BYTE_1);

            sb.append(player.avatarID).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.cash).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.isReady ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(isObserver ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(player.level));
            if (!isObserver) {
                sb.append(AIOConstants.SEPERATOR_BYTE_1).append(player.numHand);
            }
            if (i < players.size() - 1)
                sb.append(AIOConstants.SEPERATOR_BYTE_2);
        }
        return sb;
    }

    private StringBuilder phomData(ReconnectResponse matchJoin, ArrayList<PhomPlayer> players, boolean isObserver) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < players.size(); i++) {
            PhomPlayer player = players.get(i);
            sb.append(player.id).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.username).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.avatarID).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.cash).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.isReady ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);

            sb.append(isObserver ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(player.level));
            if (!isObserver) {
                sb.append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(player.cardToString(player.playingCards)).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(player.cardToString(player.eatingCards));// .append(AIOConstants.SEPERATOR_BYTE_1);
                if (player.haPhom) {
                    sb.append(AIOConstants.SEPERATOR_BYTE_1).append(player.cardToString(player.phoms));
                } else {
                    sb.append(AIOConstants.SEPERATOR_BYTE_1).append(0);
                }
            }

            if (i < players.size() - 1)
                sb.append(AIOConstants.SEPERATOR_BYTE_2);
        }
        return sb;
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            ReconnectResponse reconnectRes = (ReconnectResponse) aResponseMessage;
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(reconnectRes.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
            if (reconnectRes.mCode == ResponseCode.FAILURE) {
                sb.append(reconnectRes.mErrorMsg);
            } else {
                if (reconnectRes.isNeeded) {
                    sb.append(newProtocol(reconnectRes));
                }
            }
            encodingObj.put("v", sb.toString());
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
