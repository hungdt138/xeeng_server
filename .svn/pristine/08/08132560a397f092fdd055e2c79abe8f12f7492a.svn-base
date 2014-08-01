package com.tv.xeeng.game.trieuphu.data;

import java.util.ArrayList;

import java.util.AbstractList;
import java.util.List;
import java.util.Random;

import com.tv.xeeng.base.business.BusinessException;
import com.tv.xeeng.game.data.SimplePlayer;

public class TrieuPhuPlayer extends SimplePlayer {

    public ArrayList<Integer> currentQuestions;
    public boolean currentAnswer = false;
    public int currentAnswerPos = 0;
    public boolean preAnswer = false;
    public boolean help50 = true;
    public boolean helpCall = true;
    public boolean helpAudit = true;
    public int cauhoi = 0;
    public long point = 0;
    public long timeAnswer = 1000000;
    private List<Integer> lstVariants;
    private int achivementQuestion = 0;
    public boolean isSuper = false;
    public boolean isAnswer = false;
    public TrieuPhuTable table;

    public boolean isWin(TrieuPhuPlayer other) {
        if (isOut) {
            if (other.isOut) {
                return true;
            } else {
                return false;
            }
        } else {
            if (other.isOut) {
                return true;
            } else {
                return (this.point > other.point) ? true
                        : ((this.point < other.point) ? false
                        : ((this.cauhoi > other.cauhoi) ? true
                        : ((this.cauhoi < other.cauhoi) ? false
                        : (this.numberHelp > other.numberHelp ? true
                        : (this.numberHelp > other.numberHelp ? false
                        : (this.timeAnswer > other.timeAnswer ? false
                        : true))))));
            }
        }

    }

    public int isNewWin(TrieuPhuPlayer other) {
        if (isOut) {
            if (other.isOut) {
                return 1;
            } else {
                return -1;
            }
        }

        if (other.isOut) {
            return 1;
        }

        if (this.point > other.point) {
            return 1;
        }

        if (this.point < other.point) {
            return -1;
        }

        if (this.cauhoi > other.cauhoi) {
            return 1;
        }

        if (this.cauhoi < other.cauhoi) {
            return -1;
        }

        if (this.numberHelp > other.numberHelp) {
            return 1;
        }
        if (this.numberHelp > other.numberHelp) {
            return -1;
        }

        return 0;
    }

    public int numberHelp = 3;

    public void getNewRound() {
        if (!isStop) {
            currentAnswer = false;
            isAnswer = false;
            lstVariants = new ArrayList<Integer>();
            for (int i = 1; i < 5; i++) {
                getLstVariants().add(i);
            }
        }
        currentAnswerPos = 0;
    }

    public void xitop() throws BusinessException {
        if (isStop) {
            return;// throw new BusinessException("Ban da dung cuoc choi roi!");
        }
        if (isAnswer) {
            throw new BusinessException("Ban da tra loi roi!");
        }
        if (isMonitor) {
            throw new BusinessException("Ban dang xem, khong duoc tra loi!");
        }
        isAnswer = true;
        currentAnswer = false;
        isStop = true;
        currentAnswerPos = 0;
        // System.out.println("Xin nghi#"+this.point + ":" + this.cauhoi);
    }

    public void answer(boolean isTrue, long timeAnswer, int pos) throws BusinessException {
        if (isStop) {
            return;// throw new BusinessException("Ban da dung cuoc choi roi!");
        }
        if (isAnswer) {
            throw new BusinessException("Ban da tra loi roi!");
        }
        if (isMonitor) {
            throw new BusinessException("Ban dang xem, khong duoc tra loi!");
        }

        currentAnswerPos = pos;
        isAnswer = true;
        currentAnswer = isTrue;
        preAnswer = isTrue;
        if (currentAnswer) {
            cauhoi++;
            point = pointToMoney();
            this.timeAnswer = timeAnswer;
        } else {
            point = wrongToMoney();
            isStop = true;
        }
        // System.out.println("Tra loi# "+this.point + ":" + this.cauhoi);
    }

    /*
     * 1: 50/50 2: call 3: Audit
     */
    private int getOneHelp(Random rand, int answer) {
        int variantSize = getLstVariants().size();
        //remove one not true variant
        int exceptVariant = 0;

        int startIndex = (int) (Math.abs(rand.nextLong() % variantSize));
        int count = startIndex;
        int maxExcept = variantSize / 2;
        int countExcept = 0;
        List<Integer> exceptVariants = new ArrayList<Integer>();
        do {
            int variant = getLstVariants().get(count);
            if (variant != answer) {

                exceptVariant = variant;
                exceptVariants.add(exceptVariant);
                countExcept++;
                if (countExcept >= maxExcept) {
                    break;
                }

            }
            count = (count + 1) % variantSize;
        } while (count != startIndex);

        int exceptSize = exceptVariants.size();
        List<Integer> lstResults = new ArrayList<Integer>();
        for (int i = 0; i < variantSize; i++) {
            boolean flag = true;
            for (int j = 0; j < exceptSize; j++) {
                if (getLstVariants().get(i) == exceptVariants.get(j)) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                lstResults.add(getLstVariants().get(i));
            }
        }

        int resultSize = lstResults.size();
        rand = new Random(System.currentTimeMillis() * resultSize);
        int indexResult = (int) (Math.abs(rand.nextLong() % resultSize));

        return lstResults.get(indexResult);
    }

