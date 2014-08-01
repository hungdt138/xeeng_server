package com.tv.xeeng.game.bet;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserBetEntity {
	
	/*@
	 *  0: asian; 
	 *  1: euro;
	 *  2: O/U
	 */
	public int typeBet=-1;
	public long money;
	/*@
	 *  
	 *  0: thang;
	 *  1: thua
	 *  2: hoa; 
	 */
	
	public int bet = -1;
	public MatchEntity match;
	public Date timeBet;
	public long moneyWin;
	public UserBetEntity(int t, long m, int b, Date time,long moneyW) {
		moneyWin = moneyW;
		typeBet = t;
		timeBet = time;
		money = m;
		bet = b;
	}
	public String dateToString(){
			SimpleDateFormat format = new SimpleDateFormat("HH:mm dd-MM");
			StringBuilder sb = new StringBuilder( format.format( timeBet ) );
			return sb.toString();
			
	}
}
