/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.handlers.impl;

import com.cevapinxile.minddirector.handlers.EventHandler;
import com.cevapinxile.minddirector.obs.ObsService;

/**
 *
 * @author denijal
 */
public class NoneHandler implements EventHandler {

    @Override
    public void handlePlayerPick(int index) {
    }

    @Override
    public boolean handleReturn() {
        return true;
    }

    @Override
    public void onEnter(){
        ObsService.getInstance().reset();
    }
}
