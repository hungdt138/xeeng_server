package com.tv.xeeng.game.data;

public class ChargingInfo {
	public int id;
	public String number;
	public String value;
	public String desc;
        public int partnerId;
        public String refCode;
        public boolean isNeedActive;
        public boolean isActive;
        public int position;
        public boolean isSMS;
        public int telcoProvider;
	public ChargingInfo() {
		// TODO Auto-generated constructor stub
	}
	public ChargingInfo(int i, String n, String v, String d, int partnerId, 
                String refCode, boolean isNeedActive) {
		this.id = i;
		this.number = n;
		this.value = v;
		this.desc = d;
                this.partnerId = partnerId;
                this.refCode = refCode;
                this.isNeedActive = isNeedActive;
	}
}
