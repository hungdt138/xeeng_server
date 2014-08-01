package com.tv.xeeng.game.tienlen.data;

import java.util.ArrayList;

import com.tv.xeeng.game.data.Couple;

public class ChiaBai {

    public void main(String[] args) {
        // System.out.println(new Poker(3, PokerType.Co).toInt());
        // System.out.println(intToPoker(42).toString());
        // if(true){
        // Couple<ArrayList<Integer>, ArrayList<Integer>> temp = toiTrang();
        // for(int i : temp.e1){
        // System.out.print(intToPoker(i).toString()+", ");
        // }
        // System.out.println("\n===========");
        // for(int i : temp.e2){
        // System.out.print(intToPoker(i).toString()+", ");
        // }
        // }
    }

    public ChiaBai() {
    }

    public Couple<ArrayList<Integer>, ArrayList<Integer>> baDoiThongLon() {
        ArrayList<Integer> res = new ArrayList<>();
        res.addAll(doi(1));
        res.addAll(doi(12));
        res.addAll(doi(13));
        return makeFinalList(res, 7);
    }

    public Couple<ArrayList<Integer>, ArrayList<Integer>> bonDoiThongLon() {
        ArrayList<Integer> res = new ArrayList<>();
        res.addAll(doi(1));
        res.addAll(doi(12));
        res.addAll(doi(13));
        res.addAll(doi(11));
        return makeFinalList(res, 5);
    }

    public Couple<ArrayList<Integer>, ArrayList<Integer>> toiTrang(int index) {
        switch (index) {
            /*
            case 0:
                //return baDoiThong3Bich();
            case 1:
                //return tuquy3();
            case 2:
                return tuquy2();
            case 3:
                return namDoiThong();
            case 4:
                return sauDoi();
            default:
                return sanhRong();
            */
            
            case 0:
                return tuquy2();
            case 1:
                return namDoiThong();
            case 2:
                return sauDoi();
            default:
                return sanhRong();
        }
    }

