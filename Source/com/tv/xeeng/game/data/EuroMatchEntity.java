/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

import java.util.Date;

/**
 *
 * @author tuanda
 */
public class EuroMatchEntity {
    private int id;
    private String teamOne;
    private String teamTwo;
    private double win;
    private double lost;
    private double air;
    private int point;
    private double bonus;
    
    private double money;//all bet for this match   
    private Date time;

    public EuroMatchEntity(int id, String teamOne, String teamTwo, double win, double lost, double air)
    {
        this.id = id;
        this.teamOne = teamOne;
        this.teamTwo = teamTwo;
        this.win = win;
        this.lost = lost;
        this.air = air;
    }
    
    public EuroMatchEntity(int id, String teamOne, String teamTwo, double money)
    {
        this.id = id;
        this.teamOne = teamOne;
        this.teamTwo = teamTwo;
        this.money = money;
    }
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the teamOne
     */
    public String getTeamOne() {
        return teamOne;
    }

    /**
     * @param teamOne the teamOne to set
     */
    public void setTeamOne(String teamOne) {
        this.teamOne = teamOne;
    }

    /**
     * @return the teamTwo
     */
    public String getTeamTwo() {
        return teamTwo;
    }

    /**
     * @param teamTwo the teamTwo to set
     */
    public void setTeamTwo(String teamTwo) {
        this.teamTwo = teamTwo;
    }

    /**
     * @return the win
     */
    public double getWin() {
        return win;
    }

    /**
     * @param win the win to set
     */
    public void setWin(double win) {
        this.win = win;
    }

    /**
     * @return the lost
     */
    public double getLost() {
        return lost;
    }

    /**
     * @param lost the lost to set
     */
    public void setLost(double lost) {
        this.lost = lost;
    }

    /**
     * @return the air
     */
    public double getAir() {
        return air;
    }

    /**
     * @param air the air to set
     */
    public void setAir(double air) {
        this.air = air;
    }

    /**
     * @return the money
     */
    public double getMoney() {
        return money;
    }

    /**
     * @param money the money to set
     */
    public void setMoney(double money) {
        this.money = money;
    }

    /**
     * @return the point
     */
    public int getPoint() {
        return point;
    }

    /**
     * @param point the point to set
     */
    public void setPoint(int point) {
        this.point = point;
    }

    /**
     * @return the bonus
     */
    public double getBonus() {
        return bonus;
    }

    /**
     * @param bonus the bonus to set
     */
    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    /**
     * @return the time
     */
    public Date getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(Date time) {
        this.time = time;
    }
    
    
            
}
