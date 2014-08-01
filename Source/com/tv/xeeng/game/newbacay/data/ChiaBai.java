/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.newbacay.data;

import java.util.ArrayList;

import com.tv.xeeng.game.data.Couple;
import com.tv.xeeng.game.data.Triple;

/**
 *
 * @author tuanda
 */
public class ChiaBai {

    ArrayList<Poker> totalCards;     
    public static void main(String[] args) {
        ChiaBai c = new ChiaBai();
        StringBuilder sb = new StringBuilder();
        for (Poker p : c.totalCards) {
            sb/*.append(p.toInt()).append(":")*/.append(p.toIntNew()).append(":").append(p.toString()).append("\n");
        }
        System.out.println(sb);
    }

    public ChiaBai() {
        totalCards = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            for (int j = 1; j < 5; j++) {
                totalCards.add(new Poker(i, PokerType.toPokerType(j)));
            }
        }
    }

    private ArrayList<Poker> randomList() {
        ArrayList<Poker> res = new ArrayList<>();
        int len = totalCards.size();
        while (len > 1) {
            int index = random(0, len - 1);
            Poker p = totalCards.remove(index);
            res.add(p);
            len = totalCards.size();
        }
        res.addAll(totalCards);
        return res;
    }

    private void removeFromTotalCards(Poker[] inp) {
        int i = 0;

        while (i < totalCards.size()) {
            Poker p = totalCards.get(i);
            if (!removeFromTotalCards(p, inp)) {
                i++;
            }
        }
    }

    private boolean removeFromTotalCards(Poker p, Poker[] inp) {
        int len = inp.length;
        for (int i = 0; i < len; i++) {
            if (p.equals(inp[i])) {
                totalCards.remove(p);
                return true;
            }
        }
        return false;
    }

    private int random(int from, int to) {
        int length = to - from;

        return (int) Math.round(Math.random() * length) + from;
    }

    private int random(int from, int to, int except) {
        int length = to - from;
        int index = (int) Math.round(Math.random() * length) + from;
        while (index == except) {
            index = (int) Math.round(Math.random() * length) + from;
        }
        return index;
    }

    private int random(int from, int to, ArrayList<Integer> excepts) {
        int length = to - from;
        int index = (int) Math.round(Math.random() * length) + from;
        while (isInList(index, excepts)) {
            index = (int) Math.round(Math.random() * length) + from;
        }
        return index;
    }

    private boolean isInList(int i, ArrayList<Integer> exc) {
        for (int i1 : exc) {
            if (i == i1) {
                return true;
            }
        }
        return false;

    }

    public Couple<Poker[], ArrayList<Poker>> cheat(int point) { //point == 0 --> sap
        Poker[] inp;
        if (point == 11) {
            inp = sap();
        } else {
            inp = fixPoint(point);
        }
        removeFromTotalCards(inp);
        return new Couple<>(inp, randomList());

    }

    public Triple<Poker[], Poker[], ArrayList<Poker>> anDe() { //point == 0 --> sap
        Poker[][] inp = an();
        for (Poker[] ps : inp) {
            removeFromTotalCards(ps);
        }
        return new Triple<>(inp[0], inp[1], randomList());

    }

    private Poker[] sap() {
        Poker[] res = new Poker[3];
        int rNum = random(1, 9);
        int rEType = random(1, 4);
        int count = 0;
        for (int i = 1; i < 5; i++) {
            if (i != rEType) {
                res[count++] = new Poker(rNum, PokerType.toPokerType(i));
            }
        }
        return res;
    }

    private Poker[][] an() {
        Poker[][] res = new Poker[2][3];
        int point = random(3, 9);
        int less = random(1, point - 1);
        System.out.println("Point:" + point);
        System.out.println("PointLess:" + (point - less));
        int except = 0;
        int[] fisrtCs = triple(point, except);
        if (fisrtCs[0] == fisrtCs[1]) {
            except = fisrtCs[0];
        } else if (fisrtCs[0] == fisrtCs[2]) {
            except = fisrtCs[0];
        } else if (fisrtCs[2] == fisrtCs[1]) {
            except = fisrtCs[1];
        }
        int[] secondCs = triple(point - less, except);
        ArrayList<Poker> excepts = new ArrayList<>();
        res[0][0] = makePoker(fisrtCs[0], excepts);
        excepts.add(res[0][0]);
        res[0][1] = makePoker(fisrtCs[1], excepts);
        excepts.add(res[0][1]);
        res[0][2] = makePoker(fisrtCs[2], excepts);
        excepts.add(res[0][2]);

        res[1][0] = makePoker(secondCs[0], excepts);
        excepts.add(res[1][0]);
        res[1][1] = makePoker(secondCs[1], excepts);
        excepts.add(res[1][1]);
        res[1][2] = makePoker(secondCs[2], excepts);
        excepts.add(res[1][2]);
        return res;
    }

    private Poker makePoker(int num, ArrayList<Poker> excepts) {
        for (int i = 1; i < 5; i++) {
            Poker res = new Poker(num, PokerType.toPokerType(i));
            if (!isInList(res, excepts)) {
                return res;
            }
        }
        return null;
    }

    private boolean isInList(Poker p, ArrayList<Poker> list) {
        for (Poker p1 : list) {
            if (p.equals(p1)) {
                return true;
            }
        }
        return false;
    }

    private int[] triple(int point, int except) {
        int[] res = new int[3];
        res[2] = random(1, 9, except);
        int total2CardsNum = (res[2] >= point) ? (point + 10 - res[2]) : (point - res[2]);
        ArrayList<Integer> excepts = new ArrayList<>();
        excepts.add(except);
        excepts.add(total2CardsNum);
        res[0] = random(1, 9, excepts);
        res[1] = (total2CardsNum < res[0])
                ? ((total2CardsNum + 10) - res[0])
                : total2CardsNum - res[0];
        return res;

    }

    private Poker[] fixPoint(int point) {

        Poker[] res = new Poker[3];
        if (point < 8) {
            point = random(8, 10);
        }
        int endNum = random(1, 9);
        int total2CardsNum = (endNum >= point) ? (point + 10 - endNum) : (point - endNum);
        int first = random(1, 9, total2CardsNum);
        int second = (total2CardsNum < first)
                ? ((total2CardsNum + 10) - first)
                : total2CardsNum - first;

        int rEType = random(1, 4);
        ArrayList<Integer> excepts = new ArrayList<>();
        res[2] = new Poker(endNum, PokerType.toPokerType(rEType));
        excepts.add(rEType);
        if (first == endNum) {
            rEType = random(1, 4, excepts);
            res[0] = new Poker(first, PokerType.toPokerType(rEType));
            if (first == second) { // 3 ong bang nhau
                excepts.add(rEType);
                rEType = random(1, 4, excepts);
                res[1] = new Poker(second, PokerType.toPokerType(rEType));
            } else { // first == end != second
                rEType = random(1, 4);
                res[1] = new Poker(second, PokerType.toPokerType(rEType));
            }
        } else {
            rEType = random(1, 4);
            res[0] = new Poker(first, PokerType.toPokerType(rEType));
            if (first == second) { //f == s != e
                excepts.clear();
                excepts.add(rEType);
                rEType = random(1, 4, excepts);
                res[1] = new Poker(second, PokerType.toPokerType(rEType));
            } else if (second == endNum) { // f != s == e
                rEType = random(1, 4, excepts);
                res[1] = new Poker(second, PokerType.toPokerType(rEType));
            } else { // 3 ong khac nhau
                rEType = random(1, 4);
                res[1] = new Poker(second, PokerType.toPokerType(rEType));
            }
        }
        return res;
    }
}
