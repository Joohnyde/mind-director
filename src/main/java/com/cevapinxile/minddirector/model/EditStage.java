/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.model;

import com.cevapinxile.minddirector.handlers.EditStageHandler;
import com.cevapinxile.minddirector.handlers.impl.CardSelectionHandler;
import com.cevapinxile.minddirector.handlers.impl.NumberEnteringHandler;
import com.cevapinxile.minddirector.handlers.impl.PlayerSelectionHandler;

/**
 *
 * @author denijal
 */
public enum EditStage{
    PLAYER_SELECTION(new PlayerSelectionHandler()),
    CARD_SELECTION(new CardSelectionHandler()),
    NUMBER_ENTERING(new NumberEnteringHandler());
     
    private final EditStageHandler handler;
    
    private EditStage(EditStageHandler handler) {
        this.handler = handler;
    }
    
    public EditStageHandler getHandler() {
        return handler;
    }
}
