package com.tv.xeeng.game.poker.data;

import com.tv.xeeng.game.data.SimplePlayer;
import com.tv.xeeng.game.data.SimpleTable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author thangtd created 25/02/2014
 */
public class PokerTable extends SimpleTable {

    public ArrayList<PokerPlayer> playings = new ArrayList<>();
    private ArrayList<PokerPlayer> waitings = new ArrayList<>();

    public PokerTable(PokerPlayer owner, long money, long matchID) {
        this.matchID = matchID;
        this.owner = owner;
        this.firstCashBet = money;

        logdir = "poker_log";
    }

    public boolean containPlayer(long id) {
        for (int i = 0; i < playings.size(); i++) {
            if (playings.get(i).id == id) {
                return true;
            }
        }

        return false;
    }

    // player join
    public void join(PokerPlayer player) throws PokerException {
        // Người chơi mới vào phòng mặc định sẽ có vai trò là người quan sát
        player.setLastActivated(System.currentTimeMillis());
        player.isMonitor = true;
        ((PokerPlayer) player).isObserve = true;
        waitings.add(player);

        outCodeSB.append("player: ").append(player.username).append(" join").append(NEW_LINE);
    }

    public boolean isFullTable() {
        return playings.size() >= getMaximumPlayer();
    }

    public int tableSize() {
        return playings.size() + waitings.size();
    }

    @Override
    public List<? extends SimplePlayer> getNewPlayings() {
        return playings;
    }

    @Override
    public List<? extends SimplePlayer> getNewWaitings() {
        return waitings;
    }

    /**
     * *************************************************
     */
    public void remove(PokerPlayer player) throws PokerException {
        try {
//			getOutCodeSB().append("Remove player: ").append(player.id).append(NEW_LINE);
            getOutCodeSB().append(String.format("(kick người chơi %s)", player.username)).append(NEW_LINE).append(NEW_LINE);

            for (PokerPlayer p : playings) {
                if (p.id == player.id) {
                    playings.remove(p);
                    return;
                }
            }

            for (PokerPlayer p : waitings) {
                if (p.id == player.id) {
                    waitings.remove(p);
                    return;
                }
            }

        } catch (Exception e) {
            if (player != null) {
                mLog.error(concatString(turnInfo(), " : ", player.username));
            } else {
                mLog.error(concatString(turnInfo(), " : remove Null"));
            }

            throw new PokerException(e.getMessage());
        }
    }

    public String turnInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("---------turn info--------------------------").append(NEW_LINE);
//		try {
//			sb.append("turn : ").append(turn / playings.size() + 1)
//					.append(" id : ").append(currentPlayer.id)
//					.append(" userName: ").append(currentPlayer.username)
//					.append(" current index: ")
//					.append(indexOfPlayer(currentPlayer))
//					.append(" rescard size: ").append(restCards.size())
//					.append(NEW_LINE);
//		} catch (PokerException ex) {
//			mLog.error(ex.getMessage(), ex.getStackTrace());
//		}
        sb.append("--------------------------------------").append(NEW_LINE);

        return sb.toString();
    }

    private String concatString(String... input) {
        StringBuilder sb = new StringBuilder();
        int len = input.length;
        for (int i = 0; i < len; i++) {
            String s = input[i];
            sb.append(s);
        }
        return sb.toString();
    }
}
