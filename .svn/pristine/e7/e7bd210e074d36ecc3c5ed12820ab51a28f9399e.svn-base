/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.line.data;

import java.util.ArrayList;

import com.tv.xeeng.game.data.Couple;
import com.tv.xeeng.game.data.SimplePlayer;

/**
 * 
 * @author tuanda
 */
public class LinePlayer extends SimplePlayer {

	public int point = 0;

	public LinePlayer(long uid) {
		this.id = uid;
		this.isGiveUp = false;
	}

	public void setPoint(int code) {
		switch (code) {
		case 5:
			point += 8;
			break;
		case 6:
			point += 10;
			break;
		case 7:
			point += 12;
			break;
		case 8:
			point += 16;
			break;
		case 9:
			point += 18;
			break;
		case 10:
			point += 20;
			break;
		case 11:
			point += 22;
			break;
		case 12:
			point += 24;
			break;
		case 13:
			point += 26;
			break;
		default:
			break;
		}
	}

	public void start() {
	}

	public void reset() {
		this.isReady = false;
		this.isWin = false;
		this.isGiveUp = false;
		this.isStop = false;
		point = 0;
                this.wonMoney = 0;
	}

	@Override
	public boolean notEnoughMoney() {
		if (cash < 2 * moneyForBet)
			return true;
		return false;
	}

	// 0:false; 1:true; 2:loser; 3:other-loser
	public int play(Couple<Integer, Integer> s, Couple<Integer, Integer> d,
			boolean check,
			ArrayList<Couple<Couple<Integer, Integer>, Color>> hides)
			throws Exception {
		return 1;
	}
}
