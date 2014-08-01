/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.lieng.data;

import com.tv.xeeng.base.business.BusinessException;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.*;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DBPoolConnection;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.db.DBException;
import com.tv.xeeng.game.data.*;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.protocol.MessageFactory;
import org.json.JSONException;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author tuanda
 */
public class LiengTable extends SimpleTable {
	// private XocDiaPlayer owner;
	private List<LiengPlayer> playings = new ArrayList<LiengPlayer>();
	private List<LiengPlayer> waitings = new ArrayList<LiengPlayer>();

	private static final int UP_BO = -1;

	// private static final int BET_TURN = 20000;
	private static final int AUTO_LAT_BAI = 23000;
	// private static final int TIME_SLEEP_TO_GET_POKER = 1000;

	private long totalCall = 0;

	private boolean isChiaBai = false;
	private boolean isFirstTo = true;

	private boolean isHidePoker = false;

	private static final int LIMIT_PLAYER = 4;
	private final String FULL_PLAYER_MSG = "Phòng đã đầy";
	@SuppressWarnings("unused")
	private static final String BET_AFTER_DELIVER_POKER = "Bạn không được phép đặt cược khi bài đã chia";
	@SuppressWarnings("unused")
	private static final String TO_INVALID_RULE = "Bạn tố không đúng luật";
	private static final String CANT_CALL_ANYMORE = "Bạn không có quyền tố nữa";
	private static final int XI_TO_LOG_TYPE = 10007;

	private int currIndex;

	private long beginUid;
	private List<Integer> cards;

	private int DO_TIME_OUT_TYPE = 0;

	private LiengPlayer winner;
	private long roundBet = 0;
	// private int

	public static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(LiengTable.class);

	public LiengTable(LiengPlayer owner, long money) {
		this.owner = owner;
		this.playings.add(owner);
		this.firstCashBet = money;
		owner.isGiveUp = false; // owner doesnt bet

		logdir = "lieng";
	}

	@Override
	public boolean isFullTable() {
		return getTableSize() >= getMaximumPlayer(); // Maximum player = 4									// player
	}

	@Override
	public int getTableSize() {
		return playings.size() + waitings.size();
	}

	@Override
	public List<? extends SimplePlayer> getNewPlayings() {
		return playings;
	}

	@Override
	public List<? extends SimplePlayer> getNewWaitings() {
		return waitings;
	}

	@Override
	protected void join(SimplePlayer player) throws BusinessException {
		if (isFullTable()) {
			throw new BusinessException(FULL_PLAYER_MSG);
		}

		player.setLastActivated(System.currentTimeMillis());
		
		if (isPlaying) {

			waitings.add((LiengPlayer) player);
			player.isMonitor = true;

		} else {

			playings.add((LiengPlayer) player);
			player.isMonitor = false;

		}

		outCodeSB.append("player: ").append(player.username).append(" join")
				.append(NEW_LINE);
	}

	public void kickout(long userKickoutid, KickOutRequest rqKickOut)
			throws BusinessException {
		if (userKickoutid != owner.id) {
			throw new BusinessException(Messages.NOT_OWNER_PERSON);
		}

		LiengPlayer player = findPlayer(rqKickOut.uid);
		if (player == null) {
			throw new BusinessException(Messages.PLAYER_OUT);
		}

		if (this.isPlaying) {
			throw new BusinessException(Messages.PLAYING_TABLE);
		}

		player.currentSession.setLastFP(System.currentTimeMillis() - 20000); // for
																				// fast
																				// play

		MessageFactory msgFactory = getNotNullSession().getMessageFactory();
		OutResponse broadcastMsg = (OutResponse) msgFactory
				.getResponseMessage(MessagesID.OUT);
		broadcastMsg
				.setSuccess(ResponseCode.SUCCESS, rqKickOut.uid,
						player.username + " bị chủ bàn đá ra ngoài",
						player.username, 0);

		broadcastMsg(broadcastMsg, playings, waitings, player, true);
		remove(player);
		Room room = player.currentSession.leftRoom(matchID);

		if (room != null)
			room.left(player.currentSession);

		player.currentSession.setRoom(null);

		UserDB db = new UserDB();
		if (db.checkBotUser(rqKickOut.uid)) {
			this.isKickoutBot = true;
		}

	}

