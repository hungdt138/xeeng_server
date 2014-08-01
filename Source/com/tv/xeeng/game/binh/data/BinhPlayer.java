/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.binh.data;

import com.tv.xeeng.game.data.SimplePlayer;

import java.util.List;

/**
 *
 * @author thanhnvt
 */
public class BinhPlayer extends SimplePlayer {

    private long userId;
    private long userCash;
    private boolean ready; // đã sẵn sàng
    private int userLevel;
    private long userAvatarId;
    private boolean isViewing; // đang ở trạng thái xem

    boolean xepXong;
    boolean binhLung;
    private boolean daChiaBai; // người chơi đã được chia bài, dùng để xử lý khi người chơi rời bàn

    List<BinhPoker> chiDau;
    List<BinhPoker> chiGiua;
    List<BinhPoker> chiCuoi;
    private List<BinhPoker> pokers; // toàn bộ lá bài

    public BinhPlayer(long userId) {
        this.setUserId(userId);
    }

    public String getPokersString() {
        StringBuilder sb = new StringBuilder();
        for (BinhPoker p : getPokers()) {
            sb.append(p.toString()).append(", ");
        }

        return sb.substring(0, sb.length() - 2);
    }

    public boolean isDaChiaBai() {
        return daChiaBai;
    }

    public void setDaChiaBai(boolean daChiaBai) {
        this.daChiaBai = daChiaBai;
    }

    public List<BinhPoker> getPokers() {
        return pokers;
    }

    public void setPokers(List<BinhPoker> pokers) {
        this.pokers = pokers;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserCash() {
        return userCash;
    }

    public void setUserCash(long userCash) {
        this.userCash = userCash;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isReady() {
        return ready;
    }

    @Override
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public long getUserAvatarId() {
        return userAvatarId;
    }

    public void setUserAvatarId(long userAvatarId) {
        this.userAvatarId = userAvatarId;
    }

    public boolean isViewing() {
        return isViewing;
    }

    public void setViewing(boolean isViewing) {
        this.isViewing = isViewing;
    }
}
