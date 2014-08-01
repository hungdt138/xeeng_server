/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.daugia;

/**
 *
 * @author tuanda
 */
public enum BidType {

    NGUOC,
    XUOI,
    CAONHAT;

    public String toStringMessage() {
        switch (this) {
            case NGUOC:
                return "1";
            case XUOI:
                return "2";
            case CAONHAT:
                return "3";
            default: return "-1";    
        }
    }
}