	public CancelResponse cancel(long uid) throws BusinessException,
			ServerException, JSONException {

		LiengPlayer player = findPlayer(uid);
		if (player == null) {
			throw new BusinessException(NONE_EXISTS_PLAYER);
		}

		outCodeSB.append("cancel player:").append(player.username)
				.append(NEW_LINE);

		CancelResponse resMatchCancel;
		MessageFactory msgFactory = getNotNullSession().getMessageFactory();
		resMatchCancel = (CancelResponse) msgFactory
				.getResponseMessage(MessagesID.MATCH_CANCEL);
		resMatchCancel.setSuccess(ResponseCode.SUCCESS, uid);

		player.isOut = true;

		if (this.onlinePlayers() == 0) {
			// player.currentSession.write(resMatchCancel);
			return resMatchCancel;
		}

		if (this.isPlaying) {
			if (player.isMonitor) {
				this.remove(player);
				broadcastMsg(resMatchCancel, playings, waitings, player, false);
			} else {

				if (this.onlinePlayers() == 1)// there are two players. One
												// player(not owner) leave
												// tables
				{
					player.isWin = false;
					player.setCurrentCall(UP_BO);
					player.setTypeTo(UP_BO);
					this.endMatch(player);

				} else {
					// check current player is out so we do timeout for this
					// player
					LiengPlayer currPlayer = this.playings.get(currIndex);
					if (player.id == currPlayer.id) {
						bet(UP_BO, player.id);
					}

				}

			}

		} else {
			this.remove(player);
			if (uid == owner.id) {
				LiengPlayer newOwner = this.ownerQuit();

				if (newOwner != null) {
					resMatchCancel.newOwner = newOwner.id;
					this.setOwnerId(newOwner.id);
				}
			}

			broadcastMsg(resMatchCancel, playings, waitings, player, false);

		}
		return resMatchCancel;
	}

	@Override
	public void removePlayer(SimplePlayer player) {
		this.remove((LiengPlayer) player);
	}

	public void remove(LiengPlayer player) {
		try {
			if (player != null) {
				LiengPlayer removePlayer;
				outCodeSB.append("Remove player ").append(player.id)
						.append(NEW_LINE);
				int playingSize = this.playings.size();
				for (int i = 0; i < playingSize; i++) {
					removePlayer = this.playings.get(i);
					if (removePlayer.id == player.id) {
						this.playings.remove(removePlayer);
						break;
					}
				}

				int waitingSize = this.waitings.size();

				for (int i = 0; i < waitingSize; i++) {
					removePlayer = this.waitings.get(i);
					if (removePlayer.id == player.id) {
						this.waitings.remove(removePlayer);
						break;
					}
				}
			}
		} catch (Exception e) {
			outCodeSB.append("Remove player !!!error ").append(player.id)
					.append(NEW_LINE);
			mLog.error(e.getMessage() + " remove player: ", e.getStackTrace());
		}
	}

	public int onlinePlayers() {
		int size = 0;

		for (int i = 0; i < this.playings.size(); i++) {
			if (!playings.get(i).isOut) {
				size++;
			}
		}

		return size;
	}

	public LiengPlayer ownerQuit() {

		for (int i = 0; i < playings.size(); i++) {
			LiengPlayer p = playings.get(i);
			if (!p.notEnoughMoney() && !p.isOut) {

				owner = p;
				ownerSession = owner.currentSession;
				return p;
			}
		}

		for (int i = 0; i < waitings.size(); i++) {
			LiengPlayer p = waitings.get(i);
			if (!p.notEnoughMoney()) {

				owner = p;
				owner.setLastActivated(System.currentTimeMillis());
				ownerSession = owner.currentSession;
				return p;
			}
		}

		return null;
	}

	@Override
	public LiengPlayer findPlayer(long uid) {
		for (int i = 0; i < this.playings.size(); i++) {
			LiengPlayer player = this.playings.get(i);
			if (player.id == uid) {
				return player;
			}
		}

		for (int i = 0; i < this.waitings.size(); i++) {
			LiengPlayer player = this.waitings.get(i);
			if (player.id == uid) {
				return player;
			}
		}

		return null;
	}

	// Reset auto kickout user
	public void resetAutoKickOut() {
		long timeActivated = System.currentTimeMillis()
				+ SLEEP_BEETWEEN_MATCH_TIMEOUT;
		for (int i = 0; i < this.playings.size(); i++) {
			this.playings.get(i).setLastActivated(timeActivated);

		}
		for (int i = 0; i < this.waitings.size(); i++) {
			this.waitings.get(i).setLastActivated(timeActivated);
		}
		owner.setLastActivated(timeActivated);
	}

