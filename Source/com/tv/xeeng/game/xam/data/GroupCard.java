/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.xam.data;

import java.util.ArrayList;

import com.tv.xeeng.base.business.BusinessException;

/**
 *
 * @author tuanda
 */
public class GroupCard {

    public int number;
    public GroupCardType type;
    public ArrayList<Poker> cards;
    public Poker greatestCard;

    public boolean isHeo() {
        if (number == 1) {
            if (cards.get(0).getNum() == 2 || cards.get(0).getNum() == 15) {
                return true;
            }
        }
        return false;
    }

    public boolean isBoHeo() {
        for (Poker p : cards) {
            if (p.getNum() != 2 && p.getNum() != 15) {
                return false;
            }
        }
        return true;
    }

    public int isTK() {
        if (number == 4 && type == GroupCardType.NGANG) {
            return cards.get(0).getNum();
        }
        return 0;
    }

    public String toString() {
        String s = "";
        s += number + "-";
        s += type.toString();
        s += ":";
        for (Poker p : cards) {
            s += p.toString();
            s += "#";
        }
        return s;
    }

    public GroupCard(ArrayList<Poker> input) throws BusinessException, NullPointerException {
        if (input == null || input.isEmpty()) {
            throw new NullPointerException();
        }

        sort(input);
        int num = input.size();
        if (num == 1) {
            type = GroupCardType.COC;
        } else if (num == 2) {
            if (input.get(0).isDoi(input.get(1))) {
                type = GroupCardType.NGANG;
            } else {
                throw new BusinessException("Not valid!");
            }
        } else if (num == 3) {
            if (input.get(0).isDoi(input.get(1)) && input.get(0).isDoi(input.get(2))) {
                type = GroupCardType.NGANG;
            } else if (checkSanh(input)) {
                type = GroupCardType.DOC;
            } else {
                throw new BusinessException("Not valid!");
            }
        } else if (num == 4) {
            if (input.get(0).isDoi(input.get(1)) && input.get(0).isDoi(input.get(2)) && input.get(0).isDoi(input.get(3))) {
                type = GroupCardType.NGANG;
            } else if (checkSanh(input)) {
                type = GroupCardType.DOC;
            } else {
                throw new BusinessException("Not valid!");
            }
        } else {
            if (checkSanh(input)) {
                type = GroupCardType.DOC;
            } else {
                throw new BusinessException("Not valid!");
            }
        }

        number = num;
        cards = input;
        greatestCard = cards.get(num - 1);
    }

    public static void main(String[] args) {
        //3#17#5#45#46#47
        int[] input = {3, 17, 5, 45, 46, 47};
        ArrayList<Poker> ps = new ArrayList<Poker>();
        for (int i : input) {
            ps.add(new Poker(i));
        }
        try {
            GroupCard g = new GroupCard(ps);
            System.out.println(g.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private boolean checkSanh(ArrayList<Poker> input) throws BusinessException {
        int num = input.size();
        boolean res = true;

        for (int i = num - 1; i > 0; i--) {
            if ((i == num - 1)
                    && (input.get(num - 1).getNum() == 15)) {
                res = false;
                break;
            }
            if (input.get(i).getNum() != input.get(i - 1).nextCard()) {
                res = false;
                break;
            }
        }
        if (!res) {
            sort1(input);
            for (int i = 0; i < num - 1; i++) {
                if (input.get(i + 1).getNum() != input.get(i).nextCard()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isValidPlay(GroupCard other) {
        //System.out.println("Mine:"+this.toString());
        //System.out.println("Other:"+other.toString());
        return ((this.type == other.type && this.number == other.number
                && this.greatestCard.isGreater(other.greatestCard))
                || this.isTK() > 0 && other.isHeo()); // tu quy chat 2
    }

    private void sort1(ArrayList<Poker> playingCards) {
        int len = playingCards.size();
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                if (playingCards.get(i).isGreater1(playingCards.get(j))) {
                    Poker temp = playingCards.get(i);
                    playingCards.set(i, playingCards.get(j));
                    playingCards.set(j, temp);
                }
            }
        }
    }

    private void sort(ArrayList<Poker> input) {
        int len = input.size();
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                if (input.get(i).isGreater(input.get(j))) {
                    Poker temp = input.get(i);
                    input.set(i, input.get(j));
                    input.set(j, temp);
                }
            }
        }
    }
}

enum GroupCardType {

    COC,
    NGANG,
    DOC;

    public String toString() {
        switch (this) {
            case COC:
                return "coc";
            case NGANG:
                return "ngang";
            case DOC:
                return "doc";
            default:
                return "unknown";
        }
    }
}
