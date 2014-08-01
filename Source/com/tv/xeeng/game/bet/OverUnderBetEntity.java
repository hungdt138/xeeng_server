package com.tv.xeeng.game.bet;

public class OverUnderBetEntity {
	public double over;
	public double under;
	public String number1;
	public String number2;
	
	public OverUnderBetEntity(double over, double under, String number1, String number2) {
		this.over = over;
		this.under = under;
		this.number1 = number1;
		this.number2 = number2;
	}
	
	public String toString(){
		String res = "";
		res += number1+" "+number2+":";
		res +=over+"-";
		res +=under;
		return res;
	}
}
