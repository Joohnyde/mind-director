/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.handlers.impl;

import com.cevapinxile.minddirector.handlers.EventHandler;

/**
 *
 * @author denijal
 */
public class IdleHandler implements EventHandler {

    private Integer player = -1;

    @Override
    public void handlePlayerPick(int index) {
        player = (player == index) ? -1 : index;
    }

    @Override
    public boolean handleReturn() {
        if (player == -1) {
            return false;
        }
        System.out.println("Single full-screen of " + player);
        return true;
    }
    
    
    @Override
    public void onEnter(){
        player = -1;
        EventHandler.super.onEnter();
    }

}
