package com.tv.xeeng.game.phom.data;

import java.util.ArrayList;
import java.util.Vector;

public class Utils {
	/******************************/
	// Chia bai
	
    public static ArrayList<Integer> getRandomList(){
        ArrayList<Integer> res = new ArrayList<Integer>();
        ArrayList<Integer> currList = new ArrayList<Integer>();
        for (int i = 0; i < 52; i++){
            currList.add(i, i+1);
        }
        for(int i = 0; i < 52; i++){
            int index = getRandomNumber(currList, res);
            currList.remove(index);
	}
        return res;
    }
    public static int getRandomNumber(ArrayList<Integer> input, ArrayList<Integer> result){
	int lengh = input.size() - 1;
        int index = (int) Math.round(Math.random() * lengh);
        result.add(input.get(index));
        return index;
    }
    /******************************/
	// Sort an array of Poker increament
	public static void quicksortPokers(int low, int high, Poker[] input) {
		          int i = low, j = high;
            Poker pivot = input[low + (high - low) / 2];
            while (i <= j) {
                while (!input[i].isGreater(pivot)) {
                    i++;
                }
                while (input[j].isGreater(pivot)) {
                    j--;
                }
                if (i <= j) {
                    Poker temp = input[i];
                    input[i] = input[j];
                    input[j] = temp;

                    i++;
                    j--;
                }
            }
            // Recursion
            if (low < j) {
                quicksortPokers(low, j, input);
            }
            if (i < high) {
                quicksortPokers(i, high, input);
            }
	}
	
	/*************************************/
	// Sort an array of PhomPlayer increament
	public static void quicksortPhomPlayers(int low, int high, PhomPlayer[] input) {
            int i = low, j = high;
            PhomPlayer pivot = input[low + (high - low) / 2];
            while (i <= j) {
                while (!(input[i].isWin(pivot))) {
                    i++;
                }
                while (input[j].isWin(pivot)) {
                    j--;
                }
                if (i <= j) {
                    PhomPlayer temp = input[i];
                    input[i] = input[j];
                    input[j] = temp;

                    i++;
                    j--;
                }
            }
            // Recursion
            if (low < j) {
                quicksortPhomPlayers(low, j, input);
            }
            if (i < high) {
                quicksortPhomPlayers(i, high, input);
            }
	}
	
	/*************************************/
	public static Poker[] arrayListToArray(ArrayList<Poker> input){
		Poker[] res = new Poker[input.size()];
		for(int i = 0; i < input.size(); i++){
			res[i] = input.get(i);
		}
		return res;
	}
	public static Poker[] vectorToArray(Vector<Poker> input){
		Poker[] res = new Poker[input.size()];
		for(int i = 0; i < input.size(); i++){
			res[i] = input.get(i);
		}
		return res;
	}
	public static boolean checkPhom(Vector<Poker> input) throws PhomException{
		if (input.size() < 3) throw new PhomException("Phom co it hon 3 cards");
		Poker[] temp = vectorToArray(input);
		quicksortPokers(0, temp.length-1, temp);
		int res = -1;
		if(temp[1].getNum() - temp[0].getNum() == 0){
			res = 0;
		}else if(temp[1].getNum() - temp[0].getNum() == 1){
			res = 1;
		}else {
			throw new PhomException("Khong phai phom");
		}
		for(int i = 1; i< temp.length-1; i++){
			if(res != (temp[i+1].getNum() - temp[i].getNum()))
					throw new PhomException("Phom co it hon 3 cards");
		}
		
		return (res == 0);
	}
/******************************************/
	//Phom
    public static Poker numToPoker(int input){
        int temp = input-1;
        int type = temp/13;
        int num = temp%13;
        switch(type){
//            case 0:
//                return new Poker(num+1,PokerType.Pic);
//            case 1:
//                return new Poker(num+1,PokerType.Tep);
            case 1:
                return new Poker(num+1,PokerType.Pic);
            case 0:
                return new Poker(num+1,PokerType.Tep);
            case 2:
                return new Poker(num+1,PokerType.Ro);
            case 3:
                return new Poker(num+1,PokerType.Co);
            default:
                new Poker(0,PokerType.Pic);
        }
        return new Poker(0,PokerType.Pic);
        /*switch (input){
            case 1:
                return new Poker(1, PokerType.Pic);
            case 2:
                return new Poker(1,PokerType.Pic);
            case 3:
                return new Poker(1,PokerType.Pic);
            case 4:
                return new Poker(1,PokerType.Pic);
            case 5:
                return new Poker(2,PokerType.Pic);
            case 6:
                return new Poker(2,PokerType.Pic);
            case 7:
                return new Poker(2,PokerType.Pic);
            case 8:
                return new Poker(2,PokerType.Pic);
            case 9:
                return new Poker(3,PokerType.Pic);
            case 10:
                return new Poker(3,PokerType.Pic);
            case 11:
                return new Poker(3,PokerType.Pic);
            case 12:
                return new Poker(3,PokerType.Pic);
            case 13:
                return new Poker(4,PokerType.Pic);
            case 14:
                return new Poker(4,PokerType.Tep);
            case 15:
                return new Poker(4,PokerType.Tep);
            case 16:
                return new Poker(4,PokerType.Tep);
            case 17:
                return new Poker(5,PokerType.Tep);
            case 18:
                return new Poker(5,PokerType.Tep);
            case 19:
                return new Poker(5,PokerType.Tep);
            case 20:
                return new Poker(5,PokerType.Tep);
            case 21:
                return new Poker(6,PokerType.Tep);
            case 22:
                return new Poker(6,PokerType.Tep);
            case 23:
                return new Poker(6,PokerType.Tep);
            case 24:
                return new Poker(6,PokerType.Tep);
            case 25:
                return new Poker(7,PokerType.Tep);
            case 26:
                return new Poker(7,PokerType.Tep);
            case 27:
                return new Poker(7,PokerType.Ro);
            case 28:
                return new Poker(7,PokerType.Ro);
            case 29:
                return new Poker(8,PokerType.Ro);
            case 30:
                return new Poker(8,PokerType.Ro);
            case 31:
                return new Poker(8,PokerType.Ro);
            case 32:
                return new Poker(8,PokerType.Ro);
            case 33:
                return new Poker(9,PokerType.Ro);
            case 34:
                return new Poker(9,PokerType.Ro);
            case 35:
                return new Poker(9,PokerType.Ro);
            case 36:
                return new Poker(9,PokerType.Ro);
            case 37:
                return new Poker(10,PokerType.Ro);
            case 38:
                return new Poker(10,PokerType.Ro);
            case 39:
                return new Poker(10,PokerType.Ro);
            case 40:
                return new Poker(10,PokerType.Co);
            case 41:
                return new Poker(11,PokerType.Pic);
            case 42:
                return new Poker(11,PokerType.Tep);
            case 43:
                return new Poker(11,PokerType.Ro);
            case 44:
                return new Poker(11,PokerType.Co);
            case 45:
                return new Poker(12,PokerType.Pic);
            case 46:
                return new Poker(12,PokerType.Tep);
            case 47:
                return new Poker(12,PokerType.Ro);
            case 48:
                return new Poker(12,PokerType.Co);
            case 49:
                return new Poker(13,PokerType.Pic);
            case 50:
                return new Poker(13,PokerType.Tep);
            case 51:
                return new Poker(13,PokerType.Ro);
            case 52:
                return new Poker(13,PokerType.Co);
            default:
                return new Poker(0, PokerType.Pic);
        }*/
    }
}
