/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.phom.data;

import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author tuanda
 */
public class Duty {

    public PhomDutyType duty = PhomDutyType.NO_DUTY;

    /*public static void main(String[] args){
     Duty d = new Duty();
		
     }*/
    public int getType() {
        return duty.getValue();
    }

    public int getBonusTime() {
        return duty.bonusTime();
    }

    public Duty() {
        duty = PhomDutyType.NO_DUTY;
//        Random rand = new Random(System.currentTimeMillis());
//        int t = (int) (Math.abs(rand.nextLong() % 10));
//        if (t == 4) {
//            t = 6;
//        }
//
//        for (PhomDutyType p : PhomDutyType.values()) {
//            if (p.getValue() == t) {
//                duty = p;
//                break;
//            }
//        }
        
        //System.out.println(t + ":" + duty.toString());
    }

    public int checkDuty(PhomPlayer player) {
        if (duty.checkDuty(player)) {
            return getBonusTime();
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return duty.toString();
    }
}

enum PhomDutyType {
    NO_DUTY(0), ABC(6), AAA(5), ABCD(7), AAAA(8), ABCDE(9), ALL(1), GUI_1(2), GUI_2(3), GUI_3(4);
    private int id;

    PhomDutyType(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }

    public boolean checkDuty(PhomPlayer player) {
        ArrayList<Phom> data = player.phoms;
        Vector<Poker> guis = player.offeringCards;
        switch (this) {
            case GUI_1:
                return (guis.size() == 1);
            case GUI_2:
                return (guis.size() == 2);
            case GUI_3:
                return (guis.size() == 3);

            case NO_DUTY:// ko nhiem vu
                return false;
            case ABC: {// phom 3 doc
                for (Phom p : data) {
                    if (p.is3Doc()) {
                        return true;
                    }
                }
                return false;
            }
            case AAA:// phom 3 ngang
                for (Phom p : data) {
                    if (p.is3Ngang()) {
                        return true;
                    }
                }
                return false;
            case ABCD:// phom 4
                for (Phom p : data) {
                    if (p.is4Doc()) {
                        return true;
                    }
                }
                return false;
            case AAAA:// tu quy
                for (Phom p : data) {
                    if (p.isTuQuy()) {
                        return true;
                    }
                }
                return false;
            case ABCDE: // phom 5 cay
                for (Phom p : data) {
                    if (p.is5()) {
                        return true;
                    }
                }
                return false;
            case ALL: // All
                return true;
            default:
                return false;
        }
    }

    public String toString() {
        String res = "";
        switch (this) {
            case NO_DUTY:// ko nhiem vu
                break;
            case ABC:// phom 3 doc
                res += "(Hoàn thành nhiệm vụ một phỏm dọc 3 cây";
                break;
            case GUI_1:// phom 3 doc
                res += "(Hoàn thành nhiệm vụ gửi một cây";
                break;
            case GUI_2:// phom 3 doc
                res += "(Hoàn thành nhiệm vụ gửi hai cây";
                break;
            case GUI_3:// phom 3 doc
                res += "(Hoàn thành nhiệm vụ gửi ba cây";
                break;
            case AAA:// phom 3 ngang
                res += "(Hoàn thành nhiệm vụ một phỏm ngang 3 cây";
                break;
            case ABCD:// phom 4
                res += "(Hoàn thành nhiệm vụ một phỏm dọc 4 cây";
                break;
            case AAAA:// tu quy
                res += "(Hoàn thành nhiệm vụ một phỏm tứ quý";
                break;
            case ABCDE: // phom 5 cay
                res += "(Hoàn thành nhiệm vụ một phỏm 5 cây";
                break;
            case ALL: // All
                res += "(Hoàn thành nhiệm vụ nhân đôi tất cả";
                break;
            default:
                break;
        }
        if (id > 0) {
            res += " nhân " + bonusTime() + ")";
        }
        return res;
    }

    public int bonusTime() {
        switch (this) {
            case NO_DUTY:// ko nhiem vu
                return 1;
            case GUI_1:// phom 3 doc
                return 2;
            case GUI_2:// phom 3 doc
                return 3;
            case GUI_3:// phom 3 doc
                return 4;
            case ABC:// phom 3 doc
                return 2;
            case AAA:// phom 3 ngang
                return 2;
            case ABCD:// phom 4
                return 3;
            case AAAA:// tu quy
                return 4;
            case ABCDE: // phom 5 cay
                return 4;
            case ALL:
                return 2;
            default:
                return 1;
        }
    }
}
