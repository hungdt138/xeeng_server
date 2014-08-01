/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.line.data;

import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.CancelResponse;
import com.tv.xeeng.base.protocol.messages.EndMatchResponse;
import com.tv.xeeng.databaseDriven.DBPoolConnection;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.db.DBException;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimplePlayer;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.protocol.MessageFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author tuanda
 */
public class LineTable extends SimpleTable {

	public Room room;
	// private LinePlayer player;
	public ArrayList<LinePlayer> playings = new ArrayList<LinePlayer>();
	// private long currID;
	public long winID;
	private int MAX_NUMBER_PLAYER = 2;

	public long getWinID() {
		return winID;
	}

	public void setWinID(long winID) {
		this.winID = winID;
	}

	public ArrayList<LinePlayer> getPlayings() {
		return playings;
	}

	@Override
	public List<? extends SimplePlayer> getNewPlayings() {
		return playings;
	}

	public LinePlayer getOwner() {
		return (LinePlayer) owner;
	}

	@Override
	public Room getRoom() {
		return room;
	}

	public boolean containPlayer(long id) {
		for (LinePlayer p : this.playings) {
			if (p.id == id) {
				return true;
			}
		}

		return false;
	}

	public LineTable(LinePlayer owner, long money, long matchId, Room room) {
		this.matchID = matchId;
		this.owner = owner;
		this.playings.add(owner);
		this.firstCashBet = money;
		owner.isGiveUp = false; // owner doesnt bet
		this.room = room;
		this.winID = owner.id;
	}

	public void join(LinePlayer p) throws Exception {
		// if (this.isPlaying) {
		// throw new Exception("Bàn đang chơi rồi!.");
		// }

		if (this.playings.size() >= this.MAX_NUMBER_PLAYER) {
			throw new Exception("Phòng đã đầy!");
		} else {
			this.playings.add(p);
		}
		//
	}

	public void removePlayer(long uid) {
		for (int i = 0; i < this.playings.size(); i++) {
			LinePlayer p = this.playings.get(i);
			if (p.id == uid) {
				this.playings.remove(p);
				return;
			}
		}
	}

	public void broadCast(Object obj) throws ServerException {
		for (LinePlayer p : this.playings) {
			p.currentSession.write(obj);
		}
	}

	public void start() {
		isPlaying = true;
		setLastActivated(System.currentTimeMillis());
		setCurrentTimeOut(time);
	}

