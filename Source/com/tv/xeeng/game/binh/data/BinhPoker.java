/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.binh.data;

/**
 *
 * @author thanhnvt
 */
public class BinhPoker {
    private int rank; // giá trị: 1 (A), 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 (J), 12 (Q), 13 (K)
    private int suite; // chất: rô, cơ, bích, tép
    
    public BinhPoker(int poker) {
        this.rank = (int)Math.floor(poker / 4);
        this.suite = poker % 4;
    }

    public int toInt() {
        return (suite - 1) * 13 + this.rank;
    }


    @Override
    public String toString() {
        String res = "";

        if (rank == 1) {
            res += "A";
        } else if (rank == 11) {
            res += "J";
        } else if (rank == 12) {
            res += "Q";
        } else if (rank == 13) {
            res += "K";
        } else {
            res += String.valueOf(rank);
        }

        res += " " + pokerTypeToString();
        return res;
    }

    private String pokerTypeToString() {
        if (suite == 0) {
            return "Bích";
        } else if (suite == 1) {
            return "Tép";
        } else if (suite == 2) {
            return "Rô";
        } else if (suite == 3) {
            return "Cơ";
        }
        return "";
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getSuite() {
        return suite;
    }

    public void setSuite(int suite) {
        this.suite = suite;
    }
}