	/****************************************************/
	public void resetPlayers() {
		// System.out.println("Reset players now!");

		List<LiengPlayer> removedPlayer = new ArrayList<LiengPlayer>();

		for (int i = 0; i < this.playings.size(); i++) {
			if (playings.get(i).isOut || playings.get(i).notEnoughMoney()) {
				removedPlayer.add(this.playings.get(i));
			}

		}

		for (int i = 0; i < removedPlayer.size(); i++) {

			playings.remove(removedPlayer.get(i));

		}

		for (int i = 0; i < this.waitings.size(); i++) {
			this.waitings.get(i).isMonitor = false;
		}

		this.playings.addAll(this.waitings);
		this.waitings = new ArrayList<LiengPlayer>();

		resetAutoKickOut();

		roundBet = firstCashBet;
		int playingSize = playings.size();

		for (int i = 0; i < playingSize; i++) {
			LiengPlayer player = playings.get(i);
			player.isReady = false;

			// player.setMultiBetMoney(0);

			player.isGiveUp = false;

			player.setCards(new ArrayList<Poker>());

			player.setMinCall(0);
			player.setTotalCall(0);
			player.setShowAll(false);
			player.setTo(false);
			player.setTotalRound(0);
		}

		owner.isReady = true;
		this.isPlaying = false;

		isHidePoker = false;
		isFirstTo = true;
		isChiaBai = false;
		totalCall = 0;
		cards = new ArrayList<Integer>();

	}

	public boolean isAllReady() {
		return true;

	}

	public void start() throws ServerException, JSONException {
		lastActivated = System.currentTimeMillis();
		setCurrentTimeOut(AUTO_LAT_BAI);
		resetPlayers();

		// chiabaiFlash();
		if (beginUid < 1) {
			beginUid = owner.id;
		}
		int playingSize = this.playings.size();
		currIndex = -1;
		for (int i = 0; i < playingSize; i++) {
			LiengPlayer player = this.playings.get(i);
			if (player.id == beginUid)
				currIndex = i;
		}

		if (currIndex < 0) // winner is out
		{
			beginUid = owner.id; // winner is first to
			currIndex = 0;
		}

		chiabai();
		this.isPlaying = true;
		sendStart();

		totalCall = firstCashBet * playingSize;
		for (int i = 0; i < playingSize; i++) {
			LiengPlayer player = this.playings.get(i);
			player.setTotalCall(firstCashBet);
		}
	}

	public void latbai(long uid, int card) throws BusinessException,
			ServerException, JSONException {
		LiengPlayer player = findPlayer(uid);
		if (player == null) {
			throw new BusinessException(NONE_EXISTS_PLAYER);
		}

		// if(!player.containCard(card))
		// {
		// throw new BusinessException(Messages.NOT_EXISTS_CARD);
		//
		// }
		//
		
		if (!this.isPlaying) {
			throw new BusinessException(Messages.NOT_PLAYING_TABLE);
		}

		int latbaiResult = player.checkLatBai(card);
		switch (latbaiResult) {
		case 1:
			throw new BusinessException(Messages.NOT_EXISTS_CARD);
		case 2:
			throw new BusinessException("Bạn đã lật 2 cây rồi");
		case 3:
			throw new BusinessException("Bạn đã lật quân bài này rồi");

		}

		// send broadcast to another players about this card
		sendLatbai(player, card);

	}

	public void sendLatbai(LiengPlayer player, int card)
			throws ServerException, JSONException {
		MessageFactory msgFactory = getNotNullSession().getMessageFactory();

		LatBaiResponse resLatBai = (LatBaiResponse) msgFactory
				.getResponseMessage(MessagesID.LatBai);

		StringBuilder sb = new StringBuilder();
		sb.append(Long.toString(player.id)).append(
				AIOConstants.SEPERATOR_BYTE_1);
		sb.append(Integer.toString(card));
		resLatBai.value = sb.toString();
		resLatBai.mCode = ResponseCode.SUCCESS;
		resLatBai.zoneId = ZoneID.LIENG;
		resLatBai.session = player.currentSession;

		broadcastMsg(resLatBai, playings, waitings, player, false);

	}