    /*
     * private Poker intToPoker(int i){ int n = (i - 1) % 13 + 1; int t =
     * (i-1)/13 + 1;; return new Poker(n, intToPokerType(t)); }
     */
    private boolean isInList(ArrayList<Integer> data, int s) {
        int len = data.size();
        for (int j = 0; j < len; j++) {
            int i = data.get(j);
            if (i == s) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<Integer> reduceList(ArrayList<Integer> source,
            ArrayList<Integer> sample) {
        if (sample.isEmpty()) {
            return source;
        }
        ArrayList<Integer> res = new ArrayList<>();

        int len = source.size();
        for (int j = 0; j < len; j++) {
            int i = source.get(j);
            if (!isInList(sample, i)) {
                res.add(i);
            }
        }
        return res;
    }

    private int makeRandomPoker(int n) {
        return (new Poker(n, makeRanDomType(new ArrayList<Integer>()))).toInt();
    }

    private ArrayList<Integer> doi(int n) {
        ArrayList<Integer> res = new ArrayList<>();
        ArrayList<Integer> dataType = new ArrayList<>();
        PokerType ty = makeRanDomType(dataType);

        res.add((new Poker(n, ty)).toInt());
        dataType.add(ty.toInt());
        ty = makeRanDomType(dataType);
        res.add((new Poker(n, ty)).toInt());
        return res;
    }

    private ArrayList<Integer> make52() {
        ArrayList<Integer> res = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            res.add(i, i + 1);
        }
        return res;
    }

    private Couple<ArrayList<Integer>, ArrayList<Integer>> sanhRong() {
        ArrayList<Integer> res = new ArrayList<>();
        res.add(makeRandomPoker(1));
        for (int i = 3; i < 14; i++) {
            res.add(makeRandomPoker(i));
        }
        return makeFinalList(res, 1);
    }

    private Couple<ArrayList<Integer>, ArrayList<Integer>> sauDoi() {
        ArrayList<Integer> res = new ArrayList<>();
        ArrayList<Integer> data = new ArrayList<>();
        data.add(1);
        for (int i = 3; i < 14; i++) {
            data.add(i);
        }
        data = getRandomList(data);
        for (int i = 0; i < 6; i++) {
            res.addAll(doi(data.get(i)));
        }
        return makeFinalList(res, 1);
    }

    private Couple<ArrayList<Integer>, ArrayList<Integer>> tuquy3() {
        ArrayList<Integer> res = new ArrayList<>();
        // tk3
        res.add((new Poker(3, PokerType.Co)).toInt());
        res.add((new Poker(3, PokerType.Ro)).toInt());
        res.add((new Poker(3, PokerType.Tep)).toInt());
        res.add((new Poker(3, PokerType.Pic)).toInt());

        return makeFinalList(res, 9);
    }

    private Couple<ArrayList<Integer>, ArrayList<Integer>> baDoiThong3Bich() {
        ArrayList<Integer> res = new ArrayList<>();

        ArrayList<Integer> dataType = new ArrayList<>();
        PokerType ty;
        // doi 3 co 3 pic
        res.add((new Poker(3, PokerType.Pic)).toInt());
        dataType.add(1);
        ty = makeRanDomType(dataType);
        res.add((new Poker(3, ty)).toInt());
        // Doi 4
        res.addAll(doi(4));
        // Doi 5
        res.addAll(doi(5));

        return makeFinalList(res, 7);

    }

    private Couple<ArrayList<Integer>, ArrayList<Integer>> namDoiThong() {
        ArrayList<Integer> res = new ArrayList<>();
        int index = (int) Math.round(Math.random() * 7) + 3;
        if (index == 10) {
            for (int i = 0; i < 4; i++) {
                res.addAll(doi(i + index));
            }
            res.addAll(doi(1)); // doi At
        } else {
            for (int i = 0; i < 5; i++) {
                res.addAll(doi(i + index));
            }
        }
        return makeFinalList(res, 3);
    }

    private Couple<ArrayList<Integer>, ArrayList<Integer>> tuquy2() {
        ArrayList<Integer> res = new ArrayList<>();
        // tk3
        res.add((new Poker(2, PokerType.Co)).toInt());
        res.add((new Poker(2, PokerType.Ro)).toInt());
        res.add((new Poker(2, PokerType.Tep)).toInt());
        res.add((new Poker(2, PokerType.Pic)).toInt());

        return makeFinalList(res, 9);
    }

    private Couple<ArrayList<Integer>, ArrayList<Integer>> makeFinalList(ArrayList<Integer> res, int number) {
        ArrayList<Integer> temp = reduceList(make52(), res);
        ArrayList<Integer> temp1 = getRandomList(temp);
        for (int i = 0; i < number; i++) {
            int t = temp1.remove(0);
            res.add(t);
        }
        return new Couple<>(res, temp1);
    }

    private ArrayList<Integer> getRandomList(ArrayList<Integer> currList) {
        ArrayList<Integer> res = new ArrayList<>();
        int len = currList.size();

        for (int i = 0; i < len; i++) {
            int index = getRandomNumber(currList, res);
            currList.remove(index);
        }
        return res;
    }

    private int getRandomNumber(ArrayList<Integer> input,
            ArrayList<Integer> result) {
        int lengh = input.size() - 1;
        int index = (int) Math.round(Math.random() * lengh);
        result.add(input.get(index));
        return index;
    }

    private int getRanDomNumber(ArrayList<Integer> input) {
        int lengh = input.size() - 1;
        int index = (int) Math.round(Math.random() * lengh);
        return input.get(index);
    }

    private PokerType makeRanDomType(ArrayList<Integer> except) {
        ArrayList<Integer> data = new ArrayList<>();
        data.add(1);
        data.add(2);
        data.add(3);
        data.add(4);
        ArrayList<Integer> data1 = reduceList(data, except);
        if (!data1.isEmpty()) {
            return intToPokerType(getRanDomNumber(data1));
        } else {
            return null;
        }
    }

    private PokerType intToPokerType(int t) {
        if (t == 4) {
            return PokerType.Co;
        } else if (t == 3) {
            return PokerType.Ro;
        } else if (t == 1) {
            return PokerType.Pic;
        } else if (t == 2) {
            return PokerType.Tep;
        }
        return null;
    }
}
