/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.tank.data;

import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.CancelResponse;
import com.tv.xeeng.base.protocol.messages.EndMatchResponse;
import com.tv.xeeng.databaseDriven.DBPoolConnection;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.db.DBException;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.protocol.MessageFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author tuanda
 */
public class TankTable extends SimpleTable {

    private Room room;
    public ArrayList<TankPlayer> players = new ArrayList<TankPlayer>();
    private long winID;
    private int MAX_PLAYER = 4;

    public TankPlayer getOwner() {
        return (TankPlayer) this.owner;
    }

    @Override
    public Room getRoom() {
        return room;
    }

    public TankTable(TankPlayer owner, long money, long matchId, Room room) {
        this.matchID = matchId;
        this.owner = owner;
        this.players.add(owner);
        this.firstCashBet = money;
        owner.isGiveUp = false;  //owner doesnt bet
        this.room = room;
        this.winID = owner.id;
    }

    public void join(TankPlayer p) throws Exception {
        if (this.isPlaying) {
            throw new Exception("Phòng đang chơi rồi!");
        }
        if (this.players.size() >= MAX_PLAYER) {
            throw new Exception("Phòng đã đầy rồi!");
        } else {
            this.players.add(p);
        }
    }

    public void broadCast(Object obj) throws ServerException {
        for (TankPlayer player : this.players) {
            player.currentSession.write(obj);
        }
    }

    public void removePlayer(long uid) {
        for (int i = 0; i < players.size(); i++) {
            TankPlayer p = this.players.get(i);
            if (p.id == uid) {
                this.players.remove(i);
                break;
            }
        }
    }

    public void removePlayer(TankPlayer p) {
        for (int i = 0; i < players.size(); i++) {
            TankPlayer p1 = this.players.get(i);
            if (p1.id == p.id) {
                this.players.remove(i);
                break;
            }
        }
    }
    public int tableType = 0;

    public void start() {
        this.isPlaying = true;
        tableType = (int) Math.round(Math.random() * 15) + 1;
    }

    private long numberStillAlive() {
        int index = 0;
        long res = 0;
        for (TankPlayer p : this.players) {
            if (!p.isStop) {
                res = p.id;
                index++;
            }
        }
        if (index == 1) {
            return res;
        } else {
            return 0;
        }
    }

    public void lose(long uid) throws ServerException, DBException, JSONException {
        TankPlayer p = getPlayer(uid);
        if (p != null) {
            p.isStop = true;
            p.isWin = false;
            if (numberStillAlive() > 0) {
                TankPlayer p1 = getPlayer(numberStillAlive());
                p1.isWin = true;
                endMatch();
            }
        }
    }

    public void win(long uid) throws ServerException, DBException, JSONException {
        for (TankPlayer p : this.players) {
            if (p.id == uid) {
                p.isWin = true;
            } else {
                p.isWin = false;
            }
        }
        endMatch();
    }

    public void playerEnd(long uid, int point) {
        TankPlayer p = getPlayer(uid);
        if (p != null) {
            p.point = point;
        }
    }

    public void end() throws ServerException, DBException, JSONException {
        long winID = 0;
        long tPoint = -1;
        for (TankPlayer p : this.players) {
            if (tPoint < p.point) {
                tPoint = p.point;
                winID = p.id;
            }
        }
        for (TankPlayer p : this.players) {
            if (p.id == winID) {
                p.isWin = true;
            } else {
                p.isWin = false;
            }
        }
        endMatch();
    }

    public TankPlayer getPlayer(long uid) {
        for (TankPlayer p : this.players) {
            if (uid == p.id) {
                return p;
            }
        }
        return null;
    }

    private void removeAllNotEnoughMoney() {
        ArrayList<Long> res = new ArrayList<Long>();
        for (TankPlayer p : this.players) {
            if (p.cash < this.firstCashBet * 4) {
                res.add(p.id);
            }
        }
        for (long uid : res) {
            removePlayer(uid);
        }
    }

    public void reset() {
        this.isPlaying = false;
        removeAllNotEnoughMoney();
        for (TankPlayer p : this.players) {
            p.reset();
        }
    }