	public void sendStart() throws ServerException, JSONException {
		MessageFactory msgFactory = getNotNullSession().getMessageFactory();

		int playingSize = this.playings.size();

		for (int i = 0; i < playingSize; i++) {
			LiengPlayer player = this.playings.get(i);

			if (!player.isOut) {
				try {
					StartResponse resMatchStart = (StartResponse) msgFactory
							.getResponseMessage(MessagesID.MATCH_START);

					String pokerStr = player.pokersToString();
					StringBuilder sb = new StringBuilder();
					sb.append(Long.toString(beginUid)).append(
							AIOConstants.SEPERATOR_BYTE_1);
					sb.append(pokerStr).append(AIOConstants.SEPERATOR_BYTE_1);
					sb.append(player.getPoint());

					resMatchStart
							.setSuccess(ResponseCode.SUCCESS, ZoneID.LIENG);
					resMatchStart.value = sb.toString();
					resMatchStart.setSession(player.currentSession);
					player.currentSession.write(resMatchStart);
				} catch (Exception ex) {
					mLog.error(ex.getMessage(), ex);
				}
			}
		}
	}

	private void sendBetInfo(LiengPlayer currPlayer, int type, long betMoney,
			LiengPlayer nxtPlayer) throws JSONException {
		ISession notNullSession = this.getNotNullSession();
		MessageFactory msgFactory = notNullSession.getMessageFactory();

		StringBuilder sb = new StringBuilder();
		sb.append(Long.toString(currPlayer.id)).append(
				AIOConstants.SEPERATOR_BYTE_1);
		sb.append(Long.toString(currPlayer.getCurrentCall())).append(
				AIOConstants.SEPERATOR_BYTE_1);
		sb.append(Long.toString(currPlayer.getMinCall())).append(
				AIOConstants.SEPERATOR_BYTE_1);
		sb.append(Integer.toString(type)).append(AIOConstants.SEPERATOR_BYTE_1);
		sb.append(Long.toString(currPlayer.getTotalCall())).append(
				AIOConstants.SEPERATOR_BYTE_1);
		sb.append(Long.toString(totalCall)).append(
				AIOConstants.SEPERATOR_BYTE_1); // tien to cua moi nguoi
		sb.append(Long.toString(nxtPlayer.id)).append(
				AIOConstants.SEPERATOR_BYTE_1);
		sb.append(Long.toString(nxtPlayer.getMinCall())).append(
				AIOConstants.SEPERATOR_BYTE_1);		
		sb.append(Long.toString(currPlayer.getTotalRound())).append(
				AIOConstants.SEPERATOR_BYTE_1);		
			
		BetResponse resbet = (BetResponse) msgFactory
				.getResponseMessage(MessagesID.BET);

		resbet.value = sb.toString();
		resbet.mCode = ResponseCode.SUCCESS;
		resbet.zoneId = ZoneID.LIENG;

		broadcastMsg(resbet, playings, waitings, currPlayer, true);

	}

	private int countToPlayer() {
		int count = 0;
		int playingSize = this.playings.size();
		for (int i = 0; i < playingSize; i++) {
			LiengPlayer player = this.playings.get(i);
			if (!player.isGiveUp && (!player.isOut))
				count++;
		}

		return count;
	}

	private LiengPlayer getNextPlayer() {

		int playingSize = this.playings.size();
		int nIndex = (currIndex + 1) % playingSize;

		// find from nextIndex to the end to find next id
		int numToPlayer = countToPlayer();
		if (numToPlayer == 1)
			return null;

		for (int i = nIndex; i < playingSize; i++) {
			LiengPlayer player = this.playings.get(i);
			if (!player.isGiveUp && (!player.isOut) && !player.isShowAll()) {
				if (player.getTotalCall() < roundBet || !player.isTo()) // chua
																		// to
																		// hoac
																		// chua
																		// bang
																		// tien
																		// to
				{
					currIndex = i;
					player.setMinCall(roundBet - player.getTotalCall());
					return player;
				}

			}
		}

		// find from 0 to currindex -1 to find next id or new round

		int prevIndex = currIndex;
		for (int i = 0; i < prevIndex; i++) {

			LiengPlayer player = this.playings.get(i);
			if (!player.isGiveUp && (!player.isOut) && !player.isShowAll()) {
				if (player.getTotalCall() < roundBet || !player.isTo()) {
					currIndex = i;
					player.setMinCall(roundBet - player.getTotalCall());
					return player;
				}

			}
		}

		// error :(
		outCodeSB.append("End game").append(NEW_LINE);

		return null;
	}

