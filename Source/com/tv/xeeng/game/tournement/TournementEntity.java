package com.tv.xeeng.game.tournement;

import java.util.Date;

public class TournementEntity {
	public int id;
	public Date startDate;
	public Date endDate;
	public String name;
	public long minBet;
	public int game;
	public long creator;
	public boolean isBook;
	public TournementEntity(int i, Date s, Date e, String n, long m, int g, long c) {
		creator = c;
		id = i;
		startDate = s;
		endDate = e;
		name = n;
		minBet = m;
		game = g;
	}
}
