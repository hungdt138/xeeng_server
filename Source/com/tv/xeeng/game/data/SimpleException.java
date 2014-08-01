package com.tv.xeeng.game.data;

@SuppressWarnings("serial")
public class SimpleException extends Exception {

    public int game;
    public String msg;

    public SimpleException(String str) {
        this.msg = str;
    }

    public SimpleException(int g, String str) {
        this.game = g;
        this.msg = str;
    }

    @Override
    public String getMessage() {
        return this.msg;
    }
}
