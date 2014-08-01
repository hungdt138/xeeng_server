/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.JoinRequest;
import com.tv.xeeng.base.protocol.messages.JoinResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimplePlayer;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.lieng.data.LiengPlayer;
import com.tv.xeeng.game.phom.data.PhomPlayer;
import com.tv.xeeng.game.pikachu.datta.PikachuPlayer;
import com.tv.xeeng.game.tienlen.data.TienLenPlayer;
import com.tv.xeeng.game.xam.data.SamPlayer;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

/**
 *
 * @author tuanda
 */
public class JoinJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(JoinJSON.class);

    @Override
    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            // request data
            JSONObject jsonData = (JSONObject) aEncodedObj;
            // request messsage
            JoinRequest matchJoin = (JoinRequest) aDecodingObj;

            String[] arrV = jsonData.getString("v").split(AIOConstants.SEPERATOR_BYTE_1);
            matchJoin.mMatchId = Long.parseLong(arrV[0]);
            //jsonData.getLong("v");
            if (arrV.length > 1) {
                matchJoin.matrixSize = Integer.parseInt(arrV[1]);
            }

            return true;

        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    private void newProtocol(JoinResponse matchJoin, JSONObject encodingObj) throws JSONException {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(MessagesID.MATCH_JOIN)).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(Integer.toString(matchJoin.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
        if (matchJoin.mCode == ResponseCode.FAILURE) {
            sb.append(matchJoin.mErrorMsg);
        } else {
            sb.append(matchJoin.mMatchId).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(matchJoin.minBet).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(matchJoin.isPlaying ? "1" : "0");// .append(AIOConstants.SEPERATOR_BYTE_1);
		/*
             * sb.append(matchJoin.roomOwner.id).append(
             * AIOConstants.SEPERATOR_BYTE_1);
             */
            switch (matchJoin.zoneID) {
                case ZoneID.PHOM: {
                    sb.append(AIOConstants.SEPERATOR_BYTE_1).append(matchJoin.isAn ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(matchJoin.isTaiGui ? "1" : "0");// .append(AIOConstants.SEPERATOR_BYTE_1);
                    if (matchJoin.isObserve) {
                        sb.append(AIOConstants.SEPERATOR_BYTE_1).append(matchJoin.duty).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(matchJoin.turn).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(matchJoin.deck).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(matchJoin.currCard);
                    }
                    sb.append(AIOConstants.SEPERATOR_BYTE_3);
                    sb.append(phomData(matchJoin, matchJoin.mPlayerPhom, false));// .append(AIOConstants.SEPERATOR_BYTE_2);
                    if (!matchJoin.mWaitingPlayerPhom.isEmpty()) {
                        sb.append(AIOConstants.SEPERATOR_BYTE_2).append(phomData(matchJoin, matchJoin.mWaitingPlayerPhom, true));// .append(AIOConstants.SEPERATOR_BYTE_1);
                    }

                    break;
                }
                case ZoneID.TIENLEN: {
                    sb.append(AIOConstants.SEPERATOR_BYTE_1).append(matchJoin.isHidePoker ? "1" : "0");// .append(AIOConstants.SEPERATOR_BYTE_1);

                    if (matchJoin.isObserve) {
                        sb.append(AIOConstants.SEPERATOR_BYTE_1).append(matchJoin.duty).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(matchJoin.turn).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(matchJoin.cards);// .append(AIOConstants.SEPERATOR_BYTE_1);
                    }
                    sb.append(AIOConstants.SEPERATOR_BYTE_3);
                    sb.append(tienLenData(matchJoin, matchJoin.mTienLenPlayer, false));// .append(AIOConstants.SEPERATOR_BYTE_1);
                    if (!matchJoin.mWaitingPlayerTienlen.isEmpty()) {
                        sb.append(AIOConstants.SEPERATOR_BYTE_2).append(tienLenData(matchJoin, matchJoin.mWaitingPlayerTienlen, true));// .append(AIOConstants.SEPERATOR_BYTE_1);
                    }
                    break;
                }
                case ZoneID.SAM: {
                    sb.append(AIOConstants.SEPERATOR_BYTE_1).append("0");
                    if (matchJoin.isObserve) {
                        sb.append(AIOConstants.SEPERATOR_BYTE_1).append("0").append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(matchJoin.turn).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(matchJoin.cards);// .append(AIOConstants.SEPERATOR_BYTE_1);
                    }
                    sb.append(AIOConstants.SEPERATOR_BYTE_3);
                    sb.append(samData(matchJoin, matchJoin.mPlayerSam, false));
                    if (!matchJoin.mWaitingPlayerSam.isEmpty()) {
                        sb.append(AIOConstants.SEPERATOR_BYTE_2).append(samData(matchJoin, matchJoin.mWaitingPlayerSam, true));
                    }
                    break;
                }

                case ZoneID.PIKACHU:
                case ZoneID.NEW_BA_CAY:
                case ZoneID.BAU_CUA_TOM_CA:
                case ZoneID.AILATRIEUPHU:

                default: {
                    sb.append(AIOConstants.SEPERATOR_BYTE_3);
                    sb.append(gameData(matchJoin, matchJoin.playing, false));
                    if (!matchJoin.waiting.isEmpty()) {
                        if (!matchJoin.playing.isEmpty()) {
                            sb.append(AIOConstants.SEPERATOR_BYTE_2);
                        }
                        sb.append(gameData(matchJoin, matchJoin.waiting, true));
                    }

                    break;
                }
            }

            sb.append(AIOConstants.SEPERATOR_BYTE_3).append(matchJoin.phongID);
            if (matchJoin.zoneID == ZoneID.NEW_BA_CAY) {
                sb.append(AIOConstants.SEPERATOR_BYTE_3);
                sb.append(Long.toString(matchJoin.roomOwner.id));
            }
        }

        encodingObj.put("v", sb.toString());
        // return encodingObj;
    }

    private StringBuilder samData(JoinResponse matchJoin, List<? extends SimplePlayer> players, boolean isObserver) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < players.size(); i++) {
            SamPlayer player = (SamPlayer) players.get(i);
            sb.append(player.id).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.username).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.avatarID).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.cash).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.isReady ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(isObserver ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);//
            sb.append(player.isCai ? "1" : "0");
            if (!isObserver) {
                sb.append(AIOConstants.SEPERATOR_BYTE_1).append(player.playingCardSize());
            }
            if (i < players.size() - 1) {
                sb.append(AIOConstants.SEPERATOR_BYTE_2);
            }
        }
        
        return sb;
    }

    private StringBuilder tienLenData(JoinResponse matchJoin, ArrayList<TienLenPlayer> players, boolean isObserver) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < players.size(); i++) {
            TienLenPlayer player = players.get(i);
            if (player != null) {
                sb.append(player.id).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(player.username).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(player.avatarID).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(player.cash).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(player.isReady ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(isObserver ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);//
                sb.append(Integer.toString(player.level));

                if (!isObserver) {
                    sb.append(AIOConstants.SEPERATOR_BYTE_1).append(player.numHand);
                }
                if (i < players.size() - 1) {
                    sb.append(AIOConstants.SEPERATOR_BYTE_2);
                }
            }
        }
        return sb;
    }

    private StringBuilder phomData(JoinResponse matchJoin, ArrayList<PhomPlayer> players, boolean isObserver) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < players.size(); i++) {
            PhomPlayer player = players.get(i);
            if (player != null) {
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

                if (i < players.size() - 1) {
                    sb.append(AIOConstants.SEPERATOR_BYTE_2);
                }
            }
        }
        return sb;
    }

    private StringBuilder gameData(JoinResponse matchJoin, List<? extends SimplePlayer> players, boolean isObserver) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < players.size(); i++) {
            SimplePlayer player = players.get(i);
//			if (isObserver) {
//
//				System.out.println("waiting: " + player.username);
//			} else {
//				System.out.println("playing: " + player.username);
//			}
            if (player != null) {
                sb.append(player.id).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(player.username).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(player.avatarID).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(player.cash).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(player.isReady ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(isObserver ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(player.level));

                if (matchJoin.zoneID == ZoneID.PIKACHU) {
                    sb.append(AIOConstants.SEPERATOR_BYTE_1).append(((PikachuPlayer) player).point);// .append(AIOConstants.SEPERATOR_BYTE_1);
                } else if (matchJoin.zoneID == ZoneID.LIENG && matchJoin.isPlaying) {
                    sb.append(AIOConstants.SEPERATOR_BYTE_1).append(Integer.toString(((LiengPlayer) player).getTotalRound()));
                }

                //if (i < players.size() - 1)
                sb.append(AIOConstants.SEPERATOR_BYTE_2);
            }
        }
        
        if (players.size() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        
        return sb;
    }

    @Override
    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            JoinResponse matchJoin = (JoinResponse) aResponseMessage;
            newProtocol(matchJoin, encodingObj);
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