	public void bet(long betMoney, long uid) throws BusinessException,
			JSONException, ServerException {
		outCodeSB.append("betInfo uid: ").append(uid).append(" money:")
				.append(betMoney).append(NEW_LINE);
		LiengPlayer player = findPlayer(uid);
		if (player == null) {
			throw new BusinessException(NONE_EXISTS_PLAYER);
		}

		LiengPlayer currPlayer = this.playings.get(currIndex);
		if (currPlayer == null) {
			outCodeSB.append("error ban choi(curr player = null)")
					.append(currIndex).append(NEW_LINE);
			mLog.error("error ban choi(curr player = null) matchId " + matchID);
			throw new BusinessException("bàn chơi bị lỗi");
		}

		if (currPlayer.id != player.id) {
			throw new BusinessException("Bạn đánh không đúng vòng");
		}

		if (player.isGiveUp) {
			throw new BusinessException(Messages.GIVE_UP_PLAYER);
		}

		lastActivated = System.currentTimeMillis();

		int typeCall = -1;
		player.setTo(true);

		if (betMoney > -1) {
			if (betMoney < player.getMinCall()) {
				betMoney = player.getMinCall();
			}

			long maxBet = player.getMinCall() + 20 * firstCashBet;
			if (betMoney > maxBet) {
				betMoney = maxBet;

			}

			player.setCurrentCall(betMoney - player.getMinCall());
			
//			outCodeSB.append("player total call uid: ").append(uid).append(" money: ").player.getTotalCall()
//			.append(betMoney).append(NEW_LINE);

			player.setTotalCall(player.getTotalCall() + player.getCurrentCall()
					+ player.getMinCall());
			roundBet += player.getCurrentCall();
			if (player.cash <= player.getTotalCall()) {
				player.setShowAll(true);
				typeCall = 3; // tat tay
			} else {
				if (player.getCurrentCall() == 0) {
					if (isFirstTo) {
						typeCall = 1; // to
					} else {
						typeCall = 0; // theo;
					}
				} else {
					typeCall = 1;
				}
			}
			
			totalCall += player.getCurrentCall();

			outCodeSB.append("Total call: ").append(totalCall).append(NEW_LINE);
			outCodeSB.append("roundbet: ").append(roundBet).append(NEW_LINE);			
			
			// Tong so lan da choi
			player.setNextRound();
			
		} else {
			player.isGiveUp = true;
		}

		player.setTypeTo(typeCall);
		isFirstTo = false;

		LiengPlayer nxtPlayer = getNextPlayer();
		if (nxtPlayer == null) {
			// end game
			endMatch(player);
		} else {
			sendBetInfo(player, typeCall, betMoney, nxtPlayer);
		}
	}

	private int getRandomNumber(List<Integer> input, List<Integer> result) {
		int lengh = input.size() - 1;
		int index = (int) Math.round(Math.random() * lengh);
		result.add(input.get(index));
		return index;
	}

	private void chiabai() {

		// generate cards
		cards = new ArrayList<Integer>();
		List<Integer> currList = new ArrayList<Integer>();

		for (int i = 0; i < 52; i++) {

			currList.add(i + 1);
		}

		// remove another 3->7 card

		int playingSize = this.playings.size();
		int numCards = playingSize * 3;

		for (int i = 0; i < numCards; i++) {
			int index = getRandomNumber(currList, cards);
			currList.remove(index);
		}

		// write into log
		outCodeSB.append("pure cards: ");
		for (int i = 0; i < numCards; i++) {
			outCodeSB.append(cards.get(i)).append("#");
		}

		outCodeSB.append(NEW_LINE);

		Poker p = new Poker();
		// set pokers to user
		for (int i = 0; i < playingSize; i++) {
			int toCurrIndexPoker = (i + 1) * 3;
			int fromCurrIndexPoker = i * 3;
			LiengPlayer player = this.playings.get(i);
			outCodeSB.append("player : ").append(i).append(" : ");
			for (int j = fromCurrIndexPoker; j < toCurrIndexPoker; j++) {
				Poker poker = new Poker(cards.get(j));
				player.getCards().add(poker);
				outCodeSB.append(p.toString()).append("#");
			}
			player.sortForClient();
			player.calculatePoint(); // calculate point
			outCodeSB.append(NEW_LINE);

		}

	}

