/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.pikachu.datta;

import com.tv.xeeng.game.line.data.LinePlayer;

/**
 *
 * @author tuanda
 */
public class PikachuPlayer extends LinePlayer{
	public int hint = 0;
	public int revert = 0;
    public int point = 0;
    
    public PikachuPlayer(long uid) {
        super(uid);
    }
    
    @Override
    public void reset(){
        super.reset();
        point = 0;
        hint = 0;
        revert = 0;
    }
}
