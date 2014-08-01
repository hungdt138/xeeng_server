package com.tv.xeeng.game.bet;

public class AsianBetEntity {
	public double tyleChuNha;
	public double tyleKhach;
	public String chuNhaChap;
	public String khachChap;
	
	public AsianBetEntity(double chuNha, double khach, 
			String chuNhaChap, String khachChap) {
		this.tyleChuNha = chuNha;
		this.tyleKhach = khach;
		this.khachChap = khachChap;
		this.chuNhaChap = chuNhaChap;
	}
	public String toString(){
		String res = "";
		res+= tyleChuNha+"*";
		res+= chuNhaChap+":";
		res+= khachChap+"*";
		res+= tyleKhach;
		return res;
	}
}
