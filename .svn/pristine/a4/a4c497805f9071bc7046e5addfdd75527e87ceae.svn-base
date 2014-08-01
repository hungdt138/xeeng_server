package com.tv.xeeng.game.phom.data;

import java.util.ArrayList;
import java.util.Vector;

public class Phom {
    /*
     * true : bo ngang false: bo doc
     */

    public boolean type;

    public Vector<Poker> cards = new Vector<Poker>();
    public Vector<Poker> guis = new Vector<Poker>();

    public boolean is3Doc() {
        return (cards.size() == 3) && (cards.get(0).type == cards.get(1).type);
    }

    public boolean is3Ngang() {
        return (cards.size() == 3) && (cards.get(0).num == cards.get(1).num);
    }

    public boolean is4Doc() {
        return (cards.size() == 4) && (cards.get(0).type == cards.get(3).type);
    }

    public boolean isTuQuy() {
        return (cards.size() == 4) && (cards.get(0).num == cards.get(3).num);
    }

    public boolean is5() {
        return (cards.size() == 5) && (cards.get(0).type == cards.get(4).type);
    }

    public String toString() {
        String res = "";
        if (cards.size() > 0) {
            res += cards.get(0).toInt();
            for (int i = 1; i < cards.size(); i++) {
                res += "#";
                res += cards.get(i).toInt();
            }
        }
        for (int i = 0; i < guis.size(); i++) {
            res += "#";
            res += guis.get(i).toInt();
        }
        return res;
    }

    public boolean isAllowGui(Vector<Poker> gui) throws PhomException {
        Vector<Poker> temp = new Vector<Poker>();
        temp.addAll(cards);
        temp.addAll(guis);
        temp.addAll(gui);
        ArrayList<Poker> temp1 = sortCard(temp);
        if (temp1.size() < 3) {
            return false;
        } else {
            if (temp1.get(0).num == temp1.get(1).num) {
                for (int i = 2; i < temp1.size(); i++) {
                    if (temp1.get(0).num != temp1.get(i).num) {
                        return false;
                    }
                }
                return true;
            } else {
                for (int i = 1; i < temp1.size(); i++) {
                    if ((temp1.get(i - 1).num != temp1.get(i).num - 1)
                            || (temp1.get(i - 1).type != temp1.get(i).type)) {
                        return false;
                    }
                }
                return true;
            }
        }
    }

    private ArrayList<Poker> sortCard(Vector<Poker> cardArray) {
        // convert At to 14
        int cardSize = cardArray.size();
        ArrayList<Poker> sortedCard = new ArrayList<Poker>();
        for (int i = 0; i < cardSize; i++) {
            Poker pk1 = cardArray.get(i);
            sortedCard.add(pk1);
        }
        // sort card
        for (int i = 0; i < cardSize; i++) {
            for (int j = i + 1; j < cardSize; j++) {
                Poker pokerI = sortedCard.get(i);
                Poker pokerJ = sortedCard.get(j);
                if (pokerI.isGreater(pokerJ)) {
                    sortedCard.set(i, pokerJ);
                    sortedCard.set(j, pokerI);
                }
            }
        }

        return sortedCard;
    }

    public void gui(Vector<Poker> gui) throws PhomException {
        if (isAllowGui(gui)) {
            guis.addAll(gui);
        } else {
            throw new PhomException("Gửi không đúng rồi");
        }
    }

    public Phom(Vector<Poker> input) {
        this.cards = input;
    }

    public Phom(ArrayList<Integer> input) throws PhomException {
        Vector<Poker> ps = new Vector<Poker>();
        for (int i : input) {
            ps.add(new Poker(i));
        }
        this.cards = ps;
    }
}