    //DB
    public void updateCash() {

        Connection con = DBPoolConnection.getConnection();
        try {

            String desc = "Line:" + matchID;
            UserDB userDb = new UserDB(con);
//            long ownerWonMoney = 0;

            boolean havingMinusBalance = false;
            for (TankPlayer player : this.players) {
                if (player.isWin) {
                    havingMinusBalance = updateUserCash(userDb, player, true, this.firstCashBet, desc);
                } else {
                    havingMinusBalance = updateUserCash(userDb, player, false, this.firstCashBet, desc);
                }
            }
            if (havingMinusBalance) {
                userDb.notMinus();
            }
        } catch (DBException ex) {
            outCodeSB.append(ex.getMessage()).append(ex.getStackTrace()).append(NEW_LINE);
        } catch (SQLException ex) {
            outCodeSB.append(ex.getMessage()).append(ex.getStackTrace()).append(NEW_LINE);
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                outCodeSB.append(ex.getStackTrace()).append(NEW_LINE);
            }
        }

        saveLogToFile();
    }

    private JSONObject getEndJSonObject(long newOwnerId) throws JSONException {
        JSONObject encodingObj = new JSONObject();
        encodingObj.put("mid", MessagesID.MATCH_END);
        encodingObj.put("code", ResponseCode.SUCCESS);
        if (newOwnerId > 0) {
            encodingObj.put("newOwner", newOwnerId);
        }
        JSONArray playersArr = new JSONArray();
        for (TankPlayer player : this.players) {
            JSONObject jO = new JSONObject();
            jO.put("uid", player.id);
            jO.put("wonMOney", player.getWonMoney());
            jO.put("cash", player.cash);
            jO.put("isOut", player.isOut);
            playersArr.put(jO);
        }
        JSONObject jO1 = new JSONObject();
        jO1.put("uid", getOwner().id);
        jO1.put("wonMOney", getOwner().getWonMoney());
        jO1.put("cash", getOwner().cash);
        jO1.put("isOut", getOwner().isOut);
        playersArr.put(jO1);
        encodingObj.put("Players", playersArr);

        return encodingObj;
    }

    public long endMatch() throws ServerException, JSONException {

        long newOwnerId = 0;
        this.isPlaying = false;

        //TODO: Tính tiền
        updateCash();

        //send end message to client
        MessageFactory msgFactory = owner.currentSession.getMessageFactory();
        if (owner.isOut) {
            TankPlayer newOwner = this.ownerQuit();
            if (newOwner != null) {
                newOwnerId = newOwner.id;
            }
        }
        JSONObject endJson = getEndJSonObject(newOwnerId);
        EndMatchResponse endMatchRes = (EndMatchResponse) msgFactory.getResponseMessage(MessagesID.MATCH_END);
        // set the result
        endMatchRes.setZoneID(this.getRoom().getZoneID());
        endMatchRes.setSuccess(endJson);
        broadCast(endMatchRes);

        this.reset();
        return newOwnerId;
    }

    public TankPlayer ownerQuit() {
        if (getPlayer(winID) != null) {
            return getPlayer(winID);
        } else {
            return this.players.get(0);
        }
    }

    public CancelResponse cancel(long uid) throws Exception, ServerException, JSONException {
        TankPlayer p = getPlayer(uid);
        if (p == null) {
            throw new Exception(NONE_EXISTS_PLAYER);
        } else {
        }
        CancelResponse resMatchCancel;
        MessageFactory msgFactory = p.currentSession.getMessageFactory();
        resMatchCancel = (CancelResponse) msgFactory.getResponseMessage(MessagesID.MATCH_CANCEL);
        resMatchCancel.setSuccess(ResponseCode.SUCCESS, uid);
        p.isOut = true;
        if (this.owner == null) {
            return resMatchCancel;
        }
        long newOwnerId = 0;
        if (this.isPlaying) {
            p.isWin = false;
            if (this.players.size() == 2) {
                this.endMatch();
            }
        } else {
            if (uid == owner.id) {
                TankPlayer newOwner = this.ownerQuit();
                if (newOwner != null) {
                    newOwnerId = newOwner.id;
                    this.setOwnerId(newOwner.id);
                } else {
                    return resMatchCancel;
                }

            } else {
                removePlayer(uid);
            }
        }
        resMatchCancel.newOwner = newOwnerId;
        return resMatchCancel;
    }
}
