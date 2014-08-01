/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.line.data;

/**
 *
 * @author tuanda
 */
public enum Color {

    Red,
    Blue,
    Green,
    Yellow,
    Brown,
    Viollet,
    Orange,
    Tohop,
    Nil;

    public static Color intToColor(int n) {
        switch (n) {
            case 8:
                return Color.Orange;
            case 1:
                return Color.Red;
            case 2:
                return Color.Brown;
            case 3:
                return Color.Viollet;
            case 4:
                return Color.Blue;
            case 5:
                return Color.Yellow;
            case 6:
                return Color.Tohop;
            case 7:
                return Color.Green;
            default:
                return Color.Nil;
        }
    }

    public int toInt() {
        switch (this) {
            case Red:
                return 1;
            case Blue:
                return 4;
            case Green:
                return 7;
            case Yellow:
                return 5;
            case Brown:
                return 2;
            case Viollet:
                return 3;
            case Orange:
                return 8;
            case Tohop:
                return 6;
            default:
                return -1;
        }
    }
}
