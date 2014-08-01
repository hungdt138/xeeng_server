/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

/**
 *
 * @author tuandavrus
 */
public class PositionEntity {
    private int currPosition;   // where do u? album, album detail, blog ...
    private long valuePosition; // object record id in queue
    
    public PositionEntity(int currPosition, long valuePosition)
    {
        this.currPosition = currPosition;
        this.valuePosition = valuePosition;
    }
    
    public PositionEntity()
    {
        currPosition = 0;
        valuePosition = 0;
    }

    /**
     * @return the currPosition
     */
    public int getCurrPosition() {
        return currPosition;
    }

    /**
     * @param currPosition the currPosition to set
     */
    public void setCurrPosition(int currPosition) {
        this.currPosition = currPosition;
    }

    /**
     * @return the valuePosition
     */
    public long getValuePosition() {
        return valuePosition;
    }

    /**
     * @param valuePosition the valuePosition to set
     */
    public void setValuePosition(long valuePosition) {
        this.valuePosition = valuePosition;
    }

    
    
}
