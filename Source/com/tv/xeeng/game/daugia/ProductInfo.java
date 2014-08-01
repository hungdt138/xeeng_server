/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.daugia;

import java.util.ArrayList;

import com.tv.xeeng.game.data.AIOConstants;

/**
 *
 * @author tuanda
 */
public class ProductInfo {
    public ArrayList<String> image;
    public String name;
    public String desc;
    public long price;

    public ProductInfo() {
    }

    public ProductInfo(ArrayList<String> image, String name, String desc, long price) {
        this.image = image;
        this.name = name;
        this.desc = desc;
        this.price = price;
    }
    public String toStringMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.name).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(this.desc).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(this.price);//.append(AIOConstants.SEPERATOR_BYTE_1);
        
        if(!this.image.isEmpty()) {
            sb.append(AIOConstants.SEPERATOR_BYTE_1);
            for(String img : image) {
                sb.append(img).append(AIOConstants.SEPERATOR_BYTE_1);
            }
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }
}
