/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.lieng.data;

import java.util.ArrayList;
import java.util.List;

import com.tv.xeeng.game.data.SimplePlayer;

/**
 * 
 * @author tuanda
 */
public class LiengPlayer extends SimplePlayer {
	private List<Poker> cards;
	// private int pointRound = 0;
	private final static int SAP = 10000, LIENG = 100, DI = 10;

	// private final static int POINT_TIMES = 100;

	private int typeTo;
	// private long yourBetMoney;

	private long minCall; // tien theo

	private long totalCall; // tong so tien da to
	private long currentCall; // tien to them
	private boolean showAll = false;

	private int point;
	private boolean to = false;
	private int totalRound = 0; // so lan da dat cuoc

	public LiengPlayer() {
	}

	public LiengPlayer(long uid) {
		this.id = uid;
		this.isGiveUp = false;
	}

	public int checkLatBai(int card) {
		Poker pkResult = null;
		int cardSize = cards.size();
		for (int i = 0; i < cardSize; i++) {
			Poker pk = cards.get(i);
			if (pk.toInt() == card) {
				pkResult = pk;
				break;
			}
		}

		if (pkResult == null) {
			// not exits card
			return 1;
		}

		if (pkResult.isLatbai())
			return 3;
		// tinh so quan bai lat
		int countLatbai = 0;
		for (int i = 0; i < cardSize; i++) {
			Poker pk = cards.get(i);
			if (pk.isLatbai()) {
				countLatbai++;
			}
		}

		if (countLatbai > 1) {
			// da lat qua so quan bai cho phep(2 quan roi)
			return 2;
		}
		pkResult.setLatbai(true);
		return 0;
	}

