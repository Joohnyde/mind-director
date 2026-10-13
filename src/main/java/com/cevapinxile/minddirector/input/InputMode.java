/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.cevapinxile.minddirector.input;

import com.cevapinxile.minddirector.handlers.EventHandler;
import com.cevapinxile.minddirector.handlers.impl.*;

/**
 *
 * @author denijal
 */
public enum InputMode {
    ALL(new AllHandler()),
    CARDS(new CardsHandler()),
    DUEL(new DuelHandler()),
    IDLE(new IdleHandler()),
    NONE(new NoneHandler()),
    READ(new ReadHandler()),
    SINGLE(new SingleHandler());
    
    private final EventHandler handler;
    
    private InputMode(EventHandler handler) {
        this.handler = handler;
    }
    
    public EventHandler getHandler() {
        return handler;
    }
}