	private String getEndJSonValue(long newOwnerId, LiengPlayer currentToPlayer)
			throws JSONException {
		StringBuilder sb = new StringBuilder();
		sb.append(Long.toString(newOwnerId)).append(
				AIOConstants.SEPERATOR_BYTE_1);
		sb.append(Long.toString(currentToPlayer.id)).append(
				AIOConstants.SEPERATOR_BYTE_1);
		sb.append(Long.toString(currentToPlayer.getCurrentCall())).append(
				AIOConstants.SEPERATOR_BYTE_1);
		sb.append(Long.toString(currentToPlayer.getMinCall())).append(
				AIOConstants.SEPERATOR_BYTE_1);
		sb.append(Long.toString(currentToPlayer.getTypeTo())).append(
				AIOConstants.SEPERATOR_BYTE_1);
		sb.append(Long.toString(totalCall)).append(
				AIOConstants.SEPERATOR_BYTE_3); // tien to cua moi nguoi

		int playingSize = this.playings.size();

		for (int i = 0; i < playingSize; i++) {
			try {
				LiengPlayer player = this.playings.get(i);
				sb.append(Long.toString(player.id)).append(
						AIOConstants.SEPERATOR_BYTE_1);
				sb.append(Long.toString(player.getWonMoney())).append(
						AIOConstants.SEPERATOR_BYTE_1);
				sb.append(Long.toString(player.cash)).append(
						AIOConstants.SEPERATOR_BYTE_1);
				sb.append(player.isOut ? "1" : "0").append(
						AIOConstants.SEPERATOR_BYTE_1);
				sb.append(player.notEnoughMoney() ? "1" : "0");

				if (!player.isGiveUp && !player.isOut && !isHidePoker) {
					sb.append(AIOConstants.SEPERATOR_BYTE_1);
					// sb.append(Integer.toString(player.getType())).append(AIOConstants.SEPERATOR_BYTE_1);
					sb.append(player.pokersToString()).append(
							AIOConstants.SEPERATOR_BYTE_1);
					sb.append(player.getPoint());

				}
				sb.append(AIOConstants.SEPERATOR_BYTE_1);
				sb.append(Long.toString(player.getTotalCall()));
				sb.append(AIOConstants.SEPERATOR_BYTE_2);
			} catch (Exception ex) {
				outCodeSB.append("error end van khi chua chia bai").append(
						NEW_LINE);
			}
		}

		sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}

	public long endMatch(LiengPlayer currentToPlayer) throws ServerException,
			JSONException {
		resetAutoKickOut();
		this.isPlaying = false;

		winner = null;

		int playingSize = this.playings.size();

		if (countToPlayer() == 1) {
			// the last not give player is winner
			for (int i = 0; i < playingSize; i++) {
				LiengPlayer player = this.playings.get(i);
				if (!player.isOut && !player.isGiveUp) {
					winner = player; // find out winner who doesnt give up
					break;
				}
			}

			isHidePoker = true;

		}

		if (winner == null) {

			for (int i = 0; i < playingSize; i++) {
				LiengPlayer player = this.playings.get(i);
				if (!player.isOut && !player.isGiveUp) {
					if (winner == null) {
						winner = player;
					} else {
						if (player.getPoint() > winner.getPoint()) {
							winner = player;
						}
					}

				}
			}

		}

		if (winner == null) {
			winner = this.playings.get(0);
		}

		beginUid = winner.id;

		long newOwnerId = 0;

		updateCash();

		// send end message to client
		MessageFactory msgFactory = getNotNullSession().getMessageFactory();

		if (owner.isOut || owner.notEnoughMoney()) {
			LiengPlayer newOwner = this.ownerQuit();

			if (newOwner != null && newOwnerId == 0) {
				newOwnerId = newOwner.id;
			}
		}

		String endValue = getEndJSonValue(newOwnerId, currentToPlayer);
		EndMatchResponse endMatchRes = (EndMatchResponse) msgFactory
				.getResponseMessage(MessagesID.MATCH_END);
		// set the result
		endMatchRes.setZoneID(ZoneID.LIENG);
		endMatchRes.setSuccess(endValue);

		broadcastMsg(endMatchRes, playings, waitings, winner, true);

		outCodeSB.append("------------end match").append(NEW_LINE);
		saveLogToFile();
		// this.removeNotEnoughMoney(this.getRoom());
		this.resetPlayers();
		return newOwnerId;

	}