    public int help(int type, int answer) throws BusinessException {

        if (numberHelp < 1) {
            throw new BusinessException("Bạn đã dùng hết trợ giúp rồi nhé");
        }
        int variantSize = getLstVariants().size();
        Random rand = new Random(System.currentTimeMillis());

        switch (type) {
            case 1:
                if (help50) {
                    numberHelp--;
                    help50 = false;

                    List<Integer> lstRemoveVariants = new ArrayList<Integer>();

                    for (int i = 2; i < variantSize; i++) {
                        int startIndex = (int) (Math.abs(rand.nextLong() % variantSize));
                        int count = startIndex;
                        do {
                            int variant = getLstVariants().get(count);
                            if (variant != answer) {
                                boolean flagRemove = true;
                                for (int j = 0; j < lstRemoveVariants.size(); j++) {
                                    if (lstRemoveVariants.get(j) == variant) {
                                        //this variant was removed
                                        flagRemove = false;
                                        break;
                                    }
                                }

                                if (flagRemove) {
                                    lstRemoveVariants.add(variant);
                                    break;
                                }
                            }
                            count = (count + 1) % variantSize;
                        } while (count != startIndex);

                    }
                    int removeSize = lstRemoveVariants.size();
                    for (int i = 0; i < removeSize; i++) {
                        getLstVariants().remove(lstRemoveVariants.get(i));
                    }

                    return 1;
                } else {
                    throw new BusinessException( "Bạn đã dùng quyền trợ giúp này rồi!");
                }
            case 3:
                if (helpCall) {
                    numberHelp--;
                    helpCall = false;

                    return getOneHelp(rand, answer);
                } else {
                    throw new BusinessException( "Bạn đã dùng quyền trợ giúp này rồi!");
                }
            case 2:
                if (helpAudit) {
                    numberHelp--;
                    helpAudit = false;
                    return getOneHelp(rand, answer);
                } else {
                    throw new BusinessException( "Bạn đã dùng quyền trợ giúp này rồi!");
                }

            default:
                break;
        }

        return 0;
    }

    public TrieuPhuPlayer(long id, long minBet, boolean isObserve) {
        this.id = id;
        this.isMonitor = isObserve;
        moneyForBet = minBet;
        isGiveUp = false;
        isStop = false;
        currentQuestions = new ArrayList<Integer>();
        currentAnswerPos = 0;
    }

    public void reset() {
        setWonMoney(0);
        cauhoi = 0;
        isGiveUp = false;
        isOut = false;
        isStop = false;
        help50 = true;
        helpCall = true;
        helpAudit = true;
        numberHelp = 3;
        isMonitor = false;
        currentAnswer = false;
        preAnswer = false;
        point = 0;
        currentAnswerPos = 0;
        achivementQuestion = 0;
        isSuper = false;
        // currentQuestions.clear();
    }

    public void autoPlay() {
        if (!isAnswer) {
            currentAnswer = false;
            preAnswer = false;
            isAnswer = true;
            isStop = true;
            point = wrongToMoney();
            currentAnswerPos = -1;
        }
    }

    private long pointToMoney() {
        achivementQuestion = cauhoi;
        switch (cauhoi) {
            case 1:
                return moneyForBet;
            case 2:
                return moneyForBet * 2;
            case 3:
                return moneyForBet * 3;
            case 4:
                return moneyForBet * 5;
            case 5:
                return moneyForBet * 10;
            case 6:
                return moneyForBet * 20;
            case 7:
                return moneyForBet * 36;
            case 8:
                return moneyForBet * 60;
            case 9:
                return moneyForBet * 90;
            case 10:
                return moneyForBet * 150;
            case 11:
                return moneyForBet * 250;
            case 12:
                return moneyForBet * 350;
            case 13:
                return moneyForBet * 500;
            case 14:
                return moneyForBet * 800;
            case 15:
                return moneyForBet * 1200;
            default:
                return 0;
        }
    }

    private long wrongToMoney() {
        switch (cauhoi) {
            case 1:
            case 2:
            case 3:
            case 4:
                achivementQuestion = 0;
                return 0;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                achivementQuestion = 5;
                return moneyForBet * 10;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                achivementQuestion = 10;
                return moneyForBet * 150;
            default:
                return 0;
        }
    }

    /**
     * @return the lstVariants
     */
    public List<Integer> getLstVariants() {
        return lstVariants;
    }

    /**
     * @return the achivementQuestion
     */
    public int getAchivementQuestion() {
        return achivementQuestion;
    }
}
