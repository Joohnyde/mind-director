/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.handlers.impl;

import com.cevapinxile.minddirector.handlers.EventHandler;
import com.cevapinxile.minddirector.obs.ObsScene;
import com.cevapinxile.minddirector.obs.ObsService;

/**
 *
 * @author denijal
 */
public class AllHandler implements EventHandler {

  @Override
  public void handlePlayerPick(int index) {
    // Nothing to select
  }

  @Override
  public void onEnter() {
    ObsService.getInstance().prepareScene(ObsScene.All);
  }

}
