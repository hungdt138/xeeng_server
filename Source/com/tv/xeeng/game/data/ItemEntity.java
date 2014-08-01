/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

/**
 *
 * @author tuandavrus
 */
public class ItemEntity {
    private String name;
    private int itemId;
    private int price;
    private int categoryId;
    private int type;
    
    public ItemEntity(int itemId, String name, int price, int categoryId, int type)
    {
        this.itemId = itemId;
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
        this.categoryId = categoryId;
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the itemId
     */
    public int getItemId() {
        return itemId;
    }

    /**
     * @return the price
     */
    public int getPrice() {
        return price;
    }

    /**
     * @return the categoryId
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }
}
