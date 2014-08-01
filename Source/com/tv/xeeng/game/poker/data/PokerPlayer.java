package com.tv.xeeng.game.poker.data;

import com.tv.xeeng.game.data.SimplePlayer;

/**
 * 
 * @author thangtd
 * created 25/02/2014
 */
public class PokerPlayer extends SimplePlayer {
	public boolean isOwner;
	public boolean isObserve;
	
	public PokerPlayer(long uid) {
		this.id = uid;
	}
}