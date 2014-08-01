/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.binh.data;

import com.tv.xeeng.base.business.BusinessException;
import com.tv.xeeng.base.common.BlahBlahUtil;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.game.data.Messages;
import com.tv.xeeng.game.data.SimpleException;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.phom.data.PhomException;
import com.tv.xeeng.game.room.Room;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author thanhnvt
 */
public class BinhTable extends SimpleTable {

    public static final Logger logger = LoggerContext.getLoggerFactory().getLogger(BinhTable.class);

    private List<BinhPlayer> playings = new ArrayList<>();
    private List<BinhPlayer> waitings = new ArrayList<>();
    private int indexOfStartPlayer; // người chia bài, dùng khi tính điểm

    @Override
    public BinhPlayer findPlayer(long uid) throws SimpleException {
        throw new NotImplementedException();
    }

    @Override
    public boolean isFullTable() {
        throw new NotImplementedException();
    }

    public int getTableSize() {
        throw new NotImplementedException();
    }

    public void join(BinhPlayer p) throws BusinessException {
        if (isFullTable()) {
            throw new BusinessException(Messages.FULL_PLAYER_MSG);
        }

        if (isPlaying) {
            waitings.add(p);
            p.isMonitor = true;
        } else {
            playings.add(p);
            p.isMonitor = false;
        }

        outCodeSB.append("player: ").append(p.username).append(" join").append(NEW_LINE);
        logMini.append(BlahBlahUtil.getLogString(String.format("[%s] vào bàn", p.username)));
    }

    public void start() throws BinhException {
        int playingSize = this.playings.size();

        lastActivated = System.currentTimeMillis();
        this.isPlaying = false;
        matchNum++;
//        if (waitings.size() > 0) {
            resetPlayers();
//        }

        if (playingSize > 1) {
            this.isPlaying = true;

            String playerList = "Danh sách người chơi: ";
            for (int i = 0; i < playingSize; i++) {
                playerList += String.format("[%s (%d)] ", playings.get(i).username, playings.get(i).id);
            }

            chiaBai();

            getOutCodeSB().append(BlahBlahUtil.getLogString(playerList));
            logMini.append(BlahBlahUtil.getLogString(playerList));
        } else {
            throw new BinhException("Chưa có người chơi cùng");
        }
    }

    /**
     * Khởi tạo danh sách người chơi trước khi bắt đầu ván mới (kick người chơi không đủ tiền, chuyển những người từ trạng thái đang chờ sang trạng thái đang chơi, etc...)
     */
    public void resetPlayers() {
        List<BinhPlayer> removedPlayer = new ArrayList<>();
        int playingSize = this.playings.size();

        for (int i = 0; i < playingSize; i++) {
            BinhPlayer p = this.playings.get(i);
            if (p.isOut || p.notEnoughMoney()) {
                removedPlayer.add(p);
            }
        }

        int removeSize = removedPlayer.size();
        for (int i = 0; i < removeSize; i++) {
            playings.remove(removedPlayer.get(i));
        }

        this.playings.addAll(this.waitings);

        this.waitings.clear();
        this.isPlaying = false;
    }

    public BinhTable(BinhPlayer owner, String name, long cash, Room room) {
        this.setRoom(room);
        this.owner = owner;
        this.name = name;
        this.firstCashBet = cash;
    }

    /**
     * Kiểm tra người chơi có đang nằm trong bàn hay không
     * @param id
     * @return
     */
    public boolean containsPlayer(long id) {
        int len = playings.size();
        for (int i = 0; i < len; i++) {
            if (playings.get(i).id == id) {
                return true;
            }
        }
        len = waitings.size();
        for (int i = 0; i < len; i++) {
            if (waitings.get(i).id == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Kick người chơi.
     * @param player
     * @throws PhomException
     */
    public void remove(BinhPlayer player) throws BinhException {
        try {
            getOutCodeSB().append(String.format("(Kick người chơi %s)", player.username)).append(NEW_LINE).append(NEW_LINE);
            logMini.append(BlahBlahUtil.getLogString(String.format("Kick [%s]", player.username)));

            int playingSize = this.playings.size();

            for (int i = 0; i < playingSize; i++) {
                BinhPlayer p = this.playings.get(i);
                if (p.id == player.id) {
                    playings.remove(p);
                    return;
                }
            }

            int waitingSize = this.waitings.size();

            for (int i = 0; i < waitingSize; i++) {
                BinhPlayer p = this.waitings.get(i);
                if (p.id == player.id) {
                    waitings.remove(p);
                    return;
                }
            }

        } catch (Exception e) {
            mLog.error("Lỗi", e);

            throw new BinhException(e.getMessage());
        }
    }

    /**
     * Chia bài cho người chơi.
     */
    private void chiaBai() {
        outCodeSB.append(BlahBlahUtil.getLogString("Chia bài"));

        int numberPlayers = this.playings.size();
        checkBotUser();
        ArrayList<ArrayList<BinhPoker>> nhomBais = taoNhomBai(numberPlayers);

        for (int i = 0; i < numberPlayers; i++) {
            BinhPlayer player = this.playings.get(i);

            if (!player.isOut) {
                player.setDaChiaBai(true);
            }

            player.setPokers(nhomBais.get(i));

            String s = "";
            for (BinhPoker p : nhomBais.get(i)) {
                s += p.toString() + ", ";
            }

            outCodeSB.append(BlahBlahUtil.getLogString(String.format("    - [%s]: %s", player.username, player.getPokersString())));
        }
    }

    /**
     * Tạo các nhóm bài.
     *
     * @param numberPlayers
     * @return List gồm các mảng Poker, mỗi mảng ứng với một người chơi
     */
    private ArrayList<ArrayList<BinhPoker>> taoNhomBai(int numberPlayers) {
        ArrayList<ArrayList<BinhPoker>> res = new ArrayList<>();
        int[] randomDeck = generateRandomDeck();

        for (int i = 0; i < numberPlayers; i++) {
            ArrayList<BinhPoker> p = new ArrayList<>();
            for (int j = 0; j < 13; j++) {
                p.add(new BinhPoker(randomDeck[13 * i + j]));
            }
            res.add(p);
        }

        return res;
    }

    /**
     * Tạo bộ bài ngẫu nhiên.
     *
     * @return
     */
    private int[] generateRandomDeck() {
        int[] randomDeck = shuffleArray(generateStandardDeck());

        return randomDeck;
    }

    /**
     * Tạo bộ bài chuẩn.
     *
     * @return
     */
    private int[] generateStandardDeck() {
        int[] deck = new int[52];
        for (int i = 0; i < 52; i++) {
            deck[i] = i;
        }

        return deck;
    }

    /**
     * Trộn ngẫu nhiên các phần tử của mảng.
     *
     * Implementing Fisher–Yates shuffle http://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
     */
    private int[] shuffleArray(int[] ar) {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);

            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }

        return ar;
    }

    public List<BinhPlayer> getPlayings() {
        return playings;
    }
}
