/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.handlers.impl;

import com.cevapinxile.minddirector.handlers.EventHandler;
import static com.cevapinxile.minddirector.obs.ObsScene.Full;
import static com.cevapinxile.minddirector.obs.ObsScene.Temp;

import com.cevapinxile.minddirector.obs.ObsService;

/**
 *
 * @author denijal
 */
public class IdleHandler implements EventHandler {

    private Integer player = -1;
    private final ObsService obs = ObsService.getInstance();

    @Override
    public void handlePlayerPick(final int index) {
        player = index;
        obs.prepareScene(Full);
        obs.replaceSceneItemByIndex(Temp, 0, "Player" + (index + 1));
    }

    @Override
    public boolean handleReturn() {
        if (player == -1) {
            return false;
        }
        obs.commit();
        return true;
    }

    @Override
    public void onEnter(){
        player = -1;
        obs.reset();
    }

}
