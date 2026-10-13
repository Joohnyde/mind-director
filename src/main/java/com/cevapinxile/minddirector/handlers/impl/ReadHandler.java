/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.handlers.impl;

import com.cevapinxile.minddirector.handlers.EventHandler;
import static com.cevapinxile.minddirector.model.GameState.*;

/**
 *
 * @author denijal
 */
public class ReadHandler implements EventHandler {
    
    private final boolean[] isWorking = new boolean[NUMBER_OF_PLAYERS];

    @Override
    public void handlePlayerPick(int index) {
        if(isWorking[index]) return;
        System.out.println("Starting to read cards of player #"+index);
        isWorking[index] = true;
        System.out.println("Reading cards of player #"+index);
        isWorking[index] = false;
        System.out.println("Finished reading cards of player #"+index);
    }

    @Override
    public boolean handleReturn() {
        return true; // Always allowed
    }
    
}