	@Override
	public boolean newContainPlayer(long id) {
		for (int i = 0; i < this.playings.size(); i++) {
			if (playings.get(i).id == id) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected List<SimplePlayer> removeNotEnoughMoney() {
		List<SimplePlayer> removedPlayers = new ArrayList<SimplePlayer>();
		for (int i = 0; i < playings.size(); i++) {
			playings.get(i).moneyForBet = this.firstCashBet;
			if (playings.get(i).notEnoughMoney()) {
				removedPlayers.add(playings.get(i));
			}
		}

		boolean isChangeOwner = false;
		for (int i = 0; i < removedPlayers.size(); i++) {
			if (removedPlayers.get(i).id == owner.id) {
				isChangeOwner = true;
			}
			playings.remove(removedPlayers.get(i));
		}

		if (isChangeOwner) {
			if (playings.size() > 0) {
				owner = playings.get(0);

				resetAutoKickOut();
			}
		}
		this.setOwnerId(owner.id);

		return removedPlayers;
	}

	public void updateCash() {
		Connection con = DBPoolConnection.getConnection();
		try {
			String desc = "Lieng:" + matchID;
			UserDB userDb = new UserDB(con);
			long wonMoney = 0;

			boolean havingMinusBalance = false;

			List<LiengPlayer> lstWinners = new ArrayList<LiengPlayer>();
			List<LiengPlayer> lstLosers = new ArrayList<LiengPlayer>();
			lstWinners.add(winner);
			int playingSize = this.playings.size();

			for (int i = 0; i < playingSize; i++) {
				LiengPlayer player = this.playings.get(i);
				if (player.id != winner.id) {
					if (!player.isOut && !player.isGiveUp
							&& player.getPoint() == winner.getPoint()) {
						lstWinners.add(player);
					} else {
						lstLosers.add(player);
						player.isWin = false;
						long lostMoney = player.getTotalCall();

						if (player.cash < lostMoney) {
							lostMoney = player.cash;

						}

						wonMoney += lostMoney;

						player.setWonMoney(-lostMoney);
					}

				}

			}

			int winSize = lstWinners.size();
			long partWonMoney = wonMoney / winSize;
			long revertLoseMoney = 0;
			for (int i = 0; i < winSize; i++) {
				LiengPlayer player = lstWinners.get(i);
				player.isWin = true;
				player.setWonMoney((int) (partWonMoney));

				if (player.getWonMoney() > player.cash) {
					player.setWonMoney(player.cash);
					long revert = partWonMoney - player.cash;
					revertLoseMoney += revert;
					wonMoney -= revert;

				}
				player.setWonMoney((long) ((float) player.getWonMoney() * REAL_GOT_MONEY));

			}

			// luu nguoi thua
			int lostSize = lstLosers.size();
			long partRevert = 0;

			if (lostSize > 0 && revertLoseMoney > 0) {
				partRevert = revertLoseMoney / lostSize;
			}
			for (int i = 0; i < lostSize; i++) {
				LiengPlayer player = lstLosers.get(i);
				if (partRevert > 0) {
					long lostMoney = -player.getWonMoney();
					if (lostMoney > (partRevert))// hoan tien cho nguoi nay neu
													// nhu nguoi do hoan lai
													// tien ma van thua
					{
						lostMoney -= partRevert;

						// hoan tien ok
						player.setWonMoney(-lostMoney);

					}

				}

				player.cash = userDb.updateUserMoney(-player.getWonMoney(),
						player.isWin, player.id, desc, player.getExperience(),
						10009);

				if (player.cash < 0) {
					wonMoney += player.cash;
					havingMinusBalance = true;
					player.cash = 0;
				}

			}

			// Save database winner
			partWonMoney = wonMoney / winSize;

			for (int i = 0; i < winSize; i++) {
				LiengPlayer player = lstWinners.get(i);
				player.isWin = true;
				player.setWonMoney((int) (partWonMoney * REAL_GOT_MONEY));

				if (player.getWonMoney() > player.cash) {
					// player.setWonMoney(player.cash);

					player.setWonMoney(player.cash);
				}

				player.cash = userDb.updateUserMoney(player.getWonMoney(),
						true, player.id, desc, player.getExperience(), 10009);

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
			mLog.error(ex.getMessage(), ex);
		} finally {
			try {
				con.close();
			} catch (SQLException ex) {
				outCodeSB.append(ex.getStackTrace()).append(NEW_LINE);
				mLog.error(ex.getMessage(), ex);
			}
		}

		saveLogToFile();

	}

	@Override
	public void kickTimeout(Room room) {
		try {

			if (!this.isPlaying) {
				if (this.playings.size() > 1) {
					long now = System.currentTimeMillis();
					// check user which does nothing when he comes to table
					boolean isAllJoinReady = true;

					// Room room = bacayZone.findRoom(matchId);

					for (int i = 0; i < this.playings.size(); i++) {

						LiengPlayer bcPlayer = this.playings.get(i);

						if (!bcPlayer.isReady && bcPlayer.id != owner.id) {
							isAllJoinReady = false;
							// does this user over time out
							if (now - bcPlayer.getLastActivated() > AUTO_KICKOUT_TIMEOUT) {
								// kich him
								kickTimeout(room, bcPlayer, 0);
								this.remove(bcPlayer);
								bcPlayer.isOut = true;
								String kickOutMessage = "Auto kick out "
										+ bcPlayer.username;
								mLog.debug(kickOutMessage);
								outCodeSB.append(kickOutMessage).append(
										NEW_LINE);
							}

						}
					}

					if (isAllJoinReady) {
						// start game
						if (now - owner.getLastActivated() > AUTO_KICKOUT_OWNER_TIMEOUT) {
							SimplePlayer oldOwner = owner.clone();

							LiengPlayer currOwner = findPlayer(owner.id);
							currOwner.isOut = true;
							owner = this.ownerQuit();
							kickTimeout(room, oldOwner, owner.id);
							// autoStartGame(owner);

							this.resetPlayers();
						}

					}

				}
			}
		} catch (Exception ex) {
			try {

				mLog.error("bacay Kick time out error matchId " + matchID, ex);
				outCodeSB.append("Kick out error").append(NEW_LINE);
				cancel(playings);
				room.allLeft();
				this.destroy();
			} catch (Exception exx) {
			}
		}
	}

	@Override
	protected void joinResponse(JoinResponse joinResponse) {
		// override if there 's any game which has addition info in Join
		// response
		if (isPlaying) {
			if (this.playings.size() > currIndex)
				joinResponse.currentIdPlayer = this.playings.get(currIndex).id;
			else
				joinResponse.currentIdPlayer = owner.id;
		} 
	}

	@Override
	public ISession getNotNullSession() {
		int playingSize = this.playings.size();
		for (int i = 0; i < playingSize; i++) {
			LiengPlayer player = this.playings.get(i);
			if (!player.isOut && player.currentSession != null
					&& player.currentSession.getMessageFactory() != null) {
				return player.currentSession;
			}
		}

		return null;
	}

	public SimplePlayer getNotNullSessionPlayer() {
		int playingSize = this.playings.size();
		for (int i = 0; i < playingSize; i++) {
			LiengPlayer player = this.playings.get(i);
			if (!player.isOut && player.currentSession != null
					&& player.currentSession.getMessageFactory() != null) {
				return player;
			}
		}

		return null;
	}

	private void betDoTimeout() {
		try {
			outCodeSB.append("Do time out with currIndex ").append(currIndex)
					.append(NEW_LINE);
			LiengPlayer player = this.playings.get(currIndex);
			bet(UP_BO, player.id); // give up
		} catch (ServerException ex) {
			outCodeSB.append(ex.getMessage()).append(NEW_LINE);
			mLog.error(ex.getMessage(), ex);
		} catch (Exception ex) {
			outCodeSB.append(ex.getMessage()).append(NEW_LINE);
			mLog.error(ex.getMessage(), ex);
		}

	}

	public void doTimeout() {
		try {

			lastActivated = System.currentTimeMillis();
			outCodeSB.append("do timeout").append(NEW_LINE);
			mLog.debug("doTimeout");

			betDoTimeout();

		} catch (Exception ex) {
			try {
				this.isPlaying = false;
				mLog.error(ex.getMessage(), ex);
			} catch (Exception exx) {
			}
		}

	}

	/**
	 * @return the isChiaBai
	 */
	public boolean isIsChiaBai() {
		return isChiaBai;
	}

	/**
	 * @param isChiaBai
	 *            the isChiaBai to set
	 */
	public void setIsChiaBai(boolean isChiaBai) {
		this.isChiaBai = isChiaBai;
	}

}