	public CancelResponse cancel(long uid) throws Exception, ServerException,
			JSONException {
		LinePlayer p = getPlayer(uid);
		if (p == null) {
			throw new Exception(NONE_EXISTS_PLAYER);
		} else {
		}
		CancelResponse resMatchCancel;
		MessageFactory msgFactory = p.currentSession.getMessageFactory();
		resMatchCancel = (CancelResponse) msgFactory
				.getResponseMessage(MessagesID.MATCH_CANCEL);
		resMatchCancel.setSuccess(ResponseCode.SUCCESS, uid);
		p.isOut = true;
		if (this.owner == null) {
			return resMatchCancel;
		}
		long newOwnerId = 0;
		if (this.isPlaying) {
			p.isWin = false;
			this.stop(uid, true);
			newOwnerId = this.owner.id;
			// newOwnerId = this.endMatch(uid, 0);
		} else {
			if (uid == owner.id) {
				LinePlayer newOwner = this.ownerQuit();
				removePlayer(uid);
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

	public LinePlayer getPlayer(long uid) {
		for (LinePlayer p : this.playings) {
			if (p.id == uid) {
				return p;
			}
		}
		return null;
	}

	@Override
	public int getTableSize() {
		return this.playings.size();
	}

	@Override
	public boolean isFullTable() {
		if (getTableSize() >= MAX_NUMBER_PLAYER) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected List<SimplePlayer> removeNotEnoughMoney() {
		List<SimplePlayer> removedPlayers = new ArrayList<SimplePlayer>();
		boolean isChangeOwner = false;
		for (LinePlayer p : this.playings) {
			p.moneyForBet = this.firstCashBet;
			if (p != null && p.notEnoughMoney()) {
				if (p.id == this.owner.id) {
					isChangeOwner = true;
				}
				removedPlayers.add(p);
			}
		}

		if (isChangeOwner) {
			LinePlayer player = ownerQuit();
			if (player != null) {
				this.owner = player;
				ownerSession = owner.currentSession;
			} else {
				return removedPlayers;
			}
		}
		this.setOwnerId(owner.id);

		return removedPlayers;
	}

	// To avoid number of do the same function

	public JSONObject getEndJSonObject(long newOwnerId) throws JSONException {
		JSONObject encodingObj = new JSONObject();
		encodingObj.put("mid", MessagesID.MATCH_END);
		encodingObj.put("code", ResponseCode.SUCCESS);
		if (newOwnerId > 0) {
			encodingObj.put("newOwner", newOwnerId);
		}

		JSONArray playersArr = new JSONArray();
		for (LinePlayer player : this.playings) {
			if (player != null) {
				JSONObject jO = new JSONObject();
				jO.put("uid", player.id);
				jO.put("wonMOney", player.getWonMoney());
				jO.put("cash", player.cash);
				jO.put("isOut", player.isOut);
				playersArr.put(jO);
			}
		}
		encodingObj.put("Players", playersArr);

		return encodingObj;
	}

	public LinePlayer ownerQuit() {

		if (this.playings.isEmpty()) {
			return null;
		} else {
			for (int i = 0; i < this.playings.size(); i++) {
				LinePlayer player = this.playings.get(i);
				if (!player.notEnoughMoney() && !player.isOut
						&& player.id != this.owner.id) {
					owner = player;
					ownerSession = owner.currentSession;
					return (LinePlayer) owner;
				}
			}
			return null;
		}

	}

	public long getOtherPlayer(long uid) {
		for (LinePlayer p : playings) {
			if (p.id != uid)
				return p.id;
		}
		return uid;
	}

	private void isStop(long uid, boolean isOut) throws ServerException,
			JSONException {
		int index = 0;
		long temp = 0;
		for (LinePlayer p : this.playings) {
			if (!p.isStop) {
				index++;
				temp = p.id;
			}
		}
		if (index == 1) {
			endMatch(temp, 0);
			if (isOut) {
				removePlayer(uid);
			}
		}
	}

	public void remove(LinePlayer player) {
		synchronized (this.playings) {
			for (int i = 0; i < this.playings.size(); i++) {
				LinePlayer removePlayer = this.playings.get(i);
				if (removePlayer.id == player.id) {
					this.playings.remove(removePlayer);
					break;
				}
			}
		}
	}

	@Override
	public void removePlayer(SimplePlayer player) {
		this.remove((LinePlayer) player);
	}

	@Override
	public LinePlayer findPlayer(long uid) {
		return getPlayer(uid);
	}

	public void stop(long uid, boolean isOut) throws ServerException,
			JSONException {
		getPlayer(uid).isStop = true;
		isStop(uid, isOut);
	}

	// Win ID

	public long endMatch(long uid, int number) throws ServerException,
			JSONException {
		this.winID = uid;
		long newOwnerId = 0;
		this.isPlaying = false;
		for (LinePlayer p : this.playings) {
			if (p.id == uid) {
				p.isWin = true;
			} else {
				p.isWin = false;
			}
		}

		int mul = 1;
		if (number == 9) {
			mul = 2;
		} else if (number == 10) {
			mul = 5;
		} else if (number == 11) {
			mul = 10;
		} else if (number == 12) {
			mul = 15;
		} else if (number == 13) {
			mul = 25;
		} else if (number == 14) {
			mul = 50;
		} else if (number == 15) {
			mul = 100;
		}

		updateCash(mul);
		// send end message to client
		MessageFactory msgFactory = owner.currentSession.getMessageFactory();
		if (owner.isOut) {
			LinePlayer newOwner = this.ownerQuit();
			if (newOwner != null) {
				newOwnerId = newOwner.id;
			}
		}
		JSONObject endJson = getEndJSonObject(newOwnerId);
		EndMatchResponse endMatchRes = (EndMatchResponse) msgFactory
				.getResponseMessage(MessagesID.MATCH_END);
		// set the result
		endMatchRes.setZoneID(this.room.getZoneID());
		endMatchRes.setSuccess(endJson);
		broadcastMsg(endMatchRes, getNewPlayings(),
				new ArrayList<SimplePlayer>(), owner, true);
		/*
		 * for (LinePlayer p : this.playings) { if (!p.isOut &&
		 * (p.currentSession != null)) { p.currentSession.write(endMatchRes); }
		 * }
		 */
		this.removeNotEnoughMoney(this.room);
		this.resetPlayers();
		return newOwnerId;
	}

	public void resetPlayers() {
		for (LinePlayer p : this.playings) {
			p.reset();
		}

		owner.isReady = true;
	}

	// DB

	public void updateCash(int mult) {

		Connection con = DBPoolConnection.getConnection();
		try {

			String desc = "Line:" + matchID;
			UserDB userDb = new UserDB(con);
			// long ownerWonMoney = 0;

			boolean havingMinusBalance = false;
			LinePlayer winPlayer = getPlayer(winID);
			for (LinePlayer player : this.playings) {
				if (!player.isWin) {
					havingMinusBalance = updateUserCash(userDb, winPlayer,
							player, this.firstCashBet * mult, desc);
				}
			}

			if (havingMinusBalance) {
				userDb.notMinus();
			}
		} catch (DBException ex) {
			outCodeSB.append(ex.getMessage()).append(ex.getStackTrace())
					.append(NEW_LINE);
		} catch (SQLException ex) {
			outCodeSB.append(ex.getMessage()).append(ex.getStackTrace())
					.append(NEW_LINE);
		} finally {
			try {
				con.close();
			} catch (SQLException ex) {
				outCodeSB.append(ex.getStackTrace()).append(NEW_LINE);
			}
		}

		saveLogToFile();
	}
	private int time = 180000;
	public int getTime() {
		return time;
	}
	private int maxPoint = 300;
	public int getMaxPoint() {
		return maxPoint;
	}
	public int setPoint(int num, long uid) throws JSONException, ServerException{
		LinePlayer p = findPlayer(uid);
		p.setPoint(num);
		if(p.point >= maxPoint){
			endMatch(p.id, num);
			return -1;
		}
		return p.point;
	}
	@Override
	public void doTimeout() {
		outCodeSB.append("do timeout").append(NEW_LINE);
		mLog.debug("Line cua do timeout");
		lastActivated = System.currentTimeMillis();
		if (isPlaying) {
			int point = -1;
			for (LinePlayer p : playings) {
				if (p.point > point) {
					point = p.point;
					winID = p.id;
				}
			}
			try {
				endMatch(winID, 1);
			} catch (Throwable e) {
				mLog.error(e.getMessage());
			}
		}
	}
}
