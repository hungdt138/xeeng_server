package com.tv.xeeng.game.bet;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MatchEntity {
	public int matchID;
	public int leagueID;
	public String homeTeam;
	public String visitTeam;
	public AsianBetEntity asianBet;
	public EuroBetEntity euroBet;
	public OverUnderBetEntity OUBet;
	public Date startTime;
	public Date lockTime;
	public String description;
	public int status = -1;
	public int homeGoal = 0;
	public int visitGoal = 0;
	
	public MatchEntity() {
		
	}
	public MatchEntity(int matchID, int leagueID, String homeTeam, String visitTeam,
			AsianBetEntity asianBet, EuroBetEntity euroBet, OverUnderBetEntity OUBet,
			Date startTime, Date lockTime, String description) {
		this.matchID = matchID;
		this.leagueID = leagueID;
		this.homeTeam = homeTeam;
		this.visitTeam = visitTeam;
		this.asianBet = asianBet;
		this.euroBet = euroBet;
		this.OUBet = OUBet;
		this.startTime = startTime;
		this.lockTime = lockTime;
		this.description = description;
	}
	public MatchEntity(int matchID, int leagueID, String homeTeam, String visitTeam,
			Date startTime, Date lockTime, String description) {
		this.matchID = matchID;
		this.leagueID = leagueID;
		this.homeTeam = homeTeam;
		this.visitTeam = visitTeam;
		this.startTime = startTime;
		this.lockTime = lockTime;
		this.description = description;
	}
	public void setResult(int homeG, int visitG){
		this.homeGoal = homeG;
		this.visitGoal = visitG;
	}
	public String resultToString(){
		String res = "";
		res += homeGoal+":" + visitGoal;
		return res;
	}
	public String startDateToString(){
		SimpleDateFormat format = new SimpleDateFormat("HH:mm dd-MM");
		StringBuilder sb = new StringBuilder( format.format( startTime ) );
		return sb.toString();
		
	}
	public String lockDateToString(){
		SimpleDateFormat format = new SimpleDateFormat("HH:mm dd-MM");
		StringBuilder sb = new StringBuilder( format.format( lockTime ) );
		return sb.toString();
		
	}
}