	public boolean containCard(int card) {
		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i).toInt() == card)
				return true;
		}

		return false;
	}

	public String pokersToString() {
		StringBuilder sb = new StringBuilder();
		int lastElement = this.cards.size() - 1;
		for (int i = 0; i < this.cards.size(); i++) {
			Poker p = this.cards.get(i);

			sb.append(p.toInt());
			if (i != lastElement) {
				sb.append("#");
			}
		}

		return sb.toString();
	}

	/**
	 * @return the cards
	 */
	public List<Poker> getCards() {
		return cards;
	}

	/**
	 * @param cards
	 *            the cards to set
	 */
	public void setCards(List<Poker> cards) {
		this.cards = cards;
	}

	/**
	 * @return the pointRound
	 */
	// public int getPointRound() {
	// return pointRound;
	// }

	private int sap(List<Poker> sortedCards) {

		int cardSize1 = sortedCards.size() - 1;
		for (int i = 0; i < cardSize1; i++) {
			Poker poker = sortedCards.get(i);
			Poker pokerNext = sortedCards.get(i + 1);
			if (poker.getNum() != pokerNext.getNum()) {
				return 0;
			}
		}

		return sortedCards.get(0).getNum() * SAP;

	}

	private int lieng(List<Poker> sortedCards)// this sortedCard convert At(1)
												// to 14
	{
		Poker card1 = sortedCards.get(0);
		Poker card2 = sortedCards.get(1);
		Poker card3 = sortedCards.get(2);

		if (card1.getNum() == 14 && card2.getNum() == 3 && card3.getNum() == 2) // bo
																				// lieng
																				// A,2,3
		{
			return card2.getNum() * LIENG;
		}

		int cardSize1 = sortedCards.size() - 1;

		for (int i = 0; i < cardSize1; i++) {
			Poker pk1 = sortedCards.get(i);
			Poker pk2 = sortedCards.get(i + 1);

			if (pk1.getNum() != pk2.getNum() + 1) {
				return 0;
			}

		}

		return sortedCards.get(0).getNum() * LIENG;

	}

	private int di(List<Poker> sortedCards) {
		int cardSize = sortedCards.size();
		for (int i = 0; i < cardSize; i++) {
			Poker pk1 = sortedCards.get(i);
			if (!(pk1.getNum() > 10 && pk1.getNum() < 14)) // neu quan la khong
															// dau nguoi return
															// khong phai di
				return 0;
		}

		return DI;
	}

	private int normalPoin(List<Poker> sortedCards) {
		int cardSize = sortedCards.size();
		int resultPoint = 0;

		for (int i = 0; i < cardSize; i++) {
			// convert to normal card to calculate point
			Poker pk = sortedCards.get(i);
			if (pk.getNum() > 9 && pk.getNum() < 14) {
				pk.setNum(0);
			}

			if (pk.getNum() == 14) {
				pk.setNum(1);
			}

			// calculate point
			resultPoint += pk.getNum();
		}

		resultPoint = resultPoint % 10;

		return resultPoint;
	}

	private List<Poker> sortCard(List<Poker> cardArray) {
		// convert At to 14
		int cardSize = cardArray.size();
		List<Poker> sortedCard = new ArrayList<Poker>();
		for (int i = 0; i < cardSize; i++) {
			Poker pk1 = cardArray.get(i);
			int num = pk1.getNum();
			if (num == 1) {
				num = 14;
			}

			sortedCard.add(new Poker(num, pk1.getType()));

		}
		// sort card
		for (int i = 0; i < cardSize; i++) {
			for (int j = i + 1; j < cardSize; j++) {
				Poker pokerI = sortedCard.get(i);
				Poker pokerJ = sortedCard.get(j);
				if (pokerJ.isGreater(pokerI)) {
					sortedCard.set(i, pokerJ);
					sortedCard.set(j, pokerI);
				}
			}
		}

		return sortedCard;
	}

	public void sortForClient() {
		cards = sortCard(cards);
	}

	public void calculatePoint() {
		point = 0;

		// sorted cards
		List<Poker> lstSortedCards = sortCard(cards);

		point = sap(lstSortedCards);

		if (point > 0) {
			return;
		}

		point = lieng(lstSortedCards);
		if (point > 0) {
			return;
		}

		point = di(lstSortedCards);
		if (point > 0) {
			return;
		}

		point = normalPoin(lstSortedCards);

	}

	/**
	 * @return the point
	 */
	public int getPoint() {
		return point;
	}

	public static void main(String[] args) {
		Poker pk1 = new Poker(2, PokerType.Co);
		Poker pk2 = new Poker(3, PokerType.Ro);
		Poker pk3 = new Poker(2, PokerType.Ro);
		// Poker pk4 = new Poker(4, PokerType.Co);
		// Poker pk5 = new Poker(5, PokerType.Co);

		List<Poker> cards = new ArrayList<Poker>();
		cards.add(pk1);
		cards.add(pk2);
		cards.add(pk3);

		LiengPlayer player = new LiengPlayer();
		player.setCards(cards);
		player.calculatePoint();
		System.out.println(player.getPoint());

	}

	/**
	 * @return the minCall
	 */
	public long getMinCall() {
		return minCall;
	}

	/**
	 * @param minCall
	 *            the minCall to set
	 */
	public void setMinCall(long minCall) {
		this.minCall = minCall;
	}

	/**
	 * @return the totalCall
	 */
	public long getTotalCall() {
		return totalCall;
	}

	/**
	 * @param totalCall
	 *            the totalCall to set
	 */
	public void setTotalCall(long totalCall) {
		this.totalCall = totalCall;
	}

	/**
	 * @return the currentCall
	 */
	public long getCurrentCall() {
		return currentCall;
	}

	/**
	 * @param currentCall
	 *            the currentCall to set
	 */
	public void setCurrentCall(long currentCall) {
		this.currentCall = currentCall;
	}

	/**
	 * @return the showAll
	 */
	public boolean isShowAll() {
		return showAll;
	}

	/**
	 * @param showAll
	 *            the showAll to set
	 */
	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}

	/**
	 * @return the to
	 */
	public boolean isTo() {
		return to;
	}

	/**
	 * @param to
	 *            the to to set
	 */
	public void setTo(boolean to) {
		this.to = to;
	}

	/**
	 * @return the typeTo
	 */
	public int getTypeTo() {
		return typeTo;
	}

	/**
	 * @param typeTo
	 *            the typeTo to set
	 */
	public void setTypeTo(int typeTo) {
		this.typeTo = typeTo;
	}

	/**
	 * @return the totalRound
	 */
	public int getTotalRound() {
		return totalRound;
	}

	/**
	 * @param totalRound
	 *            the totalRound to set
	 */
	public void setTotalRound(int totalRound) {
		this.totalRound = totalRound;
	}
	
	/*
	 * Tang so lan sau khi to hoac theo
	 */
	public void setNextRound() {
		this.totalRound ++;
	}

}
