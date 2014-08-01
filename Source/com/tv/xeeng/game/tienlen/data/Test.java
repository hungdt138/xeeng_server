package com.tv.xeeng.game.tienlen.data;

public class Test {
	public static void main(String[] args){
		Poker[] p = new Poker[5];
		p[0] = new Poker(10, PokerType.Co);
		p[1] = new Poker(5, PokerType.Pic);
		p[2] = new Poker(9, PokerType.Co);
		p[3] = new Poker(5, PokerType.Co);
		p[4] = new Poker(13, PokerType.Co);
		quicksort(0, 4, p);
		System.out.println();
	}
	private static void quicksort(int low, int high, Poker[] input) {
		int i = low, j = high;
		Poker pivot = input[low + (high-low)/2];
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
		if (low < j)
			quicksort(low, j, input);
		if (i < high)
			quicksort(i, high, input);
	}
}
