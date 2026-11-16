/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.handlers.impl;

import com.cevapinxile.minddirector.handlers.EditStageHandler;
import com.cevapinxile.minddirector.model.EditStage;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

/**
 *
 * @author denijal
 */
public class PlayerSelectionHandler implements EditStageHandler {
    
    private Integer index = -1;

    @Override
    public EditStage handleClick(int keyCode) {
        if (NativeKeyEvent.VC_1 <= keyCode && keyCode <= NativeKeyEvent.VC_4) {
            this.index = keyCode - NativeKeyEvent.VC_1;
            System.out.println("Hover player: "+index);
            //TODO: [UI] Put transparent border around the player
        }
        
        if (keyCode == NativeKeyEvent.VC_ESCAPE){
            if(index == -1) 
                return null;
            System.out.println("Unhover player: "+index);
            index = -1;
            //TODO: [UI] Remove transparent border around the player
        }
        
        if(keyCode == NativeKeyEvent.VC_ENTER){
            System.out.println("Select player: "+index);
            //TODO: [UI] Put full border around the player
            EditStage.CARD_SELECTION.getHandler().enter(index);
            return EditStage.CARD_SELECTION;
        }
        return EditStage.PLAYER_SELECTION;
    }

    @Override
    public void enter(Object args) {
        this.index = -1;
    }


}
