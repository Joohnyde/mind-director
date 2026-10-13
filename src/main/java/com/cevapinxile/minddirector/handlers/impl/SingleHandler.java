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
public class SingleHandler implements EventHandler{

    private Integer player = null;

    @Override
    public void handlePlayerPick(int index) {
        player = (player == index) ? null : index;
    }

    @Override
    public boolean handleReturn() {
        if (player == null) {
            return false;
        }
        System.out.println("Overhead view + player #" + player);
        return true;
    }  
     
    @Override
    public void onEnter(){
        player = -1;
        EventHandler.super.onEnter();
    }
    
}
